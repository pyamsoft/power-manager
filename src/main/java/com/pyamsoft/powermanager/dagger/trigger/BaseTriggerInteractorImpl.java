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
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import rx.Observable;
import timber.log.Timber;

abstract class BaseTriggerInteractorImpl implements BaseTriggerInteractor {

  @NonNull final Context appContext;

  BaseTriggerInteractorImpl(Context context) {
    this.appContext = context.getApplicationContext();
  }

  @CheckResult @NonNull final Context getAppContext() {
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

  @NonNull @Override public Observable<Integer> getPosition(int percent) {
    return PowerTriggerDB.with(appContext)
        .queryAll()
        .first()
        .flatMap(Observable::from)
        .toSortedList((entry, entry2) -> {
          if (entry.percent() < entry2.percent()) {
            // This is less, goes first
            return -1;
          } else if (entry.percent() > entry2.percent()) {
            // This is greater, goes second
            return 1;
          } else {
            // Same percent. This is impossible technically due to DB rules
            throw new IllegalStateException("Cannot have two entries with the same percent");
          }
        })
        .map(powerTriggerEntries -> {
          int position = -1;
          for (int i = 0; i < powerTriggerEntries.size(); ++i) {
            final PowerTriggerEntry entry = powerTriggerEntries.get(i);
            if (entry.percent() == percent) {
              position = i;
              break;
            }
          }

          if (position == -1) {
            throw new IndexOutOfBoundsException("Could not find entry with percent: " + percent);
          }
          return position;
        });
  }
}
