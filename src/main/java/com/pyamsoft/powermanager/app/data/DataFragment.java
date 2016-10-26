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

package com.pyamsoft.powermanager.app.data;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import com.pyamsoft.powermanager.PowerManagerSingleInitProvider;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.BaseOverviewPagerFragment;
import com.pyamsoft.powermanager.app.base.BaseOverviewPagerPresenter;
import com.pyamsoft.powermanager.app.base.BasePagerAdapter;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.pydroid.app.PersistLoader;
import javax.inject.Inject;
import javax.inject.Named;

public class DataFragment extends BaseOverviewPagerFragment {

  @NonNull public static final String TAG = "Data";
  @Inject @Named("obs_data_state") BooleanInterestObserver observer;

  @CheckResult @NonNull
  public static DataFragment newInstance(@NonNull View from, @NonNull View container) {
    final DataFragment fragment = new DataFragment();
    fragment.setArguments(bundleArguments(from, container));
    return fragment;
  }

  @Override protected void injectObserverModifier() {
    PowerManagerSingleInitProvider.get().provideComponent().plusDataScreenComponent().inject(this);
  }

  @NonNull @Override protected BooleanInterestObserver getObserver() {
    return observer;
  }

  @NonNull @Override protected PersistLoader<BaseOverviewPagerPresenter> getPresenterLoader() {
    return new DataOverviewPresenterLoader(getContext());
  }

  @Override protected int getFabSetIcon() {
    return R.drawable.ic_network_cell_24dp;
  }

  @Override protected int getFabUnsetIcon() {
    return R.drawable.ic_signal_cellular_off_24dp;
  }

  @NonNull @Override protected BasePagerAdapter getPagerAdapter() {
    return new DataPagerAdapter(getChildFragmentManager());
  }

  @Override protected int provideAppBarColor() {
    return R.color.orange500;
  }

  @Override protected int provideStatusBarColor() {
    return R.color.orange700;
  }
}
