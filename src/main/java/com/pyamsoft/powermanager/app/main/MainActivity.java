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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.BuildConfig;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.manager.ManagerSettingsFragment;
import com.pyamsoft.powermanager.app.overview.OverviewFragment;
import com.pyamsoft.powermanager.app.settings.SettingsFragment;
import com.pyamsoft.powermanager.dagger.main.DaggerMainComponent;
import com.pyamsoft.pydroid.base.activity.DonationActivityBase;
import com.pyamsoft.pydroid.support.RatingDialog;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.StringUtil;
import javax.inject.Inject;

public class MainActivity extends DonationActivityBase
    implements RatingDialog.ChangeLogProvider, MainPresenter.MainView {

  @Nullable @BindView(R.id.main_toolbar) Toolbar toolbar;
  @Nullable @Inject MainPresenter presenter;

  @Nullable private Unbinder unbinder;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setPreferenceDefaultValues();

    DaggerMainComponent.builder().build().inject(this);

    assert presenter != null;
    presenter.bindView(this);

    unbinder = ButterKnife.bind(this);
    setupAppBar();
    showOverviewIfBlank();
  }

  private void showOverviewIfBlank() {
    boolean blank = true;
    final String[] fragmentTags = {
        ManagerSettingsFragment.TYPE_WIFI, ManagerSettingsFragment.TYPE_DATA, ManagerSettingsFragment.TYPE_BLUETOOTH,
        ManagerSettingsFragment.TYPE_SYNC, SettingsFragment.TAG
    };

    final FragmentManager fm = getSupportFragmentManager();
    for (final String tag : fragmentTags) {
      if (fm.findFragmentByTag(tag) != null) {
        blank = false;
        break;
      }
    }
    if (blank) {
      fm.beginTransaction().replace(R.id.main_container, new OverviewFragment()).commit();
    }
  }

  private void setPreferenceDefaultValues() {
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.manage_wifi, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.manage_data, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.manage_bluetooth, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.manage_sync, false);
    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    assert unbinder != null;
    unbinder.unbind();

    assert presenter != null;
    presenter.unbindView();
  }

  @Override protected void onResume() {
    super.onResume();
    assert presenter != null;
    presenter.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
    assert presenter != null;
    presenter.onPause();
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    RatingDialog.showRatingDialog(this, this);
  }

  private void setupAppBar() {
    assert toolbar != null;
    toolbar.setTitle(getString(R.string.app_name));
    setSupportActionBar(toolbar);
    setActionBarUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
  }

  @Override public void onBackPressed() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    final int count = fragmentManager.getBackStackEntryCount();
    if (count > 0) {
      if (count - 1 == 0) {
        setActionBarUpEnabled(false);
      }
      fragmentManager.popBackStack();
    } else {
      super.onBackPressed();
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
    Fragment fragment;
    switch (type) {
      case SettingsFragment.TAG:
        fragment = new SettingsFragment();
        break;
      default:
        fragment = ManagerSettingsFragment.newInstance(type);
    }

    getSupportFragmentManager().beginTransaction()
        .replace(R.id.main_container, fragment, type)
        .addToBackStack(null)
        .commit();
    setActionBarUpEnabled(true);
  }

  @Override public void overviewEventError() {
    AppUtil.guaranteeSingleDialogFragment(this, new ErrorDialog(), "error");
  }
}
