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

package com.pyamsoft.powermanager.dagger.modifier.preference.manage;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.dagger.modifier.preference.BooleanPreferenceModifier;
import javax.inject.Inject;
import rx.Scheduler;

class WearableManageModifier extends BooleanPreferenceModifier {

  @Inject WearableManageModifier(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences, @NonNull Scheduler subscribeScheduler,
      @NonNull Scheduler observeScheduler) {
    super(context, preferences, subscribeScheduler, observeScheduler);
  }

  @Override protected void set(@NonNull PowerManagerPreferences preferences) {
    preferences.setWearableManaged(true);
  }

  @Override protected void unset(@NonNull PowerManagerPreferences preferences) {
    preferences.setWearableManaged(false);
  }
}
