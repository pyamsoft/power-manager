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

package com.pyamsoft.powermanagerpresenter.preference.bluetooth;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanagerpresenter.PowerManagerPreferences;
import com.pyamsoft.powermanagerpresenter.preference.CustomTimeInputPreferenceInteractor;
import com.pyamsoft.powermanagerpresenter.preference.CustomTimeInputPreferencePresenter;
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
    return new BluetoothCustomTimePreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("bluetooth_custom_delay_interactor")
  CustomTimeInputPreferenceInteractor provideBluetoothCustomDelayInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new BluetoothDelayPreferenceInteractorImpl(preferences);
  }

  @Provides @Named("bluetooth_custom_enable")
  CustomTimeInputPreferencePresenter provideBluetoothCustomEnablePresenter(
      @NonNull @Named("bluetooth_custom_enable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new BluetoothCustomTimePreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("bluetooth_custom_enable_interactor")
  CustomTimeInputPreferenceInteractor provideBluetoothCustomEnableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new BluetoothEnableTimePreferenceInteractorImpl(preferences);
  }

  @Provides @Named("bluetooth_custom_disable")
  CustomTimeInputPreferencePresenter provideBluetoothCustomDisablePresenter(
      @NonNull @Named("bluetooth_custom_disable_interactor")
          CustomTimeInputPreferenceInteractor interactor, @Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new BluetoothCustomTimePreferencePresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("bluetooth_custom_disable_interactor")
  CustomTimeInputPreferenceInteractor provideBluetoothCustomDisableInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new BluetoothDisableTimePreferenceInteractorImpl(preferences);
  }
}
