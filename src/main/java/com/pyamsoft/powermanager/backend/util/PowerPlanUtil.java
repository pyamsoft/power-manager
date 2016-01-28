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
package com.pyamsoft.powermanager.backend.util;

import android.content.Context;
import com.pyamsoft.powermanager.backend.receiver.BootActionReceiver;
import com.pyamsoft.powermanager.backend.service.ActiveService;
import com.pyamsoft.pydroid.util.LogUtil;

public final class PowerPlanUtil {

  public static final int FIELD_INDEX = 0;
  public static final int FIELD_NAME = 1;
  public static final int FIELD_MANAGE_WIFI = 2;
  public static final int FIELD_MANAGE_DATA = 3;
  public static final int FIELD_MANAGE_BLUETOOTH = 4;
  public static final int FIELD_MANAGE_SYNC = 5;
  public static final int FIELD_DELAY_WIFI = 6;
  public static final int FIELD_DELAY_DATA = 7;
  public static final int FIELD_DELAY_BLUETOOTH = 8;
  public static final int FIELD_DELAY_SYNC = 9;
  public static final int FIELD_INTERVAL_TIME_WIFI = 10;
  public static final int FIELD_INTERVAL_TIME_DATA = 11;
  public static final int FIELD_INTERVAL_TIME_BLUETOOTH = 12;
  public static final int FIELD_INTERVAL_TIME_SYNC = 13;
  public static final int FIELD_REOPEN_WIFI = 14;
  public static final int FIELD_REOPEN_DATA = 15;
  public static final int FIELD_REOPEN_BLUETOOTH = 16;
  public static final int FIELD_REOPEN_SYNC = 17;
  public static final int FIELD_REOPEN_TIME_WIFI = 18;
  public static final int FIELD_REOPEN_TIME_DATA = 19;
  public static final int FIELD_REOPEN_TIME_BLUETOOTH = 20;
  public static final int FIELD_REOPEN_TIME_SYNC = 21;
  public static final int FIELD_MISC_BOOT = 22;
  public static final int FIELD_MISC_SUSPEND = 23;
  private static final int PLAN_ELEMENT_SIZE = 24;
  public static final Object[] POWER_PLAN_CUSTOM = new Object[PLAN_ELEMENT_SIZE];
  public static final Object[] POWER_PLAN_STANDARD = {
      // index
      1,
      // name
      "Standard",
      // active
      // manage
      true, false, false, true,
      // delay
      ActiveService.Constants.DELAY_RADIO_FIFTEEN, ActiveService.Constants.DELAY_RADIO_SIXTY,
      ActiveService.Constants.DELAY_RADIO_SIXTY, ActiveService.Constants.DELAY_RADIO_FIFTEEN,
      // interval time
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      // reopen
      true, false, false, true,
      // reopen time
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
      // misc
      // boot
      false,
      // suspend
      true,
  };
  private static final Object[] POWER_PLAN_SUPER = {
      // index
      2,
      // name
      "Super Saver",
      // manage
      true, true, false, true,
      // delay
      ActiveService.Constants.DELAY_RADIO_FIFTEEN, ActiveService.Constants.DELAY_RADIO_FIFTEEN,
      ActiveService.Constants.DELAY_RADIO_SIXTY, ActiveService.Constants.DELAY_RADIO_FIFTEEN,
      // interval time
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      // reopen
      true, false, false, true,
      // reopen time
      ActiveService.Constants.INTERVAL_REOPEN_FOURTYFIVE,
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
      ActiveService.Constants.INTERVAL_REOPEN_NINTY,
      // misc
      // boot
      false,
      // suspend
      true,
  };
  private static final Object[] POWER_PLAN_ULTRA = {
      // index
      3,
      // name
      "Ultraconservative",
      // manage
      true, true, true, true,
      // delay
      ActiveService.Constants.DELAY_RADIO_TEN, ActiveService.Constants.DELAY_RADIO_TEN,
      ActiveService.Constants.DELAY_RADIO_TEN, ActiveService.Constants.DELAY_RADIO_TEN,
      // interval time
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      // reopen
      true, false, false, true,
      // reopen time
      ActiveService.Constants.INTERVAL_REOPEN_THIRTY,
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      // misc
      // boot
      false,
      // suspend
      false,
  };
  private static final Object[] POWER_PLAN_NOMERCY = {
      // index
      4,
      // name
      "No Mercy",
      // manage
      true, true, false, true,
      // delay
      ActiveService.Constants.DELAY_RADIO_NONE, ActiveService.Constants.DELAY_RADIO_NONE,
      ActiveService.Constants.DELAY_RADIO_SIXTY, ActiveService.Constants.DELAY_RADIO_NONE,
      // interval time
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      // reopen
      false, false, false, false,
      // reopen time
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      // misc
      // boot
      false,
      // suspend
      true,
  };
  private static final Object[] POWER_PLAN_FLAT = {
      // index
      5,
      // name
      "Flat Zone",
      // manage
      true, true, true, true,
      // delay
      ActiveService.Constants.DELAY_RADIO_THIRTY, ActiveService.Constants.DELAY_RADIO_THIRTY,
      ActiveService.Constants.DELAY_RADIO_THIRTY, ActiveService.Constants.DELAY_RADIO_THIRTY,
      // interval time
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      // reopen
      true, true, true, true,
      // reopen time
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      // misc
      // boot
      false,
      // suspend
      true,
  };
  private static final Object[] POWER_PLAN_COOL = {
      // index
      6,
      // name
      "The Cool Parent",
      // manage
      true, false, false, true,
      // delay
      ActiveService.Constants.DELAY_RADIO_SIXTY, ActiveService.Constants.DELAY_RADIO_SIXTY,
      ActiveService.Constants.DELAY_RADIO_SIXTY, ActiveService.Constants.DELAY_RADIO_SIXTY,
      // interval time
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      // reopen
      true, false, false, true,
      // reopen time
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
      // misc
      // boot
      false,
      // suspend
      true,
  };
  private static final Object[] POWER_PLAN_NEED = {
      // index
      7,
      // name
      "I Need This Email",
      // manage
      false, true, true, false,
      // delay
      ActiveService.Constants.DELAY_RADIO_SIXTY, ActiveService.Constants.DELAY_RADIO_NONE,
      ActiveService.Constants.DELAY_RADIO_NONE, ActiveService.Constants.DELAY_RADIO_SIXTY,
      // interval time
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      // reopen
      true, false, false, true,
      // reopen time
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
      // misc
      // boot
      false,
      // suspend
      true,
  };
  private static final Object[] POWER_PLAN_WHY = {
      // index
      8,
      // name
      "Why Are You Using This",
      // manage
      false, false, false, false,
      // delay
      ActiveService.Constants.DELAY_RADIO_NONE, ActiveService.Constants.DELAY_RADIO_NONE,
      ActiveService.Constants.DELAY_RADIO_NONE, ActiveService.Constants.DELAY_RADIO_NONE,
      // interval time
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_SIXTY,
      // reopen
      false, false, false, false,
      // reopen time
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      // misc
      // boot
      false,
      // suspend
      false,
  };
  private static final String TAG = PowerPlanUtil.class.getSimpleName();
  private static PowerPlanUtil instance = null;
  private Context context;

