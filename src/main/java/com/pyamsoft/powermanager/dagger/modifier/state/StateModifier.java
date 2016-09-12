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

package com.pyamsoft.powermanager.dagger.modifier.state;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import timber.log.Timber;

abstract class StateModifier implements BooleanInterestModifier {

  @SuppressWarnings("WeakerAccess") @NonNull final Context appContext;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;

  StateModifier(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    Timber.d("New StateModifier");
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
  }

  @Override public final void set() {
    set(appContext, preferences);
  }

  @Override public final void unset() {
    unset(appContext, preferences);
  }

  abstract void set(@NonNull Context context, @NonNull PowerManagerPreferences preferences);

  abstract void unset(@NonNull Context context, @NonNull PowerManagerPreferences preferences);
}
