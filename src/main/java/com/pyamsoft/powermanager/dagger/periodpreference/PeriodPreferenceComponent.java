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

package com.pyamsoft.powermanager.dagger.periodpreference;

import com.pyamsoft.powermanager.app.bluetooth.BluetoothPeriodPresenterLoader;
import com.pyamsoft.powermanager.app.data.DataPeriodPresenterLoader;
import com.pyamsoft.powermanager.app.sync.SyncPeriodPresenterLoader;
import com.pyamsoft.powermanager.app.wifi.WifiPeriodPresenterLoader;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import com.pyamsoft.powermanager.dagger.periodpreference.bluetooth.BluetoothPeriodPreferenceModule;
import com.pyamsoft.powermanager.dagger.periodpreference.data.DataPeriodPreferenceModule;
import com.pyamsoft.powermanager.dagger.periodpreference.sync.SyncPeriodPreferenceModule;
import com.pyamsoft.powermanager.dagger.periodpreference.wifi.WifiPeriodPreferenceModule;
import dagger.Subcomponent;

@ActivityScope @Subcomponent(modules = {
    WifiPeriodPreferenceModule.class, DataPeriodPreferenceModule.class,
    BluetoothPeriodPreferenceModule.class, SyncPeriodPreferenceModule.class
}) public interface PeriodPreferenceComponent {

  void inject(WifiPeriodPresenterLoader loader);

  void inject(DataPeriodPresenterLoader loader);

  void inject(BluetoothPeriodPresenterLoader loader);

  void inject(SyncPeriodPresenterLoader loader);
}