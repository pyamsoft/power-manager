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

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry;
import io.reactivex.Single;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton class TriggerCacheInteractor {

  @Nullable private Single<List<PowerTriggerEntry>> cachedPowerTriggerEntryObservable;

  @Inject TriggerCacheInteractor() {
    cachedPowerTriggerEntryObservable = null;
  }

  /**
   * public
   */
  void clearCache() {
    cachedPowerTriggerEntryObservable = null;
  }

  /**
   * public
   */
  @CallSuper @Nullable Single<List<PowerTriggerEntry>> retrieve() {
    return cachedPowerTriggerEntryObservable;
  }

  /**
   * public
   */
  void cache(@NonNull Single<List<PowerTriggerEntry>> cache) {
    cachedPowerTriggerEntryObservable = cache;
  }
}
