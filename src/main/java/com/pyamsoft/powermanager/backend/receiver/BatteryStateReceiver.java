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
package com.pyamsoft.powermanager.backend.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;
import com.pyamsoft.powermanager.backend.manager.ManagerBase;
import com.pyamsoft.powermanager.backend.manager.ManagerBluetooth;
import com.pyamsoft.powermanager.backend.manager.ManagerData;
import com.pyamsoft.powermanager.backend.manager.ManagerSync;
import com.pyamsoft.powermanager.backend.manager.ManagerWifi;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;
import com.pyamsoft.powermanager.backend.trigger.PowerTriggerDataSource;
import com.pyamsoft.powermanager.backend.util.BatteryUtil;
import com.pyamsoft.pydroid.util.LogUtil;

public final class BatteryStateReceiver extends BroadcastReceiver {

  private static final String TAG = BatteryStateReceiver.class.getSimpleName();

  private final IntentFilter filter;
  private boolean isRegistered;

  public BatteryStateReceiver() {
    filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    filter.addAction(Intent.ACTION_BATTERY_LOW);
    filter.addAction(Intent.ACTION_BATTERY_OKAY);
    isRegistered = false;
  }

  private static void setTrigger(final ManagerBase manager, final int managed, final int state) {
    if (managed == PowerTrigger.ENABLED) {
      if (state == PowerTrigger.TOGGLE_STATE_OFF) {
        manager.disable(0);
      } else if (state == PowerTrigger.TOGGLE_STATE_ON) {
        manager.enable(0);
      }
    }
  }

  private static void setWifi(final Context context, final PowerTrigger trigger) {
    setTrigger(ManagerWifi.with(context), trigger.getManageWifi(), trigger.getStateWifi());
  }

  private static void setData(final Context context, final PowerTrigger trigger) {
    setTrigger(ManagerData.with(context), trigger.getManageData(), trigger.getStateData());
  }

  private static void setBluetooth(final PowerTrigger trigger) {
    setTrigger(ManagerBluetooth.get(), trigger.getManageBluetooth(), trigger.getStateBluetooth());
  }

  private static void setSync(final PowerTrigger trigger) {
    setTrigger(ManagerSync.get(), trigger.getManageSync(), trigger.getStateSync());
  }

  private static void updateTriggerState(final Context context, final PowerTrigger trigger) {
    final PowerTriggerDataSource source = PowerTriggerDataSource.with(context);
    if (!source.isOpened()) {
      source.open();
      source.createTrigger(trigger);
    }
    if (source.isOpened()) {
      source.close();
    }
  }

  private static boolean triggerOnDischarging(final Context c, final PowerTrigger trigger,
      final int batteryPercent) {
    boolean ret = false;
    if (trigger.getEnabled() == PowerTrigger.ENABLED) {
      if (batteryPercent == trigger.getLevel()
          && trigger.getAvailable() == PowerTrigger.AVAILABLE) {
        LogUtil.d(TAG, "Trigger: [", trigger.getId(), "]", trigger.getName(),
            " is Available, run and set not available");
        setWifi(c, trigger);
        setData(c, trigger);
        setBluetooth(trigger);
        setSync(trigger);
        trigger.setAvailable(PowerTrigger.UNAVAILABLE);
        updateTriggerState(c, trigger);
        Toast.makeText(c.getApplicationContext(), "Running power trigger:" + trigger.getName(),
            Toast.LENGTH_SHORT).show();
        ret = true;
      } else if (batteryPercent == trigger.getLevel()
          && trigger.getAvailable() == PowerTrigger.UNAVAILABLE) {
        LogUtil.d(TAG, "Trigger: [", trigger.getId(), "]", trigger.getName(),
            " skipped because not available");
      } else {
        LogUtil.d(TAG, "Trigger: [", trigger.getId(), "]", trigger.getName(), " no match, skipped");
      }
    }
    return ret;
  }

  private static boolean triggerOnCharging(final Context c, final PowerTrigger trigger,
      final int batteryPercent) {
    boolean ret = false;
    if (trigger.getEnabled() == PowerTrigger.ENABLED) {
      if (trigger.getAvailable() == PowerTrigger.UNAVAILABLE
          && batteryPercent >= trigger.getLevel()) {
        LogUtil.d(TAG, "Trigger: [", trigger.getId(), "]", trigger.getName(),
            " is not Available, set Availability");
        trigger.setAvailable(PowerTrigger.AVAILABLE);
        updateTriggerState(c, trigger);
        ret = true;
      } else if (trigger.getAvailable() == PowerTrigger.AVAILABLE
          && batteryPercent >= trigger.getLevel()) {
        LogUtil.d(TAG, "Trigger: [", trigger.getId(), "]", trigger.getName(),
            " already Available, do nothing. ");
      } else {
        LogUtil.d(TAG, "Trigger: [", trigger.getId(), "]", trigger.getName(), " no match, skipped");
      }
    }
    return ret;
  }

  public final boolean register(final Context c) {
    boolean ret = false;
    if (!isRegistered) {
      c.getApplicationContext().registerReceiver(this, filter);
      isRegistered = true;
      ret = true;
    }
    return ret;
  }

  public final boolean unregister(final Context c) {
    boolean ret = false;
    if (isRegistered) {
      c.getApplicationContext().unregisterReceiver(this);
      isRegistered = false;
      ret = true;
    }
    return ret;
  }

  @Override public final void onReceive(final Context context, final Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      final BatteryUtil bs = BatteryUtil.with(context);
      bs.updateBatteryInformation();
      switch (action) {
        case Intent.ACTION_BATTERY_CHANGED:
          final int batteryPercent = (int) bs.getPercent();
          boolean ret = false;
          if (bs.isCharging()) {
            for (final PowerTrigger trigger : PowerTriggerDataSource.TriggerSet.with(context)
                .asSet()) {
              if (trigger.getId() == PowerTriggerDataSource.TriggerSet.PLACEHOLDER_ID) {
                continue;
              }
              ret = triggerOnCharging(context, trigger, batteryPercent);
              if (ret) {
                break;
              }
            }
          } else {
            for (final PowerTrigger trigger : PowerTriggerDataSource.TriggerSet.with(context)
                .asSet()) {
              if (trigger.getId() == PowerTriggerDataSource.TriggerSet.PLACEHOLDER_ID) {
                continue;
              }
              ret = triggerOnDischarging(context, trigger, batteryPercent);
              if (ret) {
                break;
              }
            }
          }
          if (ret) {
            PersistentNotification.update(context);
          }
          break;
        case Intent.ACTION_POWER_CONNECTED:
        case Intent.ACTION_POWER_DISCONNECTED:
          // nothing yet
          break;
        default:
      }
    }
  }
}
