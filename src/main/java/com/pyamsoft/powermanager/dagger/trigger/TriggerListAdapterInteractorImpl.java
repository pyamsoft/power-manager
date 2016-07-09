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
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.sql.PowerTriggerDB;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import timber.log.Timber;

final class TriggerListAdapterInteractorImpl implements TriggerListAdapterInteractor {

  @Named private final Context appContext;

  @Inject public TriggerListAdapterInteractorImpl(Context context) {
    this.appContext = context.getApplicationContext();
  }

  @NonNull @Override public Observable<Integer> size() {
    return PowerTriggerDB.with(appContext).queryAll().first().count();
  }

  @NonNull @Override public Observable<PowerTriggerEntry> get(int position) {
    return PowerTriggerDB.with(appContext).queryAll().first().flatMap(powerTriggerEntries -> {
      if (powerTriggerEntries.isEmpty()) {
        Timber.d("Empty list -> Entry empty observable");
        return Observable.just(PowerTriggerEntry.empty());
      } else {
        Timber.d("Flat map");
        return Observable.from(powerTriggerEntries);
      }
    }).skip(position).first().map(entry -> {
      Timber.d("map");
      if (entry == null) {
        Timber.d("Null entry, replace with EMPTY");
        return PowerTriggerEntry.empty();
      } else {
        Timber.d("Return entry");
        return entry;
      }
    });
  }
}
