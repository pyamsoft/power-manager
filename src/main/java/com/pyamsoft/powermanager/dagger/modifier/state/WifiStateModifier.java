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
import com.pyamsoft.powermanager.dagger.wrapper.WifiManagerWrapper;
import javax.inject.Inject;
import rx.Scheduler;

class WifiStateModifier extends StateModifier {

  @NonNull private final WifiManagerWrapper wrapper;

  @Inject WifiStateModifier(@NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(context, preferences, observeScheduler, subscribeScheduler);
    wrapper = new WifiManagerWrapper(context);
  }

  @Override void set(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    wrapper.enable();
  }

  @Override void unset(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    wrapper.disable();
  }
}
