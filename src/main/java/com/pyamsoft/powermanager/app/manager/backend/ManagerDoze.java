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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.base.SchedulerPresenter;
import com.pyamsoft.powermanager.app.receiver.SensorFixReceiver;
import com.pyamsoft.powermanager.dagger.manager.backend.DozeJob;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerDozeInteractor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
  @Nullable private SensorFixReceiver sensorFixReceiver;

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

  public void fixSensorDisplayRotationBug() {
    Timber.d("Run sensor fix which resets both auto brightness and rotation after sensor dump");
    if (sensorFixReceiver != null) {
      sensorFixReceiver.unregister();
    }
    sensorFixReceiver = interactor.createSensorFixReceiver().toBlocking().first();
    sensorFixReceiver.register();
  }

  @CheckResult @NonNull Observable<Boolean> baseObservable() {
    return Observable.defer(() -> {
      Timber.d("Cancel old doze jobs");
      PowerManager.getInstance().getJobManager().cancelJobs(TagConstraint.ANY, DozeJob.DOZE_TAG);
      return interactor.isDozeEnabled();
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
    final Observable<Long> delayObservable =
        baseObservable().flatMap(aBoolean -> interactor.isDozeIgnoreCharging()).filter(ignore -> {
          Timber.d("Filter out if ignore doze and device is charging");
          return !(ignore && charging);
        }).flatMap(aBoolean -> {
          if (!isDozeAvailable()) {
            Timber.e("Doze is not available on this platform");
            return Observable.empty();
          }

          return interactor.getDozeDelay();
        });

    final Observable<Boolean> sensorsObservable = interactor.isManageSensors();

    subscription = Observable.zip(delayObservable, sensorsObservable, Pair::new)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(pair -> {
          PowerManager.getInstance()
              .getJobManager()
              .addJobInBackground(new DozeJob.DisableJob(pair.first * 1000L, pair.second));
        }, throwable -> {
          Timber.e(throwable, "onError");
        }, this::unsubSubscription);
  }

  @Override public void cleanup() {
    unsubSubscription();
  }

  public void handleDozeStateChange(@NonNull Context context, boolean currentState,
      boolean charging) {
    if (checkDumpsysPermission(context.getApplicationContext())) {
      Timber.d("Holds DUMP permission");
      if (currentState) {
        Timber.d("Device has entered Doze mode");
        // KLUDGE running blocking
        final boolean forceOut = interactor.isForceOutOfDoze().toBlocking().first();
        if (forceOut) {
          Timber.d("Force device out of Doze mode");
          enable();
        } else {
          Timber.d("Run Doze enter hooks");
          disable(charging);
        }
      } else {
        Timber.d("Device has exited Doze mode, return to normal operating state");
        enable();
      }
    } else {
      Timber.e("Does not have DUMP permission");
    }
  }

  public interface DozeView {
  }
}
