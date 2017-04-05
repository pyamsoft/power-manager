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

package com.pyamsoft.powermanager.main;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;
import com.pyamsoft.powermanager.BuildConfig;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.airplane.AirplaneFragment;
import com.pyamsoft.powermanager.bluetooth.BluetoothFragment;
import com.pyamsoft.powermanager.data.DataFragment;
import com.pyamsoft.powermanager.databinding.ActivityMainBinding;
import com.pyamsoft.powermanager.doze.DozeFragment;
import com.pyamsoft.powermanager.logger.LoggerDialog;
import com.pyamsoft.powermanager.overview.OverviewFragment;
import com.pyamsoft.powermanager.service.ForegroundService;
import com.pyamsoft.powermanager.settings.SettingsPreferenceFragment;
import com.pyamsoft.powermanager.sync.SyncFragment;
import com.pyamsoft.powermanager.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.wifi.WifiFragment;
import com.pyamsoft.pydroid.ads.AdSource;
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment;
import com.pyamsoft.pydroid.ui.ads.OnlineAdSource;
import com.pyamsoft.pydroid.ui.rating.RatingDialog;
import com.pyamsoft.pydroid.ui.sec.TamperActivity;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class MainActivity extends TamperActivity {

  @Nullable private final Runnable longPressBackRunnable =
      Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? null : this::handleBackLongPress;
  @Nullable private final Handler mainHandler =
      Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? null : new Handler(Looper.getMainLooper());
  @SuppressWarnings("WeakerAccess") @Inject MainPresenter presenter;
  private ActivityMainBinding binding;
  @ColorInt private int oldAppBarColor;
  @ColorInt private int oldStatusBarColor;
  @Nullable private ValueAnimator appBarAnimator;
  @Nullable private ValueAnimator statusBarAnimator;

  @SuppressWarnings("WeakerAccess") @CheckResult @ColorInt
  static int blendColors(@ColorInt int from, @ColorInt int to, float ratio) {
    final float inverseRatio = 1f - ratio;

    final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
    final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
    final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

    return Color.rgb((int) r, (int) g, (int) b);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    super.onCreate(savedInstanceState);
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    oldAppBarColor = ContextCompat.getColor(this, R.color.amber500);
    oldStatusBarColor = ContextCompat.getColor(this, R.color.amber700);
    setupAppBar();
    if (hasNoActiveFragment()) {
      loadOverviewFragment();
    }

    Injector.get().provideComponent().plusMainComponent().inject(this);
  }

  @Override protected int bindActivityToView() {
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    return R.id.ad_view;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (statusBarAnimator != null) {
      statusBarAnimator.cancel();
    }

    if (appBarAnimator != null) {
      appBarAnimator.cancel();
    }

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
    return fragmentManager.findFragmentByTag(OverviewFragment.TAG) == null
        && fragmentManager.findFragmentByTag(WifiFragment.TAG) == null
        && fragmentManager.findFragmentByTag(DataFragment.TAG) == null
        && fragmentManager.findFragmentByTag(BluetoothFragment.TAG) == null
        && fragmentManager.findFragmentByTag(SyncFragment.TAG) == null
        && fragmentManager.findFragmentByTag(PowerTriggerFragment.TAG) == null
        && fragmentManager.findFragmentByTag(DozeFragment.TAG) == null
        && fragmentManager.findFragmentByTag(AirplaneFragment.TAG) == null
        && fragmentManager.findFragmentByTag(SettingsPreferenceFragment.TAG) == null
        && fragmentManager.findFragmentByTag(AboutLibrariesFragment.TAG) == null;
  }

  private void loadOverviewFragment() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    if (fragmentManager.findFragmentByTag(OverviewFragment.TAG) == null) {
      fragmentManager.beginTransaction()
          .replace(R.id.main_container, new OverviewFragment(), OverviewFragment.TAG)
          .commitNow();
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
    final String line1 = "BUGFIX: Faster performance for automatic management";
    final String line2 = "BUGFIX: Better periodic timing for reoccuring automatic events";
    final String line3 = "BUGFIX: Faster power trigger list loading";
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

  /**
   * Color the app bar using a nice blending animation
   */
  public void colorAppBar(@ColorRes int color, long duration) {
    final int newColor = ContextCompat.getColor(this, color);
    Timber.d("Blend appbar color");
    blendAppBar(oldAppBarColor, newColor, duration);
    oldAppBarColor = newColor;
  }

  /**
   * Runs a blending animation on the app bar color
   */
  private void blendAppBar(int fromColor, int toColor, long duration) {
    if (appBarAnimator != null) {
      appBarAnimator.cancel();
    }

    appBarAnimator = ValueAnimator.ofFloat(0, 1);
    appBarAnimator.addUpdateListener(animation -> {
      // Use animation position to blend colors.
      final float position = animation.getAnimatedFraction();

      // Apply blended color to the status bar.
      final int blended = blendColors(fromColor, toColor, position);

      // To color the entire app bar
      binding.mainAppbar.setBackgroundColor(blended);
    });

    appBarAnimator.setDuration(duration).start();
  }

  /**
   * Colors the status bar using a nice blending animation
   */
  public void colorStatusBar(@ColorRes int colorDark, long duration) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      final int newColor = ContextCompat.getColor(this, colorDark);
      Timber.d("Blend statusbar color");
      blendStatusBar(oldStatusBarColor, newColor, duration);
      oldStatusBarColor = newColor;
    }
  }

  /**
   * Runs a blending animation on the status bar color
   */
  @TargetApi(Build.VERSION_CODES.LOLLIPOP) private void blendStatusBar(@ColorInt int fromColor,
      @ColorInt int toColor, long duration) {
    if (statusBarAnimator != null) {
      statusBarAnimator.cancel();
    }

    statusBarAnimator = ValueAnimator.ofFloat(0, 1);
    statusBarAnimator.addUpdateListener(animation -> {
      // Use animation position to blend colors.
      final float position = animation.getAnimatedFraction();

      // Apply blended color to the status bar.
      final int blended = blendColors(fromColor, toColor, position);
      getWindow().setStatusBarColor(blended);
    });

    statusBarAnimator.setDuration(duration).start();
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
      AppUtil.guaranteeSingleDialogFragment(this, new LoggerDialog(), tag);
      return true;
    } else {
      Timber.w("Logger dialog is already shown");
      return false;
    }
  }

  @Nullable @Override protected AdSource provideOnlineAdSource() {
    OnlineAdSource source = new OnlineAdSource(R.string.banner_main_ad_id);
    source.addTestAdIds("5681ECE0897CFFF6A56CFE947F4BC19E");
    return source;
  }

  @CheckResult @NonNull public TabLayout getTabLayout() {
    if (binding == null || binding.tabLayout == null) {
      throw new IllegalStateException("TabLayout is NULL");
    }
    return binding.tabLayout;
  }
}
