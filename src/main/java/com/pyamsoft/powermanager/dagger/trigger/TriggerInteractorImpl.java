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
import rx.functions.Func0;

final class TriggerInteractorImpl extends BaseTriggerInteractorImpl implements TriggerInteractor {

  @Inject public TriggerInteractorImpl(Context context) {
    super(context);
  }

  @NonNull @Override public Observable<PowerTriggerEntry> put(@NonNull ContentValues entry) {
    return Observable.defer(new Func0<Observable<PowerTriggerEntry>>() {
      @Override public Observable<PowerTriggerEntry> call() {
        PowerTriggerDB.with(getAppContext()).insert(entry);
        return null;
      }
    });
  }
}
