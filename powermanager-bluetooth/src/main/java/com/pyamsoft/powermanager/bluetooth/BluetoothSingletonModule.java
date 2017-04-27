/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.bluetooth;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences;
import com.pyamsoft.powermanager.base.preference.OnboardingPreferences;
import com.pyamsoft.powermanager.uicore.ManagePreferenceInteractor;
import com.pyamsoft.powermanager.uicore.PeriodPreferenceInteractor;
import com.pyamsoft.powermanager.uicore.preference.CustomTimePreferenceInteractor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class BluetoothSingletonModule {

  @Singleton @Provides @Named("bluetooth_manage_pref_interactor")
  ManagePreferenceInteractor provideBluetoothManagePreferenceInteractor(
      @NonNull OnboardingPreferences preferences) {
    return new ManagePreferenceInteractor(preferences);
  }

  @Singleton @Provides @Named("bluetooth_period_pref_interactor")
  PeriodPreferenceInteractor provideBluetoothPeriodPreferenceInteractor(
      @NonNull OnboardingPreferences preferences) {
    return new PeriodPreferenceInteractor(preferences);
  }

  @Singleton @Provides @Named("bluetooth_custom_delay_interactor")
  CustomTimePreferenceInteractor provideBluetoothCustomDelayInteractor(
      @NonNull BluetoothPreferences preferences) {
    return new BluetoothDelayPreferenceInteractor(preferences);
  }

  @Singleton @Provides @Named("bluetooth_custom_enable_interactor")
  CustomTimePreferenceInteractor provideBluetoothCustomEnableInteractor(
      @NonNull BluetoothPreferences preferences) {
    return new BluetoothEnablePreferenceInteractor(preferences);
  }

  @Singleton @Provides @Named("bluetooth_custom_disable_interactor")
  CustomTimePreferenceInteractor provideBluetoothCustomDisableInteractor(
      @NonNull BluetoothPreferences preferences) {
    return new BluetoothDisablePreferenceInteractor(preferences);
  }
}
