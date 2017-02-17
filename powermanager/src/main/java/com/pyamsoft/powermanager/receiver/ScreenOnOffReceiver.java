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

package com.pyamsoft.powermanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.Display;
import android.widget.Toast;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.manager.Manager;
import com.pyamsoft.powermanager.model.Logger;
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
  @NonNull private final DisplayManager displayManager;

  @Inject @Named("wifi_manager") Manager managerWifi;
  @Inject @Named("data_manager") Manager managerData;
  @Inject @Named("bluetooth_manager") Manager managerBluetooth;
  @Inject @Named("sync_manager") Manager managerSync;
  @Inject @Named("doze_manager") Manager managerDoze;
  @Inject @Named("airplane_manager") Manager managerAirplane;
  @Inject @Named("logger_manager") Logger logger;
  private boolean isRegistered;

  public ScreenOnOffReceiver(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
    isRegistered = false;

    displayManager = (DisplayManager) appContext.getSystemService(Context.DISPLAY_SERVICE);
    Injector.get().provideComponent().plusManagerComponent().inject(this);
  }

  /**
   * Checks the display state on API's which have finer tuned states.
   *
   * Returns true if the display is in the state we assume it to be.
   */
  @CheckResult private boolean checkDisplayState(boolean displayOn) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
      Timber.w("Old API, always trust the broadcast");
      return true;
    } else {
      final Display[] allDisplays = displayManager.getDisplays();
      final int checkState = displayOn ? Display.STATE_OFF : Display.STATE_ON;
      boolean allInState = true;
      for (final Display display : allDisplays) {
        Timber.d("Check that display: %s is %s", display.getName(), displayOn ? "ON" : "OFF");
        if (display.getState() == checkState) {
          Timber.w("Display: %s is %s", display.getName(), displayOn ? "OFF" : "ON");
          allInState = false;
          break;
        }
      }
      return allInState;
    }
  }

  @Override public final void onReceive(final Context context, final Intent intent) {
    if (null != intent) {
      final String action = intent.getAction();
      switch (action) {
        case Intent.ACTION_SCREEN_OFF:
          logger.d("Some screen off action");
          if (checkDisplayState(false)) {
            logger.i("Screen off event");
            disableManagers();
          }
          break;
        case Intent.ACTION_SCREEN_ON:
          logger.d("Some screen on action");
          if (checkDisplayState(true)) {
            logger.i("Screen on event");
            enableManagers();
          }
          break;
        default:
          Timber.e("Invalid event: %s", action);
      }
    }
  }

  private void enableManagers() {
    managerDoze.cancel(() -> managerDoze.queueSet(() -> {
      managerAirplane.cancel(() -> managerAirplane.queueSet(null));
      managerWifi.cancel(() -> managerWifi.queueSet(null));
      managerData.cancel(() -> managerData.queueSet(null));
      managerBluetooth.cancel(() -> managerBluetooth.queueSet(null));
      managerSync.cancel(() -> managerSync.queueSet(null));
    }));
  }

  private void disableManagers() {
    managerDoze.cancel(() -> {
      managerAirplane.cancel(() -> managerAirplane.queueUnset(null));
      managerWifi.cancel(() -> managerWifi.queueUnset(null));
      managerData.cancel(() -> managerData.queueUnset(null));
      managerBluetooth.cancel(() -> managerBluetooth.queueUnset(null));
      managerSync.cancel(() -> managerSync.queueUnset(null));
      managerDoze.queueUnset(null);
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
