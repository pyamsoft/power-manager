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
package com.pyamsoft.powermanager.ui.detail;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.ui.ExplanationFragment;
import com.pyamsoft.powermanager.ui.PicassoTargetFragment;
import com.pyamsoft.powermanager.ui.activity.MainActivity;
import com.pyamsoft.powermanager.ui.fragment.AboutFragment;
import com.pyamsoft.powermanager.ui.fragment.BatteryInfoFragment;
import com.pyamsoft.powermanager.ui.fragment.BluetoothRadioFragment;
import com.pyamsoft.powermanager.ui.fragment.DataRadioFragment;
import com.pyamsoft.powermanager.ui.fragment.HelpFragment;
import com.pyamsoft.powermanager.ui.fragment.PowerTriggerFragment;
import com.pyamsoft.powermanager.ui.fragment.SyncRadioFragment;
import com.pyamsoft.powermanager.ui.fragment.WifiRadioFragment;
import com.pyamsoft.powermanager.ui.plan.PowerPlanFragment;
import com.pyamsoft.powermanager.ui.setting.SettingsFragment;
import com.pyamsoft.pydroid.base.PreferenceBase;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import com.pyamsoft.pydroid.util.StringUtil;
import com.squareup.picasso.Picasso;

public final class DetailBaseFragment extends PicassoTargetFragment implements DetailInterface {

  public static final String EXTRA_PARAM_ID = "TARGET_ID";
  public static final String EXTRA_PARAM_IMAGE = "TARGET_IMAGE";
  private static final String TAG = DetailBaseFragment.class.getSimpleName();
  private String targetString;
  private int targetImageResId;
  private int backgroundColor;
  private ExplanationFragment fragment;
  private FloatingActionButton largeFAB;
  private FloatingActionButton smallFAB;
  private TextView title;
  private ImageView image;
  private DetailPresenter presenter;
  private int largeIconOn;
  private int largeIconOff;
  private int smallIconOn;
  private int smallIconOff;
  private AlertDialog largeFABDialog;
  private AlertDialog smallFABDialog;
  private final PreferenceBase.OnSharedPreferenceChangeListener listener =
      new PreferenceBase.OnSharedPreferenceChangeListener(
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_WIFI,
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_DATA,
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_BLUETOOTH,
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_SYNC
      ) {
        @Override
        protected void preferenceChanged(SharedPreferences sharedPreferences, String key) {
          onPreferenceChanged(sharedPreferences, key);
        }
      };

