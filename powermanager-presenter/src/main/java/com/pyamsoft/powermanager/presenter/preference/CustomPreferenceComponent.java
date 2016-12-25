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

package com.pyamsoft.powermanager.presenter.preference;

import com.pyamsoft.powermanager.presenter.preference.airplane.AirplaneCustomPreferenceModule;
import com.pyamsoft.powermanager.presenter.preference.airplane.AirplanePreferenceLoader;
import com.pyamsoft.powermanager.presenter.preference.bluetooth.BluetoothCustomPreferenceModule;
import com.pyamsoft.powermanager.presenter.preference.bluetooth.BluetoothPreferenceLoader;
import com.pyamsoft.powermanager.presenter.preference.data.DataCustomPreferenceModule;
import com.pyamsoft.powermanager.presenter.preference.data.DataPreferenceLoader;
import com.pyamsoft.powermanager.presenter.preference.doze.DozeCustomPreferenceModule;
import com.pyamsoft.powermanager.presenter.preference.doze.DozePreferenceLoader;
import com.pyamsoft.powermanager.presenter.preference.sync.SyncCustomPreferenceModule;
import com.pyamsoft.powermanager.presenter.preference.sync.SyncPreferenceLoader;
import com.pyamsoft.powermanager.presenter.preference.wifi.WifiCustomPreferenceModule;
import com.pyamsoft.powermanager.presenter.preference.wifi.WifiPreferenceLoader;
import dagger.Subcomponent;

@Subcomponent(modules = {
    WifiCustomPreferenceModule.class, DataCustomPreferenceModule.class,
    BluetoothCustomPreferenceModule.class, SyncCustomPreferenceModule.class,
    AirplaneCustomPreferenceModule.class, DozeCustomPreferenceModule.class
}) public interface CustomPreferenceComponent {

  void inject(WifiPreferenceLoader loader);

  void inject(DataPreferenceLoader loader);

  void inject(BluetoothPreferenceLoader loader);

  void inject(SyncPreferenceLoader loader);

  void inject(DozePreferenceLoader loader);

  void inject(AirplanePreferenceLoader loader);
}
