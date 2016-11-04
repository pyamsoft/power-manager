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

package com.pyamsoft.powermanager.app.sync;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.ModulePagerAdapter;
import com.pyamsoft.powermanager.app.base.OverviewPagerFragment;
import com.pyamsoft.powermanager.app.base.OverviewPagerPresenter;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.pydroid.app.PersistLoader;
import javax.inject.Inject;
import javax.inject.Named;

public class SyncFragment extends OverviewPagerFragment {

  @NonNull public static final String TAG = "Sync";
  @Inject @Named("obs_sync_state") BooleanInterestObserver observer;

  @CheckResult @NonNull
  public static SyncFragment newInstance(@NonNull View from, @NonNull View container) {
    final SyncFragment fragment = new SyncFragment();
    fragment.setArguments(bundleArguments(from, container));
    return fragment;
  }

  @Override protected void injectObserverModifier() {
    Injector.get().provideComponent().plusSyncScreenComponent().inject(this);
  }

  @NonNull @Override protected BooleanInterestObserver getObserver() {
    return observer;
  }

  @NonNull @Override protected PersistLoader<OverviewPagerPresenter> getPresenterLoader() {
    return new SyncOverviewPresenterLoader();
  }

  @Override protected int getFabSetIcon() {
    return R.drawable.ic_sync_24dp;
  }

  @Override protected int getFabUnsetIcon() {
    return R.drawable.ic_sync_disabled_24dp;
  }

  @NonNull @Override protected ModulePagerAdapter getPagerAdapter() {
    return new SyncPagerAdapter(getChildFragmentManager());
  }

  @Override protected int provideAppBarColor() {
    return R.color.yellow500;
  }

  @Override protected int provideStatusBarColor() {
    return R.color.yellow700;
  }
}
