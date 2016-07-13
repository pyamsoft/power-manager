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

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.modifier.InterestModifier;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class ManageModifierModule {

  @ActivityScope @Named("wifi") @Provides InterestModifier provideWifiModifier(
      @NonNull PowerManagerPreferences preferences) {
    return new WifiManageModifier(preferences);
  }

  @ActivityScope @Named("data") @Provides InterestModifier provideDataModifier(
      @NonNull PowerManagerPreferences preferences) {
    return new DataManageModifier(preferences);
  }

  @ActivityScope @Named("bluetooth") @Provides InterestModifier provideBluetoothModifier(
      @NonNull PowerManagerPreferences preferences) {
    return new BluetoothManageModifier(preferences);
  }

  @ActivityScope @Named("sync") @Provides InterestModifier provideSyncModifier(
      @NonNull PowerManagerPreferences preferences) {
    return new SyncManageModifier(preferences);
  }
}
