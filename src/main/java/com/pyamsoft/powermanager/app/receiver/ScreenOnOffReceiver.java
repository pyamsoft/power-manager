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
import com.pyamsoft.powermanager.app.manager.Manager;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public final class ScreenOnOffReceiver extends BroadcastReceiver {

  @NonNull private final IntentFilter filter;
  @NonNull private final Application application;
  @Nullable @Inject @Named("wifi") Manager managerWifi;
  @Nullable @Inject @Named("data") Manager managerData;
  @Nullable @Inject @Named("bluetooth") Manager managerBluetooth;
  @Nullable @Inject @Named("sync") Manager managerSync;
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
    if (managerSync.isManaged()) {
      if (managerSync.isEnabled()) {
        Timber.e("Sync was already enabled, ignoring");
      } else {
        managerSync.enable();
      }
    } else {
      Timber.e("Sync was not managed, ignoring");
    }
  }

  private void enableBluetooth() {
    assert managerBluetooth != null;
    if (managerBluetooth.isManaged()) {
      if (managerBluetooth.isEnabled()) {
        Timber.e("Bluetooth was already enabled, ignoring");
      } else {
        managerBluetooth.enable();
      }
    } else {
      Timber.e("Bluetooth was not managed, ignoring");
    }
  }

  private void enableData() {
    assert managerData != null;
    if (managerData.isManaged()) {
      if (managerData.isEnabled()) {
        Timber.e("Data was already enabled, ignoring");
      } else {
        managerData.enable();
      }
    } else {
      Timber.e("Data was not managed, ignoring");
    }
  }

  private void enableWifi() {
    assert managerWifi != null;
    if (managerWifi.isManaged()) {
      if (managerWifi.isEnabled()) {
        Timber.e("Wifi was already enabled, ignoring");
      } else {
        managerWifi.enable();
      }
    } else {
      Timber.e("Wifi was not managed, ignoring");
    }
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
    if (managerSync.isManaged()) {
      if (managerSync.isEnabled()) {
        managerSync.disable();
      } else {
        Timber.e("Sync was already disabled, ignoring");
      }
    } else {
      Timber.e("Sync was not managed, ignoring");
    }
  }

  private void disableBluetooth() {
    assert managerBluetooth != null;
    if (managerBluetooth.isManaged()) {
      if (managerBluetooth.isEnabled()) {
        managerBluetooth.disable();
      } else {
        Timber.e("Bluetooth was already disabled, ignoring");
      }
    } else {
      Timber.e("Bluetooth was not managed, ignoring");
    }
  }

  private void disableData() {
    assert managerData != null;
    if (managerData.isManaged()) {
      if (managerData.isEnabled()) {
        managerData.disable();
      } else {
        Timber.e("Data was already disabled, ignoring");
      }
    } else {
      Timber.e("Data was not managed, ignoring");
    }
  }

  private void disableWifi() {
    assert managerWifi != null;
    if (managerWifi.isManaged()) {
      if (managerWifi.isEnabled()) {
        managerWifi.disable();
      } else {
        Timber.e("Wifi was already disabled, ignoring");
      }
    } else {
      Timber.e("Wifi was not managed, ignoring");
    }
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

