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

package com.pyamsoft.powermanager.settings;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.shell.RootChecker;
import com.pyamsoft.powermanager.trigger.TriggerInteractor;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerDB;
import io.reactivex.Observable;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton class SettingsPreferenceInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final RootChecker rootChecker;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerTriggerDB powerTriggerDB;
  @SuppressWarnings("WeakerAccess") @NonNull final TriggerInteractor triggerInteractor;

  @Inject SettingsPreferenceInteractor(@NonNull PowerTriggerDB powerTriggerDB,
      @NonNull PowerManagerPreferences preferences, @NonNull RootChecker rootChecker,
      @NonNull TriggerInteractor triggerInteractor) {
    this.powerTriggerDB = powerTriggerDB;
    this.preferences = preferences;
    this.rootChecker = rootChecker;
    this.triggerInteractor = triggerInteractor;
  }

  @CheckResult @NonNull public Observable<Boolean> isRootEnabled() {
    return Observable.fromCallable(preferences::isRootEnabled);
  }

  @NonNull @CheckResult public Observable<Boolean> checkRoot(boolean rootEnable) {
    return Observable.fromCallable(() -> {
      // If we are enabling root, check SU available
      // If we are not enabling root, then everything is ok
      return !rootEnable || rootChecker.isSUAvailable();
    });
  }

  @NonNull @CheckResult public Observable<Boolean> clearDatabase() {
    return Observable.defer(() -> {
      Timber.d("Clear database of all entries");
      return powerTriggerDB.deleteAll();
    }).map(result -> {
      Timber.d("Database is cleared: %s", result);
      powerTriggerDB.deleteDatabase();
      triggerInteractor.clearCached();

      // TODO just return something valid
      return Boolean.TRUE;
    });
  }

  @NonNull @CheckResult public Observable<Boolean> clearAll() {
    return clearDatabase().map(aBoolean -> {
      Timber.d("Clear all preferences");
      preferences.clearAll();
      return Boolean.TRUE;
    });
  }
}
