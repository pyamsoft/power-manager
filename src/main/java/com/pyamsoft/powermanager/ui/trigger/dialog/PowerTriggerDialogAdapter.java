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
package com.pyamsoft.powermanager.ui.trigger.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;

public final class PowerTriggerDialogAdapter extends FragmentStatePagerAdapter {

  private static final int POSITION_NAME = 0;
  private static final int POSITION_WIFI = 1;
  private static final int POSITION_DATA = 2;
  private static final int POSITION_BLUETOOTH = 3;
  private static final int POSITION_SYNC = 4;
  private static final int NUMBER_ITEMS = 5;

  private final PowerTriggerDialogNameFragment name = new PowerTriggerDialogNameFragment();
  private final PowerTriggerDialogWifiFragment wifi = new PowerTriggerDialogWifiFragment();
  private final PowerTriggerDialogDataFragment data = new PowerTriggerDialogDataFragment();
  private final PowerTriggerDialogBluetoothFragment bluetooth =
      new PowerTriggerDialogBluetoothFragment();
  private final PowerTriggerDialogSyncFragment sync = new PowerTriggerDialogSyncFragment();

  public PowerTriggerDialogAdapter(final Fragment f) {
    super(f.getChildFragmentManager());
  }

  @Override public Fragment getItem(final int position) {
    Fragment fragment;
    switch (position) {
      case POSITION_NAME:
        fragment = name;
        break;
      case POSITION_WIFI:
        fragment = wifi;
        break;
      case POSITION_DATA:
        fragment = data;
        break;
      case POSITION_BLUETOOTH:
        fragment = bluetooth;
        break;
      case POSITION_SYNC:
        fragment = sync;
        break;
      default:
        fragment = null;
        break;
    }
    return fragment;
  }

  @Override public int getCount() {
    return NUMBER_ITEMS;
  }

  public final String getName() {
    return name.getName();
  }

  public final int getLevel() {
    return name.getLevel();
  }

  public final int getManageEnabledWifi() {
    return wifi.getManageEnabled() ? PowerTrigger.ENABLED : PowerTrigger.DISABLED;
  }

  public final int getManageEnabledData() {
    return data.getManageEnabled() ? PowerTrigger.ENABLED : PowerTrigger.DISABLED;
  }

  public final int getManageEnabledBluetooth() {
    return bluetooth.getManageEnabled() ? PowerTrigger.ENABLED : PowerTrigger.DISABLED;
  }

  public final int getManageEnabledSync() {
    return sync.getManageEnabled() ? PowerTrigger.ENABLED : PowerTrigger.DISABLED;
  }

  public final int getReOpenEnabledWifi() {
    return wifi.getReOpenEnabled() ? PowerTrigger.ENABLED : PowerTrigger.DISABLED;
  }

  public final int getReOpenEnabledData() {
    return data.getReOpenEnabled() ? PowerTrigger.ENABLED : PowerTrigger.DISABLED;
  }

  public final int getReOpenEnabledBluetooth() {
    return bluetooth.getReOpenEnabled() ? PowerTrigger.ENABLED : PowerTrigger.DISABLED;
  }

  public final int getReOpenEnabledSync() {
    return sync.getReOpenEnabled() ? PowerTrigger.ENABLED : PowerTrigger.DISABLED;
  }

  public final int getStateWifi() {
    return wifi.getToggleState();
  }

  public final int getStateData() {
    return data.getToggleState();
  }

  public final int getStateBluetooth() {
    return bluetooth.getToggleState();
  }

  public final int getStateSync() {
    return sync.getToggleState();
  }
}
