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

package com.pyamsoft.powermanager.dagger.trigger;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.sql.PowerTriggerDB;
import rx.Observable;
import timber.log.Timber;

abstract class BaseTriggerInteractorImpl implements BaseTriggerInteractor {

  @NonNull private final Context appContext;

  protected BaseTriggerInteractorImpl(Context context) {
    this.appContext = context.getApplicationContext();
  }

  @CheckResult @NonNull public final Context getAppContext() {
    return appContext;
  }

  @NonNull @Override public Observable<Integer> size() {
    return PowerTriggerDB.with(appContext).queryAll().first().map(powerTriggerEntries -> {
      // Can't use actual .count operator here as it always returns 1, for 1 List
      // We actually want to count the number of items in the list
      final int count = powerTriggerEntries.size();
      Timber.d("Count of elements: %d", count);
      return count;
    });
  }
}
