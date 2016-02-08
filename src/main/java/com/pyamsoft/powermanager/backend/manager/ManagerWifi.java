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

package com.pyamsoft.powermanager.backend.manager;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;

public final class ManagerWifi extends ManagerBase {

  private static final String WIFI_MANAGER = "WifiManager";
  private static ManagerBase instance = null;
  private WifiManager wifi;

  private ManagerWifi(final Context context) {
    super();
    LogUtil.d(WIFI_MANAGER, "Initialize ManagerWifi");
    this.wifi =
        (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
  }

  public static ManagerBase with(final Context context) {
    if (instance == null) {
      synchronized (ManagerWifi.class) {
        if (instance == null) {
          instance = new ManagerWifi(context);
        }
      }
    }
    return instance;
  }

  @Override public String getTag() {
    return WIFI_MANAGER;
  }

  @Override public String getName() {
    return "WiFi";
  }

  @Override synchronized void disable() {
    if (!isNull()) {
      wifi.setWifiEnabled(false);
      LogUtil.i(WIFI_MANAGER, "setWifiEnabled: false");
    }
  }

  @Override synchronized void enable() {
    if (!isNull()) {
      wifi.setWifiEnabled(true);
      LogUtil.i(WIFI_MANAGER, "setWifiEnabled: true");
    }
  }

  private synchronized boolean isNull() {
    return (null == wifi);
  }

  @Override public final synchronized boolean isEnabled() {
    return (wifi != null && wifi.isWifiEnabled());
  }

  public static final class Interval extends ManagerBase.Interval {

    public Interval() {
      super(Interval.class.getName());
    }

    @Override protected long getTargetCloseTime(GlobalPreferenceUtil preferenceUtil) {
      return preferenceUtil.intervalDisableService().getWifiReopenTime();
    }

    @Override protected ManagerBase getTargetManager() {
      return with(getApplicationContext());
    }

    @Override protected long getTargetIntervalTime(GlobalPreferenceUtil preferenceUtil) {
      return preferenceUtil.powerManagerActive().getIntervalTimeWifi();
    }

    @Override protected Class<? extends ManagerBase.Interval> getServiceClass() {
      return Interval.class;
    }
  }

  public static final class Toggle extends ManagerBase.Toggle {

    public Toggle() {
      super(Toggle.class.getName());
    }

    @Override protected void setManageState(GlobalPreferenceUtil preferenceUtil) {
      final boolean managed = preferenceUtil.powerManagerActive().isManagedWifi();
      preferenceUtil.powerManagerActive().setManagedWifi(!managed);
      PowerPlanUtil.with(getApplicationContext())
          .updateCustomPlan(PowerPlanUtil.FIELD_MANAGE_WIFI, !managed);
    }
  }
}
