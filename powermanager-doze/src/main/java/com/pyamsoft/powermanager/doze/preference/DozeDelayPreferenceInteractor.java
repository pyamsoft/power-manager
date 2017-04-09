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

package com.pyamsoft.powermanager.doze.preference;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.DozePreferences;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferenceInteractor;
import javax.inject.Inject;

class DozeDelayPreferenceInteractor extends CustomTimePreferenceInteractor<DozePreferences> {

  @Inject DozeDelayPreferenceInteractor(@NonNull DozePreferences preferences) {
    super(preferences);
  }

  @Override protected void saveTimeToPreferences(@NonNull DozePreferences preferences, long time) {
    preferences.setDozeDelay(time);
  }

  @Override protected long getTimeFromPreferences(@NonNull DozePreferences preferences) {
    return preferences.getDozeDelay();
  }
}
