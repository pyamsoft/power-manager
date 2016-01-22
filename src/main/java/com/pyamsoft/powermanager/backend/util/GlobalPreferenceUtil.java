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
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.backend.service.ActiveService;
import com.pyamsoft.pydroid.base.PreferenceBase;

public final class GlobalPreferenceUtil extends PreferenceBase {

  private static GlobalPreferenceUtil instance = null;
  private final IntervalDisableService intervalDisableService;
  private final PowerManagerActive powerManagerActive;
  private final PowerManagerMonitor powerManagerMonitor;
  private final GridOrder gridOrder;
  private final PowerPlans powerPlans;

  private GlobalPreferenceUtil() {
    super(PowerManager.PREFERENCES);
    intervalDisableService = new IntervalDisableService();
    powerManagerActive = new PowerManagerActive();
    powerManagerMonitor = new PowerManagerMonitor();
    gridOrder = new GridOrder();
    powerPlans = new PowerPlans();
  }

  public static synchronized GlobalPreferenceUtil get() {
    if (instance == null) {
      instance = new GlobalPreferenceUtil();
    }
    return instance;
  }

  @Override public void init(final Context c) {
    super.init(c);
    intervalDisableService.init(c);
    powerManagerActive.init(c);
    powerManagerMonitor.init(c);
    gridOrder.init(c);
    powerPlans.init(c);
  }

  public final IntervalDisableService intervalDisableService() {
    return intervalDisableService;
  }

  public final PowerManagerActive powerManagerActive() {
    return powerManagerActive;
  }

  public final PowerManagerMonitor powerManagerMonitor() {
    return powerManagerMonitor;
  }

  public final PowerPlans powerPlans() {
    return powerPlans;
  }

  public final GridOrder gridOrder() {
    return gridOrder;
  }

  @Override public void clear() {
    if (intervalDisableService != null) {
      intervalDisableService.clear();
    }
    if (powerManagerActive != null) {
      powerManagerActive.clear();
    }
    if (powerManagerMonitor != null) {
      powerManagerMonitor.clear();
    }
    if (gridOrder != null) {
      gridOrder.clear();
    }
    if (powerPlans != null) {
      powerPlans.clear();
    }
    super.clear();
  }

  public static final class IntervalDisableService extends PreferenceBase {

    private static final String TAG = IntervalDisableService.class.getName();
    private static final String PREFERENCE = PowerManager.createPreferenceFileName(TAG);
    private static final String REOPEN_TIME_BLUETOOTH = TAG + ".reopen_time_bluetooth";
    private static final String REOPEN_TIME_WIFI = TAG + ".reopen_time_wifi";
    private static final String REOPEN_TIME_DATA = TAG + ".reopen_time_data";
    private static final String REOPEN_TIME_SYNC = TAG + ".reopen_time_sync";
    private static final String REOPEN_BLUETOOTH = TAG + ".reopen__bluetooth";
    private static final String REOPEN_WIFI = TAG + ".reopen_wifi";
    private static final String REOPEN_DATA = TAG + ".reopen_data";
    private static final String REOPEN_SYNC = TAG + ".reopen_sync";

    public IntervalDisableService() {
      super(PREFERENCE);
    }

    public long getBluetoothReopenTime() {
      return getLong(REOPEN_TIME_BLUETOOTH, ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY);
    }

    public void setBluetoothReopenTime(final long l) {
      putLong(REOPEN_TIME_BLUETOOTH, l);
    }

    public long getSyncReopenTime() {
      return getLong(REOPEN_TIME_SYNC, ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY);
    }

    public void setSyncReopenTime(final long l) {
      putLong(REOPEN_TIME_SYNC, l);
    }

    public long getDataReopenTime() {
      return getLong(REOPEN_TIME_DATA, ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY);
    }

    public void setDataReopenTime(final long l) {
      putLong(REOPEN_TIME_DATA, l);
    }

    public long getWifiReopenTime() {
      return getLong(REOPEN_TIME_WIFI, ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY);
    }

    public void setWifiReopenTime(final long l) {
      putLong(REOPEN_TIME_WIFI, l);
    }

    public boolean isBluetoothReopen() {
      return getBoolean(REOPEN_BLUETOOTH, false);
    }

    public void setBluetoothReopen(final boolean l) {
      putBoolean(REOPEN_BLUETOOTH, l);
    }

    public boolean isSyncReopen() {
      return getBoolean(REOPEN_SYNC, true);
    }

    public void setSyncReopen(final boolean l) {
      putBoolean(REOPEN_SYNC, l);
    }

    public boolean isDataReopen() {
      return getBoolean(REOPEN_DATA, false);
    }

    public void setDataReopen(final boolean l) {
      putBoolean(REOPEN_DATA, l);
    }

    public boolean isWifiReopen() {
      return getBoolean(REOPEN_WIFI, true);
    }

    public void setWifiReopen(final boolean l) {
      putBoolean(REOPEN_WIFI, l);
    }
  }

  public static final class PowerManagerActive extends PreferenceBase {

