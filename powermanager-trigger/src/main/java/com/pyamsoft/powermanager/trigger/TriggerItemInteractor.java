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

package com.pyamsoft.powermanager.trigger;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerDB;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry;
import io.reactivex.Flowable;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton class TriggerItemInteractor extends TriggerBaseInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final TriggerCacheInteractor cacheInteractor;

  @Inject TriggerItemInteractor(@NonNull PowerTriggerDB powerTriggerDB,
      @NonNull TriggerCacheInteractor cacheInteractor) {
    super(powerTriggerDB);
    this.cacheInteractor = cacheInteractor;
  }

  /**
   * public
   */
  @CheckResult @NonNull Flowable<PowerTriggerEntry> update(@NonNull PowerTriggerEntry entry,
      boolean enabled) {
    return Flowable.defer(() -> {
      final int percent = entry.percent();
      Timber.d("Update enabled state with percent: %d", percent);
      Timber.d("Update entry to enabled state: %s", enabled);
      return getPowerTriggerDB().updateEnabled(enabled, percent);
    }).flatMap(updated -> {
      cacheInteractor.clearCache();
      return get(entry.percent());
    });
  }
}
