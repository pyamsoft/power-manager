/*
 * Copyright 2016 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.powermanager.app.manager.backend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.base.SchedulerPresenter;
import com.pyamsoft.powermanager.dagger.manager.backend.DozeJob;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerDozeInteractor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class ManagerDoze extends SchedulerPresenter<ManagerDoze.DozeView> implements Manager {
  @NonNull public static final String DUMPSYS_DOZE_START = "deviceidle force-idle deep";
  @NonNull public static final String DUMPSYS_DOZE_END = "deviceidle step";
  @NonNull public static final String DUMPSYS_SENSOR_ENABLE = "sensorservice enable";
  @NonNull public static final String DUMPSYS_SENSOR_RESTRICT =
      "sensorservice restrict com.pyamsoft.powermanager";
  @NonNull private final ManagerDozeInteractor interactor;
  @NonNull private Subscription subscription = Subscriptions.empty();

  @Inject public ManagerDoze(@NonNull ManagerDozeInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(mainScheduler, ioScheduler);
    this.interactor = interactor;
  }

  @CheckResult public static boolean isDozeAvailable() {
    return Build.VERSION.SDK_INT == Build.VERSION_CODES.M;
  }

  @CheckResult public static boolean checkDumpsysPermission(@NonNull Context context) {
    return context.getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.DUMP)
        == PackageManager.PERMISSION_GRANTED;
  }

  @CheckResult private static boolean isAutoRotateEnabled(@NonNull Context context) {
    final boolean autorotate =
        Settings.System.getInt(context.getApplicationContext().getContentResolver(),
            Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    Timber.d("is auto rotate: %s", autorotate);
    return autorotate;
  }

  private static void setAutoRotateEnabled(@NonNull Context context, boolean enabled) {
    Timber.d("Set auto rotate: %s", enabled);
    Settings.System.putInt(context.getApplicationContext().getContentResolver(),
        Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
  }

  @CheckResult private static boolean isAutoBrightnessEnabled(@NonNull Context context) {
    try {
      final boolean autobright =
          Settings.System.getInt(context.getApplicationContext().getContentResolver(),
              Settings.System.SCREEN_BRIGHTNESS_MODE)
              == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
      Timber.d("is auto bright: %s", autobright);
      return autobright;
    } catch (Settings.SettingNotFoundException e) {
      Timber.e(e, "error getting autobrightness");
      return false;
    }
  }

  private static void setAutoBrightnessEnabled(@NonNull Context context, boolean enabled) {
    Timber.d("Set auto brightness: %s", enabled);
    Settings.System.putInt(context.getApplicationContext().getContentResolver(),
        Settings.System.SCREEN_BRIGHTNESS_MODE,
        enabled ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
  }

  public static void fixSensorDisplayRotationBug(@NonNull Context context) {
    Timber.d("Run sensor fix which resets both auto brightness and rotation after sensor dump");
    Timber.w("Run as blocking observable");
    final boolean result = Observable.defer(
        () -> Observable.just(isAutoBrightnessEnabled(context.getApplicationContext())))
        .map(autobright -> {
          setAutoBrightnessEnabled(context.getApplicationContext(), !autobright);
          return true;
        })
        .delay(100, TimeUnit.MILLISECONDS)
        .map(ignore -> isAutoRotateEnabled(context.getApplicationContext()))
        .map(autorotate -> {
          setAutoRotateEnabled(context.getApplicationContext(), !autorotate);
          return true;
        })
        .delay(100, TimeUnit.MILLISECONDS)
        .toBlocking()
        .first();
    // Always true
    Timber.d("Result is %s", result);
  }

  @SuppressLint("NewApi")
  public static void executeDumpsys(@NonNull Context context, @NonNull String cmd) {
    if (!(checkDumpsysPermission(context) && isDozeAvailable())) {
      Timber.e("Does not have permission to call dumpsys");
      return;
    }

    final Process process;
    boolean caughtPermissionDenial = false;
    try {
      final String command = "dumpsys " + cmd;
      process = Runtime.getRuntime().exec(command);
      try (final BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(process.getInputStream()))) {
        Timber.d("Read results of exec: '%s'", command);
        String line = bufferedReader.readLine();
        while (line != null && !line.isEmpty()) {
          if (line.startsWith("Permission Denial")) {
            Timber.e("Command resulted in permission denial");
            caughtPermissionDenial = true;
            break;
          }
          Timber.d("%s", line);
          line = bufferedReader.readLine();
        }
      }

      if (caughtPermissionDenial) {
        throw new IllegalStateException("Error running command: " + command);
      }

      // Will always be 0
    } catch (IOException e) {
      Timber.e(e, "Error running shell command");
    }
  }

  @CheckResult @NonNull Observable<Boolean> baseObservable() {
    return interactor.isDozeEnabled().map(aBoolean -> {
      Timber.d("Cancel old doze jobs");
      PowerManager.getInstance().getJobManager().cancelJobs(TagConstraint.ANY, DozeJob.DOZE_TAG);
      return aBoolean;
    }).filter(aBoolean -> {
      Timber.d("filter Doze not enabled");
      return aBoolean;
    });
  }

  @Override public void enable() {
    unsubSubscription();
    subscription = baseObservable().flatMap(aBoolean -> {
      if (!isDozeAvailable()) {
        Timber.e("Doze is not available on this platform");
        return Observable.empty();
      }

      return Observable.just(0L);
    }).subscribeOn(getSubscribeScheduler()).observeOn(getObserveScheduler()).subscribe(delay -> {
      PowerManager.getInstance().getJobManager().addJobInBackground(new DozeJob.EnableJob());
    }, throwable -> {
      Timber.e(throwable, "onError");
    }, this::unsubSubscription);
  }

  private void unsubSubscription() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @Override public void disable(boolean charging) {
    unsubSubscription();
    subscription =
        baseObservable().flatMap(aBoolean -> interactor.isIgnoreCharging())
            .filter(ignore -> {
              Timber.d("Filter out if ignore doze and device is charging");
              return !(ignore && charging);
            })
            .flatMap(aBoolean -> {
              if (!isDozeAvailable()) {
                Timber.e("Doze is not available on this platform");
                return Observable.empty();
              }

              return interactor.getDozeDelay();
            })
            .subscribeOn(getSubscribeScheduler())
            .observeOn(getObserveScheduler())
            .subscribe(delay -> {
              PowerManager.getInstance()
                  .getJobManager()
                  .addJobInBackground(new DozeJob.DisableJob(delay));
            }, throwable -> {
              Timber.e(throwable, "onError");
            }, this::unsubSubscription);
  }

  @Override public void cleanup() {
    unsubSubscription();
  }

  public void forceOutOfDoze() {
    final boolean forceOut = interactor.isForceOutOfDoze().toBlocking().first();
    if (forceOut) {
      Timber.d("Force device out of Doze mode");
      enable();
    }
  }

  public interface DozeView {
  }
}
