package com.pyamsoft.powermanager.ui.radio;

import android.content.Context;
import com.pyamsoft.powermanager.backend.service.ActiveService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import java.lang.ref.WeakReference;

public final class RadioModel {
  public static final int NUMBER_ITEMS = 3;

  private WeakReference<Context> weakContext;

  public RadioModel(final Context context) {
    this.weakContext = new WeakReference<>(context);
  }

  public long getDelayTimeWifi() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getDelayWifi();
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  public long getDelayTimeData() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getDelayData();
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  public long getDelayTimeBluetooth() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getDelayBluetooth();
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  public long getDelayTimeSync() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getDelaySync();
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  public long getReOpenTimeWifi() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().getWifiReopenTime();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  public long getReOpenTimeData() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().getDataReopenTime();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN;
    }
  }

  public long getReOpenTimeBluetooth() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().getBluetoothReopenTime();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }
  }

  public long getReOpenTimeSync() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().getSyncReopenTime();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }
  }

  public long getIntervalTimeWifi() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getIntervalTimeWifi();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  public long getIntervalTimeData() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getIntervalTimeData();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  public long getIntervalTimeBluetooth() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getIntervalTimeBluetooth();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  public long getIntervalTimeSync() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getIntervalTimeSync();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  public void setDelayTimeWifi(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setDelayWifi(value);
      setPowerPlan(context);
    }
  }

  public void setDelayTimeData(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setDelayData(value);
      setPowerPlan(context);
    }
  }

  public void setDelayTimeBluetooth(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setDelayBluetooth(value);
      setPowerPlan(context);
    }
  }

  public void setDelayTimeSync(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setDelaySync(value);
      setPowerPlan(context);
    }
  }

  public void setIntervalTimeWifi(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setIntervalTimeWifi(value);
      setPowerPlan(context);
    }
  }

  public void setIntervalTimeData(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setIntervalTimeData(value);
      setPowerPlan(context);
    }
  }

  public void setIntervalTimeBluetooth(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setIntervalTimeBluetooth(value);
      setPowerPlan(context);
    }
  }

  public void setIntervalTimeSync(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).powerManagerActive().setIntervalTimeSync(value);
      setPowerPlan(context);
    }
  }

  public void setReOpenTimeWifi(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).intervalDisableService().setWifiReopenTime(value);
      setPowerPlan(context);
    }
  }

  public void setReOpenTimeData(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).intervalDisableService().setDataReopenTime(value);
      setPowerPlan(context);
    }
  }

  public void setReOpenTimeBluetooth(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).intervalDisableService().setBluetoothReopenTime(value);
      setPowerPlan(context);
    }
  }

  public void setReOpenTimeSync(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      GlobalPreferenceUtil.with(context).intervalDisableService().setSyncReopenTime(value);
      setPowerPlan(context);
    }
  }

  private void setPowerPlan(final Context context) {
    final PowerPlanUtil powerPlan = PowerPlanUtil.with(context);
    powerPlan.setPlan(
        PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_CUSTOM[PowerPlanUtil.FIELD_INDEX]));
  }
}
