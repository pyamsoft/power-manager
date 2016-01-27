package com.pyamsoft.powermanager.ui.plan;

import android.content.Context;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.service.ActiveService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import java.lang.ref.WeakReference;

public final class PowerPlanModel {

  private static final String TAG = PowerPlanModel.class.getSimpleName();
  private final WeakReference<Context> weakContext;

  public PowerPlanModel(Context context) {
    LogUtil.d(TAG, "Create new PowerPlanModel");
    weakContext = new WeakReference<>(context.getApplicationContext());
  }

  public String getName(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toString(plan[PowerPlanUtil.FIELD_NAME]);
    } else {
      return null;
    }
  }

  public int getIndex(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      final int index = PowerPlanUtil.toInt(plan[PowerPlanUtil.FIELD_INDEX]);
      return index;
    } else {
      return PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_STANDARD[PowerPlanUtil.FIELD_INDEX]);
    }
  }

  public boolean isWifiManaged(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_WIFI]);
    } else {
      return false;
    }
  }

  public boolean isDataManaged(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_DATA]);
    } else {
      return false;
    }
  }

  public boolean isBluetoothManaged(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_BLUETOOTH]);
    } else {
      return false;
    }
  }

  public boolean isSyncManaged(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_SYNC]);
    } else {
      return false;
    }
  }

  public void setActivePlan(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      final int index = PowerPlanUtil.toInt(plan[PowerPlanUtil.FIELD_INDEX]);
      LogUtil.d(TAG, "setActivePlan: ", index);
      PowerPlanUtil.with(context).setPlan(index);
      PersistentNotification.update(context);
    }
  }

  public boolean isActivePlan(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      final int index = PowerPlanUtil.toInt(plan[PowerPlanUtil.FIELD_INDEX]);
      final int activeIndex = GlobalPreferenceUtil.with(context).powerPlans().getActivePlan();
      return activeIndex == index;
    } else {
      return false;
    }
  }

  public long getWifiDelay(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_WIFI]);
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  public long getDataDelay(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_DATA]);
    } else {
      return ActiveService.Constants.DELAY_RADIO_SIXTY;
    }
  }

  public long getBluetoothDelay(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_BLUETOOTH]);
    } else {
      return ActiveService.Constants.DELAY_RADIO_SIXTY;
    }
  }

  public long getSyncDelay(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_SYNC]);
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  public boolean isBootEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MISC_BOOT]);
    } else {
      return false;
    }
  }

  public boolean isSuspendEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MISC_SUSPEND]);
    } else {
      return false;
    }
  }

  public boolean isWifiReOpenEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_WIFI]);
    } else {
      return false;
    }
  }

  public boolean isDataReOpenEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_DATA]);
    } else {
      return false;
    }
  }

  public boolean isBluetoothReOpenEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_BLUETOOTH]);
    } else {
      return false;
    }
  }

  public boolean isSyncReOpenEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_SYNC]);
    } else {
      return false;
    }
  }

  public long getWifiReOpenTime(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_WIFI]);
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  public long getDataReOpenTime(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_DATA]);
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN;
    }
  }

  public long getBluetoothReOpenTime(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_BLUETOOTH]);
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }
  }

  public long getSyncReOpenTime(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_SYNC]);
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }
  }
}
