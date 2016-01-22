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
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;

public final class ManagerWifi extends ManagerBase {

  private static final String WIFI_MANAGER = "WifiManager";
  private static ManagerBase instance = null;
  private Context context;
  private WifiManager wifi;

  private ManagerWifi() {
    super();
  }

  public static synchronized ManagerBase get() {
    if (instance == null) {
      instance = new ManagerWifi();
    }
    return instance;
  }

  @Override public final void init(final Context context) {
    LogUtil.d(WIFI_MANAGER, "Initialize ManagerWifi");
    this.context = context.getApplicationContext();
    this.wifi = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
  }

  @Override public String getTag() {
    return WIFI_MANAGER;
  }

  @Override synchronized void disable() {
    if (!isNull()) {
      wifi.setWifiEnabled(false);
      LogUtil.i(WIFI_MANAGER, context.getString(R.string.set_wifi_false));
    }
  }

  @Override synchronized void enable() {
    if (!isNull()) {
      wifi.setWifiEnabled(true);
      LogUtil.i(WIFI_MANAGER, context.getString(R.string.set_wifi_true));
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
      return get();
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
      PowerPlanUtil.get().updateCustomPlan(PowerPlanUtil.FIELD_MANAGE_WIFI, !managed);
    }
  }
}
