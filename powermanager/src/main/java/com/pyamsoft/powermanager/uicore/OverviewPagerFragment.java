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

package com.pyamsoft.powermanager.uicore;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.databinding.FragmentPreferenceContainerPagerBinding;
import com.pyamsoft.powermanager.main.MainActivity;
import com.pyamsoft.powermanager.model.overlord.StateChangeObserver;
import com.pyamsoft.pydroid.drawable.AsyncDrawable;
import com.pyamsoft.pydroid.drawable.AsyncMap;
import com.pyamsoft.pydroid.drawable.AsyncMapEntry;
import com.pyamsoft.pydroid.helper.AsyncMapHelper;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.CircularRevealFragmentUtil;
import timber.log.Timber;

public abstract class OverviewPagerFragment extends AppBarColoringFragment {

  @NonNull private static final String CURRENT_TAB_KEY = "current_tab";
  @NonNull private static final String FAB_TAG = "fab_tag";

  @SuppressWarnings("WeakerAccess") StateChangeObserver observer;
  @SuppressWarnings("WeakerAccess") OverviewPagerPresenter presenter;
  private FragmentPreferenceContainerPagerBinding binding;
  private TabLayout tabLayout;
  @NonNull private AsyncMapEntry subscription = AsyncMap.emptyEntry();

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectDependencies();
    observer = provideObserver();
    presenter = providePresenter();
  }

  @Nullable @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentPreferenceContainerPagerBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override public final void onDestroyView() {
    super.onDestroyView();
    setActionBarUpEnabled(false);
    tabLayout.setVisibility(View.GONE);
    tabLayout.setupWithViewPager(null);
    subscription = AsyncMapHelper.unsubscribe(subscription);
    binding.unbind();
  }

  @Override public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    CircularRevealFragmentUtil.runCircularRevealOnViewCreated(view, getArguments());
    addPreferenceFragments();
    addTabLayoutToAppBar();
    selectCurrentTab(savedInstanceState);
    setupFab();
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(null);
    observer.register(FAB_TAG, this::setFab, this::unsetFab);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
    observer.unregister(FAB_TAG);
  }

  @Override public final void onResume() {
    super.onResume();
    setActionBarUpEnabled(true);

    if (getFabSetIcon() != 0 && getFabUnsetIcon() != 0) {
      AnimUtil.popShow(binding.preferenceContainerFab, 300, 600);
    }
  }

  @CheckResult @NonNull private MainActivity getMainActivity() {
    final FragmentActivity fragmentActivity = getActivity();
    if (fragmentActivity instanceof MainActivity) {
      return (MainActivity) fragmentActivity;
    } else {
      throw new RuntimeException("Activity is not MainActivity");
    }
  }

  @Override public final void onSaveInstanceState(Bundle outState) {
    if (tabLayout != null) {
      outState.putInt(CURRENT_TAB_KEY, tabLayout.getSelectedTabPosition());
    }
    super.onSaveInstanceState(outState);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    PowerManager.getRefWatcher(this).watch(this);
  }

  private void addPreferenceFragments() {
    final ModulePagerAdapter adapter = getPagerAdapter();
    binding.preferenceContainerPager.setAdapter(adapter);
    binding.preferenceContainerPager.setCurrentItem(0);
  }

  private void addTabLayoutToAppBar() {
    tabLayout = getMainActivity().getTabLayout();
    tabLayout.setVisibility(View.VISIBLE);
    tabLayout.setupWithViewPager(binding.preferenceContainerPager);
  }

  private void selectCurrentTab(@Nullable Bundle savedInstanceState) {
    int index;
    if (savedInstanceState == null) {
      index = 0;
    } else {
      index = savedInstanceState.getInt(CURRENT_TAB_KEY, 0);
    }

    final TabLayout.Tab tab = tabLayout.getTabAt(index);
    if (tab != null) {
      tab.select();
    }
  }

  private void setupFab() {
    if (observer.enabled()) {
      setFab();
    } else {
      unsetFab();
    }

    binding.preferenceContainerFab.setOnClickListener(view -> {
      if (observer.enabled()) {
        presenter.wrapUnset();
      } else {
        presenter.wrapSet();
      }
    });
  }

  void setFab() {
    loadDrawableIntoFab(getFabSetIcon());
  }

  void unsetFab() {
    loadDrawableIntoFab(getFabUnsetIcon());
  }

  private void loadDrawableIntoFab(@DrawableRes int fabIcon) {
    if (fabIcon == 0) {
      Timber.w("Icon is 0, hiding FAB");
      binding.preferenceContainerFab.setVisibility(View.GONE);
    } else {
      subscription = AsyncMapHelper.unsubscribe(subscription);
      subscription = AsyncDrawable.load(fabIcon)
          .tint(android.R.color.white)
          .into(binding.preferenceContainerFab);
    }
  }

  @CheckResult @NonNull protected abstract OverviewPagerPresenter providePresenter();

  @CheckResult @NonNull protected abstract StateChangeObserver provideObserver();

  protected abstract void injectDependencies();

  @CheckResult @DrawableRes protected abstract int getFabSetIcon();

  @CheckResult @DrawableRes protected abstract int getFabUnsetIcon();

  @CheckResult @NonNull protected abstract ModulePagerAdapter getPagerAdapter();
}
