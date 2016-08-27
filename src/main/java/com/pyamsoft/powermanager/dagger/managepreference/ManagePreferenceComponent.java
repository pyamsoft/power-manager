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

package com.pyamsoft.powermanager.dagger.managepreference;

import com.pyamsoft.powermanager.app.bluetooth.BluetoothManagePresenterLoader;
import com.pyamsoft.powermanager.app.data.DataManagePresenterLoader;
import com.pyamsoft.powermanager.app.doze.DozePresenterLoader;
import com.pyamsoft.powermanager.app.sync.SyncManagePresenterLoader;
import com.pyamsoft.powermanager.app.wifi.WifiManagePresenterLoader;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import com.pyamsoft.powermanager.dagger.managepreference.bluetooth.BluetoothManagePreferenceModule;
import com.pyamsoft.powermanager.dagger.managepreference.data.DataManagePreferenceModule;
import com.pyamsoft.powermanager.dagger.managepreference.doze.DozeManagePreferenceModule;
import com.pyamsoft.powermanager.dagger.managepreference.sync.SyncManagePreferenceModule;
import com.pyamsoft.powermanager.dagger.managepreference.wifi.WifiManagePreferenceModule;
import dagger.Subcomponent;

@ActivityScope @Subcomponent(modules = {
    DozeManagePreferenceModule.class, WifiManagePreferenceModule.class,
    DataManagePreferenceModule.class, BluetoothManagePreferenceModule.class,
    SyncManagePreferenceModule.class
}) public interface ManagePreferenceComponent {

  void inject(DozePresenterLoader loader);

  void inject(WifiManagePresenterLoader loader);

  void inject(DataManagePresenterLoader loader);

  void inject(BluetoothManagePresenterLoader loader);

  void inject(SyncManagePresenterLoader loader);
}
