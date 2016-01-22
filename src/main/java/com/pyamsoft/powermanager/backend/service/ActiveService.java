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
package com.pyamsoft.powermanager.backend.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.util.Pools;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.manager.ManagerBase;
import com.pyamsoft.powermanager.backend.manager.ManagerBluetooth;
import com.pyamsoft.powermanager.backend.manager.ManagerData;
import com.pyamsoft.powermanager.backend.manager.ManagerSync;
import com.pyamsoft.powermanager.backend.manager.ManagerWifi;
import com.pyamsoft.powermanager.backend.util.BatteryUtil;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.pydroid.util.LogUtil;

public final class ActiveService extends IntentService {

  private static final String TAG = ActiveService.class.getSimpleName();

  public ActiveService() {
    super(TAG);
  }

  private static void setPowerManagerIntent(final Intent intent, final Context context,
      final boolean start) {
    intent.setClass(context.getApplicationContext(), ActiveService.class);
    intent.putExtra(ActiveService.Constants.RUN_FIELD,
        start ? ActiveService.Constants.RUN_TRUE : ActiveService.Constants.RUN_FALSE);
  }

  private static void setIntervalDisableIntent(final Intent intent, final Context context,
      final Class<? extends ManagerBase.Interval> cls) {
    intent.setClass(context.getApplicationContext(), cls);
  }

  public static void startService(final Context con, final boolean start) {
    final Context c = con.getApplicationContext();
    final Intent pm = IntentPool.acquire();
    setPowerManagerIntent(pm, con, start);
    c.startService(pm);
    IntentPool.release(pm);
  }

  private static void disableWifi(final Context context, final boolean isCharging) {
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.get();
    final boolean controlled =
        disable(context, ManagerWifi.get(), preferenceUtil.powerManagerActive().isManagedWifi(),
            isCharging, preferenceUtil.powerManagerActive().getDelayWifi());
    preferenceUtil.powerManagerActive().setControlledWifi(controlled);
    final Intent disable = IntentPool.acquire();
    setIntervalDisableIntent(disable, context, ManagerWifi.Interval.class);
    setDisableAlarm(context, disable, controlled);
    IntentPool.release(disable);
  }

  private static void disableData(final Context context, final boolean isCharging) {
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.get();
    final boolean controlled =
        disable(context, ManagerData.get(), preferenceUtil.powerManagerActive().isManagedData(),
            isCharging, preferenceUtil.powerManagerActive().getDelayData());
    preferenceUtil.powerManagerActive().setControlledData(controlled);
    final Intent disable = IntentPool.acquire();
    setIntervalDisableIntent(disable, context, ManagerData.Interval.class);
    setDisableAlarm(context, disable, controlled);
    IntentPool.release(disable);
  }

  private static void disableBluetooth(final Context context, final boolean isCharging) {
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.get();
    final boolean controlled = disable(context, ManagerBluetooth.get(),
        preferenceUtil.powerManagerActive().isManagedBluetooth(), isCharging,
        preferenceUtil.powerManagerActive().getDelayBluetooth());
    preferenceUtil.powerManagerActive().setControlledBluetooth(controlled);
    final Intent disable = IntentPool.acquire();
    setIntervalDisableIntent(disable, context, ManagerBluetooth.Interval.class);
    setDisableAlarm(context, disable, controlled);
    IntentPool.release(disable);
  }

  private static void disableSync(final Context context, final boolean isCharging) {
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.get();
    final boolean controlled =
        disable(context, ManagerSync.get(), preferenceUtil.powerManagerActive().isManagedSync(),
            isCharging, preferenceUtil.powerManagerActive().getDelaySync());
    preferenceUtil.powerManagerActive().setControlledSync(controlled);
    final Intent disable = IntentPool.acquire();
    setIntervalDisableIntent(disable, context, ManagerSync.Interval.class);
    setDisableAlarm(context, disable, controlled);
    IntentPool.release(disable);
  }