    private static final String TAG = PowerManagerActive.class.getName();
    public static final String MANAGE_WIFI = TAG + ".manage_wifi";
    public static final String MANAGE_DATA = TAG + ".manage_data";
    public static final String MANAGE_BLUETOOTH = TAG + ".manage_bluetooth";
    public static final String MANAGE_SYNC = TAG + ".manage_sync";
    private static final String PREFERENCE = PowerManager.createPreferenceFileName(TAG);
    private static final String SUSPEND_PLUGGED = TAG + ".suspend_plugged";
    private static final String INTERVAL_TIME = TAG + ".interval_time";
    private static final String CONTROL_WIFI = TAG + ".control_wifi";
    private static final String CONTROL_DATA = TAG + ".control_data";
    private static final String CONTROL_BLUETOOTH = TAG + ".control_bluetooth";
    private static final String CONTROL_SYNC = TAG + ".control_sync";
    private static final String DELAY_WIFI = TAG + ".delay_wifi";
    private static final String DELAY_DATA = TAG + ".delay_data";
    private static final String DELAY_BLUETOOTH = TAG + ".delay_bluetooth";
    private static final String DELAY_SYNC = TAG + ".delay_sync";

    public PowerManagerActive() {
      super(PREFERENCE);
    }

    public boolean isSuspendPlugged() {
      return getBoolean(SUSPEND_PLUGGED, true);
    }

    public void setSuspendPlugged(final boolean b) {
      putBoolean(SUSPEND_PLUGGED, b);
    }

    public long getIntervalTime() {
      return getLong(INTERVAL_TIME, ActiveService.Constants.DEFAULT_INTERVAL_TIME);
    }

    public void setIntervalTime(final long l) {
      putLong(INTERVAL_TIME, l);
    }

    public long getDelayWifi() {
      return getLong(DELAY_WIFI, ActiveService.Constants.DELAY_RADIO_FIFTEEN);
    }

    public void setDelayWifi(final long l) {
      putLong(DELAY_WIFI, l);
    }

    public long getDelayData() {
      return getLong(DELAY_DATA, ActiveService.Constants.DELAY_RADIO_SIXTY);
    }

    public void setDelayData(final long l) {
      putLong(DELAY_DATA, l);
    }

    public long getDelayBluetooth() {
      return getLong(DELAY_BLUETOOTH, ActiveService.Constants.DELAY_RADIO_SIXTY);
    }

    public void setDelayBluetooth(final long l) {
      putLong(DELAY_BLUETOOTH, l);
    }

    public long getDelaySync() {
      return getLong(DELAY_SYNC, ActiveService.Constants.DELAY_RADIO_FIFTEEN);
    }

    public void setDelaySync(final long l) {
      putLong(DELAY_SYNC, l);
    }

    public boolean isManagedWifi() {
      return getBoolean(MANAGE_WIFI, ActiveService.Constants.DEFAULT_MANAGE_WIFI);
    }

    public void setManagedWifi(final boolean c) {
      putBoolean(MANAGE_WIFI, c);
    }

    public boolean isControlledWifi() {
      return getBoolean(CONTROL_WIFI, false);
    }

    public void setControlledWifi(final boolean c) {
      putBoolean(CONTROL_WIFI, c);
    }

    public boolean isControlledData() {
      return getBoolean(CONTROL_DATA, false);
    }

    public void setControlledData(final boolean c) {
      putBoolean(CONTROL_DATA, c);
    }

    public boolean isControlledBluetooth() {
      return getBoolean(CONTROL_BLUETOOTH, false);
    }

    public void setControlledBluetooth(final boolean c) {
      putBoolean(CONTROL_BLUETOOTH, c);
    }

    public boolean isControlledSync() {
      return getBoolean(CONTROL_SYNC, false);
    }

    public void setControlledSync(final boolean c) {
      putBoolean(CONTROL_SYNC, c);
    }

    public boolean isManagedData() {
      return getBoolean(MANAGE_DATA, ActiveService.Constants.DEFAULT_MANAGE_DATA);
    }

    public void setManagedData(final boolean c) {
      putBoolean(MANAGE_DATA, c);
    }

    public boolean isManagedBluetooth() {
      return getBoolean(MANAGE_BLUETOOTH, ActiveService.Constants.DEFAULT_MANAGE_BLUETOOTH);
    }

    public void setManagedBluetooth(final boolean c) {
      putBoolean(MANAGE_BLUETOOTH, c);
    }

    public boolean isManagedSync() {
      return getBoolean(MANAGE_SYNC, ActiveService.Constants.DEFAULT_MANAGE_SYNC);
    }

    public void setManagedSync(final boolean c) {
      putBoolean(MANAGE_SYNC, c);
    }
  }

  public static final class PowerManagerMonitor extends PreferenceBase {

    private static final String TAG = PowerManagerMonitor.class.getName();
    public static final String FOREGROUND = TAG + ".foreground";
    public static final String NOTIFICATION = TAG + ".notification";
    private static final String PREFERENCE = PowerManager.createPreferenceFileName(TAG);

    public PowerManagerMonitor() {
      super(PREFERENCE);
    }

    public boolean isForeground() {
      return getBoolean(FOREGROUND, false);
    }

    public void setForeground(final boolean b) {
      putBoolean(FOREGROUND, b);
    }

