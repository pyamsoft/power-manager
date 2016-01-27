package com.pyamsoft.powermanager.ui.plan;

import android.content.Context;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;

public final class PowerPlanModel {

  private static final String TAG = PowerPlanModel.class.getSimpleName();
  private Context context;
  private final Object[] plan;
  private final int index;

  public PowerPlanModel(Context context, int position) {
    LogUtil.d(TAG, "Create new PowerPlanModel");
    this.context = context.getApplicationContext();
    plan = PowerPlanUtil.with(context).getPowerPlan(position);
    index = PowerPlanUtil.toInt(plan[PowerPlanUtil.FIELD_INDEX]);
  }

  public String getName() {
    return PowerPlanUtil.toString(plan[PowerPlanUtil.FIELD_NAME]);
  }

  public int getIndex() {
    return index;
  }

  public boolean isWifiManaged() {
    return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_WIFI]);
  }

  public boolean isDataManaged() {
    return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_DATA]);
  }

  public boolean isBluetoothManaged() {
    return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_BLUETOOTH]);
  }

  public boolean isSyncManaged() {
    return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_SYNC]);
  }

  public void setActivePlan() {
    LogUtil.d(TAG, "setActivePlan: ", index);
    PowerPlanUtil.with(context).setPlan(index);
    PersistentNotification.update(context);
  }

  public boolean isActivePlan() {
    final int activeIndex = GlobalPreferenceUtil.with(context).powerPlans().getActivePlan();
    return activeIndex == index;
  }

  public long getWifiDelay() {
    return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_WIFI]);
  }

  public long getDataDelay() {
    return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_DATA]);
  }

  public long getBluetoothDelay() {
    return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_BLUETOOTH]);
  }

  public long getSyncDelay() {
    return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_SYNC]);
  }

  public boolean isBootEnabled() {
    return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MISC_BOOT]);
  }

  public boolean isSuspendEnabled() {
    return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MISC_SUSPEND]);
  }

  public boolean isWifiReOpenEnabled() {
    return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_WIFI]);
  }

  public boolean isDataReOpenEnabled() {
    return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_DATA]);
  }

  public boolean isBluetoothReOpenEnabled() {
    return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_BLUETOOTH]);
  }

  public boolean isSyncReOpenEnabled() {
    return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_SYNC]);
  }

  public long getWifiReOpenTime() {
    return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_WIFI]);
  }

  public long getDataReOpenTime() {
    return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_DATA]);
  }

  public long getBluetoothReOpenTime() {
    return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_BLUETOOTH]);
  }

  public long getSyncReOpenTime() {
    return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_SYNC]);
  }
}
