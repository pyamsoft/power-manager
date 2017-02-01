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

package com.pyamsoft.powermanager.base.observer.preference.manage;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.PermissionObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class ManageObserverModule {

  @Singleton @Named("obs_wifi_manage") @Provides BooleanInterestObserver provideWifiObserver(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new WifiManageObserver(context, preferences);
  }

  @Singleton @Named("obs_data_manage") @Provides BooleanInterestObserver provideDataObserver(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    return new DataManageObserver(context, preferences, rootPermissionObserver);
  }

  @Singleton @Named("obs_bluetooth_manage") @Provides
  BooleanInterestObserver provideBluetoothObserver(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new BluetoothManageObserver(context, preferences);
  }

  @Singleton @Named("obs_sync_manage") @Provides BooleanInterestObserver provideSyncObserver(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new SyncManageObserver(context, preferences);
  }

  @Singleton @Named("obs_wear_manage") @Provides BooleanInterestObserver provideWearableObserver(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    return new WearableManageObserver(context, preferences);
  }

  @Singleton @Named("obs_doze_manage") @Provides BooleanInterestObserver provideDozeObserver(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @Named("obs_doze_permission") PermissionObserver dozePermissionObserver) {
    return new DozeManageObserver(context, preferences, dozePermissionObserver);
  }

  @Singleton @Named("obs_airplane_manage") @Provides
  BooleanInterestObserver provideAirplaneObserver(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences,
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    return new AirplaneManageObserver(context, preferences, rootPermissionObserver);
  }
}
