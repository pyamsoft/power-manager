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
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.base.db.PowerTriggerDB;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

public class TriggerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final PowerTriggerDB powerTriggerDB;
  @SuppressWarnings("WeakerAccess") @Nullable volatile Observable<List<PowerTriggerEntry>>
      cachedPowerTriggerEntryObservable;

  @Inject TriggerInteractor(@NonNull PowerTriggerDB powerTriggerDB) {
    this.powerTriggerDB = powerTriggerDB;
  }

  public void clearCached() {
    cachedPowerTriggerEntryObservable = null;
  }

  @CheckResult @NonNull public Observable<PowerTriggerEntry> queryAll(boolean forceRefresh) {
    return Observable.defer(() -> {
      final Observable<List<PowerTriggerEntry>> result;
      synchronized (this) {
        if (cachedPowerTriggerEntryObservable == null || forceRefresh) {
          Timber.d("Refresh power triggers");
          result = powerTriggerDB.queryAll().first().cache();
          cachedPowerTriggerEntryObservable = result;
        } else {
          Timber.d("Fetch triggers from cache");
          result = cachedPowerTriggerEntryObservable;
        }
      }

      return result;
    }).flatMap(Observable::from).sorted((entry, entry2) -> {
      if (entry.percent() == entry2.percent()) {
        return 0;
      } else if (entry.percent() < entry2.percent()) {
        return -1;
      } else {
        return 1;
      }
    });
  }

  @CheckResult @NonNull public Observable<PowerTriggerEntry> put(@NonNull PowerTriggerEntry entry) {
    return powerTriggerDB.queryWithPercent(entry.percent()).first().flatMap(triggerEntry -> {
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
        return powerTriggerDB.insert(entry);
      }
    }).map(aLong -> {
      if (aLong == -1L) {
        throw new IllegalStateException("Trigger is EMPTY");
      } else {
        synchronized (this) {
          cachedPowerTriggerEntryObservable = null;
        }
        Timber.d("new trigger created");
        return entry;
      }
    });
  }

  @CheckResult @NonNull public Observable<Integer> delete(int percent) {
    return powerTriggerDB.queryAll().first().map(powerTriggerEntries -> {

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
    }).flatMap(position -> {
      Timber.d("Delete trigger with percent: %d", percent);
      return powerTriggerDB.deleteWithPercent(percent).map(integer -> {
        Timber.d("Return the position");
        synchronized (this) {
          cachedPowerTriggerEntryObservable = null;
        }
        return position;
      });
    });
  }

  @CheckResult @NonNull
  public Observable<PowerTriggerEntry> update(@NonNull PowerTriggerEntry entry, boolean enabled) {
    return Observable.defer(() -> {
      final int percent = entry.percent();
      Timber.d("Update enabled state with percent: %d", percent);
      Timber.d("Update entry to enabled state: %s", enabled);
      return powerTriggerDB.updateEnabled(enabled, percent);
    }).flatMap(updated -> {
      synchronized (this) {
        cachedPowerTriggerEntryObservable = null;
      }
      return get(entry.percent());
    });
  }

  @CheckResult @NonNull public Observable<PowerTriggerEntry> get(int percent) {
    return powerTriggerDB.queryWithPercent(percent).first();
  }
}