  private static void enableSync(final Context context) {
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.get();
    enable(context, ManagerSync.get(), preferenceUtil.powerManagerActive().isManagedSync(),
        preferenceUtil.powerManagerActive().isControlledSync());
    preferenceUtil.powerManagerActive().setControlledSync(false);
    final Intent disable = IntentPool.acquire();
    setIntervalDisableIntent(disable, context, ManagerSync.Interval.class);
    cancelDisableAlarm(context, disable);
    IntentPool.release(disable);
  }

  private static void enableBluetooth(final Context context) {
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.get();
    enable(context, ManagerBluetooth.get(),
        preferenceUtil.powerManagerActive().isManagedBluetooth(),
        preferenceUtil.powerManagerActive().isControlledBluetooth());
    preferenceUtil.powerManagerActive().setControlledBluetooth(false);
    final Intent disable = IntentPool.acquire();
    setIntervalDisableIntent(disable, context, ManagerBluetooth.Interval.class);
    cancelDisableAlarm(context, disable);
    IntentPool.release(disable);
  }

  private static void enableData(final Context context) {
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.get();
    enable(context, ManagerData.get(), preferenceUtil.powerManagerActive().isManagedData(),
        preferenceUtil.powerManagerActive().isControlledData());
    preferenceUtil.powerManagerActive().setControlledData(false);
    final Intent disable = IntentPool.acquire();
    setIntervalDisableIntent(disable, context, ManagerData.Interval.class);
    cancelDisableAlarm(context, disable);
    IntentPool.release(disable);
  }

  private static void enableWifi(final Context context) {
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.get();
    enable(context, ManagerWifi.get(), preferenceUtil.powerManagerActive().isManagedWifi(),
        preferenceUtil.powerManagerActive().isControlledWifi());
    preferenceUtil.powerManagerActive().setControlledWifi(false);
    final Intent disable = IntentPool.acquire();
    setIntervalDisableIntent(disable, context, ManagerWifi.Interval.class);
    cancelDisableAlarm(context, disable);
    IntentPool.release(disable);
  }

  private static void enable(final Context context, final ManagerBase manager,
      final boolean isManaged, final boolean isControlled) {
    if (isManaged && isControlled) {
      LogUtil.d(TAG, manager.getTag(), context.getString(R.string.enable_radio_in));
      manager.enable(0);
    } else {
      LogUtil.d(TAG, manager.getTag(), context.getString(R.string.radio_not_managed));
    }
  }

  private static boolean disable(final Context context, final ManagerBase manager,
      final boolean isManaged, final boolean isCharging, final long waitTime) {
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.get();
    final boolean suspendOnPlugged =
        preferenceUtil.powerManagerActive().isSuspendPlugged() && isCharging;
    boolean controlled = false;
    if (isManaged && !suspendOnPlugged && manager.isEnabled()) {
      manager.disable(waitTime);
      LogUtil.d(TAG, manager.getTag(), context.getString(R.string.radio_disable_in), waitTime);
      controlled = true;
    } else {
      LogUtil.d(TAG, manager.getTag(), context.getString(R.string.radio_not_managed));
    }
    return controlled;
  }

