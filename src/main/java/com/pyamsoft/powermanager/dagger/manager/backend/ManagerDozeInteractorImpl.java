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

package com.pyamsoft.powermanager.dagger.manager.backend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.receiver.SensorFixReceiver;
import com.pyamsoft.powermanager.dagger.base.BaseJob;
import com.pyamsoft.powermanager.dagger.manager.jobs.DozeJob;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

public class ManagerDozeInteractorImpl extends ManagerInteractorBase
    implements ManagerDozeInteractor {

  @NonNull private final Context appContext;

  @Inject ManagerDozeInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    super(context, preferences);
    this.appContext = context.getApplicationContext();
  }

  @NonNull @Override public Observable<Long> getDelayTime() {
    return Observable.defer(() -> Observable.just(getPreferences().getDozeDelay()));
  }

  @NonNull @Override Observable<Long> getPeriodicEnableTime() {
    return getDelayTime();
  }

  @NonNull @Override Observable<Long> getPeriodicDisableTime() {
    return getDelayTime();
  }

  @NonNull @Override public Observable<Boolean> isForceOutOfDoze() {
    return Observable.defer(() -> Observable.just(getPreferences().isForceOutDoze()));
  }

  @NonNull @Override public Observable<Boolean> isManageSensors() {
    return Observable.defer(() -> {
      final boolean manage = getPreferences().isManageSensors();

      final boolean hasPermission;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Timber.d("Check that we have write permission on Marshmallow");
        hasPermission = Settings.System.canWrite(appContext);
      } else {
        Timber.d("Write permission is auto-granted on <M");
        hasPermission = true;
      }
      return Observable.just(manage && hasPermission);
    });
  }

  @NonNull @Override public Observable<SensorFixReceiver> createSensorFixReceiver() {
    return Observable.defer(() -> Observable.just(new SensorFixReceiver(appContext)));
  }

  @SuppressLint("NewApi") @Override public void executeDumpsys(@NonNull String cmd) {
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

  @NonNull @Override public Observable<Boolean> isChargingIgnore() {
    return isDozeIgnoreCharging();
  }

  @NonNull @Override public Observable<Boolean> isEnabled() {
    return isDozeEnabled();
  }

  @NonNull @Override public Observable<Boolean> isManaged() {
    return isDozeEnabled();
  }

  @NonNull @Override public Observable<Boolean> isPeriodic() {
    return Observable.defer(() -> Observable.just(false));
  }

  @NonNull @Override public Observable<BaseJob> createEnableJob(long delayTime, boolean periodic) {
    return isForceOutOfDoze().map(DozeJob.EnableJob::new);
  }

  @NonNull @Override
  public Observable<BaseJob> createDisableJob(long delayTime, boolean manageSensors) {
    return isForceOutOfDoze().map(
        forceDoze -> new DozeJob.DisableJob(delayTime, forceDoze, manageSensors));
  }

  @NonNull @Override public Observable<ManagerInteractor> cancelJobs() {
    return cancelJobs(DozeJob.DOZE_TAG);
  }
}
