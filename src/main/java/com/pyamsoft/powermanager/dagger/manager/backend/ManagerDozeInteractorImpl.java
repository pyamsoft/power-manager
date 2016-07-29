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
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.receiver.SensorFixReceiver;
import javax.inject.Inject;
import rx.Observable;

public class ManagerDozeInteractorImpl implements ManagerDozeInteractor {

  @NonNull private final Context appContext;
  @NonNull private final PowerManagerPreferences preferences;

  @Inject public ManagerDozeInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
  }

  @NonNull @Override public Observable<Long> getDozeDelay() {
    return Observable.defer(() -> Observable.just(preferences.getDozeDelay()));
  }

  // KLUDGE duplicates code with base
  @NonNull @Override public Observable<Boolean> isDozeEnabled() {
    return Observable.defer(() -> Observable.just(preferences.isDozeEnabled()));
  }

  @NonNull @Override public Observable<Boolean> isIgnoreCharging() {
    return Observable.defer(() -> Observable.just(preferences.isIgnoreChargingDoze()));
  }

  @NonNull @Override public Observable<Boolean> isForceOutOfDoze() {
    return Observable.defer(() -> Observable.just(preferences.isForceOutDoze()));
  }

  @NonNull @Override public Observable<Boolean> isManageSensors() {
    return Observable.defer(() -> Observable.just(preferences.isManageSensors()));
  }

  @NonNull @Override public Observable<SensorFixReceiver> createSensorFixReceiver() {
    return Observable.defer(() -> Observable.just(new SensorFixReceiver(appContext)));
  }
}
