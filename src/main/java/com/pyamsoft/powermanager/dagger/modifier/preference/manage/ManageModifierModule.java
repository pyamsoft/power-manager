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
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Scheduler;

@Module public class ManageModifierModule {

  @Singleton @Named("mod_wifi_manage") @Provides BooleanInterestModifier provideWifiModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("io") Scheduler subscribeScheduler,
      @NonNull @Named("main") Scheduler observeScheduler) {
    return new WifiManageModifier(context, preferences, subscribeScheduler, observeScheduler);
  }

  @Singleton @Named("mod_data_manage") @Provides BooleanInterestModifier provideDataModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("io") Scheduler subscribeScheduler,
      @NonNull @Named("main") Scheduler observeScheduler) {
    return new DataManageModifier(context, preferences, subscribeScheduler, observeScheduler);
  }

  @Singleton @Named("mod_bluetooth_manage") @Provides
  BooleanInterestModifier provideBluetoothModifier(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("io") Scheduler subscribeScheduler,
      @NonNull @Named("main") Scheduler observeScheduler) {
    return new BluetoothManageModifier(context, preferences, subscribeScheduler, observeScheduler);
  }

  @Singleton @Named("mod_sync_manage") @Provides BooleanInterestModifier provideSyncModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("io") Scheduler subscribeScheduler,
      @NonNull @Named("main") Scheduler observeScheduler) {
    return new SyncManageModifier(context, preferences, subscribeScheduler, observeScheduler);
  }

  @Singleton @Named("mod_wear_manage") @Provides BooleanInterestModifier provideWearModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("io") Scheduler subscribeScheduler,
      @NonNull @Named("main") Scheduler observeScheduler) {
    return new WearableManageModifier(context, preferences, subscribeScheduler, observeScheduler);
  }

  @Singleton @Named("mod_doze_manage") @Provides BooleanInterestModifier provideDozModifier(
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull @Named("io") Scheduler subscribeScheduler,
      @NonNull @Named("main") Scheduler observeScheduler) {
    return new DozeManageModifier(context, preferences, subscribeScheduler, observeScheduler);
  }
}