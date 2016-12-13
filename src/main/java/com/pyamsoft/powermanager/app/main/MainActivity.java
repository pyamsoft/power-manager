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
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.pyamsoft.powermanager.BuildConfig;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.airplane.AirplaneFragment;
import com.pyamsoft.powermanager.app.bluetooth.BluetoothFragment;
import com.pyamsoft.powermanager.app.data.DataFragment;
import com.pyamsoft.powermanager.app.doze.DozeFragment;
import com.pyamsoft.powermanager.app.logger.LoggerDialog;
import com.pyamsoft.powermanager.app.overview.OverviewFragment;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.powermanager.app.settings.SettingsFragment;
import com.pyamsoft.powermanager.app.sync.SyncFragment;
import com.pyamsoft.powermanager.app.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.app.wifi.WifiFragment;
import com.pyamsoft.powermanager.databinding.ActivityMainBinding;
import com.pyamsoft.pydroid.about.AboutLibrariesFragment;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.sec.TamperActivity;
import com.pyamsoft.pydroid.support.RatingDialog;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class MainActivity extends TamperActivity implements MainPresenter.View {

  @NonNull private static final String KEY_PRESENTER = "key_main_presenter";
  @NonNull private final Map<String, View> addedViewMap = new HashMap<>();
  @Nullable private final Runnable longPressBackRunnable =
      Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? null : this::handleBackLongPress;
  @Nullable private final Handler mainHandler =
      Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? null : new Handler(Looper.getMainLooper());
  MainPresenter presenter;
  private ActivityMainBinding binding;
  @ColorInt private int oldAppBarColor;
  @ColorInt private int oldStatusBarColor;
  @Nullable private ValueAnimator appBarAnimator;
  @Nullable private ValueAnimator statusBarAnimator;
  private long loadedKey;

  @CheckResult @ColorInt static int blendColors(@ColorInt int from, @ColorInt int to, float ratio) {
    final float inverseRatio = 1f - ratio;

    final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
    final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
    final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

    return Color.rgb((int) r, (int) g, (int) b);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    super.onCreate(savedInstanceState);

    oldAppBarColor = ContextCompat.getColor(this, R.color.amber500);
    oldStatusBarColor = ContextCompat.getColor(this, R.color.amber700);

    setupPreferenceDefaults();
    setupAppBar();
    if (hasNoActiveFragment()) {
      loadOverviewFragment();
    }

    loadedKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState, new PersistLoader.Callback<MainPresenter>() {
          @NonNull @Override public PersistLoader<MainPresenter> createLoader() {
            return new MainPresenterLoader();
          }

          @Override public void onPersistentLoaded(@NonNull MainPresenter persist) {
            presenter = persist;
          }
        });
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

    for (final String key : addedViewMap.keySet()) {
      removeViewFromAppBar(key);
    }

    if (!isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }

    addedViewMap.clear();
    binding.unbind();
  }

  @Override public void onBackPressed() {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    if (fragmentManager.getBackStackEntryCount() > 0) {
      fragmentManager.popBackStack();
    } else {
      super.onBackPressed();
    }
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
        && fragmentManager.findFragmentByTag(SettingsFragment.TAG) == null
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
    RatingDialog.showRatingDialog(this, this);
  }

  @NonNull @Override protected String getSafePackageName() {
    return "com.pyamsoft.powermanager";
  }

  public void addViewToAppBar(@NonNull String tag, @NonNull View view) {
    if (addedViewMap.containsKey(tag)) {
      Timber.w("AppBar already has view with this tag: %s", tag);
      removeViewFromAppBar(tag);
    }

    Timber.d("Add view to map with tag: %s", tag);
    addedViewMap.put(tag, view);
    binding.mainAppbar.addView(view);
  }

  public void removeViewFromAppBar(@NonNull String tag) {
    if (addedViewMap.containsKey(tag)) {
      Timber.d("Remove tag from map: %s", tag);
      final View viewToRemove = addedViewMap.remove(tag);
      if (viewToRemove == null) {
        Timber.e("View to remove was NULL for tag: %s", tag);
      } else {
        binding.mainAppbar.removeView(viewToRemove);
      }
    } else {
      Timber.e("Viewmap does not contain a view for tag: %s", tag);
    }
  }

  @NonNull @Override protected String[] getChangeLogLines() {
    final String line1 =
        "CHANGE: Always start the application on boot. This change is done to fix an issue where, on some devices, Home Button does not always start on boot.";
    final String line2 = "BUGFIX: Fix crash in donation page";
    final String line3 = "BUGFIX: Fix crash in image loading on KitKat";
    final String line4 = "BUGFIX: Smaller APK size";
    final String line5 = "BUGFIX: Various optimizations and fixes";
    return new String[] { line1, line2, line3, line4, line5 };
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

  @Override public void onServiceEnabledWhenOpen() {
    Timber.d("Should refresh service when opened");
    ForegroundService.start(getApplicationContext());
  }

  @Override public void explainRootRequirement() {
    // TODO explain with dialog
    Toast.makeText(getApplicationContext(),
        "Root is required for certain functions like Doze and Airplane mode", Toast.LENGTH_SHORT)
        .show();
  }

  @Override protected void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override protected void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    PersistentCache.get().saveKey(outState, KEY_PRESENTER, loadedKey);
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

  boolean handleBackLongPress() {
    final String tag = "logger_dialog";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    if (fragmentManager.findFragmentByTag(tag) == null) {
      Timber.d("Show logger dialog");
      AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(), new LoggerDialog(), tag);
      return true;
    } else {
      Timber.w("Logger dialog is already shown");
      return false;
    }
  }
}
