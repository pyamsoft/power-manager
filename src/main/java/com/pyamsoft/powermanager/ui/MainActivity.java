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

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.pyamsoft.powermanager.BuildConfig;
import com.pyamsoft.powermanager.PowerManager;
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
import com.pyamsoft.pydroid.base.ActivityBase;
import com.pyamsoft.pydroid.misc.DividerItemDecoration;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.ElevationUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.squareup.picasso.Picasso;

public class MainActivity extends ActivityBase implements ContainerInterface {

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
  private Toast backToast;
  private final Handler handler = new Handler();
  private boolean backPressed = false;
  private ItemTouchHelper helper;
  private final Runnable runOnBackPressed = new Runnable() {

    @Override public void run() {
      backPressed = false;
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.Theme_PowerManager_Light);
    setupFakeFullscreenWindow();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViews();
    setupBackBeenPressedHandler();
    setupFakeFullscreenToolbarPadding(toolbar);
    createExplanationDialog();
    setupGiftAd();
    setupViewElevations();

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

  @Override public void onBackPressed() {
    if (currentView == null) {
      if (backPressed || !toastOnBackPressed()) {
        backPressed = false;
        super.onBackPressed();
      } else {
        setBackBeenPressed();
      }
    } else {
      popCurrentView();
    }
  }

  /**
   * Override to use a single back press for exit
   */
  public boolean toastOnBackPressed() {
    return true;
  }

  @SuppressLint("ShowToast") private void setupBackBeenPressedHandler() {
    backToast = Toast.makeText(this, "Press again to Exit", Toast.LENGTH_SHORT);
    handler.removeCallbacksAndMessages(null);
  }

  private void setBackBeenPressed() {
    backPressed = true;
    backToast.show();
    handler.removeCallbacksAndMessages(null);
    handler.postDelayed(runOnBackPressed, DELAY);
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

  private void setupFABBehavior(final FloatingActionButton fab) {
    if (fab != null) {
      final ViewGroup.LayoutParams params = fab.getLayoutParams();
      if (params instanceof CoordinatorLayout.LayoutParams) {
        final CoordinatorLayout.LayoutParams coordParams = (CoordinatorLayout.LayoutParams) params;
        if (adapter != null) {
          final ScrollingFABBehavior behavior = new ScrollingFABBehavior(adapter);
          coordParams.setBehavior(behavior);
        } else {
          coordParams.setBehavior(null);
        }
      }
    }
  }

  private void setFABVisibility() {
    if (adapter != null && fabLarge != null && fabSmall != null) {
      if (adapter.isLargeFABShown()) {
        final int icon = adapter.getLargeFABIcon();
        if (icon != 0) {
          fabLarge.show();
          Picasso.with(this).load(icon).into(fabLarge);
          fabLarge.setOnClickListener(adapter.getLargeFABOnClick());
        } else {
          fabLarge.hide();
        }
      } else {
        fabLarge.hide();
      }

      if (adapter.isSmallFABShown()) {
        final int icon = adapter.getSmallFABIcon();
        if (icon != 0) {
          fabSmall.show();
          Picasso.with(this).load(icon).into(fabSmall);
          fabSmall.setOnClickListener(adapter.getSmallFABOnClick());
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

    setupFABBehavior(fabLarge);
    setupFABBehavior(fabSmall);
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
          adapter = new RadioContentAdapter(this, RadioContentInterface.WIFI);
          decoration = null;
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
          adapter = new RadioContentAdapter(this, RadioContentInterface.DATA);
          decoration = null;
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
          adapter = new RadioContentAdapter(this, RadioContentInterface.BLUETOOTH);
          decoration = null;
          break;
        case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
          adapter = new RadioContentAdapter(this, RadioContentInterface.SYNC);
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
}
