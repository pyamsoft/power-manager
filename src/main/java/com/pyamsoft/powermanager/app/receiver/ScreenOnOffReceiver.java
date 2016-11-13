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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.manager.ExclusiveManager;
import com.pyamsoft.powermanager.app.manager.Manager;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class ScreenOnOffReceiver extends BroadcastReceiver {

  @NonNull private final static IntentFilter SCREEN_FILTER;

  static {
    SCREEN_FILTER = new IntentFilter(Intent.ACTION_SCREEN_OFF);
    SCREEN_FILTER.addAction(Intent.ACTION_SCREEN_ON);
  }

  @NonNull private final Context appContext;

  @Inject @Named("obs_charging_state") BooleanInterestObserver chargingObserver;
  @Inject @Named("wifi_manager") Manager managerWifi;
  @Inject @Named("data_manager") Manager managerData;
  @Inject @Named("bluetooth_manager") Manager managerBluetooth;
  @Inject @Named("sync_manager") Manager managerSync;
  @Inject @Named("doze_manager") ExclusiveManager managerDoze;
  @Inject @Named("airplane_manager") Manager managerAirplane;
  @Inject @Named("logger_manager") Logger logger;
  private boolean isRegistered;

  public ScreenOnOffReceiver(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
    isRegistered = false;

    Injector.get().provideComponent().plusManagerComponent().inject(this);
  }

  @Override public final void onReceive(final Context context, final Intent intent) {
    if (null != intent) {
      final String action = intent.getAction();
      final boolean charging = chargingObserver.is();
      switch (action) {
        case Intent.ACTION_SCREEN_OFF:
          Timber.d("Screen off event");
          disableManagers(charging);
          break;
        case Intent.ACTION_SCREEN_ON:
          Timber.d("Screen on event");
          enableManagers();
          break;
        default:
          Timber.e("Invalid event: %s", action);
      }
    }
  }

  private void enableManagers() {
    Timber.d("Enable all managed managers");
    logger.i("Screen is ON, enable Managers");
    managerAirplane.queueSet();
    managerDoze.queueExclusiveSet(() -> {
      managerWifi.queueSet();
      managerData.queueSet();
      managerBluetooth.queueSet();
      managerSync.queueSet();
    });
  }

  private void disableManagers(boolean charging) {
    Timber.d("Disable all managed managers");
    logger.i("Screen is OFF, disable Managers");
    managerAirplane.queueUnset(charging);
    managerDoze.queueExclusiveUnset(charging, () -> {
      managerWifi.queueUnset(charging);
      managerData.queueUnset(charging);
      managerBluetooth.queueUnset(charging);
      managerSync.queueUnset(charging);
    });
  }

  public final void register() {
    if (!isRegistered) {
      cleanup();
      appContext.registerReceiver(this, SCREEN_FILTER);
      isRegistered = true;

      Toast.makeText(appContext, "Power Manager started", Toast.LENGTH_SHORT).show();
    } else {
      Timber.w("Already registered");
    }
  }

  private void cleanup() {
    managerWifi.cleanup();
    managerData.cleanup();
    managerBluetooth.cleanup();
    managerSync.cleanup();
    managerDoze.cleanup();
    managerAirplane.cleanup();
  }

  public final void unregister() {
    if (isRegistered) {
      appContext.unregisterReceiver(this);
      cleanup();
      isRegistered = false;

      Toast.makeText(appContext, "Power Manager suspended", Toast.LENGTH_SHORT).show();
    } else {
      Timber.w("Already unregistered");
    }
  }
}

