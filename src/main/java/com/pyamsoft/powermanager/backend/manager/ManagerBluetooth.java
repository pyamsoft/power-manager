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

import android.bluetooth.BluetoothAdapter;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;

public final class ManagerBluetooth extends ManagerBase {

  private static final String BLUETOOTH_MANAGER = "BluetoothManager";
  private static ManagerBase instance = null;
  private final BluetoothAdapter bluetooth;

  private ManagerBluetooth() {
    super();
    LogUtil.d(BLUETOOTH_MANAGER, "Initialize ManagerBluetooth");
    this.bluetooth = BluetoothAdapter.getDefaultAdapter();
  }

  public static ManagerBase get() {
    if (instance == null) {
      synchronized (ManagerBluetooth.class) {
        if (instance == null) {
          instance = new ManagerBluetooth();
        }
      }
    }
    return instance;
  }

  private synchronized boolean isNull() {
    return (null == bluetooth);
  }

  @Override synchronized void disable() {
    if (!isNull()) {
      bluetooth.disable();
      LogUtil.i(BLUETOOTH_MANAGER, "setBluetoothEnabled: false");
    }
  }

  @Override public String getTag() {
    return BLUETOOTH_MANAGER;
  }

  @Override public String getName() {
    return "Bluetooth";
  }

  @Override synchronized void enable() {
    if (!isNull()) {
      bluetooth.enable();
      LogUtil.i(BLUETOOTH_MANAGER, "setBluetoothEnabled: true");
    }
  }

  @Override public final synchronized boolean isEnabled() {
    return (!isNull() && bluetooth.isEnabled());
  }

  public static final class Interval extends ManagerBase.Interval {

    public Interval() {
      super(Interval.class.getName());
    }

    @Override protected long getTargetIntervalTime(GlobalPreferenceUtil preferenceUtil) {
      return preferenceUtil.powerManagerActive().getIntervalTimeBluetooth();
    }

    @Override protected Class<? extends ManagerBase.Interval> getServiceClass() {
      return Interval.class;
    }

    @Override protected long getTargetCloseTime(GlobalPreferenceUtil preferenceUtil) {
      return preferenceUtil.intervalDisableService().getBluetoothReopenTime();
    }

    @Override protected ManagerBase getTargetManager() {
      return get();
    }
  }

  public static final class Toggle extends ManagerBase.Toggle {

    public Toggle() {
      super(Toggle.class.getName());
    }

    @Override protected void setManageState(GlobalPreferenceUtil preferenceUtil) {
      final boolean managed = preferenceUtil.powerManagerActive().isManagedBluetooth();
      preferenceUtil.powerManagerActive().setManagedBluetooth(!managed);
      PowerPlanUtil.with(getApplicationContext())
          .updateCustomPlan(PowerPlanUtil.FIELD_MANAGE_BLUETOOTH, !managed);
    }
  }
}
