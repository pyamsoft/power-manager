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
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.base.activity.DonationActivityBase;

public class MainActivity extends DonationActivityBase {

  @BindView(R.id.main_toolbar) Toolbar toolbar;
  private Unbinder unbinder;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    super.onCreate(savedInstanceState);
    unbinder = ButterKnife.bind(this);

    setupPreferenceDefaults();
    setupAppBar();
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
}
