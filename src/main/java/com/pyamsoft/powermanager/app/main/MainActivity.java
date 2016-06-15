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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.BuildConfig;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.manager.ManagerBluetooth;
import com.pyamsoft.powermanager.app.manager.ManagerData;
import com.pyamsoft.powermanager.app.manager.ManagerSync;
import com.pyamsoft.powermanager.app.manager.ManagerWifi;
import com.pyamsoft.pydroid.base.activity.DonationActivityBase;
import com.pyamsoft.pydroid.support.RatingDialog;
import com.pyamsoft.pydroid.util.StringUtil;
import javax.inject.Inject;

public class MainActivity extends DonationActivityBase implements RatingDialog.ChangeLogProvider {

  @Nullable @BindView(R.id.main_toolbar) Toolbar toolbar;
  @Nullable @BindView(R.id.toggle_wifi_test) Button toggleWifi;
  @Nullable @BindView(R.id.toggle_data_test) Button toggleData;
  @Nullable @BindView(R.id.toggle_bluetooth_test) Button toggleBluetooth;
  @Nullable @BindView(R.id.toggle_sync_test) Button toggleSync;
  @Nullable @BindView(R.id.toggle_all_off) Button toggleOff;
  @Nullable @BindView(R.id.toggle_all_on) Button toggleOn;

  @Nullable @Inject ManagerWifi managerWifi;
  @Nullable @Inject ManagerData managerData;
  @Nullable @Inject ManagerBluetooth managerBluetooth;
  @Nullable @Inject ManagerSync managerSync;
  @Nullable private Unbinder unbinder;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    PowerManager.getInstance().getPowerManagerComponent().inject(this);

    unbinder = ButterKnife.bind(this);
    setupAppBar();

    setupTestButton();
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    assert unbinder != null;
    unbinder.unbind();
  }

  private void setupTestButton() {
    assert toggleWifi != null;
    toggleWifi.setOnClickListener(v -> {
      assert managerWifi != null;
      if (managerWifi.isEnabled()) {
        managerWifi.disable(0);
      } else {
        managerWifi.enable(0);
      }
    });

    assert toggleData != null;
    toggleData.setOnClickListener(v -> {
      assert managerData != null;
      if (managerData.isEnabled()) {
        managerData.disable(0);
      } else {
        managerData.enable(0);
      }
    });

    assert toggleBluetooth != null;
    toggleBluetooth.setOnClickListener(v -> {
      assert managerBluetooth != null;
      if (managerBluetooth.isEnabled()) {
        managerBluetooth.disable(0);
      } else {
        managerBluetooth.enable(0);
      }
    });

    assert toggleSync != null;
    toggleSync.setOnClickListener(v -> {
      assert managerSync != null;
      if (managerSync.isEnabled()) {
        managerSync.disable(0);
      } else {
        managerSync.enable(0);
      }
    });

    assert toggleOff != null;
    toggleOff.setOnClickListener(v -> {
      assert managerWifi != null;
      managerWifi.disable(0);
      assert managerData != null;
      managerData.disable(0);
      assert managerBluetooth != null;
      managerBluetooth.disable(0);
      assert managerSync != null;
      managerSync.disable(0);
    });

    assert toggleOn != null;
    toggleOn.setOnClickListener(v -> {
      assert managerWifi != null;
      managerWifi.enable(0);
      assert managerData != null;
      managerData.enable(0);
      assert managerBluetooth != null;
      managerBluetooth.enable(0);
      assert managerSync != null;
      managerSync.enable(0);
    });
  }

  private void setupAppBar() {
    assert toolbar != null;
    toolbar.setTitle(getString(R.string.app_name));
    setSupportActionBar(toolbar);
    setActionBarUpEnabled(false);
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
}
