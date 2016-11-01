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

package com.pyamsoft.powermanager.dagger.preference;

import com.pyamsoft.powermanager.app.preference.airplane.AirplaneDelayPreference;
import com.pyamsoft.powermanager.app.preference.airplane.AirplaneDisableTimePreference;
import com.pyamsoft.powermanager.app.preference.airplane.AirplaneEnableTimePreference;
import com.pyamsoft.powermanager.app.preference.bluetooth.BluetoothDelayPreference;
import com.pyamsoft.powermanager.app.preference.bluetooth.BluetoothDisableTimePreference;
import com.pyamsoft.powermanager.app.preference.bluetooth.BluetoothEnableTimePreference;
import com.pyamsoft.powermanager.app.preference.data.DataDelayPreference;
import com.pyamsoft.powermanager.app.preference.data.DataDisableTimePreference;
import com.pyamsoft.powermanager.app.preference.data.DataEnableTimePreference;
import com.pyamsoft.powermanager.app.preference.doze.DozeDelayPreference;
import com.pyamsoft.powermanager.app.preference.doze.DozeDisableTimePreference;
import com.pyamsoft.powermanager.app.preference.doze.DozeEnableTimePreference;
import com.pyamsoft.powermanager.app.preference.sync.SyncDelayPreference;
import com.pyamsoft.powermanager.app.preference.sync.SyncDisableTimePreference;
import com.pyamsoft.powermanager.app.preference.sync.SyncEnableTimePreference;
import com.pyamsoft.powermanager.app.preference.wifi.WifiDelayPreference;
import com.pyamsoft.powermanager.app.preference.wifi.WifiDisableTimePreference;
import com.pyamsoft.powermanager.app.preference.wifi.WifiEnableTimePreference;
import com.pyamsoft.powermanager.dagger.preference.airplane.AirplaneCustomPreferenceModule;
import com.pyamsoft.powermanager.dagger.preference.bluetooth.BluetoothCustomPreferenceModule;
import com.pyamsoft.powermanager.dagger.preference.data.DataCustomPreferenceModule;
import com.pyamsoft.powermanager.dagger.preference.doze.DozeCustomPreferenceModule;
import com.pyamsoft.powermanager.dagger.preference.sync.SyncCustomPreferenceModule;
import com.pyamsoft.powermanager.dagger.preference.wifi.WifiCustomPreferenceModule;
import dagger.Subcomponent;

@Subcomponent(modules = {
    WifiCustomPreferenceModule.class, DataCustomPreferenceModule.class,
    BluetoothCustomPreferenceModule.class, SyncCustomPreferenceModule.class,
    AirplaneCustomPreferenceModule.class, DozeCustomPreferenceModule.class
}) public interface CustomPreferenceComponent {

  void inject(WifiDelayPreference preference);

  void inject(WifiEnableTimePreference preference);

  void inject(WifiDisableTimePreference preference);

  void inject(DataDelayPreference preference);

  void inject(DataEnableTimePreference preference);

  void inject(DataDisableTimePreference preference);

  void inject(BluetoothDelayPreference preference);

  void inject(BluetoothEnableTimePreference preference);

  void inject(BluetoothDisableTimePreference preference);

  void inject(SyncDelayPreference preference);

  void inject(SyncEnableTimePreference preference);

  void inject(SyncDisableTimePreference preference);

  void inject(AirplaneDelayPreference preference);

  void inject(AirplaneEnableTimePreference preference);

  void inject(AirplaneDisableTimePreference preference);

  void inject(DozeDelayPreference preference);

  void inject(DozeEnableTimePreference preference);

  void inject(DozeDisableTimePreference preference);
}
