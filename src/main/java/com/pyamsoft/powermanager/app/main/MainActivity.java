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
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.ViewTreeObserver;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.overview.OverviewFragment;
import com.pyamsoft.pydroid.base.activity.DonationActivityBase;

public class MainActivity extends DonationActivityBase {

  @BindView(R.id.main_root) CoordinatorLayout rootView;
  @BindView(R.id.main_toolbar) Toolbar toolbar;
  private Unbinder unbinder;

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
    unbinder.unbind();
  }

  private void setupPreferenceDefaults() {
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    PreferenceManager.setDefaultValues(this, R.xml.manage_wifi, false);
    PreferenceManager.setDefaultValues(this, R.xml.manage_data, false);
    PreferenceManager.setDefaultValues(this, R.xml.manage_bluetooth, false);
    PreferenceManager.setDefaultValues(this, R.xml.manage_sync, false);
    PreferenceManager.setDefaultValues(this, R.xml.periodic_wifi, false);
    PreferenceManager.setDefaultValues(this, R.xml.periodic_data, false);
    PreferenceManager.setDefaultValues(this, R.xml.periodic_bluetooth, false);
    PreferenceManager.setDefaultValues(this, R.xml.periodic_sync, false);
    PreferenceManager.setDefaultValues(this, R.xml.doze, false);
  }

  private void setupAppBar() {
    setSupportActionBar(toolbar);
    toolbar.setTitle(getString(R.string.app_name));
  }

  @CheckResult private boolean hasNoActiveFragment() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    return fragmentManager.findFragmentByTag(OverviewFragment.TAG) == null;
  }

  private void loadOverviewFragment() {
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
}
