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

    PowerManager.powerManagerComponent(application).inject(this);
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
    assert managerWifi != null;
    managerWifi.enable(application);
    assert managerData != null;
    managerData.enable(application);
    assert managerBluetooth != null;
    managerBluetooth.enable(application);
    assert managerSync != null;
    managerSync.enable(application);
  }

  private void disableManagers() {
    Timber.d("Disable all managed managers");
    assert managerWifi != null;
    managerWifi.disable(application);
    assert managerData != null;
    managerData.disable(application);
    assert managerBluetooth != null;
    managerBluetooth.disable(application);
    assert managerSync != null;
    managerSync.disable(application);
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

