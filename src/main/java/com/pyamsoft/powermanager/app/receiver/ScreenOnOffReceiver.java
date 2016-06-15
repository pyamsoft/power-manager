/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.app.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.manager.ManagerBluetooth;
import com.pyamsoft.powermanager.app.manager.ManagerData;
import com.pyamsoft.powermanager.app.manager.ManagerSync;
import com.pyamsoft.powermanager.app.manager.ManagerWifi;
import javax.inject.Inject;
import timber.log.Timber;

public final class ScreenOnOffReceiver extends BroadcastReceiver {

  @NonNull private final IntentFilter filter;
  @NonNull private final Application application;
  @Nullable @Inject ManagerWifi managerWifi;
  @Nullable @Inject ManagerData managerData;
  @Nullable @Inject ManagerBluetooth managerBluetooth;
  @Nullable @Inject ManagerSync managerSync;
  private boolean isRegistered;

  public ScreenOnOffReceiver(@NonNull Application application) {
    this.application = application;
    filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
    filter.addAction(Intent.ACTION_SCREEN_ON);
    isRegistered = false;

    PowerManager.getInstance().getPowerManagerComponent().inject(this);
  }

  @Override public final void onReceive(final Context context, final Intent intent) {
    if (null != intent) {
      final String action = intent.getAction();
      switch (action) {
        case Intent.ACTION_SCREEN_OFF:
          Timber.d("Screen off event");
          disableManagers();
          break;
        case Intent.ACTION_SCREEN_ON:
          Timber.d("Screen on event");
          enableManagers();
          break;
        default:
      }
    }
  }

  private void enableManagers() {
    Timber.d("Enable all managed managers");
    enableWifi();
    enableData();
    enableBluetooth();
    enableSync();
  }

  private void enableSync() {
    assert managerSync != null;
    managerSync.enable();
  }

  private void enableBluetooth() {
    assert managerBluetooth != null;
    managerBluetooth.enable();
  }

  private void enableData() {
    assert managerData != null;
    managerData.enable();
  }

  private void enableWifi() {
    assert managerWifi != null;
    managerWifi.enable();
  }

  private void disableManagers() {
    Timber.d("Disable all managed managers");
    disableWifi();
    disableData();
    disableBluetooth();
    disableSync();
  }

  private void disableSync() {
    assert managerSync != null;
    managerSync.disable();
  }

  private void disableBluetooth() {
    assert managerBluetooth != null;
    managerBluetooth.disable();
  }

  private void disableData() {
    assert managerData != null;
    managerData.disable();
  }

  private void disableWifi() {
    assert managerWifi != null;
    managerWifi.disable();
  }

  public final void register() {
    if (!isRegistered) {
      application.getApplicationContext().registerReceiver(this, filter);
      isRegistered = true;
    }
  }

  public final void unregister() {
    if (isRegistered) {
      application.getApplicationContext().unregisterReceiver(this);
      isRegistered = false;
    }
  }
}
