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

package com.pyamsoft.powermanager.dagger.managepreference;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;

abstract class BaseManagePreferenceInteractorImpl implements BaseManagePreferenceInteractor {

  @NonNull private final BooleanInterestModifier manageModifier;

  BaseManagePreferenceInteractorImpl(@NonNull BooleanInterestModifier manageModifier) {
    this.manageModifier = manageModifier;
  }

  @Override public void updateManage(boolean state) {
    if (state) {
      manageModifier.set();
    } else {
      manageModifier.unset();
    }
  }
}
