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

package com.pyamsoft.powermanager.doze;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.model.StateInterestObserver;
import com.pyamsoft.powermanager.uicore.ModulePagerAdapter;
import com.pyamsoft.powermanager.uicore.OverviewPagerFragment;
import com.pyamsoft.powermanager.uicore.OverviewPagerPresenter;
import javax.inject.Inject;
import javax.inject.Named;

public class DozeFragment extends OverviewPagerFragment {

  @NonNull public static final String TAG = "Doze";
  @Inject @Named("obs_doze_state") StateInterestObserver observer;
  @Inject @Named("doze_overview") OverviewPagerPresenter presenter;

  @CheckResult @NonNull
  public static DozeFragment newInstance(@NonNull View from, @NonNull View container) {
    final DozeFragment fragment = new DozeFragment();
    fragment.setArguments(bundleArguments(from, container));
    return fragment;
  }

  @NonNull @Override protected OverviewPagerPresenter providePresenter() {
    return presenter;
  }

  @NonNull @Override protected StateInterestObserver provideObserver() {
    return observer;
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusDozeScreenComponent().inject(this);
  }

  @Override protected int getFabSetIcon() {
    return 0;
  }

  @Override protected int getFabUnsetIcon() {
    return 0;
  }

  @NonNull @Override protected ModulePagerAdapter getPagerAdapter() {
    return new DozePagerAdapter(getChildFragmentManager());
  }

  @Override protected int provideAppBarColor() {
    return R.color.purple500;
  }

  @Override protected int provideStatusBarColor() {
    return R.color.purple700;
  }
}
