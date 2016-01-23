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
import java.util.concurrent.Callable;

public final class DetailBaseFragment extends Fragment {

  public static final String EXTRA_PARAM_ID = "TARGET_ID";
  public static final String EXTRA_PARAM_IMAGE = "TARGET_IMAGE";
  private static final String TAG = DetailBaseFragment.class.getSimpleName();
  private static final int FIELD_NOOP = -1;
  private static final int IMAGE_NONE = -1;
  private String targetString;
  private int targetImageResId;
  private int backgroundColor;
  private final PreferenceBase.OnSharedPreferenceChangeListener listener =
      new PreferenceBase.OnSharedPreferenceChangeListener(
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_WIFI,
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_DATA,
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_BLUETOOTH,
          GlobalPreferenceUtil.PowerManagerActive.MANAGE_SYNC) {

        @Override protected void preferenceChanged(final SharedPreferences sharedPreferences,
            final String key) {
          if (largeFABBase != null) {
            largeFABBase.setChecked(largeFABBase.isChecked());
          }
        }
      };
  private ExplanationFragment fragment;
  private FloatingActionButton largeFAB;
  private FloatingActionButton smallFAB;
  private TextView title;
  private ImageView image;
  private int powerPlanField;
  private Callable<Boolean> isReopen;
  private Callable<Boolean> isManage;
  private BooleanRunnable setReopen;
  private BooleanRunnable setManage;
  private int largeFABIconOn;
  private int largeFABIconOff;
  private int smallFABIconOn;
  private int smallFABIconOff;
  private FABBase largeFABBase;
  private FABBase smallFABBase;

  public static abstract class BooleanRunnable implements Runnable {

    private boolean state;

    public final void run(final boolean newState) {
      setState(newState);
      run();
    }

    public final void setState(boolean state) {
      this.state = state;
    }

