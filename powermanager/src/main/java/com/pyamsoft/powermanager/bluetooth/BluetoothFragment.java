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

package com.pyamsoft.powermanager.bluetooth;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.uicore.ModulePagerAdapter;
import com.pyamsoft.powermanager.uicore.OverviewPagerFragment;
import com.pyamsoft.powermanager.uicore.OverviewPagerPresenter;
import com.pyamsoft.pydroid.app.PersistLoader;

public class BluetoothFragment extends OverviewPagerFragment {

  @NonNull public static final String TAG = "Bluetooth";
  private BooleanInterestObserver observer;

  @CheckResult @NonNull
  public static BluetoothFragment newInstance(@NonNull View from, @NonNull View container) {
    final BluetoothFragment fragment = new BluetoothFragment();
    fragment.setArguments(bundleArguments(from, container));
    return fragment;
  }

  @Override protected void injectObserverModifier() {
    observer = new BluetoothLoader().loadPersistent();
  }

  @NonNull @Override protected BooleanInterestObserver getObserver() {
    return observer;
  }

  @NonNull @Override protected PersistLoader<OverviewPagerPresenter> getPresenterLoader() {
    return new BluetoothOverviewPresenterLoader();
  }

  @Override protected int getFabSetIcon() {
    return R.drawable.ic_bluetooth_24dp;
  }

  @Override protected int getFabUnsetIcon() {
    return R.drawable.ic_bluetooth_disabled_24dp;
  }

  @NonNull @Override protected ModulePagerAdapter getPagerAdapter() {
    return new BluetoothPagerAdapter(getChildFragmentManager());
  }

  @Override protected int provideAppBarColor() {
    return R.color.blue500;
  }

  @Override protected int provideStatusBarColor() {
    return R.color.blue700;
  }
}
