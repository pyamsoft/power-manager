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

import com.pyamsoft.powermanager.app.preference.data.DataDelayPreference;
import com.pyamsoft.powermanager.app.preference.data.DataDisableTimePreference;
import com.pyamsoft.powermanager.app.preference.data.DataEnableTimePreference;
import com.pyamsoft.powermanager.app.preference.wifi.WifiDelayPreference;
import com.pyamsoft.powermanager.app.preference.wifi.WifiDisableTimePreference;
import com.pyamsoft.powermanager.app.preference.wifi.WifiEnableTimePreference;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import com.pyamsoft.powermanager.dagger.preference.data.DataCustomPreferenceModule;
import com.pyamsoft.powermanager.dagger.preference.wifi.WifiCustomPreferenceModule;
import dagger.Subcomponent;

@ActivityScope @Subcomponent(modules = {
    WifiCustomPreferenceModule.class, DataCustomPreferenceModule.class
}) public interface CustomPreferenceComponent {

  void inject(WifiDelayPreference preference);

  void inject(WifiEnableTimePreference preference);

  void inject(WifiDisableTimePreference preference);

  void inject(DataDelayPreference preference);

  void inject(DataEnableTimePreference preference);

  void inject(DataDisableTimePreference preference);
}
