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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.FabColorBus;
import com.pyamsoft.powermanager.app.manager.manage.ManagerManageFragment;
import com.pyamsoft.powermanager.app.manager.period.ManagerPeriodicFragment;
import com.pyamsoft.powermanager.app.modifier.InterestModifier;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.modifier.state.DaggerStateModifierComponent;
import com.pyamsoft.powermanager.dagger.modifier.state.StateModifierComponent;
import com.pyamsoft.powermanager.dagger.observer.manage.BluetoothManageObserver;
import com.pyamsoft.powermanager.dagger.observer.manage.DaggerManageObserverComponent;
import com.pyamsoft.powermanager.dagger.observer.manage.DataManageObserver;
import com.pyamsoft.powermanager.dagger.observer.manage.ManageObserverComponent;
import com.pyamsoft.powermanager.dagger.observer.manage.SyncManageObserver;
import com.pyamsoft.powermanager.dagger.observer.manage.WifiManageObserver;
import com.pyamsoft.powermanager.dagger.observer.state.BluetoothStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.DaggerStateObserverComponent;
import com.pyamsoft.powermanager.dagger.observer.state.DataStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.StateObserverComponent;
import com.pyamsoft.powermanager.dagger.observer.state.SyncStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.WifiStateObserver;
import com.pyamsoft.powermanager.model.FabColorEvent;
import timber.log.Timber;

