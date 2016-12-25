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

package com.pyamsoft.powermanager.presenter.trigger;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanagermodel.sql.PowerTriggerEntry;
import java.util.Collections;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class TriggerInteractorImpl implements TriggerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final PowerTriggerDB powerTriggerDB;

  @Inject TriggerInteractorImpl(@NonNull PowerTriggerDB powerTriggerDB) {
    this.powerTriggerDB = powerTriggerDB;
  }

  @NonNull @Override public Observable<PowerTriggerEntry> queryAll() {
    return powerTriggerDB.queryAll().first().concatMap(Observable::from);
  }

  @NonNull @Override public Observable<PowerTriggerEntry> put(@NonNull PowerTriggerEntry entry) {
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
        Timber.d("new trigger created");
        return entry;
      }
    });
  }

  @NonNull @Override public Observable<Integer> delete(int percent) {
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
        return position;
      });
    });
  }

  @NonNull @Override
  public Observable<Boolean> update(@NonNull PowerTriggerEntry entry, boolean enabled) {
    return Observable.defer(() -> {
      final int percent = entry.percent();
      Timber.d("Update enabled state with percent: %d", percent);
      Timber.d("Update entry to enabled state: %s", enabled);
      return powerTriggerDB.updateEnabled(enabled, percent);
    }).map(integer -> {
      Timber.d("Return code for update(): %d", integer);

      // For now, just return true
      return Boolean.TRUE;
    });
  }

  @NonNull @Override public Observable<PowerTriggerEntry> get(int percent) {
    return powerTriggerDB.queryWithPercent(percent).first();
  }
}