  public DetailBaseFragment() {
    presenter = new DetailPresenter();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    applyStatusBarColor(true);
    return inflater.inflate(R.layout.fragment_detail, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    findViews(view);
    initialize();
    setupContentArea();
    setupSmallFAB();
    setupLargeFAB();
  }

  private void findViews(View v) {
    largeFAB = (FloatingActionButton) v.findViewById(R.id.detail_fab);
    smallFAB = (FloatingActionButton) v.findViewById(R.id.detail_fab_small);
    title = (TextView) v.findViewById(R.id.detail_title);
    image = (ImageView) v.findViewById(R.id.detail_image);
  }

  private void initialize() {
    targetString = getArguments().getString(EXTRA_PARAM_ID);
    targetImageResId = getArguments().getInt(EXTRA_PARAM_IMAGE, 0);

    switch (targetString) {
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
        fragment = new WifiRadioFragment();
        largeIconOn = R.drawable.ic_network_wifi_white_24dp;
        largeIconOff = R.drawable.ic_signal_wifi_off_white_24dp;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
        fragment = new DataRadioFragment();
        largeIconOn = R.drawable.ic_network_cell_white_24dp;
        largeIconOff = R.drawable.ic_signal_cellular_off_white_24dp;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
        fragment = new BluetoothRadioFragment();
        largeIconOn = R.drawable.ic_bluetooth_white_24dp;
        largeIconOff = R.drawable.ic_bluetooth_disabled_white_24dp;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
        fragment = new SyncRadioFragment();
        largeIconOn = R.drawable.ic_sync_white_24dp;
        largeIconOff = R.drawable.ic_sync_disabled_white_24dp;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_PLAN:
        fragment = new PowerPlanFragment();
        largeIconOn = 0;
        largeIconOff = 0;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_TRIGGER:
        fragment = new PowerTriggerFragment();
        largeIconOn = 0;
        largeIconOff = 0;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BATTERY_INFO:
        fragment = new BatteryInfoFragment();
        largeIconOn = 0;
        largeIconOff = 0;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SETTINGS:
        fragment = new SettingsFragment();
        largeIconOn = 0;
        largeIconOff = 0;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_HELP:
        fragment = new HelpFragment();
        largeIconOn = 0;
        largeIconOff = 0;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_ABOUT:
        fragment = new AboutFragment();
        largeIconOn = 0;
        largeIconOff = 0;
        break;
      default:
        fragment = null;
        largeIconOn = 0;
        largeIconOff = 0;
    }

    // For now
    smallIconOn = R.drawable.ic_check_white_24dp;
    smallIconOff = R.drawable.ic_close_white_24dp;

    largeFABDialog = new AlertDialog.Builder(getContext()).setPositiveButton("Okay",
        new DialogInterface.OnClickListener() {
          @Override public void onClick(final DialogInterface dialog, final int which) {
            dialog.dismiss();
          }
        })
        .setTitle(StringUtil.formatString("%s Manage", targetString))
        .setMessage(StringUtil.formatResString(getContext().getResources(), R.string.manage_explain,
            targetString, targetString))
        .create();

    smallFABDialog = new AlertDialog.Builder(getContext()).setPositiveButton("Okay",
        new DialogInterface.OnClickListener() {
          @Override public void onClick(final DialogInterface dialog, final int which) {
            dialog.dismiss();
          }
        })
        .setTitle(StringUtil.formatString("%s Interval", targetString))
        .setMessage(
            StringUtil.formatResString(getContext().getResources(), R.string.interval_explain,
                targetString, targetString))
        .create();

    // Bind presenter after initialized
    presenter.bind(getContext(), this);
  }

  private void setupContentArea() {
    final int width = getActivity().getWindow().getDecorView().getMeasuredWidth();
    final int height = (int) AppUtil.convertToDP(getContext(), 240);
    Picasso.with(getContext()).load(targetImageResId).resize(width, height).into(this);

    title.setText(targetString);
    backgroundColor = fragment.getBackgroundColor();
    getChildFragmentManager().beginTransaction().replace(R.id.detail_content, fragment).commit();
  }

  private boolean shouldShowFAB() {
    return largeIconOff != 0 && largeIconOn != 0;
  }

  private void setupSmallFAB() {
    if (shouldShowFAB()) {
      smallFAB.setOnClickListener(new View.OnClickListener() {

        @Override public void onClick(final View v) {
          presenter.onClickSmallFAB();
        }
      });

      smallFAB.setOnLongClickListener(new View.OnLongClickListener() {
        @Override public boolean onLongClick(final View v) {
          return presenter.onLongClickSmallFAB();
        }
      });

      final boolean isChecked = presenter.isSmallFABChecked();
      final int drawable = isChecked ? smallIconOn : smallIconOff;
      smallFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), drawable));
    } else {
      smallFAB.setVisibility(View.GONE);
    }
  }

  private void setupLargeFAB() {
    if (shouldShowFAB()) {
      largeFAB.setOnClickListener(new View.OnClickListener() {

        @Override public void onClick(View v) {
          presenter.onClickLargeFAB();
        }
      });

      largeFAB.setOnLongClickListener(new View.OnLongClickListener() {
        @Override public boolean onLongClick(final View v) {
          return presenter.onLongClickLargeFAB();
        }
      });

      final boolean isChecked = presenter.isLargeFABChecked();
      final int drawable = isChecked ? largeIconOn : largeIconOff;
      largeFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), drawable));
    } else {
      largeFAB.setVisibility(View.GONE);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    largeFAB.setOnClickListener(null);
    AppUtil.nullifyCallback(largeFAB);
    smallFAB.setOnClickListener(null);
    AppUtil.nullifyCallback(smallFAB);
    AppUtil.nullifyCallback(image);
    presenter.unbind();
  }

  @Override public void onResume() {
    super.onResume();
    AnimUtil.pop(largeFAB, 500, 300).start();
    AnimUtil.pop(smallFAB, 800, 300).start();
    listener.register(GlobalPreferenceUtil.with(getContext()).powerManagerActive());

    final MainActivity a = ((MainActivity) getActivity());
    if (a != null) {
      a.setActionBarUp(true);
      a.colorizeActionBarToolbar(false);
    }
  }

  private void applyStatusBarColor(final boolean placeHolder) {
    final Activity a = getActivity();
    if (a instanceof MainActivity) {
      final MainActivity main = (MainActivity) a;
      int color;
      if (placeHolder) {
        color = R.color.shadow_scrim45;
      } else {
        if (backgroundColor == 0) {
          color = R.color.shadow_scrim45;
        } else {
          color = backgroundColor;
        }
      }

      main.colorizeStatusBar(color);
    }
  }

  @Override public void onPause() {
    super.onPause();
    listener.unregister(GlobalPreferenceUtil.with(getContext()).powerManagerActive());
  }

  @Override public void onSmallFABChecked() {
    if (smallFAB != null) {
      smallFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), smallIconOn));
    }
  }

  @Override public void onSmallFABUnchecked() {
    if (smallFAB != null) {
      smallFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), smallIconOff));
    }
  }

  @Override public void onLargeFABChecked() {
    if (largeFAB != null) {
      largeFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), largeIconOn));
    }
  }

  @Override public void onLargeFABUnchecked() {
    if (largeFAB != null) {
      largeFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), largeIconOff));
    }
  }

  @Override public void onLongClickSmallFAB() {
    smallFABDialog.show();
  }

  @Override public void onLongClickLargeFAB() {
    largeFABDialog.show();
  }

  @Override public void onPreferenceChanged(SharedPreferences preferences, String key) {
    LogUtil.d(TAG, "onPreferenceChanged");
    if (largeFAB != null) {
      final boolean state = preferences.getBoolean(key, false);
      if (largeIconOn != 0 && largeIconOff != 0) {
        LogUtil.d(TAG, "largeFAB setImage");
        largeFAB.setImageDrawable(ContextCompat.getDrawable(getContext(), state ? largeIconOn : largeIconOff));
      }
    }
  }

  @Override public String getTarget() {
    return targetString;
  }

  @Override protected void loadDrawableIntoView(Drawable drawable) {
    if (image != null) {
      image.setImageDrawable(drawable);
    }
    // Also set status bar coloring at this point
    applyStatusBarColor(false);
  }
}