  private PowerPlanUtil(final Context context) {
    LogUtil.d(TAG, "Initialized PowerPlanUtil");
    POWER_PLAN_CUSTOM[FIELD_INDEX] = 0;
    POWER_PLAN_CUSTOM[FIELD_NAME] = "Custom";
    this.context = context.getApplicationContext();
    updateCustomPlanToCurrent();
  }

  public static String toString(final Object o) {
    if (o instanceof String) {
      return (String) o;
    }
    throw new RuntimeException("Can't cast object to String");
  }

  public static Long toLong(final Object o) {
    if (o instanceof Long) {
      return (Long) o;
    }
    throw new RuntimeException("Can't cast object to Long");
  }

  public static Boolean toBoolean(final Object o) {
    if (o instanceof Boolean) {
      return (Boolean) o;
    }
    throw new RuntimeException("Can't cast object to Boolean");
  }

  public static Integer toInt(final Object o) {
    if (o instanceof Integer) {
      return (Integer) o;
    }
    throw new RuntimeException("Can't cast object to Integer");
  }

  public static PowerPlanUtil with(final Context context) {
    if (instance == null) {
      synchronized (PowerPlanUtil.class) {
        if (instance == null) {
          instance = new PowerPlanUtil(context);
        }
      }
    }
    return instance;
  }

