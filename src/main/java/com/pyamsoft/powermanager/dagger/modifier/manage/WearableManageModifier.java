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

package com.pyamsoft.powermanager.dagger.modifier.manage;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;

class WearableManageModifier extends ManageModifier {

  @Inject WearableManageModifier(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    super(context, preferences);
  }

  @Override void mainThreadSet(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    preferences.setWearableManaged(true);
  }

  @Override void mainThreadUnset(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    preferences.setWearableManaged(false);
  }
}