    public boolean isNotificationEnabled() {
      return getBoolean(NOTIFICATION, true);
    }

    public void setNotificationEnabled(final boolean b) {
      putBoolean(NOTIFICATION, b);
    }
  }

  public static final class GridOrder extends PreferenceBase {

    public static final String VIEW_POSITION_WIFI = "Wifi";
    public static final String VIEW_POSITION_DATA = "Data";
    public static final String VIEW_POSITION_BLUETOOTH = "Bluetooth";
    public static final String VIEW_POSITION_SYNC = "Sync";
    public static final String VIEW_POSITION_POWER_PLAN = "Power Plans";
    public static final String VIEW_POSITION_POWER_TRIGGER = "Power Triggers";
    public static final String VIEW_POSITION_BATTERY_INFO = "Battery Info";
    public static final String VIEW_POSITION_SETTINGS = "Settings";
    public static final String VIEW_POSITION_HELP = "Help";
    public static final String VIEW_POSITION_ABOUT = "About";
    private static final String TAG = GridOrder.class.getName();
    private static final String PREFERENCE = PowerManager.createPreferenceFileName(TAG);
    private static final String ONE = TAG + ".one";
    private static final String TWO = TAG + ".two";
    private static final String THREE = TAG + ".three";
    private static final String FOUR = TAG + ".four";
    private static final String FIVE = TAG + ".five";
    private static final String SIX = TAG + ".six";
    private static final String SEVEN = TAG + ".seven";
    private static final String EIGHT = TAG + ".eight";
    private static final String NINE = TAG + ".nine";
    private static final String TEN = TAG + ".ten";

    GridOrder() {
      super(PREFERENCE);
    }

    public String getOne() {
      return getString(ONE, VIEW_POSITION_WIFI);
    }

    public void setOne(final String put) {
      putString(ONE, put);
    }

    public String getTwo() {
      return getString(TWO, VIEW_POSITION_DATA);
    }

    public void setTwo(final String put) {
      putString(TWO, put);
    }

    public String getThree() {
      return getString(THREE, VIEW_POSITION_BLUETOOTH);
    }

    public void setThree(final String put) {
      putString(THREE, put);
    }

    public String getFour() {
      return getString(FOUR, VIEW_POSITION_SYNC);
    }

    public void setFour(final String put) {
      putString(FOUR, put);
    }

    public String getFive() {
      return getString(FIVE, VIEW_POSITION_POWER_PLAN);
    }

    public void setFive(final String put) {
      putString(FIVE, put);
    }

    public String getSix() {
      return getString(SIX, VIEW_POSITION_POWER_TRIGGER);
    }

    public void setSix(final String put) {
      putString(SIX, put);
    }

    public String getSeven() {
      return getString(SEVEN, VIEW_POSITION_BATTERY_INFO);
    }

    public void setSeven(final String put) {
      putString(SEVEN, put);
    }

    public String getEight() {
      return getString(EIGHT, VIEW_POSITION_SETTINGS);
    }

    public void setEight(final String put) {
      putString(EIGHT, put);
    }

    public String getNine() {
      return getString(NINE, VIEW_POSITION_HELP);
    }

    public void setNine(final String put) {
      putString(NINE, put);
    }

    public String getTen() {
      return getString(TEN, VIEW_POSITION_ABOUT);
    }

    public void setTen(final String put) {
      putString(TEN, put);
    }

    public String get(final int position) {
      String ret;
      switch (position) {
        case 0:
          ret = getOne();
          break;
        case 1:
          ret = getTwo();
          break;
        case 2:
          ret = getThree();
          break;
        case 3:
          ret = getFour();
          break;
        case 4:
          ret = getFive();
          break;
        case 5:
          ret = getSix();
          break;
        case 6:
          ret = getSeven();
          break;
        case 7:
          ret = getEight();
          break;
        case 8:
          ret = getNine();
          break;
        case 9:
          ret = getTen();
          break;
        default:
          ret = getOne();
      }
      return ret;
    }

    public void set(final int position, final String put) {
      switch (position) {
        case 0:
          setOne(put);
          break;
        case 1:
          setTwo(put);
          break;
        case 2:
          setThree(put);
          break;
        case 3:
          setFour(put);
          break;
        case 4:
          setFive(put);
          break;
        case 5:
          setSix(put);
          break;
        case 6:
          setSeven(put);
          break;
        case 7:
          setEight(put);
          break;
        case 8:
          setNine(put);
          break;
        case 9:
          setTen(put);
          break;
        default:
          setOne(put);
      }
    }
  }

  public static final class PowerPlans extends PreferenceBase {

    private static final String TAG = PowerPlans.class.getName();
    private static final String PREFERENCE = PowerManager.createPreferenceFileName(TAG);
    private static final String ACTIVE = TAG + ".active";

    PowerPlans() {
      super(PREFERENCE);
    }

    public int getActivePlan() {
      return getInt(ACTIVE,
          PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_STANDARD[PowerPlanUtil.FIELD_INDEX]));
    }

    public void setActivePlan(final int put) {
      putInt(ACTIVE, put);
    }
  }
}
