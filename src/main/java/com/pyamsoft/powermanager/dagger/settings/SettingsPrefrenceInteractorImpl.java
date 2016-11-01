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

package com.pyamsoft.powermanager.dagger.settings;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.wrapper.PowerTriggerDB;
import com.pyamsoft.powermanager.dagger.ShellCommandHelper;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class SettingsPrefrenceInteractorImpl implements SettingsPreferenceInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerTriggerDB powerTriggerDB;

  @Inject SettingsPrefrenceInteractorImpl(@NonNull PowerTriggerDB powerTriggerDB,
      @NonNull PowerManagerPreferences preferences) {
    this.powerTriggerDB = powerTriggerDB;
    this.preferences = preferences;
  }

  @NonNull @Override public Observable<Boolean> checkRoot() {
    return Observable.defer(() -> Observable.just(ShellCommandHelper.isSUAvailable()));
  }

  @NonNull @Override public Observable<Boolean> clearDatabase() {
    return Observable.defer(() -> {
      Timber.d("Clear database of all entries");
      return powerTriggerDB.deleteAll();
    }).map(result -> {
      Timber.d("Database is cleared: %s", result);
      powerTriggerDB.deleteDatabase();

      // TODO just return something valid
      return true;
    });
  }

  @NonNull @Override public Observable<Boolean> clearAll() {
    return clearDatabase().map(aBoolean -> {
      Timber.d("Clear all preferences");
      preferences.clearAll();
      return true;
    });
  }
}
