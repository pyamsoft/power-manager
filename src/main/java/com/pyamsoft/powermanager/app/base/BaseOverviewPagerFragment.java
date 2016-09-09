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

package com.pyamsoft.powermanager.app.base;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.MainActivity;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.pydroid.app.fragment.CircularRevealFragmentUtil;
import com.pyamsoft.pydroid.base.ActionBarFragment;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncDrawableMap;
import rx.Subscription;

public abstract class BaseOverviewPagerFragment extends ActionBarFragment {

  @NonNull private static final String TABS_TAG = "tablayout";
  @NonNull private static final String CURRENT_TAB_KEY = "current_tab";
  @NonNull private static final String FAB_TAG = "fab_tag";
  @NonNull private final AsyncDrawableMap asyncDrawableMap = new AsyncDrawableMap();
  @BindView(R.id.preference_container_fab) FloatingActionButton fab;
  @BindView(R.id.preference_container_pager) ViewPager pager;
  @SuppressWarnings("WeakerAccess") BooleanInterestObserver observer;
  @SuppressWarnings("WeakerAccess") BooleanInterestModifier modifier;
  private TabLayout tabLayout;
  private Unbinder unbinder;

  @Nullable @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    injectObserverModifier();
    final View view =
        inflater.inflate(R.layout.fragment_preference_container_pager, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public final void onDestroyView() {
    super.onDestroyView();
    removeTabLayout();
    setActionBarUpEnabled(false);
    asyncDrawableMap.clear();
    unbinder.unbind();
  }

  @Override public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    CircularRevealFragmentUtil.runCircularRevealOnViewCreated(view, getArguments());
    observer = getObserver();
    modifier = getModifier();
    addPreferenceFragments();
    addTabLayoutToAppBar();
    selectCurrentTab(savedInstanceState);
    setupFab();
  }

  @Override public void onStart() {
    super.onStart();
    if (!isFabHidden()) {
      observer.register(FAB_TAG, this::setFab, this::unsetFab);
    }
  }

  @Override public void onStop() {
    super.onStop();
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
    super.onSaveInstanceState(outState);
  }

  private void addTabLayout(@NonNull TabLayout tabLayout) {
    getMainActivity().addViewToAppBar(TABS_TAG, tabLayout);
  }

  private void removeTabLayout() {
    getMainActivity().removeViewFromAppBar(TABS_TAG);
  }

  private void addPreferenceFragments() {
    final PagerAdapter adapter = getPagerAdapter();
    pager.setAdapter(adapter);
  }

  private void addTabLayoutToAppBar() {
    tabLayout = new TabLayout(getActivity());
    tabLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), android.R.color.white),
        ContextCompat.getColor(getContext(), R.color.lightblueA200));
    tabLayout.setTabMode(TabLayout.MODE_FIXED);

    addTabLayout(tabLayout);
    tabLayout.setupWithViewPager(pager);
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
    if (isFabHidden()) {
      fab.hide();
      return;
    }

    if (observer.is()) {
      setFab();
    } else {
      unsetFab();
    }

    fab.setOnClickListener(view -> {
      if (observer.is()) {
        modifier.unset();
      } else {
        modifier.set();
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
    final Subscription subscription =
        AsyncDrawable.with(getContext()).load(fabIcon).tint(android.R.color.white).into(fab);
    asyncDrawableMap.put("fab", subscription);
  }

  /**
   * Override to not use the FAB
   */
  @CheckResult private boolean isFabHidden() {
    return false;
  }

  protected abstract void injectObserverModifier();

  @CheckResult @NonNull protected abstract BooleanInterestObserver getObserver();

  @CheckResult @NonNull protected abstract BooleanInterestModifier getModifier();

  @CheckResult @DrawableRes protected abstract int getFabSetIcon();

  @CheckResult @DrawableRes protected abstract int getFabUnsetIcon();

  @CheckResult @NonNull protected abstract BasePagerAdapter getPagerAdapter();
}
