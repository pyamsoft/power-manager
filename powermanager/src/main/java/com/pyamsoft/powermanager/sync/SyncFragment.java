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

package com.pyamsoft.powermanager.sync;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.model.StateObserver;
import com.pyamsoft.powermanager.uicore.ModulePagerAdapter;
import com.pyamsoft.powermanager.uicore.OverviewPagerFragment;
import com.pyamsoft.powermanager.uicore.OverviewPagerPresenter;
import javax.inject.Inject;
import javax.inject.Named;

public class SyncFragment extends OverviewPagerFragment {

  @NonNull public static final String TAG = "Sync";
  @Inject @Named("obs_sync_state") StateObserver observer;
  @Inject @Named("sync_overview") OverviewPagerPresenter presenter;

  @NonNull @Override protected OverviewPagerPresenter providePresenter() {
    return presenter;
  }

  @NonNull @Override protected StateObserver provideObserver() {
    return observer;
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusSyncComponent().inject(this);
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
