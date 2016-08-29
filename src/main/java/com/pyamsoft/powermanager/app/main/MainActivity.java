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

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.bluetooth.BluetoothFragment;
import com.pyamsoft.powermanager.app.data.DataFragment;
import com.pyamsoft.powermanager.app.doze.DozeFragment;
import com.pyamsoft.powermanager.app.overview.OverviewFragment;
import com.pyamsoft.powermanager.app.sync.SyncFragment;
import com.pyamsoft.powermanager.app.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.app.wifi.WifiFragment;
import com.pyamsoft.pydroid.base.activity.DonationActivity;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class MainActivity extends DonationActivity {

  @NonNull final Map<String, View> addedViewMap = new HashMap<>();

  @BindView(R.id.main_appbar) AppBarLayout appBarLayout;
  @BindView(R.id.main_root) CoordinatorLayout rootView;
  @BindView(R.id.main_toolbar) Toolbar toolbar;
  Unbinder unbinder;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    super.onCreate(savedInstanceState);
    unbinder = ButterKnife.bind(this);

    setupPreferenceDefaults();
    setupAppBar();
    if (hasNoActiveFragment()) {
      loadOverviewFragment();
    }
  }

  @Override protected int bindActivityToView() {
    setContentView(R.layout.activity_main);
    return R.id.ad_view;
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    for (final String key : addedViewMap.keySet()) {
      removeViewFromAppBar(key);
    }
    addedViewMap.clear();

    unbinder.unbind();
  }

  @Override public void onBackPressed() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    if (fragmentManager.getBackStackEntryCount() > 0) {
      fragmentManager.popBackStack();
    } else {
      super.onBackPressed();
    }
  }

  void setupPreferenceDefaults() {
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    PreferenceManager.setDefaultValues(this, R.xml.manage_wifi, false);
    PreferenceManager.setDefaultValues(this, R.xml.manage_data, false);
    PreferenceManager.setDefaultValues(this, R.xml.manage_bluetooth, false);
    PreferenceManager.setDefaultValues(this, R.xml.manage_sync, false);
    PreferenceManager.setDefaultValues(this, R.xml.periodic_wifi, false);
    PreferenceManager.setDefaultValues(this, R.xml.periodic_data, false);
    PreferenceManager.setDefaultValues(this, R.xml.periodic_bluetooth, false);
    PreferenceManager.setDefaultValues(this, R.xml.periodic_sync, false);
    PreferenceManager.setDefaultValues(this, R.xml.manage_doze, false);
  }

  @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled;
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        handled = true;
        break;
      default:
        handled = false;
    }
    return handled || super.onOptionsItemSelected(item);
  }

  void setupAppBar() {
    setSupportActionBar(toolbar);
    toolbar.setTitle(getString(R.string.app_name));
  }

  @CheckResult boolean hasNoActiveFragment() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    return fragmentManager.findFragmentByTag(OverviewFragment.TAG) == null &&
        fragmentManager.findFragmentByTag(WifiFragment.TAG) == null &&
        fragmentManager.findFragmentByTag(DataFragment.TAG) == null &&
        fragmentManager.findFragmentByTag(BluetoothFragment.TAG) == null &&
        fragmentManager.findFragmentByTag(SyncFragment.TAG) == null &&
        fragmentManager.findFragmentByTag(PowerTriggerFragment.TAG) == null &&
        fragmentManager.findFragmentByTag(DozeFragment.TAG) == null;
  }

  void loadOverviewFragment() {
    rootView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            final FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(OverviewFragment.TAG) == null) {
              final int cX = rootView.getLeft() + rootView.getWidth() / 2;
              final int cY = rootView.getBottom() + rootView.getHeight() / 2;
              fragmentManager.beginTransaction()
                  .replace(R.id.main_container, OverviewFragment.newInstance(cX, cY),
                      OverviewFragment.TAG)
                  .commit();
            }
          }
        });
  }

  public void addViewToAppBar(@NonNull String tag, @NonNull View view) {
    if (addedViewMap.containsKey(tag)) {
      Timber.w("AppBar already has view with this tag: %s", tag);
      removeViewFromAppBar(tag);
    }

    Timber.d("Add view to map with tag: %s", tag);
    addedViewMap.put(tag, view);
    appBarLayout.addView(view);
  }

  public void removeViewFromAppBar(@NonNull String tag) {
    if (addedViewMap.containsKey(tag)) {
      Timber.d("Remove tag from map: %s", tag);
      final View viewToRemove = addedViewMap.remove(tag);
      if (viewToRemove == null) {
        Timber.e("View to remove was NULL for tag: %s", tag);
      } else {
        appBarLayout.removeView(viewToRemove);
      }
    } else {
      Timber.e("Viewmap does not contain a view for tag: %s", tag);
    }
  }
}