public final class ManagerSettingsPagerAdapter extends FragmentStatePagerAdapter
    implements WifiStateObserver.View, DataStateObserver.View, BluetoothStateObserver.View,
    SyncStateObserver.View, WifiManageObserver.View, DataManageObserver.View,
    BluetoothManageObserver.View, SyncManageObserver.View {

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
  @NonNull private final ManagerManageFragment manageFragment;
  @NonNull private final Fragment periodicFragment;
  @NonNull private final String type;

  private InterestModifier stateModifier;
  private InterestObserver stateObserver;
  private InterestObserver manageObserver;

  public ManagerSettingsPagerAdapter(@NonNull FragmentActivity activity, @NonNull String type) {
    super(activity.getSupportFragmentManager());
    Timber.d("new ManagerSettingsPagerAdapter");
    manageFragment = ManagerManageFragment.newInstance(type);
    periodicFragment = ManagerPeriodicFragment.newInstance(type);
    this.type = type;

    final StateObserverComponent stateComponent = DaggerStateObserverComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build();

    final ManageObserverComponent manageComponent = DaggerManageObserverComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build();

    final StateModifierComponent stateModifierComponent = DaggerStateModifierComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build();

    int icon;
    switch (type) {
      case TYPE_WIFI:
        stateModifier = stateModifierComponent.provideWifiStateModifier();
        stateObserver = stateComponent.provideWifiStateObserver();
        manageObserver = manageComponent.provideWifiManagerObserver();
        icon = stateObserver.is() ? FAB_ICON_WIFI_ON : FAB_ICON_WIFI_OFF;
        break;
      case TYPE_DATA:
        stateModifier = stateModifierComponent.provideDataStateModifier();
        stateObserver = stateComponent.provideDataStateObserver();
        manageObserver = manageComponent.provideDataManagerObserver();
        icon = stateObserver.is() ? FAB_ICON_DATA_ON : FAB_ICON_DATA_OFF;
        break;
      case TYPE_BLUETOOTH:
        stateModifier = stateModifierComponent.provideBluetoothStateModifier();
        stateObserver = stateComponent.provideBluetoothStateObserver();
        manageObserver = manageComponent.provideBluetoothManagerObserver();
        icon = stateObserver.is() ? FAB_ICON_BLUETOOTH_ON : FAB_ICON_BLUETOOTH_OFF;
        break;
      case TYPE_SYNC:
        stateModifier = stateModifierComponent.provideSyncStateModifier();
        stateObserver = stateComponent.provideSyncStateObserver();
        manageObserver = manageComponent.provideSyncManagerObserver();
        icon = stateObserver.is() ? FAB_ICON_SYNC_ON : FAB_ICON_SYNC_OFF;
        break;
      default:
        throw new IllegalStateException("Invalid type: " + type);
    }

    //noinspection unchecked
    stateObserver.setView(this);
    //noinspection unchecked
    manageObserver.setView(this);

    stateObserver.register();
    manageObserver.register();

    // Register the initial states here
    FabColorBus.get().post(FabColorEvent.create(icon, () -> {
      Timber.d("Click!");
      if (stateObserver.is()) {
        stateModifier.unset();
      } else {
        stateModifier.set();
      }
    }));
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

    stateObserver.unregister();
    manageObserver.unregister();
  }

  @Override public void onWifiStateEnabled() {
    Timber.d("Wifi state enabled");
    FabColorBus.get().post(FabColorEvent.create(FAB_ICON_WIFI_ON, () -> {
      Timber.d("Click!");
      stateModifier.unset();
    }));
  }

  @Override public void onWifiStateDisabled() {
    Timber.d("Wifi state disabled");
    FabColorBus.get().post(FabColorEvent.create(FAB_ICON_WIFI_OFF, () -> {
      Timber.d("Click!");
      stateModifier.set();
    }));
  }

  @Override public void onDataStateEnabled() {
    Timber.d("Data state disabled");
    FabColorBus.get().post(FabColorEvent.create(FAB_ICON_DATA_ON, () -> {
      Timber.d("Click!");
      stateModifier.unset();
    }));
  }

  @Override public void onDataStateDisabled() {
    Timber.d("Data state disabled");
    FabColorBus.get().post(FabColorEvent.create(FAB_ICON_DATA_OFF, () -> {
      Timber.d("Click!");
      stateModifier.set();
    }));
  }

  @Override public void onBluetoothStateEnabled() {
    Timber.d("Bluetooth state enabled");
    FabColorBus.get().post(FabColorEvent.create(FAB_ICON_BLUETOOTH_ON, () -> {
      Timber.d("Click!");
      stateModifier.unset();
    }));
  }

  @Override public void onBluetoothStateDisabled() {
    Timber.d("Bluetooth state disabled");
    FabColorBus.get().post(FabColorEvent.create(FAB_ICON_BLUETOOTH_OFF, () -> {
      Timber.d("Click!");
      stateModifier.set();
    }));
  }

  @Override public void onSyncStateEnabled() {
    Timber.d("Sync state enabled");
    FabColorBus.get().post(FabColorEvent.create(FAB_ICON_SYNC_ON, () -> {
      Timber.d("Click!");
      stateModifier.unset();
    }));
  }

  @Override public void onSyncStateDisabled() {
    Timber.d("Sync state disabled");
    FabColorBus.get().post(FabColorEvent.create(FAB_ICON_SYNC_OFF, () -> {
      Timber.d("Click!");
      stateModifier.set();
    }));
  }

  @Override public void onDataManageEnabled() {
    Timber.d("Data manage enabled");
    manageFragment.enableManaged();
  }

  @Override public void onDataManageDisabled() {
    Timber.d("Data manage disabled");
    manageFragment.disableManaged();
  }

  @Override public void onSyncManageEnabled() {
    Timber.d("Sync manage enabled");
    manageFragment.enableManaged();
  }

  @Override public void onSyncManageDisabled() {
    Timber.d("Wifi manage disabled");
    manageFragment.disableManaged();
  }

  @Override public void onWifiManageEnabled() {
    Timber.d("Wifi manage enabled");
    manageFragment.enableManaged();
  }

  @Override public void onWifiManageDisabled() {
    Timber.d("Wifi manage disabled");
    manageFragment.disableManaged();
  }

  @Override public void onBluetoothManageEnabled() {
    Timber.d("Bluetooth manage enabled");
    manageFragment.enableManaged();
  }

  @Override public void onBluetoothManageDisabled() {
    Timber.d("Wifi manage disabled");
    manageFragment.disableManaged();
  }
}
