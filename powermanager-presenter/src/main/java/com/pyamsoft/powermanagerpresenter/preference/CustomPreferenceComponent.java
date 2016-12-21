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
import com.pyamsoft.powermanagerpresenter.preference.bluetooth.BluetoothCustomPreferenceModule;
import com.pyamsoft.powermanagerpresenter.preference.data.DataCustomPreferenceModule;
import com.pyamsoft.powermanagerpresenter.preference.doze.DozeCustomPreferenceModule;
import com.pyamsoft.powermanagerpresenter.preference.sync.SyncCustomPreferenceModule;
import com.pyamsoft.powermanagerpresenter.preference.wifi.WifiCustomPreferenceModule;
import dagger.Subcomponent;

@Subcomponent(modules = {
    WifiCustomPreferenceModule.class, DataCustomPreferenceModule.class,
    BluetoothCustomPreferenceModule.class, SyncCustomPreferenceModule.class,
    AirplaneCustomPreferenceModule.class, DozeCustomPreferenceModule.class
}) public interface CustomPreferenceComponent {

  void inject(PreferenceLoader loader);
}
