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

package com.pyamsoft.powermanager.base.observer.preference.preference;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.R;
import com.pyamsoft.powermanager.base.observer.preference.BooleanPreferenceObserver;
import javax.inject.Inject;

class AirplanePeriodicObserver extends BooleanPreferenceObserver {

  @Inject AirplanePeriodicObserver(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    super(preferences, context.getString(R.string.periodic_airplane_key));
  }

  @Override protected boolean is(@NonNull PowerManagerPreferences preferences) {
    return preferences.isPeriodicAirplane();
  }
}