  public final Object[] getPowerPlan(final int index) {
    Object[] plan;
    switch (index) {
      case 1:
        plan = POWER_PLAN_STANDARD;
        break;
      case 2:
        plan = POWER_PLAN_SUPER;
        break;
      case 3:
        plan = POWER_PLAN_ULTRA;
        break;
      case 4:
        plan = POWER_PLAN_NOMERCY;
        break;
      case 5:
        plan = POWER_PLAN_FLAT;
        break;
      case 6:
        plan = POWER_PLAN_COOL;
        break;
      case 7:
        plan = POWER_PLAN_NEED;
        break;
      case 8:
        plan = POWER_PLAN_WHY;
        break;
      default:
        plan = POWER_PLAN_CUSTOM;
        break;
    }
    return plan;
  }

  public final void setPlan(final int plan) {
    final Object[] powerPlan = getPowerPlan(plan);
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.with(context);

    final GlobalPreferenceUtil.PowerManagerActive active = preferenceUtil.powerManagerActive();
    active.setManagedWifi(toBoolean(powerPlan[FIELD_MANAGE_WIFI]));
    active.setManagedData(toBoolean(powerPlan[FIELD_MANAGE_DATA]));
    active.setManagedBluetooth(toBoolean(powerPlan[FIELD_MANAGE_BLUETOOTH]));
    active.setManagedSync(toBoolean(powerPlan[FIELD_MANAGE_SYNC]));

    active.setDelayWifi(toLong(powerPlan[FIELD_DELAY_WIFI]));
    active.setDelayData(toLong(powerPlan[FIELD_DELAY_DATA]));
    active.setDelayBluetooth(toLong(powerPlan[FIELD_DELAY_BLUETOOTH]));
    active.setDelaySync(toLong(powerPlan[FIELD_DELAY_SYNC]));

    active.setIntervalTimeWifi(toLong(powerPlan[FIELD_INTERVAL_TIME_WIFI]));
    active.setIntervalTimeData(toLong(powerPlan[FIELD_INTERVAL_TIME_DATA]));
    active.setIntervalTimeBluetooth(toLong(powerPlan[FIELD_INTERVAL_TIME_BLUETOOTH]));
    active.setIntervalTimeSync(toLong(powerPlan[FIELD_INTERVAL_TIME_SYNC]));
    active.setSuspendPlugged(toBoolean(powerPlan[FIELD_MISC_SUSPEND]));

    final GlobalPreferenceUtil.IntervalDisableService interval =
        preferenceUtil.intervalDisableService();
    interval.setWifiReopen(toBoolean(powerPlan[FIELD_REOPEN_WIFI]));
    interval.setDataReopen(toBoolean(powerPlan[FIELD_REOPEN_DATA]));
    interval.setBluetoothReopen(toBoolean(powerPlan[FIELD_REOPEN_BLUETOOTH]));
    interval.setSyncReopen(toBoolean(powerPlan[FIELD_REOPEN_SYNC]));

    interval.setWifiReopenTime(toLong(powerPlan[FIELD_REOPEN_TIME_WIFI]));
    interval.setDataReopenTime(toLong(powerPlan[FIELD_REOPEN_TIME_DATA]));
    interval.setBluetoothReopenTime(toLong(powerPlan[FIELD_REOPEN_TIME_BLUETOOTH]));
    interval.setSyncReopenTime(toLong(powerPlan[FIELD_REOPEN_TIME_SYNC]));

    BootActionReceiver.setBootEnabled(context, toBoolean(powerPlan[FIELD_MISC_BOOT]));

    preferenceUtil.powerPlans().setActivePlan(toInt(powerPlan[FIELD_INDEX]));

    if (plan != toInt(POWER_PLAN_CUSTOM[FIELD_INDEX])) {
      updateCustomPlanToCurrent();
    }
  }

