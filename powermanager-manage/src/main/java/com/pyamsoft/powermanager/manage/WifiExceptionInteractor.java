/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.manage;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.pyamsoft.powermanager.base.preference.WifiPreferences;
import io.reactivex.Completable;
import io.reactivex.Single;
import javax.inject.Inject;

class WifiExceptionInteractor extends ExceptionInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final WifiPreferences preferences;

  @Inject WifiExceptionInteractor(@NonNull WifiPreferences preferences) {
    this.preferences = preferences;
  }

  @NonNull @Override Completable setIgnoreCharging(boolean state) {
    return Completable.fromAction(() -> preferences.setIgnoreChargingWifi(state));
  }

  @NonNull @Override Completable setIgnoreWear(boolean state) {
    return Completable.fromAction(() -> preferences.setIgnoreWearWifi(state));
  }

  @NonNull @Override Single<Pair<Boolean, Boolean>> isIgnoreCharging() {
    return Single.fromCallable(
        () -> new Pair<>(preferences.isWifiManaged() ? Boolean.TRUE : Boolean.FALSE,
            preferences.isIgnoreChargingWifi() ? Boolean.TRUE : Boolean.FALSE));
  }

  @NonNull @Override Single<Pair<Boolean, Boolean>> isIgnoreWear() {
    return Single.fromCallable(
        () -> new Pair<>(preferences.isWifiManaged() ? Boolean.TRUE : Boolean.FALSE,
            preferences.isIgnoreWearWifi() ? Boolean.TRUE : Boolean.FALSE));
  }
}
