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

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.BaseOverviewPagerFragment;
import com.pyamsoft.powermanager.app.base.BasePagerAdapter;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.pydroid.app.fragment.CircularRevealFragmentUtil;
import javax.inject.Inject;
import javax.inject.Named;

public class SyncFragment extends BaseOverviewPagerFragment {

  @NonNull public static final String TAG = "Sync";
  @Inject @Named("obs_sync_state") BooleanInterestObserver observer;
  @Inject @Named("mod_sync_state") BooleanInterestModifier modifier;

  @CheckResult @NonNull
  public static SyncFragment newInstance(@NonNull View from, @NonNull View container) {
    final Bundle args = CircularRevealFragmentUtil.bundleArguments(from, container);
    final SyncFragment fragment = new SyncFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected void injectObserverModifier() {
    PowerManager.get(getContext()).provideComponent().plusSyncScreenComponent().inject(this);
  }

  @NonNull @Override protected BooleanInterestObserver getObserver() {
    return observer;
  }

  @NonNull @Override protected BooleanInterestModifier getModifier() {
    return modifier;
  }

  @Override protected int getFabSetIcon() {
    return R.drawable.ic_sync_24dp;
  }

  @Override protected int getFabUnsetIcon() {
    return R.drawable.ic_sync_disabled_24dp;
  }

  @NonNull @Override protected BasePagerAdapter getPagerAdapter() {
    return new SyncPagerAdapter(getChildFragmentManager());
  }
}
