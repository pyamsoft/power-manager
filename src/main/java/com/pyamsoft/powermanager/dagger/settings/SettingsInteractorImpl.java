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

package com.pyamsoft.powermanager.dagger.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.sql.PowerTriggerOpenHelper;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

final class SettingsInteractorImpl implements SettingsInteractor {

  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final Context appContext;

  @Inject public SettingsInteractorImpl(final @NonNull Context context,
      final @NonNull PowerManagerPreferences preferences) {
    appContext = context.getApplicationContext();
    this.preferences = preferences;
  }

  @NonNull @Override public Observable<Boolean> clearDatabase() {
    return Observable.defer(() -> {
      Timber.d("Clear database of all entries");
      PowerTriggerOpenHelper.deleteAll(appContext);
      return Observable.just(true);
    });
  }

  @NonNull @Override public Observable<Boolean> clearAll() {
    return Observable.zip(clearDatabase(), Observable.defer(() -> {
      Timber.d("Clear all preferences");
      preferences.clearAll();
      return Observable.just(true);
    }), (aBoolean, aBoolean2) -> true);
  }
}
