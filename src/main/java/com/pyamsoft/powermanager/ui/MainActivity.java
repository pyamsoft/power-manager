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

package com.pyamsoft.powermanager.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.BuildConfig;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.ui.grid.GridContentAdapter;
import com.pyamsoft.powermanager.ui.grid.GridItemTouchCallback;
import com.pyamsoft.pydroid.base.ActivityBase;
import com.pyamsoft.pydroid.base.SocialMediaViewBase;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;

public class MainActivity extends ActivityBase implements SocialMediaViewBase.SocialMediaInterface {

  private static final String TAG = MainActivity.class.getSimpleName();
  private RecyclerView recyclerView;
  private final StaggeredGridLayoutManager gridLayoutManager =
      new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
  private FloatingActionButton fab;
  private FloatingActionButton fabSmall;
  private FloatingActionButton fabLarge;
  private AppBarLayout appBarLayout;
  private ItemTouchHelper helper;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private Toolbar toolbar;
  private View googlePlay;
  private View googlePlus;
  private View blogger;
  private View facebook;
  private SocialMediaViewBase.SocialMediaPresenter presenter;
  private ExplanationDialog dialog;
  private CoordinatorLayout coordinatorLayout;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    setupWindow();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupTintManager();
    findViews();
    setupAppBar();
    createExplanationDialog();
    setupGiftAd();
    setupRecyclerView();

    presenter = new SocialMediaViewBase.SocialMediaPresenter();
    presenter.bind(this, this);
  }

  private void setupWindow() {
    getWindow().getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
  }

  private void setupTintManager() {
    //tintManager = new SystemBarTintManager(this);
    //tintManager.setStatusBarTintEnabled(true);
    //tintManager.setNavigationBarTintEnabled(false);

    // TODO move into adapter
    colorizeStatusBar(R.color.amber700);
  }

  private void setupRecyclerView() {
    final GridContentAdapter adapter = new GridContentAdapter(this);
    helper = new ItemTouchHelper(new GridItemTouchCallback(adapter));
    helper.attachToRecyclerView(recyclerView);
    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  public void colorizeStatusBar(final int color) {

  }

  private void createExplanationDialog() {
    dialog = ExplanationDialog.createDialog(this);
  }

  /**
   * Butterknife leaks the activity?
   */
  private void findViews() {
    googlePlay = findViewById(R.id.google_play);
    googlePlus = findViewById(R.id.google_plus);
    blogger = findViewById(R.id.blogger);
    facebook = findViewById(R.id.facebook);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
    collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsebar);
    recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    fab = (FloatingActionButton) findViewById(R.id.fab);
    fabSmall = (FloatingActionButton) findViewById(R.id.fab_small);
    fabLarge = (FloatingActionButton) findViewById(R.id.fab_large);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    //googlePlay.setOnClickListener(null);
    //googlePlus.setOnClickListener(null);
    //blogger.setOnClickListener(null);
    //facebook.setOnClickListener(null);
    //AppUtil.nullifyCallback(googlePlay);
    //AppUtil.nullifyCallback(googlePlus);
    //AppUtil.nullifyCallback(blogger);
    //AppUtil.nullifyCallback(facebook);
    presenter.unbind();
  }

  private void setupAppBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      // Find the statusbar height, add it to the height of the toolbar
      // and as padding
      int statusBarHeight;
      int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
      if (resourceId > 0) {
        LogUtil.d(TAG, "Found height via reflection");
        statusBarHeight = getResources().getDimensionPixelSize(resourceId);
      } else {
        LogUtil.e(TAG, "Assign fallback height");
        statusBarHeight = (int) AppUtil.convertToDP(this, 25);
      }
      final ViewGroup.LayoutParams params = toolbar.getLayoutParams();
      params.height += statusBarHeight;
      toolbar.setLayoutParams(params);
      toolbar.setPadding(0, statusBarHeight, 0, 0);
      toolbar.requestLayout();
    }
    setSupportActionBar(toolbar);
    collapsingToolbarLayout.setTitle(getString(R.string.app_name));
  }

  public final void colorizeAppBar(final int color) {
    //final boolean noColor = (color == 0);
    //final int stringRes = noColor ? 0 : R.string.app_name;
    //final int backgroundColor = noColor ? android.R.color.transparent : color;
    //if (noColor) {
    //  disableShadows(toolbar, shadow);
    //} else {
    //  enableShadows(toolbar, shadow);
    //}
    //collapsingToolbarLayout.setTitle(stringRes == 0 ? null : getString(stringRes));
    //appBarLayout.setBackgroundColor(ContextCompat.getColor(this, backgroundColor));
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
    hideExplainView();
  }

  @Override protected void onBackPressedFragmentHook() {
    hideExplainView();
  }

  @Override public void onGooglePlayClicked(String rateUsed) {

  }

  @Override public void onGooglePlusClicked() {

  }

  @Override public void onBloggerClicked() {

  }

  @Override public void onFacebookClicked() {

  }

  public void showExplainView(Spannable explanation, int backgroundColor) {
    if (dialog != null) {
      dialog.setText(explanation).setBackgroundColor(backgroundColor).show();
    }
  }

  private void hideExplainView() {
    if (dialog != null) {
      dialog.dismiss();
    }
  }

  public void click(final View v) {
    if (coordinatorLayout == null) {
      coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }
    Snackbar.make(coordinatorLayout, "Test Snackbar", Snackbar.LENGTH_SHORT).show();
  }
}
