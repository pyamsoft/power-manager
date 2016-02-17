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

package com.pyamsoft.powermanager.ui.radio;

import android.content.Context;
import com.pyamsoft.powermanager.backend.service.ActiveService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import java.lang.ref.WeakReference;

final class RadioModel {
  private static final String TAG = RadioModel.class.getSimpleName();

  private final WeakReference<Context> weakContext;

  RadioModel(final Context context) {
    this.weakContext = new WeakReference<>(context);
  }

  long getDelayTimeWifi() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getDelayWifi();
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  void setDelayTimeWifi(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set delay wifi: ", value);
      GlobalPreferenceUtil.with(context).powerManagerActive().setDelayWifi(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_DELAY_WIFI, value);
    }
  }

  long getDelayTimeData() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getDelayData();
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  void setDelayTimeData(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set delay data: ", value);
      GlobalPreferenceUtil.with(context).powerManagerActive().setDelayData(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_DELAY_DATA, value);
    }
  }

  long getDelayTimeBluetooth() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getDelayBluetooth();
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  void setDelayTimeBluetooth(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set delay bluetooth: ", value);
      GlobalPreferenceUtil.with(context).powerManagerActive().setDelayBluetooth(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_DELAY_BLUETOOTH, value);
    }
  }

  long getDelayTimeSync() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getDelaySync();
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  void setDelayTimeSync(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set delay sync: ", value);
      GlobalPreferenceUtil.with(context).powerManagerActive().setDelaySync(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_DELAY_SYNC, value);
    }
  }

  long getReOpenTimeWifi() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().getWifiReopenTime();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  void setReOpenTimeWifi(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set reopen wifi: ", value);
      GlobalPreferenceUtil.with(context).intervalDisableService().setWifiReopenTime(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_REOPEN_TIME_WIFI, value);
    }
  }

  long getReOpenTimeData() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().getDataReopenTime();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN;
    }
  }

  void setReOpenTimeData(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set reopen data: ", value);
      GlobalPreferenceUtil.with(context).intervalDisableService().setDataReopenTime(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_REOPEN_TIME_DATA, value);
    }
  }

  long getReOpenTimeBluetooth() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().getBluetoothReopenTime();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }
  }

  void setReOpenTimeBluetooth(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set reopen bluetooth: ", value);
      GlobalPreferenceUtil.with(context).intervalDisableService().setBluetoothReopenTime(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_REOPEN_TIME_BLUETOOTH, value);
    }
  }

  long getReOpenTimeSync() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).intervalDisableService().getSyncReopenTime();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }
  }

  void setReOpenTimeSync(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set reopen sync: ", value);
      GlobalPreferenceUtil.with(context).intervalDisableService().setSyncReopenTime(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_REOPEN_TIME_SYNC, value);
    }
  }

  long getIntervalTimeWifi() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getIntervalTimeWifi();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  void setIntervalTimeWifi(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set interval wifi: ", value);
      GlobalPreferenceUtil.with(context).powerManagerActive().setIntervalTimeWifi(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_INTERVAL_TIME_WIFI, value);
    }
  }

  long getIntervalTimeData() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getIntervalTimeData();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  void setIntervalTimeData(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set interval data: ", value);
      GlobalPreferenceUtil.with(context).powerManagerActive().setIntervalTimeData(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_INTERVAL_TIME_DATA, value);
    }
  }

  long getIntervalTimeBluetooth() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getIntervalTimeBluetooth();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  void setIntervalTimeBluetooth(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set interval bluetooth: ", value);
      GlobalPreferenceUtil.with(context).powerManagerActive().setIntervalTimeBluetooth(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_INTERVAL_TIME_BLUETOOTH, value);
    }
  }

  long getIntervalTimeSync() {
    final Context context = weakContext.get();
    if (context != null) {
      return GlobalPreferenceUtil.with(context).powerManagerActive().getIntervalTimeSync();
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  void setIntervalTimeSync(long value) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set interval sync: ", value);
      GlobalPreferenceUtil.with(context).powerManagerActive().setIntervalTimeSync(value);
      setPowerPlan(context, PowerPlanUtil.FIELD_INTERVAL_TIME_SYNC, value);
    }
  }

  private void setPowerPlan(final Context context, final int field, final long value) {
    LogUtil.d(TAG, "Refreshing power plan");
    final PowerPlanUtil powerPlan = PowerPlanUtil.with(context);
    powerPlan.updateCustomPlan(field, value);
    powerPlan.setPlan(
        PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_CUSTOM[PowerPlanUtil.FIELD_INDEX]));
  }
}
