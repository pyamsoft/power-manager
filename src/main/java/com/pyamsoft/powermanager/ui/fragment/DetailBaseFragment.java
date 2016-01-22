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
package com.pyamsoft.powermanager.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.powermanager.ui.activity.MainActivity;
import com.pyamsoft.pydroid.base.FABBase;
import com.pyamsoft.pydroid.base.PreferenceBase;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import com.pyamsoft.pydroid.util.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public final class DetailBaseFragment extends Fragment {

  public static final String EXTRA_PARAM_ID = "TARGET_ID";
  public static final String EXTRA_PARAM_IMAGE = "TARGET_IMAGE";
  private static final String TAG = DetailBaseFragment.class.getSimpleName();
  private String targetType;
  private ImageView image;
  private FloatingActionButton fab;
  private FABBase fabBase;
  private final PreferenceBase.OnSharedPreferenceChangeListener listener =
      new PreferenceBase.OnSharedPreferenceChangeListener(
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_WIFI,
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_DATA,
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_BLUETOOTH,
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_SYNC) {

        @Override protected void preferenceChanged(final SharedPreferences sharedPreferences,
            final String key) {
          if (fabBase != null) {
            fabBase.setChecked(fabBase.isChecked());
          }
        }
      };
  private FloatingActionButton fabMini;
  private FABBase fabBaseMini;
  private int backgroundColor;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    applyStatusBarColor(true);
    return inflater.inflate(R.layout.fragment_detail, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    targetType =
        getArguments().getString(EXTRA_PARAM_ID, GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI);
    setupContentArea();
    setupHeroImage(view);
    setupFAB(view);
    setupFABMini(view);
    setupDetailName(view);
  }

  private void setupHeroImage(final View view) {
    final int imageType = getArguments().getInt(EXTRA_PARAM_IMAGE, 0);
    image = (ImageView) view.findViewById(R.id.detail_image);
    final int width = getActivity().getWindow().getDecorView().getMeasuredWidth();
    final int height = (int) AppUtil.convertToDP(getContext(), 240);
    Picasso.with(getContext()).load(imageType).resize(width, height).into(image, new Callback() {

      @Override public void onSuccess() {
        applyStatusBarColor(false);
      }

      @Override public void onError() {
        LogUtil.e(TAG, "Picasso image load failure");
      }
    });
  }

  private void setupContentArea() {
    final FragmentManager fm = getChildFragmentManager();
    ExplanationFragment fragment;
    switch (targetType) {
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
        fragment = new WifiRadioFragment();
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
        fragment = new DataRadioFragment();
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
        fragment = new BluetoothRadioFragment();
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
        fragment = new SyncRadioFragment();
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_PLAN:
        fragment = new PowerPlanFragment();
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_TRIGGER:
        fragment = new PowerTriggerFragment();
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BATTERY_INFO:
        fragment = new BatteryInfoFragment();
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SETTINGS:
        fragment = new SettingsFragment();
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_HELP:
        fragment = new HelpFragment();
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_ABOUT:
        fragment = new AboutFragment();
        break;
      default:
        fragment = null;
    }
    if (fragment != null) {
      backgroundColor = fragment.getBackgroundColor();
      fm.beginTransaction().replace(R.id.detail_content, fragment).commit();
    } else {
      backgroundColor = R.color.shadow_scrim45;
    }
  }

  private void setupDetailName(final View v) {
    final TextView detailName = (TextView) v.findViewById(R.id.detail_title);
    detailName.setText(targetType);
  }

  private void setupFABMini(final View v) {
    fabMini = (FloatingActionButton) v.findViewById(R.id.detail_fab_small);
    FABBase.setupFAB(fabMini, R.color.lightblueA200);
    fabBaseMini = new FABBase(fabMini) {

      @Override public boolean isChecked() {
        final GlobalPreferenceUtil p = GlobalPreferenceUtil.get();
        boolean r;
        switch (targetType) {
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
            r = p.intervalDisableService().isWifiReopen();
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
            r = p.intervalDisableService().isDataReopen();
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
            r = p.intervalDisableService().isBluetoothReopen();
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
            r = p.intervalDisableService().isSyncReopen();
            break;
          default:
            r = false;
        }
        return r;
      }

      @Override public void setChecked(boolean checked) {
        int d;
        switch (targetType) {
          // fall through
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
            d = checked ? R.drawable.ic_check_white_24dp : R.drawable.ic_close_white_24dp;
            break;
          default:
            d = 0;
        }
        if (d != 0) {
          setFabImage(d);
        } else {
          fabMini.setVisibility(View.GONE);
        }
      }

      @Override public void startService() {
        int field;
        final boolean state = !isChecked();
        final GlobalPreferenceUtil p = GlobalPreferenceUtil.get();
        switch (targetType) {
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
            p.intervalDisableService().setWifiReopen(state);
            field = PowerPlanUtil.FIELD_REOPEN_WIFI;
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
            p.intervalDisableService().setDataReopen(state);
            field = PowerPlanUtil.FIELD_REOPEN_DATA;
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
            p.intervalDisableService().setBluetoothReopen(state);
            field = PowerPlanUtil.FIELD_REOPEN_BLUETOOTH;
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
            p.intervalDisableService().setSyncReopen(state);
            field = PowerPlanUtil.FIELD_REOPEN_SYNC;
            break;
          default:
            field = -1;
        }

        if (field >= 0) {
          final PowerPlanUtil powerPlan = PowerPlanUtil.get();
          powerPlan.updateCustomPlan(field, state);
          powerPlan.setPlan(
              PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_CUSTOM[PowerPlanUtil.FIELD_INDEX]));
        }
      }
    };

    fabMini.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(final View v) {
        fabBaseMini.setChecked(!fabBaseMini.isChecked());
        fabBaseMini.startService();
      }
    });
    fabMini.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(final View v) {
        new AlertDialog.Builder(v.getContext()).setPositiveButton("Okay",
            new DialogInterface.OnClickListener() {
              @Override public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
              }
            })
            .setTitle(StringUtil.formatString("%s Interval", targetType))
            .setMessage(
                StringUtil.formatResString(v.getContext().getResources(), R.string.interval_explain,
                    targetType, targetType))
            .create()
            .show();
        return true;
      }
    });

    fabBaseMini.setChecked(fabBaseMini.isChecked());
  }

  private void setupFAB(final View v) {
    fab = (FloatingActionButton) v.findViewById(R.id.detail_fab);
    FABBase.setupFAB(fab, R.color.lightblueA200);
    fabBase = new FABBase(fab) {

      @Override public boolean isChecked() {
        final GlobalPreferenceUtil p = GlobalPreferenceUtil.get();
        boolean r;
        switch (targetType) {
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
            r = p.powerManagerActive().isManagedWifi();
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
            r = p.powerManagerActive().isManagedData();
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
            r = p.powerManagerActive().isManagedBluetooth();
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
            r = p.powerManagerActive().isManagedSync();
            break;
          default:
            r = false;
        }
        return r;
      }

      @Override public void setChecked(boolean checked) {
        int d;
        switch (targetType) {
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
            d = checked ? R.drawable.ic_network_wifi_white_24dp
                : R.drawable.ic_signal_wifi_off_white_24dp;
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
            d = checked ? R.drawable.ic_network_cell_white_24dp
                : R.drawable.ic_signal_cellular_off_white_24dp;
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
            d = checked ? R.drawable.ic_bluetooth_white_24dp
                : R.drawable.ic_bluetooth_disabled_white_24dp;
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
            d = checked ? R.drawable.ic_sync_white_24dp : R.drawable.ic_sync_disabled_white_24dp;
            break;
          default:
            d = 0;
        }
        if (d != 0) {
          setFabImage(d);
        } else {
          fab.setVisibility(View.GONE);
        }
      }

      @Override public void startService() {
        int field;
        final GlobalPreferenceUtil p = GlobalPreferenceUtil.get();
        final boolean state = !isChecked();
        switch (targetType) {
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
            p.powerManagerActive().setManagedWifi(state);
            field = PowerPlanUtil.FIELD_MANAGE_WIFI;
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
            p.powerManagerActive().setManagedData(state);
            field = PowerPlanUtil.FIELD_MANAGE_DATA;
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
            p.powerManagerActive().setManagedBluetooth(state);
            field = PowerPlanUtil.FIELD_MANAGE_BLUETOOTH;
            break;
          case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
            p.powerManagerActive().setManagedSync(state);
            field = PowerPlanUtil.FIELD_MANAGE_SYNC;
            break;
          default:
            field = -1;
        }
        if (field >= 0) {
          final PowerPlanUtil powerPlan = PowerPlanUtil.get();
          powerPlan.updateCustomPlan(field, state);
          powerPlan.setPlan(
              PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_CUSTOM[PowerPlanUtil.FIELD_INDEX]));
        }
      }
    };

    fab.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View v) {
        fabBase.setChecked(!fabBase.isChecked());
        fabBase.startService();
        MonitorService.updateService(v.getContext());
      }
    });
    fab.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(final View v) {
        new AlertDialog.Builder(v.getContext()).setPositiveButton("Okay",
            new DialogInterface.OnClickListener() {
              @Override public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
              }
            })
            .setTitle(StringUtil.formatString("%s Manage", targetType))
            .setMessage(
                StringUtil.formatResString(v.getContext().getResources(), R.string.manage_explain,
                    targetType, targetType))
            .create()
            .show();

        return true;
      }
    });

    fabBase.setChecked(fabBase.isChecked());
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    fab.setOnClickListener(null);
    AppUtil.nullifyCallback(fab);
    fabMini.setOnClickListener(null);
    AppUtil.nullifyCallback(fabMini);
    AppUtil.nullifyCallback(image);
  }

  @Override public void onResume() {
    super.onResume();
    listener.register(GlobalPreferenceUtil.get().powerManagerActive());
    AnimUtil.pop(fab, 500, 300).start();
    AnimUtil.pop(fabMini, 800, 300).start();

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
    listener.unregister(GlobalPreferenceUtil.get().powerManagerActive());
  }
}
