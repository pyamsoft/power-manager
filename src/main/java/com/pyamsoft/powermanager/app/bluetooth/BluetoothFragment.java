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

package com.pyamsoft.powermanager.app.bluetooth;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import com.pyamsoft.powermanager.app.base.BaseOverviewPagerFragment;
import com.pyamsoft.powermanager.app.base.BasePagerAdapter;
import com.pyamsoft.pydroid.base.fragment.CircularRevealFragmentUtil;

public class BluetoothFragment extends BaseOverviewPagerFragment {

  @NonNull public static final String TAG = "Bluetooth";

  @CheckResult @NonNull
  public static BluetoothFragment newInstance(@NonNull View from, @NonNull View container) {
    final Bundle args = CircularRevealFragmentUtil.bundleArguments(from, container);
    final BluetoothFragment fragment = new BluetoothFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override protected BasePagerAdapter getPagerAdapter() {
    return new BluetoothPagerAdapter(getChildFragmentManager());
  }
}
