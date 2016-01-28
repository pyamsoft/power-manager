package com.pyamsoft.powermanager.ui.detail;

import android.content.Context;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import java.lang.ref.WeakReference;

public final class DetailModel {

  private static final String TAG = DetailModel.class.getSimpleName();
  private final WeakReference<Context> weakContext;

  public DetailModel(final Context c) {
    weakContext = new WeakReference<>(c.getApplicationContext());
  }

  private void setPowerPlan(final Context context, final int field, final boolean value) {
    LogUtil.d(TAG, "Refreshing power plan");
    final PowerPlanUtil powerPlan = PowerPlanUtil.with(context);
    powerPlan.updateCustomPlan(field, value);
    powerPlan.setPlan(
        PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_CUSTOM[PowerPlanUtil.FIELD_INDEX]));
  }

  public void setWifiReOpen(final boolean isChecked) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).intervalDisableService().setWifiReopen(isChecked);
      setPowerPlan(context, PowerPlanUtil.FIELD_REOPEN_WIFI, isChecked);
    }
  }

  public void setDataReOpen(final boolean isChecked) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).intervalDisableService().setDataReopen(isChecked);
      setPowerPlan(context, PowerPlanUtil.FIELD_REOPEN_DATA, isChecked);
    }
  }

  public void setBluetoothReOpen(final boolean isChecked) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).intervalDisableService().setBluetoothReopen(isChecked);
      setPowerPlan(context, PowerPlanUtil.FIELD_REOPEN_BLUETOOTH, isChecked);
    }
  }

  public void setSyncReOpen(final boolean isChecked) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).intervalDisableService().setSyncReopen(isChecked);
      setPowerPlan(context, PowerPlanUtil.FIELD_REOPEN_SYNC, isChecked);
    }
  }

  @SuppressWarnings("SimplifiableIfStatement") public boolean isWifiReOpen() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().isWifiReopen();
    } else {
      return true;
    }
  }

  @SuppressWarnings("SimplifiableIfStatement") public boolean isDataReOpen() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().isDataReopen();
    } else {
      return false;
    }
  }

  @SuppressWarnings("SimplifiableIfStatement") public boolean isBluetoothReOpen() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().isBluetoothReopen();
    } else {
      return false;
    }
  }

  @SuppressWarnings("SimplifiableIfStatement") public boolean isSyncReOpen() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().isSyncReopen();
    } else {
      return true;
    }
  }

  @SuppressWarnings("SimplifiableIfStatement") public boolean isWifiManaged() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().isManagedWifi();
    } else {
      return true;
    }
  }

  @SuppressWarnings("SimplifiableIfStatement") public boolean isDataManaged() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().isManagedData();
    } else {
      return false;
    }
  }

  @SuppressWarnings("SimplifiableIfStatement") public boolean isBluetoothManaged() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().isManagedBluetooth();
    } else {
      return false;
    }
  }

  @SuppressWarnings("SimplifiableIfStatement") public boolean isSyncManaged() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().isManagedSync();
    } else {
      return true;
    }
  }

  public void setWifiManaged(final boolean isChecked) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setManagedWifi(isChecked);
      setPowerPlan(context, PowerPlanUtil.FIELD_MANAGE_WIFI, isChecked);
    }
  }

  public void setDataManaged(final boolean isChecked) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setManagedData(isChecked);
      setPowerPlan(context, PowerPlanUtil.FIELD_MANAGE_DATA, isChecked);
    }
  }

  public void setBluetoothManaged(final boolean isChecked) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setManagedBluetooth(isChecked);
      setPowerPlan(context, PowerPlanUtil.FIELD_MANAGE_BLUETOOTH, isChecked);
    }
  }

  public void setSyncManaged(final boolean isChecked) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setManagedSync(isChecked);
      setPowerPlan(context, PowerPlanUtil.FIELD_MANAGE_SYNC, isChecked);
    }
  }
}
