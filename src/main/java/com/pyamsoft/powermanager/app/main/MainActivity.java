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

package com.pyamsoft.powermanager.app.main;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.BuildConfig;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.manager.ManagerSettingsPagerAdapter;
import com.pyamsoft.powermanager.app.overview.OverviewPagerAdapter;
import com.pyamsoft.powermanager.app.settings.SettingsFragment;
import com.pyamsoft.powermanager.app.settings.SettingsPagerAdapter;
import com.pyamsoft.powermanager.app.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.app.trigger.PowerTriggerPagerAdapter;
import com.pyamsoft.powermanager.dagger.main.DaggerMainComponent;
import com.pyamsoft.pydroid.base.activity.DonationActivityBase;
import com.pyamsoft.pydroid.model.AsyncDrawable;
import com.pyamsoft.pydroid.support.RatingDialog;
import com.pyamsoft.pydroid.tool.AsyncTaskMap;
import com.pyamsoft.pydroid.tool.AsyncVectorDrawableTask;
import com.pyamsoft.pydroid.tool.DataHolderFragment;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.StringUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class MainActivity extends DonationActivityBase
    implements RatingDialog.ChangeLogProvider, MainPresenter.MainView {

  @NonNull private final AsyncTaskMap taskMap = new AsyncTaskMap();
  @BindView(R.id.main_tablayout) TabLayout tabLayout;
  @BindView(R.id.main_appbar) AppBarLayout appBarLayout;
  @BindView(R.id.main_toolbar) Toolbar toolbar;
  @BindView(R.id.main_pager) ViewPager viewPager;
  @BindView(R.id.main_fab) FloatingActionButton fab;
  @Inject MainPresenter presenter;
  private Unbinder unbinder;
  private DataHolderFragment<String> adapterDataHolderFragment;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setPreferenceDefaultValues();

    DaggerMainComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build()
        .inject(this);
    adapterDataHolderFragment = DataHolderFragment.getInstance(this, "adapter");

    // Resolve color here, just once

    presenter.bindView(this);

    unbinder = ButterKnife.bind(this);
    final String storedType = adapterDataHolderFragment.pop(0);
    setupAppBar();
    setupTabLayout(storedType);
    setupViewPager(storedType);
    setFabStateFromAdapter();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    if (isChangingConfigurations()) {
      String type;
      final PagerAdapter pagerAdapter = viewPager.getAdapter();
      if (pagerAdapter instanceof ManagerSettingsPagerAdapter) {
        final ManagerSettingsPagerAdapter settingsPagerAdapter =
            (ManagerSettingsPagerAdapter) pagerAdapter;
        type = settingsPagerAdapter.getType();
        Timber.d("Save type of manager fragment for later");
      } else if (pagerAdapter instanceof SettingsPagerAdapter) {
        type = SettingsFragment.TAG;
        Timber.d("Save type of settings fragment for later");
      } else if (pagerAdapter instanceof PowerTriggerPagerAdapter) {
        type = PowerTriggerFragment.TAG;
        Timber.d("Save type of power triggers fragment for later");
      } else {
        Timber.d("Fragment is overview");
        type = null;
      }
      adapterDataHolderFragment.put(0, type);
    } else {
      adapterDataHolderFragment.clear();
    }
    super.onSaveInstanceState(outState);
  }

  private void setupViewPager(@Nullable String storedType) {
    PagerAdapter adapter;
    if (storedType == null) {
      Timber.d("No stored fragment, load overview");
      adapter = new OverviewPagerAdapter(getSupportFragmentManager());
    } else if (storedType.equals(SettingsFragment.TAG)) {
      Timber.d("Stored fragment exists, is settings fragment");
      adapter = new SettingsPagerAdapter(getSupportFragmentManager());
    } else if (storedType.equals(PowerTriggerFragment.TAG)) {
      Timber.d("Stored fragment exists, is power trigger fragment");
      adapter = new PowerTriggerPagerAdapter(getSupportFragmentManager());
    } else {
      Timber.d("Stored fragment exists, is manager fragment");
      adapter = new ManagerSettingsPagerAdapter(this, storedType);
    }

    recycleOldAdapter();
    viewPager.setAdapter(adapter);
    setActionBarUpEnabled(storedType != null);
  }

  private void setFabStateFromAdapter() {
    final PagerAdapter pagerAdapter = viewPager.getAdapter();
    String type;
    if (pagerAdapter instanceof ManagerSettingsPagerAdapter) {
      final ManagerSettingsPagerAdapter settingsPagerAdapter =
          (ManagerSettingsPagerAdapter) pagerAdapter;
      type = settingsPagerAdapter.getType();
      Timber.d("Save type of manager fragment for later");
    } else if (pagerAdapter instanceof SettingsPagerAdapter) {
      type = SettingsFragment.TAG;
      Timber.d("Save type of settings fragment for later");
    } else if (pagerAdapter instanceof PowerTriggerPagerAdapter) {
      type = PowerTriggerFragment.TAG;
      Timber.d("Save type of power triggers fragment for later");
    } else {
      Timber.d("Fragment is overview");
      type = null;
    }

    boolean visible = false;
    if (type != null) {
      switch (type) {
        case ManagerSettingsPagerAdapter.TYPE_WIFI:
          visible = true;
          break;
        case ManagerSettingsPagerAdapter.TYPE_DATA:
          visible = true;
          break;
        case ManagerSettingsPagerAdapter.TYPE_BLUETOOTH:
          visible = true;
          break;
        case ManagerSettingsPagerAdapter.TYPE_SYNC:
          visible = true;
          break;
        case PowerTriggerFragment.TAG:
          visible = true;
          break;
        case SettingsFragment.TAG:
          visible = false;
          break;
        default:
          throw new IllegalStateException("Not valid type: " + type);
      }
    }

    if (visible) {
      Timber.d("Show the FAB");
      fab.show();
    } else {
      Timber.d("Hide the FAB");
      fab.hide();
    }
  }

  private void setupTabLayout(@Nullable String storedType) {
    ViewPager pager;
    int visibility;
    if (storedType == null) {
      Timber.d("No stored fragment, no tabs");
      pager = null;
      visibility = View.GONE;
    } else if (storedType.equals(SettingsFragment.TAG) || storedType.equals(
        PowerTriggerFragment.TAG)) {
      Timber.d("Stored fragment exists, is settings fragment or power trigger. no tabs");
      pager = null;
      visibility = View.GONE;
    } else {
      Timber.d("Stored fragment exists, is manager fragment. has tabs");
      pager = viewPager;
      visibility = View.VISIBLE;
    }
    tabLayout.setVisibility(visibility);
    tabLayout.setupWithViewPager(pager);
  }

  private void setupAppBar() {
    toolbar.setTitle(getString(R.string.app_name));
    setSupportActionBar(toolbar);
    appBarLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
  }

  private void setPreferenceDefaultValues() {
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.manage_wifi, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.manage_data, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.manage_bluetooth, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.manage_sync, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.periodic_wifi, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.periodic_data, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.periodic_bluetooth, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.periodic_sync, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    taskMap.clear();
    presenter.unbindView();
    unbinder.unbind();
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.resume();
  }

  @Override protected void onPause() {
    super.onPause();
    presenter.pause();
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    RatingDialog.showRatingDialog(this, this);
  }

  @Override protected void onStart() {
    super.onStart();
    presenter.start();
  }

  @Override protected void onStop() {
    super.onStop();
    presenter.stop();
  }

  @Override public void onBackPressed() {
    final PagerAdapter adapter = viewPager.getAdapter();
    if (adapter instanceof OverviewPagerAdapter) {
      Timber.d("Current pager holds overview, do super onBackPressed");
      super.onBackPressed();
    } else {
      Timber.d("Current pager does not hold overview, pop");
      recycleOldAdapter();
      viewPager.setAdapter(new OverviewPagerAdapter(getSupportFragmentManager()));
      tabLayout.setVisibility(View.GONE);
      tabLayout.setupWithViewPager(null);
      setActionBarUpEnabled(false);
      setFabStateFromAdapter();
    }
  }

  @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled;
    switch (item.getItemId()) {
      case android.R.id.home:
        handled = true;
        onBackPressed();
        break;
      default:
        handled = false;
    }
    return handled || super.onOptionsItemSelected(item);
  }

  @NonNull @Override protected String getPlayStoreAppPackage() {
    return getPackageName();
  }

  @NonNull @Override public Spannable getChangeLogText() {
    // The changelog text
    final String title = "What's New in Version " + BuildConfig.VERSION_NAME;
    final String line1 = "BUGFIX: Code cleanup and general bugfixes";
    final String line2 = "FEATURE: This change log screen";

    // Turn it into a spannable
    final Spannable spannable = StringUtil.createBuilder(title, "\n\n", line1, "\n\n", line2);

    int start = 0;
    int end = title.length();
    final int largeSize =
        StringUtil.getTextSizeFromAppearance(this, android.R.attr.textAppearanceLarge);
    final int largeColor =
        StringUtil.getTextColorFromAppearance(this, android.R.attr.textAppearanceLarge);
    final int smallSize =
        StringUtil.getTextSizeFromAppearance(this, android.R.attr.textAppearanceSmall);
    final int smallColor =
        StringUtil.getTextColorFromAppearance(this, android.R.attr.textAppearanceSmall);

    StringUtil.boldSpan(spannable, start, end);
    StringUtil.sizeSpan(spannable, start, end, largeSize);
    StringUtil.colorSpan(spannable, start, end, largeColor);

    start += end + 2;
    end += 2 + line1.length() + 2 + line2.length();

    StringUtil.sizeSpan(spannable, start, end, smallSize);
    StringUtil.colorSpan(spannable, start, end, smallColor);

    return spannable;
  }

  @Override public int getChangeLogIcon() {
    return R.mipmap.ic_launcher;
  }

  @NonNull @Override public String getChangeLogPackageName() {
    return getPackageName();
  }

  @Override public int getChangeLogVersion() {
    return BuildConfig.VERSION_CODE;
  }

  @Override public void loadFragmentFromOverview(@NonNull String type) {
    PagerAdapter adapter;
    boolean showTabs;
    switch (type) {
      case SettingsFragment.TAG:
        adapter = new SettingsPagerAdapter(getSupportFragmentManager());
        showTabs = false;
        break;
      case PowerTriggerFragment.TAG:
        adapter = new PowerTriggerPagerAdapter(getSupportFragmentManager());
        showTabs = false;
        break;
      default:
        adapter = new ManagerSettingsPagerAdapter(this, type);
        showTabs = true;
    }
    recycleOldAdapter();
    viewPager.setAdapter(adapter);
    tabLayout.setVisibility(showTabs ? View.VISIBLE : View.GONE);
    tabLayout.setupWithViewPager(showTabs ? viewPager : null);
    setActionBarUpEnabled(true);
    setFabStateFromAdapter();
  }

  private void recycleOldAdapter() {
    Timber.d("Recycle old adapter");
    final PagerAdapter adapter = viewPager.getAdapter();
    if (adapter != null) {
      if (adapter instanceof ManagerSettingsPagerAdapter) {
        final ManagerSettingsPagerAdapter settingsPagerAdapter =
            (ManagerSettingsPagerAdapter) adapter;
        settingsPagerAdapter.recycle();
      }
    }
  }

  @Override public void overviewEventError() {
    AppUtil.guaranteeSingleDialogFragment(this, new ErrorDialog(), "error");
  }

  @Override public void loadFabColoring(@DrawableRes int icon, @NonNull Runnable runnable) {
    final AsyncVectorDrawableTask fabColorTask =
        new AsyncVectorDrawableTask(fab, android.R.color.white);
    fabColorTask.execute(new AsyncDrawable(getApplicationContext(), icon));
    taskMap.put("fab", fabColorTask);
    fab.setOnClickListener(view -> runnable.run());
  }
}
