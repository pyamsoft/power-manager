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

package com.pyamsoft.powermanager.dagger.preference.bluetooth;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.preference.CustomTimeInputPreferencePresenter;
import com.pyamsoft.powermanager.dagger.preference.CustomTimeInputPreferenceInteractor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class BluetoothCustomPreferenceModule {

  @Provides @Named("bluetooth_custom_delay")
  CustomTimeInputPreferencePresenter provideBluetoothCustomDelayPresenter(
      @NonNull @Named("bluetooth_custom_delay_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new BluetoothDelayPreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("bluetooth_custom_delay_interactor")
  CustomTimeInputPreferenceInteractor provideBluetoothCustomDelayInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new BluetoothDelayPreferenceInteractor(preferences);
  }

  @Provides @Named("bluetooth_custom_enable")
  CustomTimeInputPreferencePresenter provideBluetoothCustomEnablePresenter(
      @NonNull @Named("bluetooth_custom_enable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new BluetoothEnableTimePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("bluetooth_custom_enable_interactor")
  CustomTimeInputPreferenceInteractor provideBluetoothCustomEnableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new BluetoothEnableTimePreferenceInteractor(preferences);
  }

  @Provides @Named("bluetooth_custom_disable")
  CustomTimeInputPreferencePresenter provideBluetoothCustomDisablePresenter(
      @NonNull @Named("bluetooth_custom_disable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new BluetoothDisableTimePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("bluetooth_custom_disable_interactor")
  CustomTimeInputPreferenceInteractor provideBluetoothCustomDisableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new BluetoothDisableTimePreferenceInteractor(preferences);
  }
}
