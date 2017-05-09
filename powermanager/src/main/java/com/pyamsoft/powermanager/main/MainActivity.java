/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.main;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;
import com.pyamsoft.powermanager.BuildConfig;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.ActivityMainBinding;
import com.pyamsoft.powermanager.logger.LoggerDialog;
import com.pyamsoft.powermanager.service.ForegroundService;
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment;
import com.pyamsoft.pydroid.ui.rating.RatingDialog;
import com.pyamsoft.pydroid.ui.sec.TamperActivity;
import com.pyamsoft.pydroid.util.DialogUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class MainActivity extends TamperActivity {

  @Nullable private final Runnable longPressBackRunnable =
      Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? null : this::handleBackLongPress;
  @Nullable private final Handler mainHandler =
      Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? null : new Handler(Looper.getMainLooper());
  @SuppressWarnings("WeakerAccess") @Inject MainPresenter presenter;
  private ActivityMainBinding binding;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    Injector.get().provideComponent().inject(this);
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    setupAppBar();

    if (hasNoActiveFragment()) {
      loadOverviewFragment();
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.destroy();
    binding.unbind();
  }

  @Override public void onBackPressed() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    if (fragmentManager.getBackStackEntryCount() > 0) {
      fragmentManager.popBackStackImmediate();
    } else {
      super.onBackPressed();
    }
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

  private void setupAppBar() {
    setSupportActionBar(binding.mainToolbar);
    binding.mainToolbar.setTitle(getString(R.string.app_name));
  }

  @CheckResult private boolean hasNoActiveFragment() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    return fragmentManager.findFragmentByTag(AboutLibrariesFragment.TAG) == null
        && fragmentManager.findFragmentByTag(MainFragment.TAG) == null;
  }

  private void loadOverviewFragment() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    if (fragmentManager.findFragmentByTag(MainFragment.TAG) == null) {
      fragmentManager.beginTransaction()
          .replace(R.id.main_container, MainFragment.newInstance(), MainFragment.TAG)
          .commit();
    }
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    RatingDialog.showRatingDialog(this, this, false);
  }

  @NonNull @Override protected String getSafePackageName() {
    return "com.pyamsoft.powermanager";
  }

  @NonNull @Override protected String[] getChangeLogLines() {
    final String line1 = "BUGFIX: Bugfixes and improvements";
    final String line2 = "BUGFIX: Removed all Advertisements";
    final String line3 = "BUGFIX: Faster loading of Open Source Licenses page";
    return new String[] { line1, line2, line3 };
  }

  @NonNull @Override protected String getVersionName() {
    return BuildConfig.VERSION_NAME;
  }

  @Override public int getApplicationIcon() {
    return R.mipmap.ic_launcher;
  }

  @NonNull @Override public String provideApplicationName() {
    return "Power Manager";
  }

  @Override public int getCurrentApplicationVersion() {
    return BuildConfig.VERSION_CODE;
  }

  @Override protected void onStart() {
    super.onStart();
    presenter.runStartupHooks(new MainPresenter.StartupCallback() {
      @Override public void onServiceEnabledWhenOpen() {
        Timber.d("Should refresh service when opened");
        ForegroundService.start(getApplicationContext());
      }

      @Override public void explainRootRequirement() {
        Toast.makeText(getApplicationContext(),
            "Root is required for certain functions like Doze and Airplane mode",
            Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override protected void onStop() {
    super.onStop();
    presenter.stop();
  }

  // https://github.com/mozilla/gecko-dev/blob/master/mobile/android/base/java/org/mozilla/gecko/BrowserApp.java
  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && keyCode == KeyEvent.KEYCODE_BACK) {
      if (mainHandler != null && longPressBackRunnable != null) {
        mainHandler.removeCallbacksAndMessages(null);
        mainHandler.postDelayed(longPressBackRunnable, 1400L);
      }
    }

    return super.onKeyDown(keyCode, event);
  }

  @Override public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && keyCode == KeyEvent.KEYCODE_BACK) {
      if (handleBackLongPress()) {
        return true;
      }
    }

    return super.onKeyLongPress(keyCode, event);
  }

  @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && keyCode == KeyEvent.KEYCODE_BACK) {
      if (mainHandler != null) {
        mainHandler.removeCallbacksAndMessages(null);
      }
    }

    return super.onKeyUp(keyCode, event);
  }

  @SuppressWarnings("WeakerAccess") boolean handleBackLongPress() {
    final String tag = "logger_dialog";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    if (fragmentManager.findFragmentByTag(tag) == null) {
      Timber.d("Show logger dialog");
      DialogUtil.guaranteeSingleDialogFragment(this, new LoggerDialog(), tag);
      return true;
    } else {
      Timber.w("Logger dialog is already shown");
      return false;
    }
  }
}