  private void updateCustomPlanToCurrent() {
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.with(context);
    final GlobalPreferenceUtil.PowerManagerActive active = preferenceUtil.powerManagerActive();

    POWER_PLAN_CUSTOM[FIELD_MANAGE_WIFI] = active.isManagedWifi();
    POWER_PLAN_CUSTOM[FIELD_MANAGE_DATA] = active.isManagedData();
    POWER_PLAN_CUSTOM[FIELD_MANAGE_BLUETOOTH] = active.isManagedBluetooth();
    POWER_PLAN_CUSTOM[FIELD_MANAGE_SYNC] = active.isManagedSync();

    POWER_PLAN_CUSTOM[FIELD_DELAY_WIFI] = active.getDelayWifi();
    POWER_PLAN_CUSTOM[FIELD_DELAY_DATA] = active.getDelayData();
    POWER_PLAN_CUSTOM[FIELD_DELAY_BLUETOOTH] = active.getDelayBluetooth();
    POWER_PLAN_CUSTOM[FIELD_DELAY_SYNC] = active.getDelaySync();

    POWER_PLAN_CUSTOM[FIELD_INTERVAL_TIME_WIFI] = active.getIntervalTimeWifi();
    POWER_PLAN_CUSTOM[FIELD_INTERVAL_TIME_DATA] = active.getIntervalTimeData();
    POWER_PLAN_CUSTOM[FIELD_INTERVAL_TIME_BLUETOOTH] = active.getIntervalTimeBluetooth();
    POWER_PLAN_CUSTOM[FIELD_INTERVAL_TIME_SYNC] = active.getIntervalTimeSync();
    POWER_PLAN_CUSTOM[FIELD_MISC_SUSPEND] = active.isSuspendPlugged();

    final GlobalPreferenceUtil.IntervalDisableService interval =
        preferenceUtil.intervalDisableService();
    POWER_PLAN_CUSTOM[FIELD_REOPEN_WIFI] = interval.isWifiReopen();
    POWER_PLAN_CUSTOM[FIELD_REOPEN_DATA] = interval.isDataReopen();
    POWER_PLAN_CUSTOM[FIELD_REOPEN_BLUETOOTH] = interval.isBluetoothReopen();
    POWER_PLAN_CUSTOM[FIELD_REOPEN_SYNC] = interval.isSyncReopen();

    POWER_PLAN_CUSTOM[FIELD_REOPEN_TIME_WIFI] = interval.getWifiReopenTime();
    POWER_PLAN_CUSTOM[FIELD_REOPEN_TIME_DATA] = interval.getDataReopenTime();
    POWER_PLAN_CUSTOM[FIELD_REOPEN_TIME_BLUETOOTH] = interval.getBluetoothReopenTime();
    POWER_PLAN_CUSTOM[FIELD_REOPEN_TIME_SYNC] = interval.getSyncReopenTime();

    POWER_PLAN_CUSTOM[FIELD_MISC_BOOT] = BootActionReceiver.isBootEnabled(context);
  }

  public final void updateCustomPlan(final int field, final Object value) {
    POWER_PLAN_CUSTOM[field] = value;
  }
}
