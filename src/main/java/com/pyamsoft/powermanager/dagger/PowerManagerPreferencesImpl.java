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

package com.pyamsoft.powermanager.dagger;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.base.app.ApplicationPreferences;
import javax.inject.Inject;

final class PowerManagerPreferencesImpl extends ApplicationPreferences
    implements PowerManagerPreferences {

  @NonNull private final String manageWifi;
  @NonNull private final String manageData;
  @NonNull private final String manageBluetooth;
  @NonNull private final String manageSync;
  private final boolean manageWifiDefault;
  private final boolean manageDataDefault;
  private final boolean manageBluetoothDefault;
  private final boolean manageSyncDefault;

  @Inject protected PowerManagerPreferencesImpl(@NonNull Context context) {
    super(context);
    final Context appContext = context.getApplicationContext();
    final Resources resources = appContext.getResources();
    manageWifi = appContext.getString(R.string.manage_wifi_key);
    manageData = appContext.getString(R.string.manage_data_key);
    manageBluetooth = appContext.getString(R.string.manage_bluetooth_key);
    manageSync = appContext.getString(R.string.manage_sync_key);
    manageWifiDefault = resources.getBoolean(R.bool.manage_wifi_default);
    manageDataDefault = resources.getBoolean(R.bool.manage_data_default);
    manageBluetoothDefault = resources.getBoolean(R.bool.manage_bluetooth_default);
    manageSyncDefault = resources.getBoolean(R.bool.manage_sync_default);
  }

  @Override public long getWifiDelay() {
    // TODO
    return 5;
  }

  @Override public void setWifiDelay(long time) {
    // TODO
  }

  @Override public long getDataDelay() {
    return 5;
  }

  @Override public void setDataDelay(long time) {
    // TODO
  }

  @Override public long getBluetoothDelay() {
    return 5;
  }

  @Override public void setBluetoothDelay(long time) {
    // TODO
  }

  @Override public long getMasterSyncDelay() {
    return 5;
  }

  @Override public void setMasterSyncDelay(long time) {
    // TODO
  }

  @Override public int getNotificationPriority() {
    // TODO
    return NotificationCompat.PRIORITY_MIN;
  }

  @Override public boolean isBluetoothManaged() {
    return get(manageBluetooth, manageBluetoothDefault);
  }

  @Override public boolean isDataManaged() {
    return get(manageData, manageDataDefault);
  }

  @Override public boolean isSyncManaged() {
    return get(manageSync, manageSyncDefault);
  }

  @Override public boolean isWifiManaged() {
    return get(manageWifi, manageWifiDefault);
  }

  @Override public boolean isWearableManaged() {
    return false;
  }

  @Override public void setWearableManaged(boolean enable) {
    // TODO
  }
}