    public final boolean isState() {
      return state;
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    applyStatusBarColor(true);
    return inflater.inflate(R.layout.fragment_detail, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initialize(view);
    setupContentArea();
    setupFAB();
    setupFABMini();
  }

  private boolean shouldShowFAB() {
    return isReopen != null && isManage != null && setReopen != null && setManage != null;
  }

  private void initialize(final View v) {
    targetString = getArguments().getString(EXTRA_PARAM_ID);
    targetImageResId = getArguments().getInt(EXTRA_PARAM_IMAGE, 0);
    largeFAB = (FloatingActionButton) v.findViewById(R.id.detail_fab);
    smallFAB = (FloatingActionButton) v.findViewById(R.id.detail_fab_small);
    title = (TextView) v.findViewById(R.id.detail_title);
    image = (ImageView) v.findViewById(R.id.detail_image);

    switch (targetString) {
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
        fragment = new WifiRadioFragment();
        powerPlanField = PowerPlanUtil.FIELD_MANAGE_WIFI;
        isReopen = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return GlobalPreferenceUtil.get().intervalDisableService().isWifiReopen();
          }
        };
        isManage = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return GlobalPreferenceUtil.get().powerManagerActive().isManagedWifi();
          }
        };
        setReopen = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.get().intervalDisableService().setWifiReopen(isState());
          }
        };
        setManage = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.get().powerManagerActive().setManagedWifi(isState());
          }
        };
        largeFABIconOn = R.drawable.ic_network_wifi_white_24dp;
        largeFABIconOff = R.drawable.ic_signal_wifi_off_white_24dp;
        smallFABIconOn = R.drawable.ic_check_white_24dp;
        smallFABIconOff = R.drawable.ic_close_white_24dp;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
        fragment = new DataRadioFragment();
        powerPlanField = PowerPlanUtil.FIELD_MANAGE_DATA;
        isReopen = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return GlobalPreferenceUtil.get().intervalDisableService().isDataReopen();
          }
        };
        isManage = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return GlobalPreferenceUtil.get().powerManagerActive().isManagedData();
          }
        };
        setReopen = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.get().intervalDisableService().setDataReopen(isState());
          }
        };
        setManage = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.get().powerManagerActive().setManagedData(isState());
          }
        };
        largeFABIconOn = R.drawable.ic_network_cell_white_24dp;
        largeFABIconOff = R.drawable.ic_signal_cellular_off_white_24dp;
        smallFABIconOn = R.drawable.ic_check_white_24dp;
        smallFABIconOff = R.drawable.ic_close_white_24dp;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
        fragment = new BluetoothRadioFragment();
        powerPlanField = PowerPlanUtil.FIELD_MANAGE_BLUETOOTH;
        isReopen = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return GlobalPreferenceUtil.get().intervalDisableService().isBluetoothReopen();
          }
        };
        isManage = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return GlobalPreferenceUtil.get().powerManagerActive().isManagedBluetooth();
          }
        };
        setReopen = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.get().intervalDisableService().setBluetoothReopen(isState());
          }
        };
        setManage = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.get().powerManagerActive().setManagedBluetooth(isState());
          }
        };
        largeFABIconOn = R.drawable.ic_bluetooth_white_24dp;
        largeFABIconOff = R.drawable.ic_bluetooth_disabled_white_24dp;
        smallFABIconOn = R.drawable.ic_check_white_24dp;
        smallFABIconOff = R.drawable.ic_close_white_24dp;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
        fragment = new SyncRadioFragment();
        powerPlanField = PowerPlanUtil.FIELD_MANAGE_SYNC;
        isReopen = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return GlobalPreferenceUtil.get().intervalDisableService().isSyncReopen();
          }
        };
        isManage = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return GlobalPreferenceUtil.get().powerManagerActive().isManagedSync();
          }
        };
        setReopen = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.get().intervalDisableService().setSyncReopen(isState());
          }
        };
        setManage = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.get().powerManagerActive().setManagedSync(isState());
          }
        };
        largeFABIconOn = R.drawable.ic_sync_white_24dp;
        largeFABIconOff = R.drawable.ic_sync_disabled_white_24dp;
        smallFABIconOn = R.drawable.ic_check_white_24dp;
        smallFABIconOff = R.drawable.ic_close_white_24dp;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_PLAN:
        fragment = new PowerPlanFragment();
        powerPlanField = FIELD_NOOP;
        isReopen = null;
        isManage = null;
        setReopen = null;
        setManage = null;
        largeFABIconOn = IMAGE_NONE;
        largeFABIconOff = IMAGE_NONE;
        smallFABIconOn = IMAGE_NONE;
        smallFABIconOff = IMAGE_NONE;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_TRIGGER:
        fragment = new PowerTriggerFragment();
        powerPlanField = FIELD_NOOP;
        isReopen = null;
        isManage = null;
        setReopen = null;
        setManage = null;
        largeFABIconOn = IMAGE_NONE;
        largeFABIconOff = IMAGE_NONE;
        smallFABIconOn = IMAGE_NONE;
        smallFABIconOff = IMAGE_NONE;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BATTERY_INFO:
        fragment = new BatteryInfoFragment();
        powerPlanField = FIELD_NOOP;
        isReopen = null;
        isManage = null;
        setReopen = null;
        setManage = null;
        largeFABIconOn = IMAGE_NONE;
        largeFABIconOff = IMAGE_NONE;
        smallFABIconOn = IMAGE_NONE;
        smallFABIconOff = IMAGE_NONE;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SETTINGS:
        fragment = new SettingsFragment();
        powerPlanField = FIELD_NOOP;
        isReopen = null;
        isManage = null;
        setReopen = null;
        setManage = null;
        largeFABIconOn = IMAGE_NONE;
        largeFABIconOff = IMAGE_NONE;
        smallFABIconOn = IMAGE_NONE;
        smallFABIconOff = IMAGE_NONE;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_HELP:
        powerPlanField = FIELD_NOOP;
        fragment = new HelpFragment();
        isReopen = null;
        isManage = null;
        setReopen = null;
        setManage = null;
        largeFABIconOn = IMAGE_NONE;
        largeFABIconOff = IMAGE_NONE;
        smallFABIconOn = IMAGE_NONE;
        smallFABIconOff = IMAGE_NONE;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_ABOUT:
        fragment = new AboutFragment();
        powerPlanField = FIELD_NOOP;
        isReopen = null;
        isManage = null;
        setReopen = null;
        setManage = null;
        largeFABIconOn = IMAGE_NONE;
        largeFABIconOff = IMAGE_NONE;
        smallFABIconOn = IMAGE_NONE;
        smallFABIconOff = IMAGE_NONE;
        break;
      default:
        fragment = null;
        powerPlanField = FIELD_NOOP;
        isReopen = null;
        isManage = null;
        setReopen = null;
        setManage = null;
        largeFABIconOn = IMAGE_NONE;
        largeFABIconOff = IMAGE_NONE;
        smallFABIconOn = IMAGE_NONE;
        smallFABIconOff = IMAGE_NONE;
    }
  }

  private void setupContentArea() {
    final int width = getActivity().getWindow().getDecorView().getMeasuredWidth();
    final int height = (int) AppUtil.convertToDP(getContext(), 240);
    Picasso.with(getContext())
        .load(targetImageResId)
        .resize(width, height)
        .into(image, new Callback() {

          @Override public void onSuccess() {
            applyStatusBarColor(false);
          }

          @Override public void onError() {
            LogUtil.e(TAG, "Picasso image load failure");
          }
        });

    title.setText(targetString);

    backgroundColor = fragment.getBackgroundColor();
    getChildFragmentManager().beginTransaction().replace(R.id.detail_content, fragment).commit();
  }

  private void setupFABMini() {
    FABBase.setupFAB(smallFAB, R.color.lightblueA200);
    smallFABBase = new FABBase(smallFAB) {

      @Override public boolean isChecked() {
        if (isReopen != null) {
          try {
            return isReopen.call();
          } catch (final Exception e) {
            return false;
          }
        } else {
          return false;
        }
      }

      @Override public void setChecked(boolean checked) {
        if (shouldShowFAB()) {
          final int image = checked ? smallFABIconOn : smallFABIconOff;
          setFabImage(image);
        } else {
          smallFAB.setVisibility(View.GONE);
        }
      }

      @Override public void startService() {
        final boolean state = !isChecked();
        if (setReopen != null) {
          setReopen.run(state);
        }

        if (powerPlanField != FIELD_NOOP) {
          final PowerPlanUtil powerPlan = PowerPlanUtil.get();
          powerPlan.updateCustomPlan(powerPlanField, state);
          powerPlan.setPlan(
              PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_CUSTOM[PowerPlanUtil.FIELD_INDEX]));
        }
      }
    };

    smallFAB.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(final View v) {
        smallFABBase.setChecked(!smallFABBase.isChecked());
        smallFABBase.startService();
      }
    });

    smallFAB.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(final View v) {
        new AlertDialog.Builder(v.getContext()).setPositiveButton("Okay",
            new DialogInterface.OnClickListener() {
              @Override public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
              }
            })
            .setTitle(StringUtil.formatString("%s Interval", targetString))
            .setMessage(
                StringUtil.formatResString(v.getContext().getResources(), R.string.interval_explain,
                    targetString, targetString))
            .create()
            .show();
        return true;
      }
    });

    smallFABBase.setChecked(smallFABBase.isChecked());
  }

  private void setupFAB() {
    FABBase.setupFAB(largeFAB, R.color.lightblueA200);
    largeFABBase = new FABBase(largeFAB) {

      @Override public boolean isChecked() {
        if (isManage != null) {
          try {
            return isManage.call();
          } catch (Exception e) {
            return false;
          }
        }
        return false;
      }

      @Override public void setChecked(boolean checked) {
        if (shouldShowFAB()) {
          final int image = checked ? largeFABIconOn : largeFABIconOff;
          setFabImage(image);
        } else {
          largeFAB.setVisibility(View.GONE);
        }
      }

      @Override public void startService() {
        final boolean state = !isChecked();
        if (setManage != null) {
          setManage.run(state);
        }
        if (powerPlanField != FIELD_NOOP) {
          final PowerPlanUtil powerPlan = PowerPlanUtil.get();
          powerPlan.updateCustomPlan(powerPlanField, state);
          powerPlan.setPlan(
              PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_CUSTOM[PowerPlanUtil.FIELD_INDEX]));
        }
      }
    };

    largeFAB.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View v) {
        largeFABBase.setChecked(!largeFABBase.isChecked());
        largeFABBase.startService();
        MonitorService.updateService(v.getContext());
      }
    });

    largeFAB.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(final View v) {
        new AlertDialog.Builder(v.getContext()).setPositiveButton("Okay",
            new DialogInterface.OnClickListener() {
              @Override public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
              }
            })
            .setTitle(StringUtil.formatString("%s Manage", targetString))
            .setMessage(
                StringUtil.formatResString(v.getContext().getResources(), R.string.manage_explain,
                    targetString, targetString))
            .create()
            .show();

        return true;
      }
    });

    largeFABBase.setChecked(largeFABBase.isChecked());
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    largeFAB.setOnClickListener(null);
    AppUtil.nullifyCallback(largeFAB);
    smallFAB.setOnClickListener(null);
    AppUtil.nullifyCallback(smallFAB);
    AppUtil.nullifyCallback(image);
  }

  @Override public void onResume() {
    super.onResume();
    listener.register(GlobalPreferenceUtil.get().powerManagerActive());
    AnimUtil.pop(largeFAB, 500, 300).start();
    AnimUtil.pop(smallFAB, 800, 300).start();

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
