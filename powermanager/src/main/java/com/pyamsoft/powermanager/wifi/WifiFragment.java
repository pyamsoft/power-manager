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

package com.pyamsoft.powermanager.wifi;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.base.ModulePagerAdapter;
import com.pyamsoft.powermanager.base.OverviewPagerFragment;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.presenter.base.OverviewPagerPresenter;
import com.pyamsoft.powermanager.presenter.wifi.WifiLoader;
import com.pyamsoft.powermanager.presenter.wifi.WifiOverviewPresenterLoader;
import com.pyamsoft.pydroid.app.PersistLoader;

public class WifiFragment extends OverviewPagerFragment {

  @NonNull public static final String TAG = "Wifi";
  private BooleanInterestObserver observer;

  @CheckResult @NonNull
  public static WifiFragment newInstance(@NonNull View from, @NonNull View container) {
    final WifiFragment fragment = new WifiFragment();
    fragment.setArguments(bundleArguments(from, container));
    return fragment;
  }

  @Override protected void injectObserverModifier() {
    observer = new WifiLoader().loadPersistent();
  }

  @NonNull @Override protected BooleanInterestObserver getObserver() {
    return observer;
  }

  @NonNull @Override protected PersistLoader<OverviewPagerPresenter> getPresenterLoader() {
    return new WifiOverviewPresenterLoader();
  }

  @Override protected int getFabSetIcon() {
    return R.drawable.ic_network_wifi_24dp;
  }

  @Override protected int getFabUnsetIcon() {
    return R.drawable.ic_signal_wifi_off_24dp;
  }

  @NonNull @Override protected ModulePagerAdapter getPagerAdapter() {
    return new WifiPagerAdapter(getChildFragmentManager());
  }

  @Override protected int provideAppBarColor() {
    return R.color.green500;
  }

  @Override protected int provideStatusBarColor() {
    return R.color.green700;
  }
}
