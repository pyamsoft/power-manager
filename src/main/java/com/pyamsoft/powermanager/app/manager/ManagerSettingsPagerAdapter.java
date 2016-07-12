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

package com.pyamsoft.powermanager.app.manager;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.FabColorBus;
import com.pyamsoft.powermanager.app.manager.backend.ManagerBluetooth;
import com.pyamsoft.powermanager.app.manager.backend.ManagerData;
import com.pyamsoft.powermanager.app.manager.backend.ManagerSync;
import com.pyamsoft.powermanager.app.manager.backend.ManagerWifi;
import com.pyamsoft.powermanager.app.manager.manage.ManagerManageFragment;
import com.pyamsoft.powermanager.app.manager.period.ManagerPeriodicFragment;
import com.pyamsoft.powermanager.dagger.manager.DaggerManagerSettingsComponent;
import com.pyamsoft.powermanager.model.FabColorEvent;
import javax.inject.Inject;
import timber.log.Timber;

public final class ManagerSettingsPagerAdapter extends FragmentStatePagerAdapter
    implements WifiView, DataView, BluetoothView, SyncView {

  @NonNull public static final String TYPE_WIFI = "wifi";
  @NonNull public static final String TYPE_DATA = "data";
  @NonNull public static final String TYPE_BLUETOOTH = "bluetooth";
  @NonNull public static final String TYPE_SYNC = "sync";
  @NonNull public static final String FRAGMENT_TYPE = "fragment_type";
  @DrawableRes private static final int FAB_ICON_WIFI_ON = R.drawable.ic_network_wifi_24dp;
  @DrawableRes private static final int FAB_ICON_WIFI_OFF = R.drawable.ic_signal_wifi_off_24dp;
  @DrawableRes private static final int FAB_ICON_DATA_ON = R.drawable.ic_network_cell_24dp;
  @DrawableRes private static final int FAB_ICON_DATA_OFF = R.drawable.ic_signal_cellular_off_24dp;
  @DrawableRes private static final int FAB_ICON_BLUETOOTH_ON = R.drawable.ic_bluetooth_24dp;
  @DrawableRes private static final int FAB_ICON_BLUETOOTH_OFF =
      R.drawable.ic_bluetooth_disabled_24dp;
  @DrawableRes private static final int FAB_ICON_SYNC_ON = R.drawable.ic_sync_24dp;
  @DrawableRes private static final int FAB_ICON_SYNC_OFF = R.drawable.ic_sync_disabled_24dp;
  @NonNull private final Fragment manageFragment;
  @NonNull private final Fragment periodicFragment;
  @NonNull private final String type;

  @Inject WifiPresenter wifiPresenter;
  @Inject ManagerWifi managerWifi;

  @Inject DataPresenter dataPresenter;
  @Inject ManagerData managerData;

  @Inject BluetoothPresenter bluetoothPresenter;
  @Inject ManagerBluetooth managerBluetooth;

  @Inject SyncPresenter syncPresenter;
  @Inject ManagerSync managerSync;

  public ManagerSettingsPagerAdapter(@NonNull FragmentManager fm, @NonNull String type) {
    super(fm);
    Timber.d("new ManagerSettingsPagerAdapter");
    manageFragment = ManagerManageFragment.newInstance(type);
    periodicFragment = ManagerPeriodicFragment.newInstance(type);
    this.type = type;

    DaggerManagerSettingsComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build()
        .inject(this);

    wifiPresenter.bindView(this);
    dataPresenter.bindView(this);
    bluetoothPresenter.bindView(this);
    syncPresenter.bindView(this);

    switch (type) {
      case TYPE_WIFI:
        wifiPresenter.getCurrentState();
        break;
      case TYPE_DATA:
        dataPresenter.getCurrentState();
        break;
      case TYPE_BLUETOOTH:
        bluetoothPresenter.getCurrentState();
        break;
      case TYPE_SYNC:
        syncPresenter.getCurrentState();
        break;
      default:
        throw new IllegalStateException("Invalid type: " + type);
    }
  }

  @NonNull public final String getType() {
    return type;
  }

  @NonNull @Override public Fragment getItem(int position) {
    Fragment fragment;
    switch (position) {
      case 0:
        fragment = manageFragment;
        break;
      case 1:
        fragment = periodicFragment;
        break;
      default:
        throw new IllegalStateException("Fragment index is OOB");
    }
    return fragment;
  }

  @Override public int getCount() {
    return 2;
  }

  @Override public CharSequence getPageTitle(int position) {
    CharSequence title;
    switch (position) {
      case 0:
        title = "Manage";
        break;
      case 1:
        title = "Periodic";
        break;
      default:
        throw new IllegalStateException("Invalid index");
    }
    return title;
  }

  public final void recycle() {
    Timber.d("Recycle ManagerSettingsPagerAdapter");
    wifiPresenter.unbindView();
    dataPresenter.unbindView();
    bluetoothPresenter.unbindView();
    syncPresenter.unbindView();

    managerWifi.cleanup();
    managerData.cleanup();
    managerBluetooth.cleanup();
    managerSync.cleanup();
  }

  @Override public void toggleWifiDisabled() {
    Timber.d("Wifi currently enabled, disable");
    managerWifi.disable(0, false);
  }

  @Override public void toggleWifiEnabled() {
    Timber.d("Wifi currently disabled, enabled");
    managerWifi.enable(0, false);
  }

  @Override public void wifiStartManaged() {
    // noop
    throw new RuntimeException("NOOP");
  }

  @Override public void wifiStopManaged() {
    // noop
    throw new RuntimeException("NOOP");
  }

  @Override public void toggleBluetoothEnabled() {
    Timber.d("Bluetooth currently disabled, enabled");
    managerBluetooth.enable(0, false);
  }

  @Override public void toggleBluetoothDisabled() {
    Timber.d("Bluetooth currently enabled, disable");
    managerBluetooth.disable(0, false);
  }

  @Override public void bluetoothStartManaged() {
    // noop
    throw new RuntimeException("NOOP");
  }

  @Override public void bluetoothStopManaged() {
    // noop
    throw new RuntimeException("NOOP");
  }

  @Override public void toggleDataEnabled() {
    Timber.d("Data currently disabled, enable");
    managerData.enable(0, false);
  }

  @Override public void toggleDataDisabled() {
    Timber.d("Data currently enabled, disable");
    managerData.disable(0, false);
  }

  @Override public void dataStartManaged() {
    // noop
    throw new RuntimeException("NOOP");
  }

  @Override public void dataStopManaged() {
    // noop
    throw new RuntimeException("NOOP");
  }

  @Override public void toggleSyncEnabled() {
    Timber.d("Sync currently disabled, enable");
    managerSync.enable(0, false);
  }

  @Override public void toggleSyncDisabled() {
    Timber.d("Bluetooth currently enabled, disable");
    managerSync.disable(0, false);
  }

  @Override public void syncStartManaged() {
    // noop
    throw new RuntimeException("NOOP");
  }

  @Override public void syncStopManaged() {
    // noop
    throw new RuntimeException("NOOP");
  }

  @Override public void startManagingWearable() {

  }

  @Override public void stopManagingWearable() {

  }

  @Override public void bluetoothInitialState(boolean enabled, boolean managed) {
    @DrawableRes final int icon = enabled ? FAB_ICON_BLUETOOTH_ON : FAB_ICON_BLUETOOTH_OFF;
    FabColorBus.get().post(FabColorEvent.create(icon, () -> {
      Timber.d("Runnable bluetoothPresenter");
      bluetoothPresenter.toggleState();
    }));
  }

  @Override public void dataInitialState(boolean enabled, boolean managed) {
    @DrawableRes final int icon = enabled ? FAB_ICON_DATA_ON : FAB_ICON_DATA_OFF;
    FabColorBus.get().post(FabColorEvent.create(icon, () -> {
      Timber.d("Runnable dataPresenter");
      dataPresenter.toggleState();
    }));
  }

  @Override public void syncInitialState(boolean enabled, boolean managed) {
    @DrawableRes final int icon = enabled ? FAB_ICON_SYNC_ON : FAB_ICON_SYNC_OFF;
    FabColorBus.get().post(FabColorEvent.create(icon, () -> {
      Timber.d("Runnable syncPresenter");
      syncPresenter.toggleState();
    }));
  }

  @Override public void wifiInitialState(boolean enabled, boolean managed) {
    @DrawableRes final int icon = enabled ? FAB_ICON_WIFI_ON : FAB_ICON_WIFI_OFF;
    FabColorBus.get().post(FabColorEvent.create(icon, () -> {
      Timber.d("Runnable wifiPresenter");
      wifiPresenter.toggleState();
    }));
  }
}
