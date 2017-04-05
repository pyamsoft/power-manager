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

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerDB;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class TriggerInteractor extends TriggerBaseInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final TriggerCacheInteractor cacheInteractor;

  @Inject TriggerInteractor(@NonNull PowerTriggerDB powerTriggerDB,
      @NonNull TriggerCacheInteractor cacheInteractor) {
    super(powerTriggerDB);
    this.cacheInteractor = cacheInteractor;
  }

  public void clearCached() {
    cacheInteractor.clearCache();
  }

  /**
   * public
   */
  @CheckResult @NonNull Observable<PowerTriggerEntry> queryAll(boolean forceRefresh) {
    return Single.defer(() -> {
      final Single<List<PowerTriggerEntry>> result;
      synchronized (this) {
        Single<List<PowerTriggerEntry>> cache = cacheInteractor.retrieve();
        if (cache == null || forceRefresh) {
          Timber.d("Refresh power triggers");
          result = getPowerTriggerDB().queryAll().first(Collections.emptyList()).cache();
          cacheInteractor.cache(result);
        } else {
          Timber.d("Fetch triggers from cache");
          result = cache;
        }
      }

      return result;
    }).toObservable().flatMap(Observable::fromIterable).sorted((entry, entry2) -> {
      if (entry.percent() == entry2.percent()) {
        return 0;
      } else if (entry.percent() < entry2.percent()) {
        return -1;
      } else {
        return 1;
      }
    });
  }

  /**
   * public
   */
  @CheckResult @NonNull Observable<PowerTriggerEntry> put(@NonNull PowerTriggerEntry entry) {
    return getPowerTriggerDB().queryWithPercent(entry.percent())
        .first(PowerTriggerEntry.empty())
        .toObservable()
        .flatMap(triggerEntry -> {
          if (!PowerTriggerEntry.isEmpty(triggerEntry)) {
            Timber.e("Entry already exists, throw");
            throw new SQLiteConstraintException(
                "Entry already exists with percent: " + entry.percent());
          }

          if (PowerTriggerEntry.isEmpty(entry)) {
            Timber.e("Trigger is EMPTY");
            return Observable.just(-1L);
          } else if (entry.percent() > 100 || entry.percent() <= 0) {
            Timber.e("Percent too high");
            return Observable.just(-1L);
          } else {
            Timber.d("Insert new Trigger into DB");
            return getPowerTriggerDB().insert(entry);
          }
        })
        .map(aLong -> {
          if (aLong == -1L) {
            throw new IllegalStateException("Trigger is EMPTY");
          } else {
            cacheInteractor.clearCache();
            Timber.d("new trigger created");
            return entry;
          }
        });
  }

  /**
   * public
   */
  @CheckResult @NonNull Observable<Integer> delete(int percent) {
    return getPowerTriggerDB().queryAll()
        .first(Collections.emptyList())
        .map(powerTriggerEntries -> {

          // Sort first
          Collections.sort(powerTriggerEntries, (entry, entry2) -> {
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
          });

          int foundEntry = -1;
          for (int i = 0; i < powerTriggerEntries.size(); ++i) {
            final PowerTriggerEntry entry = powerTriggerEntries.get(i);
            if (entry.percent() == percent) {
              foundEntry = i;
              break;
            }
          }

          if (foundEntry == -1) {
            throw new IllegalStateException("Could not find entry with percent: " + percent);
          }

          return foundEntry;
        })
        .toObservable()
        .flatMap(position -> {
          Timber.d("Delete trigger with percent: %d", percent);
          return getPowerTriggerDB().deleteWithPercent(percent).map(integer -> {
            Timber.d("Return the position");
            cacheInteractor.clearCache();
            return position;
          });
        });
  }
}
