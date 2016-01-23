/*
 * Copyright 2013 - 2016 Peter Kenji Yamanaka
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
package com.pyamsoft.powermanager.ui.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pyamsoft.powermanager.BuildConfig;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.ui.fragment.GridFragment;
import com.pyamsoft.pydroid.base.ActivityBase;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.ElevationUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;

public class MainActivity extends ActivityBase {

  private static final int EXPLANATION_ANIM_TIME = 600;
  private static final String TAG = MainActivity.class.getSimpleName();
  private View statusBarPadding;
  private Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupStatusBar();
    setupToolbar();
    setupViewElevation();
    getSupportFragmentManager().beginTransaction()
        .add(R.id.fragment_place, new GridFragment())
        .commit();
    setupGiftAd();
  }

  private void setupStatusBar() {
    statusBarPadding = findViewById(R.id.statusbar_padding);
    super.setupStatusBar(statusBarPadding);
  }

  public final void colorizeStatusBar(final int color) {
    if (statusBarPadding != null) {
      super.colorizeStatusBar(statusBarPadding, color);
    }
  }

  public final void showExplanation(final Spannable explanationText, final int backgroundColor) {
    final View explainView = findViewById(R.id.explain_view);
    explainView.setBackgroundColor(ContextCompat.getColor(this, backgroundColor));
    AnimUtil.expand(explainView, EXPLANATION_ANIM_TIME);
    final Button button = (Button) explainView.findViewById(R.id.explain_read);
    final TextView text = (TextView) explainView.findViewById(R.id.explain_text);

    text.setText(explanationText);
    button.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(final View v) {
        dismissExplanation();
      }
    });
  }

  private boolean isExplanationShown() {
    final View explainView = findViewById(R.id.explain_view);
    return explainView.isShown();
  }

  private void dismissExplanation() {
    final View explainView = findViewById(R.id.explain_view);
    AnimUtil.collapse(explainView, EXPLANATION_ANIM_TIME);
    final Button button = (Button) explainView.findViewById(R.id.explain_read);
    final TextView text = (TextView) explainView.findViewById(R.id.explain_text);

    text.setText(null);
    button.setOnClickListener(null);
  }

  private void hideExplanation() {
    final View explainView = findViewById(R.id.explain_view);
    explainView.setVisibility(View.GONE);
    final Button button = (Button) explainView.findViewById(R.id.explain_read);
    final TextView text = (TextView) explainView.findViewById(R.id.explain_text);

    text.setText(null);
    button.setOnClickListener(null);
  }

  private void setupViewElevation() {
    final View shadow = findViewById(R.id.dropshadow);
    if (toolbar != null && shadow != null) {
      enableShadows(toolbar, shadow);
    }

    final int dp = (int) AppUtil.convertToDP(this, ElevationUtil.ELEVATION_APP_BAR);
    final LinearLayout adMediaViews = (LinearLayout) findViewById(R.id.media_ads);
    if (adMediaViews != null) {
      ViewCompat.setElevation(adMediaViews, dp);
    }
    final View content = findViewById(R.id.fragment_place);
    if (content != null) {
      ViewCompat.setElevation(content, ElevationUtil.ELEVATION_NONE);
    }
  }

  private void setupToolbar() {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setVisibility(View.GONE);
    setSupportActionBar(toolbar);
    AnimUtil.expand(toolbar);
  }

  public final void colorizeActionBarToolbar(final boolean color) {
    final View shadow = findViewById(R.id.dropshadow);
    colorizeActionBarToolbar(toolbar, shadow, color ? R.color.amber500 : 0,
        color ? R.string.app_name : 0);
  }

  @Override protected void onResume() {
    super.onResume();
    animateActionBarToolbar(toolbar);
  }

  @Override protected String setGiftAdId() {
    return getString(R.string.AD_ID_GIFT);
  }

  @Override protected void addTestDevices() {
    if (BuildConfig.DEBUG) {
      addTestDevice(getString(R.string.dev_1)); // Nexus 6
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override public boolean onPrepareOptionsMenu(Menu menu) {
    final MenuItem proStatus = menu.findItem(R.id.menu_is_pro);
    final boolean pro = isKeyInstalled(getPackageName());
    proStatus.setTitle(pro ? "PRO" : "FREE");
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    final int itemId = item.getItemId();
    boolean handled = false;
    switch (itemId) {
      case android.R.id.home:
        onBackPressed();
        handled = true;
        break;
      case R.id.menu_is_pro:
        NetworkUtil.newLink(getApplicationContext(), PowerManager.RATE + "pro");
        handled = true;
        break;
      case R.id.menu_gift_ad:
        showGiftAd();
        handled = true;
        break;
      default:
    }
    return handled;
  }

  @Override protected void onBackPressedActivityHook() {
    if (isExplanationShown()) {
      hideExplanation();
    }
  }

  @Override protected void onBackPressedFragmentHook() {
    if (isExplanationShown()) {
      dismissExplanation();
    }
  }
}
