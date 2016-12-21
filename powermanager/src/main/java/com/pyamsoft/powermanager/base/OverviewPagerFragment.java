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

package com.pyamsoft.powermanager.base;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.FragmentPreferenceContainerPagerBinding;
import com.pyamsoft.powermanager.main.MainActivity;
import com.pyamsoft.powermanagermodel.BooleanInterestObserver;
import com.pyamsoft.powermanagerpresenter.base.OverviewPagerPresenter;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncMap;
import com.pyamsoft.pydroid.tool.AsyncMapHelper;
import com.pyamsoft.pydroid.util.CircularRevealFragmentUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import timber.log.Timber;

public abstract class OverviewPagerFragment extends AppBarColoringFragment
    implements OverviewPagerPresenter.View {

  @NonNull private static final String TABS_TAG = "tablayout";
  @NonNull private static final String CURRENT_TAB_KEY = "current_tab";
  @NonNull private static final String FAB_TAG = "fab_tag";
  @NonNull private static final String KEY_PRESENTER = "key_overview_presenter";
  @SuppressWarnings("WeakerAccess") BooleanInterestObserver observer;
  @SuppressWarnings("WeakerAccess") OverviewPagerPresenter presenter;
  private FragmentPreferenceContainerPagerBinding binding;
  private TabLayout tabLayout;
  private long loadedKey;
  @Nullable private AsyncMap.Entry subscription;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<OverviewPagerPresenter>() {
              @NonNull @Override public PersistLoader<OverviewPagerPresenter> createLoader() {
                return getPresenterLoader();
              }

              @Override public void onPersistentLoaded(@NonNull OverviewPagerPresenter persist) {
                presenter = persist;
              }
            });
  }

  @Nullable @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    injectObserverModifier();
    binding =
        DataBindingUtil.inflate(inflater, R.layout.fragment_preference_container_pager, container,
            false);
    return binding.getRoot();
  }

  @Override public final void onDestroyView() {
    super.onDestroyView();
    removeTabLayout();
    setActionBarUpEnabled(false);
    AsyncMapHelper.unsubscribe(subscription);
    binding.unbind();
  }

  @Override public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    CircularRevealFragmentUtil.runCircularRevealOnViewCreated(view, getArguments());
    observer = getObserver();
    addPreferenceFragments();
    addTabLayoutToAppBar();
    selectCurrentTab(savedInstanceState);
    setupFab();
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
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
    PersistentCache.get().saveKey(outState, KEY_PRESENTER, loadedKey, OverviewPagerPresenter.class);
    super.onSaveInstanceState(outState);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  private void addTabLayout(@NonNull TabLayout tabLayout) {
    getMainActivity().addViewToAppBar(TABS_TAG, tabLayout);
  }

  private void removeTabLayout() {
    getMainActivity().removeViewFromAppBar(TABS_TAG);
  }

  private void addPreferenceFragments() {
    final ModulePagerAdapter adapter = getPagerAdapter();
    binding.preferenceContainerPager.setAdapter(adapter);
    binding.preferenceContainerPager.setCurrentItem(0);
  }

  private void addTabLayoutToAppBar() {
    tabLayout = new TabLayout(getActivity());
    tabLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.grey500),
        ContextCompat.getColor(getContext(), android.R.color.white));
    tabLayout.setSelectedTabIndicatorColor(
        ContextCompat.getColor(getContext(), android.R.color.white));
    tabLayout.setTabMode(TabLayout.MODE_FIXED);

    addTabLayout(tabLayout);
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
    if (observer.is()) {
      setFab();
    } else {
      unsetFab();
    }

    binding.preferenceContainerFab.setOnClickListener(view -> {
      if (observer.is()) {
        presenter.wrapUnset();
      } else {
        presenter.wrapSet();
      }
    });
  }

  private void setFab() {
    loadDrawableIntoFab(getFabSetIcon());
  }

  private void unsetFab() {
    loadDrawableIntoFab(getFabUnsetIcon());
  }

  private void loadDrawableIntoFab(@DrawableRes int fabIcon) {
    if (fabIcon == 0) {
      Timber.w("Icon is 0, hiding FAB");
      binding.preferenceContainerFab.setVisibility(View.GONE);
    } else {
      AsyncMapHelper.unsubscribe(subscription);
      subscription = AsyncDrawable.load(fabIcon)
          .tint(android.R.color.white)
          .into(binding.preferenceContainerFab);
    }
  }

  @CheckResult @Nullable FloatingActionButton getFabTarget() {
    if (binding == null) {
      throw new NullPointerException("Binding is NULL");
    }

    if (binding.preferenceContainerFab.getVisibility() == View.GONE) {
      Timber.d("FAB is hidden, no target");
      return null;
    } else {
      return binding.preferenceContainerFab;
    }
  }

  protected abstract void injectObserverModifier();

  @CheckResult @NonNull protected abstract BooleanInterestObserver getObserver();

  @CheckResult @NonNull
  protected abstract PersistLoader<OverviewPagerPresenter> getPresenterLoader();

  @CheckResult @DrawableRes protected abstract int getFabSetIcon();

  @CheckResult @DrawableRes protected abstract int getFabUnsetIcon();

  @CheckResult @NonNull protected abstract ModulePagerAdapter getPagerAdapter();
}