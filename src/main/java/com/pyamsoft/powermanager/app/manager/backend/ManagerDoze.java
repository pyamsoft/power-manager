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

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.base.SchedulerPresenter;
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

  @NonNull private static final String DUMPSYS_COMMAND = "dumpsys";
  @NonNull private static final String DUMPSYS_SENSORSERVICE_COMMAND =
      DUMPSYS_COMMAND + " sensorservice";
  @NonNull private static final String DUMPSYS_ENABLE_COMMAND =
      DUMPSYS_SENSORSERVICE_COMMAND + " enable";
  @NonNull private static final String DUMPSYS_DISABLE_COMMAND =
      DUMPSYS_SENSORSERVICE_COMMAND + " restrict com.pyamsoft.powermanager";
  @NonNull private static final String DUMPSYS_DOZE_START_COMMAND;
  @NonNull private static final String DUMPSYS_DOZE_END_COMMAND;
  @NonNull private Subscription subscription = Subscriptions.empty();

  static {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      DUMPSYS_DOZE_START_COMMAND = DUMPSYS_COMMAND + " deviceidle force-idle deep";
      DUMPSYS_DOZE_END_COMMAND = DUMPSYS_COMMAND + " deviceidle"
    } else {
      DUMPSYS_DOZE_START_COMMAND = DUMPSYS_COMMAND + " deviceidle force-idle";
    }
  }

  @Inject public ManagerDoze(@NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(mainScheduler, ioScheduler);
  }

  @CheckResult public static boolean isDozeAvailable() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  }

  @SuppressLint("NewApi") @NonNull @CheckResult
  private static Observable<Integer> executeShellCommand(@NonNull String command) {
    return Observable.defer(() -> {
      final Process process;
      boolean caughtPermissionDenial = false;
      try {
        process = Runtime.getRuntime().exec(command);
        try (final BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {
          Timber.d("Read results of exec: '%s'", command);
          String line = bufferedReader.readLine();
          while (line != null && !line.isEmpty()) {
            if (line.startsWith("Permission Denial")) {
              Timber.e("Command resulted in permission denial");
              caughtPermissionDenial = true;
            }
            Timber.d("%s", line);
            line = bufferedReader.readLine();
          }
        }
        final int code = process.waitFor();
        Timber.d("exit: %d", code);

        if (caughtPermissionDenial || code != 0) {
          throw new IllegalStateException("Error running command: " + command);
        }

        // Will always be 0
        return Observable.just(0);
      } catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public void checkDumpsysPermission() {
    unsubSubscription();
    subscription =
        executeShellCommand(DUMPSYS_SENSORSERVICE_COMMAND).subscribeOn(getSubscribeScheduler())
            .observeOn(getObserveScheduler())
            .subscribe(exitCode -> {
              Timber.d("Dumpsys exit: %d", exitCode);
              getView().onDumpSysPermissionSuccess();
            }, throwable -> {
              getView().onDumpSysPermissionError();
            });
  }

  @Override public void enable() {
    if (!isDozeAvailable()) {
      Timber.e("Doze is not available on this platform");
      return;
    }

    Timber.d("Do doze enable");
    unsubSubscription();
    subscription = Observable.defer(() -> executeShellCommand("ls"))
        .flatMap(integer -> executeShellCommand(DUMPSYS_ENABLE_COMMAND))
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(exitCode -> {
          Timber.d("Dumpsys exit: %d", exitCode);
        }, throwable -> {
          Timber.e(throwable, "onError");
        });
  }

  private void unsubSubscription() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @Override public void disable(boolean charging) {
    if (!isDozeAvailable()) {
      Timber.e("Doze is not available on this platform");
      return;
    }

    Timber.d("Do doze disable");
    unsubSubscription();
    subscription = Observable.defer(() -> executeShellCommand("ls"))
        .flatMap(integer -> executeShellCommand(DUMPSYS_DISABLE_COMMAND))
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(exitCode -> {
          Timber.d("Dumpsys exit: %d", exitCode);
        }, throwable -> {
          Timber.e(throwable, "onError");
        });
  }

  @Override public void cleanup() {
    unsubSubscription();
  }

  public interface DozeView {

    void onDumpSysPermissionSuccess();

    void onDumpSysPermissionError();
  }
}
