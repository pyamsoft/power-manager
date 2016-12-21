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

package com.pyamsoft.powermanagerpresenter.preference;

import com.pyamsoft.powermanagerpresenter.preference.airplane.AirplaneCustomPreferenceModule;
import com.pyamsoft.powermanagerpresenter.preference.airplane.AirplaneDelayPreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.airplane.AirplaneDisablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.airplane.AirplaneEnablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.bluetooth.BluetoothCustomPreferenceModule;
import com.pyamsoft.powermanagerpresenter.preference.bluetooth.BluetoothDelayPreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.bluetooth.BluetoothDisablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.bluetooth.BluetoothEnablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.data.DataCustomPreferenceModule;
import com.pyamsoft.powermanagerpresenter.preference.data.DataDelayPreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.data.DataDisablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.data.DataEnablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.doze.DozeCustomPreferenceModule;
import com.pyamsoft.powermanagerpresenter.preference.doze.DozeDelayPreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.doze.DozeDisablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.doze.DozeEnablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.sync.SyncCustomPreferenceModule;
import com.pyamsoft.powermanagerpresenter.preference.sync.SyncDelayPreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.sync.SyncDisablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.sync.SyncEnablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.wifi.WifiCustomPreferenceModule;
import com.pyamsoft.powermanagerpresenter.preference.wifi.WifiDelayPreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.wifi.WifiDisablePreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.wifi.WifiEnablePreferenceLoader;
import dagger.Subcomponent;

@Subcomponent(modules = {
    WifiCustomPreferenceModule.class, DataCustomPreferenceModule.class,
    BluetoothCustomPreferenceModule.class, SyncCustomPreferenceModule.class,
    AirplaneCustomPreferenceModule.class, DozeCustomPreferenceModule.class
}) public interface CustomPreferenceComponent {

  void inject(WifiDelayPreferenceLoader wifiDelayPreferenceLoader);

  void inject(WifiDisablePreferenceLoader wifiDisablePreferenceLoader);

  void inject(WifiEnablePreferenceLoader wifiEnablePreferenceLoader);

  void inject(SyncEnablePreferenceLoader syncEnablePreferenceLoader);

  void inject(SyncDisablePreferenceLoader syncDisablePreferenceLoader);

  void inject(SyncDelayPreferenceLoader syncDelayPreferenceLoader);

  void inject(DozeEnablePreferenceLoader dozeEnablePreferenceLoader);

  void inject(DozeDisablePreferenceLoader dozeDisablePreferenceLoader);

  void inject(DozeDelayPreferenceLoader dozeDelayPreferenceLoader);

  void inject(DataDelayPreferenceLoader dataDelayPreferenceLoader);

  void inject(DataDisablePreferenceLoader dataDisablePreferenceLoader);

  void inject(DataEnablePreferenceLoader dataEnablePreferenceLoader);

  void inject(BluetoothEnablePreferenceLoader bluetoothEnablePreferenceLoader);

  void inject(BluetoothDisablePreferenceLoader bluetoothDisablePreferenceLoader);

  void inject(BluetoothDelayPreferenceLoader bluetoothDelayPreferenceLoader);

  void inject(AirplaneDisablePreferenceLoader airplaneDisablePreferenceLoader);

  void inject(AirplaneEnablePreferenceLoader airplaneEnablePreferenceLoader);

  void inject(AirplaneDelayPreferenceLoader airplaneDelayPreferenceLoader);
}
