package com.pyamsoft.powermanager.ui.detail;

import android.content.Context;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.ui.BooleanRunnable;
import com.pyamsoft.pydroid.util.LogUtil;
import java.util.concurrent.Callable;

public final class DetailModel {

  private static final String TAG = DetailModel.class.getSimpleName();
  private final Context context;
  private int type;

  public static final int FAB_TYPE_SMALL = 0;
  public static final int FAB_TYPE_LARGE = 1;

  private BooleanRunnable setProp;
  private Callable<Boolean> getProp;

  public DetailModel(final Context c, final String target, final int type) {
    context = c.getApplicationContext();
    this.type = type;
    switch (type) {
      case FAB_TYPE_SMALL:
        LogUtil.d(TAG, "Initialize model for small FAB");
        initializeSmallFAB(context, target);
        break;
      case FAB_TYPE_LARGE:
        LogUtil.d(TAG, "Initialize model for large FAB");
        initializeLargeFAB(context, target);
        break;
      default:
        // Ignored
    }
  }

  // TODO
  private void initializeSmallFAB(final Context context, final String target) {
    LogUtil.d(TAG, "Initialize model for: ", target);
    switch (target) {
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
        setProp = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.with(context).intervalDisableService().setWifiReopen(isState());
          }
        };
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() {
            return GlobalPreferenceUtil.with(context).intervalDisableService().isWifiReopen();
          }
        };
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
        setProp = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.with(context).intervalDisableService().setDataReopen(isState());
          }
        };
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() {
            return GlobalPreferenceUtil.with(context).intervalDisableService().isDataReopen();
          }
        };
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
        setProp = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.with(context)
                .intervalDisableService()
                .setBluetoothReopen(isState());
          }
        };
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() {
            return GlobalPreferenceUtil.with(context).intervalDisableService().isBluetoothReopen();
          }
        };
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
        setProp = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.with(context).intervalDisableService().setSyncReopen(isState());
          }
        };
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() {
            return GlobalPreferenceUtil.with(context).intervalDisableService().isSyncReopen();
          }
        };
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_PLAN:
        setProp = null;
        getProp = null;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_TRIGGER:
        setProp = null;
        getProp = null;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BATTERY_INFO:
        setProp = null;
        getProp = null;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SETTINGS:
        setProp = null;
        getProp = null;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_HELP:
        setProp = null;
        getProp = null;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_ABOUT:
        setProp = null;
        getProp = null;
        break;
      default:
        setProp = null;
        getProp = null;
    }
  }

  private void initializeLargeFAB(final Context context, final String target) {
    LogUtil.d(TAG, "Initialize model for: ", target);
    switch (target) {
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
        setProp = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.with(context).powerManagerActive().setManagedWifi(isState());
          }
        };
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() {
            return GlobalPreferenceUtil.with(context).powerManagerActive().isManagedWifi();
          }
        };
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
        setProp = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.with(context).powerManagerActive().setManagedData(isState());
          }
        };
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() {
            return GlobalPreferenceUtil.with(context).powerManagerActive().isManagedData();
          }
        };
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
        setProp = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.with(context).powerManagerActive().setManagedBluetooth(isState());
          }
        };
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() {
            return GlobalPreferenceUtil.with(context).powerManagerActive().isManagedBluetooth();
          }
        };
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
        setProp = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.with(context).powerManagerActive().setManagedSync(isState());
          }
        };
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() {
            return GlobalPreferenceUtil.with(context).powerManagerActive().isManagedSync();
          }
        };
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_PLAN:
        setProp = null;
        getProp = null;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_TRIGGER:
        setProp = null;
        getProp = null;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BATTERY_INFO:
        setProp = null;
        getProp = null;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SETTINGS:
        setProp = null;
        getProp = null;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_HELP:
        setProp = null;
        getProp = null;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_ABOUT:
        setProp = null;
        getProp = null;
        break;
      default:
        setProp = null;
        getProp = null;
    }
  }

  public boolean isFABChecked() {
    if (getProp != null) {
      try {
        final boolean checked = getProp.call();
        LogUtil.d(TAG, "FAB is checked: ", checked);
        return checked;
      } catch (Exception ignored) {
      }
    }
    return false;
  }

  public void setFABChecked(final boolean checked) {
    if (setProp != null) {
      LogUtil.d(TAG, "FAB set checked: ", checked);
      setProp.run(checked);
      if (type == FAB_TYPE_LARGE) {
        PersistentNotification.update(context);
      }
    }
  }
}
