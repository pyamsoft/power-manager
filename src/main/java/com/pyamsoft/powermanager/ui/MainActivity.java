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

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.ui.about.AboutAdapter;
import com.pyamsoft.powermanager.ui.battery.BatteryInfoAdapter;
import com.pyamsoft.powermanager.ui.grid.GridContentAdapter;
import com.pyamsoft.powermanager.ui.grid.GridItemTouchCallback;
import com.pyamsoft.powermanager.ui.help.HelpAdapter;
import com.pyamsoft.powermanager.ui.plan.PowerPlanAdapter;
import com.pyamsoft.powermanager.ui.radio.RadioContentAdapter;
import com.pyamsoft.powermanager.ui.radio.RadioContentInterface;
import com.pyamsoft.powermanager.ui.setting.SettingsContentAdapter;
import com.pyamsoft.powermanager.ui.trigger.PowerTriggerAdapter;
import com.pyamsoft.pydroid.ContainerInterface;
import com.pyamsoft.pydroid.DividerItemDecoration;
import com.pyamsoft.pydroid.IgnoreAppBarLayoutFABBehavior;
import com.pyamsoft.pydroid.base.ActivityBase;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.ElevationUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import com.squareup.picasso.Picasso;

public class MainActivity extends ActivityBase
    implements ContainerInterface, FABStateReceiver, FABMiniStateReceiver,
    BillingProcessor.IBillingHandler {

  private static final long DELAY = 1600L;
  private static final String TAG = MainActivity.class.getSimpleName();
  private RecyclerView recyclerView;
  private final StaggeredGridLayoutManager gridLayoutManager =
      new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
  private BindableRecyclerAdapter<? extends RecyclerView.ViewHolder> adapter;
  private RecyclerView.ItemDecoration decoration;
  private FloatingActionButton fab;
  private FloatingActionButton fabSmall;
  private FloatingActionButton fabLarge;
  private AppBarLayout appBarLayout;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private ImageView heroImage;
  private Toolbar toolbar;
  private ExplanationDialog dialog;
  private CoordinatorLayout coordinatorLayout;
  private String currentView;
  private ItemTouchHelper helper;
  private BillingProcessor billingProcessor = null;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    setupFakeFullscreenWindow();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    billingProcessor =
        new BillingProcessor(this, getString(R.string.google_billing_license_key), this);
    findViews();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
        && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      setupFakeFullscreenToolbarPadding(toolbar);
    }
    setSupportActionBar(toolbar);
    createExplanationDialog();
    setupViewElevations();
    enablepBackBeenPressedConfirmation();

    setCurrentView(null, 0);
  }

  private void setupViewElevations() {
    final int dp = (int) AppUtil.convertToDP(this, ElevationUtil.ELEVATION_APP_BAR);
    ViewCompat.setElevation(appBarLayout, dp);
  }

  private void createExplanationDialog() {
    dialog = ExplanationDialog.createDialog(this);
  }

  private void findViews() {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
    collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsebar);
    recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    fab = (FloatingActionButton) findViewById(R.id.fab);
    fabSmall = (FloatingActionButton) findViewById(R.id.fab_small);
    fabLarge = (FloatingActionButton) findViewById(R.id.fab_large);
    heroImage = (ImageView) collapsingToolbarLayout.findViewById(R.id.hero_image);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    fab.setOnClickListener(null);
    fab.setOnLongClickListener(null);
    AppUtil.nullifyCallback(fab);

    fabLarge.setOnClickListener(null);
    fabLarge.setOnLongClickListener(null);
    AppUtil.nullifyCallback(fabLarge);

    fabSmall.setOnClickListener(null);
    fabSmall.setOnLongClickListener(null);
    AppUtil.nullifyCallback(fabSmall);

    recyclerView.setOnClickListener(null);
    recyclerView.setLayoutManager(null);
    recyclerView.setAdapter(null);

    if (adapter != null) {
      adapter.destroy();
    }

    if (helper != null) {
      helper.attachToRecyclerView(null);
    }

    if (billingProcessor != null) {
      billingProcessor.release();
    }
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    LogUtil.d(TAG, "onConfigurationChanged");
    if (toolbar != null) {
      toolbar.setTitleTextAppearance(this, R.style.TextAppearance_PYDroid_Toolbar_Title);
    }
  }

  @Override protected void onResume() {
    super.onResume();
    animateActionBarToolbar(toolbar);
    if (!BillingProcessor.isIabServiceAvailable(this)) {
      createDonationUnavailableDialog();
    }
  }

  @Override protected void onPause() {
    super.onPause();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
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
      case R.id.menu_donate:
        createDonationDialog(billingProcessor, getString(R.string.app_name));
        handled = true;
        break;
      default:
    }
    return handled || super.onOptionsItemSelected(item);
  }

  @Override public void onBackPressed() {
    if (currentView == null) {
      super.onBackPressed();
    } else {
      popCurrentView();
    }
  }

  /**
   * Override to use a single back press for exit
   */
  @Override protected boolean shouldConfirmBackPress() {
    return true;
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

  private void setFABVisibility() {
    if (adapter != null && fabLarge != null && fabSmall != null) {
      if (adapter.isFABShown(fabLarge)) {
        final int icon =
            adapter.isFABEnabled() ? adapter.getFABIconEnabled() : adapter.getFABIconDisabled();
        if (icon != 0) {
          fabLarge.show();
          Picasso.with(this).load(icon).into(fabLarge);
          fabLarge.setOnClickListener(adapter.getFABOnClickListener());
        } else {
          fabLarge.hide();
        }
      } else {
        fabLarge.hide();
      }

      if (adapter.isFABShown(fabSmall)) {
        final int icon = adapter.isFABMiniEnabled() ? adapter.getFABMiniIconEnabled()
            : adapter.getFABMiniIconDisabled();
        if (icon != 0) {
          fabSmall.show();
          Picasso.with(this).load(icon).into(fabSmall);
          fabSmall.setOnClickListener(adapter.getFABMiniOnClickListener());
        } else {
          fabSmall.hide();
        }
      } else {
        fabSmall.hide();
      }
    }
  }

  @Override public void setCurrentView(final String viewCode, final int image) {
    setCurrentRecyclerViewContent(viewCode);
    setCurrentAppBarState(viewCode, image);

    IgnoreAppBarLayoutFABBehavior behavior;
    if (adapter == null) {
      behavior = null;
    } else {
      behavior = new IgnoreAppBarLayoutFABBehavior(adapter);
    }
    AppUtil.setupFABBehavior(fabLarge, behavior);
    AppUtil.setupFABBehavior(fabSmall, behavior);
    setFABVisibility();
  }

  private void setCurrentAppBarState(final String viewCode, final int image) {
    boolean up;
    up = viewCode != null;
    if (image == 0) {
      heroImage.setImageResource(image);
      collapsingToolbarLayout.setTitle(getString(R.string.app_name));
    } else {
      Picasso.with(this).load(image).into(heroImage);
      collapsingToolbarLayout.setTitle(viewCode);
    }

    appBarLayout.setExpanded(up, true);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().setStatusBarColor(ContextCompat.getColor(this, adapter.getStatusbarColor()));
    }
    collapsingToolbarLayout.setContentScrimColor(
        ContextCompat.getColor(this, adapter.getToolbarColor()));
    setActionBarHomeEnabled(up);
  }

  private void setCurrentRecyclerViewContent(final String viewCode) {
    if (helper != null) {
      // Remove helper
      helper.attachToRecyclerView(null);
    }
    if (decoration != null) {
      recyclerView.removeItemDecoration(decoration);
    }
    if (adapter != null) {
      adapter.destroy();
    }
    if (viewCode == null) {
      final GridContentAdapter grid = new GridContentAdapter(this, this);
      adapter = grid;
      helper = new ItemTouchHelper(new GridItemTouchCallback(grid));
      gridLayoutManager.setSpanCount(2);
      gridLayoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);
      decoration = null;
    } else {
      helper = null;
      gridLayoutManager.setSpanCount(1);
      gridLayoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);
      switch (viewCode) {
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
          adapter = new RadioContentAdapter(this, RadioContentInterface.WIFI, this, this);
          decoration = null;
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
          adapter = new RadioContentAdapter(this, RadioContentInterface.DATA, this, this);
          decoration = null;
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
          adapter = new RadioContentAdapter(this, RadioContentInterface.BLUETOOTH, this, this);
          decoration = null;
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
          adapter = new RadioContentAdapter(this, RadioContentInterface.SYNC, this, this);
          decoration = null;
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_PLAN:
          adapter = new PowerPlanAdapter(this);
          decoration = null;
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_TRIGGER:
          adapter = new PowerTriggerAdapter(this);
          decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BATTERY_INFO:
          adapter = new BatteryInfoAdapter();
          decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SETTINGS:
          adapter = new SettingsContentAdapter(this);
          decoration = null;
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_HELP:
          adapter = new HelpAdapter();
          decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_ABOUT:
          adapter = new AboutAdapter(this);
          decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
          break;
        default:
          throw new RuntimeException("Invalid viewCode: " + viewCode);
      }
    }

    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.setAdapter(adapter);
    if (helper != null) {
      helper.attachToRecyclerView(recyclerView);
    }
    if (decoration != null) {
      recyclerView.addItemDecoration(decoration);
    }
    currentView = viewCode;
  }

  @Override protected void onStart() {
    super.onStart();
    if (adapter != null) {
      adapter.start();
    }
  }

  @Override protected void onStop() {
    super.onStop();
    if (adapter != null) {
      adapter.stop();
    }
  }

  @Override public String popCurrentView() {
    final String oldCode = currentView;
    setCurrentView(null, 0);
    return oldCode;
  }

  private static void setFABIcon(final FloatingActionButton fab, final int icon) {
    if (icon != 0 && fab != null) {
      Picasso.with(fab.getContext()).load(icon).into(fab);
    }
  }

  @Override public void onFABMiniStateEnabled() {
    if (adapter != null) {
      setFABIcon(fabSmall, adapter.getFABMiniIconEnabled());
    }
  }

  @Override public void onFABMiniStateDisabled() {
    if (adapter != null) {
      setFABIcon(fabSmall, adapter.getFABMiniIconDisabled());
    }
  }

  @Override public void onFABStateEnabled() {
    if (adapter != null) {
      setFABIcon(fabLarge, adapter.getFABIconEnabled());
    }
  }

  @Override public void onFABStateDisabled() {
    if (adapter != null) {
      setFABIcon(fabLarge, adapter.getFABIconDisabled());
    }
  }

  @Override public void onProductPurchased(String productId, TransactionDetails details) {

  }

  @Override public void onPurchaseHistoryRestored() {

  }

  @Override public void onBillingError(int errorCode, Throwable error) {

  }

  @Override public void onBillingInitialized() {

  }

  @Override protected String getPlayStoreAppPackage() {
    return "com.pyamsoft.powermanager";
  }
}
