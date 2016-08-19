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

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.app.receiver.SensorFixReceiver;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

public class ManagerDozeInteractorImpl extends ManagerInteractorDozeBase
    implements ManagerDozeInteractor {

  @NonNull private final Context appContext;

  @Inject ManagerDozeInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    super(context, preferences);
    this.appContext = context.getApplicationContext();
  }

  @NonNull @Override public Observable<Long> getDozeDelay() {
    return Observable.defer(() -> Observable.just(getPreferences().getDozeDelay()));
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

  @Override public void cancelAllJobs() {
    Singleton.Jobs.with(appContext).cancelJobs(TagConstraint.ANY, DozeJob.DOZE_TAG);
  }

  @Override public void queueEnableJob(boolean forceDoze) {
    Singleton.Jobs.with(appContext).addJobInBackground(new DozeJob.EnableJob(forceDoze));
  }

  @Override public void queueDisableJob(long delay, boolean forceDoze, boolean manageSensors) {
    Singleton.Jobs.with(appContext)
        .addJobInBackground(new DozeJob.DisableJob(delay, forceDoze, manageSensors));
  }
}