  private static void setDisableAlarm(final Context context, final Intent disableIntent,
      final boolean shouldSetAlarm) {
    if (shouldSetAlarm) {
      final Context app = context.getApplicationContext();
      final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.get();
      final long interval = preferenceUtil.powerManagerActive().getIntervalTime();
      final PendingIntent pendingIntent =
          PendingIntent.getService(app, 0, disableIntent, PendingIntent.FLAG_CANCEL_CURRENT);
      final AlarmManager alarmManager = (AlarmManager) app.getSystemService(Context.ALARM_SERVICE);
      alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
          SystemClock.elapsedRealtime() + interval, pendingIntent);
      LogUtil.d(TAG, app.getString(R.string.set_alarm_for_interval), interval);
    } else {
      LogUtil.d(TAG, context.getString(R.string.interval_not_needed));
    }
  }

  private static void cancelDisableAlarm(final Context context, final Intent stopThis) {
    final Context app = context.getApplicationContext();
    app.stopService(stopThis);
    final AlarmManager alarmManager = (AlarmManager) app.getSystemService(Context.ALARM_SERVICE);
    alarmManager.cancel(
        PendingIntent.getService(app, 0, stopThis, PendingIntent.FLAG_CANCEL_CURRENT));
  }

  private static void runPowerManager(final Context context, final boolean isScreenOff) {
    if (isScreenOff) {
      final BatteryUtil batteryUtil = BatteryUtil.get();
      batteryUtil.updateBatteryInformation();
      final boolean isCharging = batteryUtil.isCharging();
      disableWifi(context, isCharging);
      disableData(context, isCharging);
      disableBluetooth(context, isCharging);
      disableSync(context, isCharging);
    } else {
      enableWifi(context);
      enableData(context);
      enableBluetooth(context);
      enableSync(context);
    }
  }

  @Override protected final void onHandleIntent(final Intent intent) {
    if (null != intent) {
      final String message = intent.getStringExtra(Constants.RUN_FIELD);
      if (message != null) {
        if (message.equals(Constants.RUN_TRUE)) {
          runPowerManager(getApplicationContext(), true);
        } else if (message.equals(Constants.RUN_FALSE)) {
          runPowerManager(getApplicationContext(), false);
        } else {
          LogUtil.e(TAG, getString(R.string.empty_run_cmd));
        }
      } else {
        LogUtil.e(TAG, getString(R.string.null_msg));
      }
    }
  }

  public static final class Constants {

    public static final String RUN_FIELD = ActiveService.class.getName() + ".run_command";
    public static final String RUN_TRUE = RUN_FIELD + ".run_true";
    public static final String RUN_FALSE = RUN_FIELD + ".run_false";
    public static final long DELAY_RADIO_NONE = 0;
    public static final long DELAY_RADIO_FIVE = 5 * 1000;
    public static final long DELAY_RADIO_TEN = 10 * 1000;
    public static final long DELAY_RADIO_FIFTEEN = 15 * 1000;
    public static final long DELAY_RADIO_THIRTY = 30 * 1000;
    public static final long DELAY_RADIO_FOURTYFIVE = 45 * 1000;
    public static final long DELAY_RADIO_SIXTY = 60 * 1000;
    public static final long INTERVAL_REOPEN_FIFTEEN = 15 * 1000;
    public static final long INTERVAL_REOPEN_THIRTY = 30 * 1000;
    public static final long INTERVAL_REOPEN_FOURTYFIVE = 45 * 1000;
    public static final long INTERVAL_REOPEN_SIXTY = 60 * 1000;
    public static final long INTERVAL_REOPEN_NINTY = 90 * 1000;
    public static final long INTERVAL_REOPEN_ONETWENTY = 120 * 1000;
    public static final boolean DEFAULT_MANAGE_WIFI = true;
    public static final boolean DEFAULT_MANAGE_DATA = false;
    public static final boolean DEFAULT_MANAGE_BLUETOOTH = false;
    public static final boolean DEFAULT_MANAGE_SYNC = true;
    public static final long DEFAULT_INTERVAL_TIME = INTERVAL_REOPEN_SIXTY;
  }

  static final class IntentPool {

    private static final int MAX_POOL_SIZE = 10;
    private static final Pools.SynchronizedPool<Intent> intentPool =
        new Pools.SynchronizedPool<>(MAX_POOL_SIZE);

    static Intent acquire() {
      final Intent intent = intentPool.acquire();
      return (null == intent) ? new Intent() : intent;
    }

    static void release(final Intent intent) {
      intentPool.release(intent);
    }
  }
}
