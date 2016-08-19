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

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.sql.PowerTriggerDB;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

final class TriggerListAdapterInteractorImpl extends BaseTriggerInteractorImpl
    implements TriggerListAdapterInteractor {

  @Inject TriggerListAdapterInteractorImpl(Context context) {
    super(context);
  }

  @NonNull @Override public Observable<PowerTriggerEntry> get(int position) {
    return PowerTriggerDB.with(getAppContext())
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
        .flatMap(Observable::from)
        .skip(position)
        .first();
  }

  @NonNull @Override
  public Observable<Boolean> update(@NonNull PowerTriggerEntry entry, boolean enabled) {
    return Observable.defer(() -> {
      final PowerTriggerEntry updated = PowerTriggerEntry.updatedEnabled(entry, enabled);
      final ContentValues values = PowerTriggerEntry.asContentValues(updated);

      final int percent = updated.percent();
      Timber.d("Update enabled state with percent: %d", percent);
      Timber.d("Update entry to enabled state: %s", updated.enabled());

      // KLUDGE Update states it is successful, but changes are not actually written
      return PowerTriggerDB.with(getAppContext()).update(values, percent);
    }).map(integer -> {
      // TODO handle the int return value
      Timber.d("Return code for update(): %d", integer);

      // For now, just return true
      return true;
    });
  }
}
