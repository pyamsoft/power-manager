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
import android.support.v4.app.FragmentManager;
import com.pyamsoft.powermanager.uicore.ManagePreferenceFragment;
import com.pyamsoft.powermanager.uicore.ModulePagerAdapter;
import com.pyamsoft.powermanager.uicore.PeriodicPreferenceFragment;

class BluetoothPagerAdapter extends ModulePagerAdapter {

  BluetoothPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @NonNull @Override protected ManagePreferenceFragment getManageFragment() {
    return new BluetoothManagePreferenceFragment();
  }

  @NonNull @Override protected PeriodicPreferenceFragment getPeriodicFragment() {
    return new BluetoothPeriodicPreferenceFragment();
  }
}
