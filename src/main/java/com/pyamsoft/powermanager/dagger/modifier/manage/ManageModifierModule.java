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
import com.pyamsoft.powermanager.app.modifier.InterestModifier;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class ManageModifierModule {

  @Singleton @Named("mod_wifi_manage") @Provides InterestModifier provideWifiModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new WifiManageModifier(context, preferences);
  }

  @Singleton @Named("mod_data_manage") @Provides InterestModifier provideDataModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new DataManageModifier(context, preferences);
  }

  @Singleton @Named("mod_bluetooth_manage") @Provides InterestModifier provideBluetoothModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new BluetoothManageModifier(context, preferences);
  }

  @Singleton @Named("mod_sync_manage") @Provides InterestModifier provideSyncModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new SyncManageModifier(context, preferences);
  }

  @Singleton @Named("mod_wear_manage") @Provides InterestModifier provideWearModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new WearableManageModifier(context, preferences);
  }

  @Singleton @Named("mod_doze_manage") @Provides InterestModifier provideDozModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new DozeManageModifier(context, preferences);
  }
}
