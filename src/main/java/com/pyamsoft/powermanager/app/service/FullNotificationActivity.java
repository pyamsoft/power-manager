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

package com.pyamsoft.powermanager.app.service;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.manager.BluetoothPresenter;
import com.pyamsoft.powermanager.app.manager.BluetoothView;
import com.pyamsoft.powermanager.app.manager.DataPresenter;
import com.pyamsoft.powermanager.app.manager.DataView;
import com.pyamsoft.powermanager.app.manager.SyncPresenter;
import com.pyamsoft.powermanager.app.manager.SyncView;
import com.pyamsoft.powermanager.app.manager.WifiPresenter;
import com.pyamsoft.powermanager.app.manager.WifiView;
import com.pyamsoft.powermanager.app.manager.backend.ManagerBluetooth;
import com.pyamsoft.powermanager.app.manager.backend.ManagerData;
import com.pyamsoft.powermanager.app.manager.backend.ManagerSync;
import com.pyamsoft.powermanager.app.manager.backend.ManagerWifi;
import com.pyamsoft.powermanager.app.observer.DataStateObserver;
import com.pyamsoft.powermanager.app.observer.WifiStateObserver;
import com.pyamsoft.powermanager.dagger.manager.DaggerManagerSettingsComponent;
import com.pyamsoft.powermanager.dagger.service.DaggerFullNotificationComponent;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.DrawableUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class FullNotificationActivity extends AppCompatActivity
    implements FullNotificationPresenter.FullNotificationView {

  @Inject FullNotificationPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    DaggerFullNotificationComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build()
        .inject(this);

    presenter.bindView(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.unbindView();
  }

  @Override protected void onResume() {
    super.onResume();

    Timber.d("onResume");
    presenter.onResume();
  }

  @Override protected void onPause() {
    super.onPause();

    Timber.d("onPause");
    presenter.onPause();
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    Timber.d("Show Full Notification");
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(), new FullDialog(),
        "full_dialog");
  }

  @Override public void onDismissEvent() {
    Timber.d("Full Notification Dismissed");
    finish();
  }

  public static final class FullDialog extends DialogFragment
      implements WifiView, DataView, BluetoothView, SyncView,
      WifiStateObserver.WifiStateObserverView, DataStateObserver.DataStateObserverView {

    @Inject WifiPresenter wifiPresenter;
    @Inject ManagerWifi managerWifi;

    @Inject DataPresenter dataPresenter;
    @Inject ManagerData managerData;

    @Inject BluetoothPresenter bluetoothPresenter;
    @Inject ManagerBluetooth managerBluetooth;

    @Inject SyncPresenter syncPresenter;
    @Inject ManagerSync managerSync;

    @BindView(R.id.full_notification_wifi_manage) SwitchCompat wifiManage;
    @BindView(R.id.full_notification_wifi_toggle) ImageButton wifiToggle;
    @BindView(R.id.full_notification_bluetooth_manage) SwitchCompat bluetoothManage;
    @BindView(R.id.full_notification_bluetooth_toggle) ImageButton bluetoothToggle;
    @BindView(R.id.full_notification_data_manage) SwitchCompat dataManage;
    @BindView(R.id.full_notification_data_toggle) ImageButton dataToggle;
    @BindView(R.id.full_notification_sync_manage) SwitchCompat syncManage;
    @BindView(R.id.full_notification_sync_toggle) ImageButton syncToggle;

    private WifiStateObserver wifiStateObserver;
    private DataStateObserver dataStateObserver;
    private Unbinder unbinder;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setCancelable(true);

      DaggerManagerSettingsComponent.builder()
          .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
          .build()
          .inject(this);

      wifiPresenter.bindView(this);
      dataPresenter.bindView(this);
      bluetoothPresenter.bindView(this);
      syncPresenter.bindView(this);

      wifiStateObserver = new WifiStateObserver(getContext(), this);
      dataStateObserver = new DataStateObserver(getContext(), this);

      wifiStateObserver.register();
      dataStateObserver.register();
    }

    @Override public void onDestroy() {
      super.onDestroy();

      wifiPresenter.unbindView();
      dataPresenter.unbindView();
      bluetoothPresenter.unbindView();
      syncPresenter.unbindView();

      managerWifi.cleanup();
      managerData.cleanup();
      managerBluetooth.cleanup();
      managerSync.cleanup();

      wifiStateObserver.unregister();
      dataStateObserver.unregister();

      unbinder.unbind();
    }

    @Override public void onResume() {
      super.onResume();
      wifiPresenter.onResume();
      dataPresenter.onResume();
      bluetoothPresenter.onResume();
      syncPresenter.onResume();
    }

    @Override public void onPause() {
      super.onPause();
      wifiPresenter.onPause();
      dataPresenter.onPause();
      bluetoothPresenter.onPause();
      syncPresenter.onPause();
    }

    private void destroy() {
      Timber.d("Destroy FullNotification");
      dismiss();
      FullNotificationPresenter.Bus.get().post(new FullNotificationPresenter.DismissEvent());
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
      @SuppressLint("InflateParams") final View dialogView = LayoutInflater.from(getActivity())
          .inflate(R.layout.dialog_full_notification, null, false);

      // TODO init view
      unbinder = ButterKnife.bind(this, dialogView);
      wifiPresenter.getCurrentState();
      dataPresenter.getCurrentState();
      bluetoothPresenter.getCurrentState();
      syncPresenter.getCurrentState();

      return new AlertDialog.Builder(getActivity()).setView(dialogView).create();
    }

    @Override public void onCancel(DialogInterface dialog) {
      super.onCancel(dialog);
      destroy();
    }

    @Override public void bluetoothInitialState(boolean enabled, boolean managed) {
      Timber.d("bluetoothInitialState");
      int res = enabled ? R.drawable.ic_bluetooth_24dp : R.drawable.ic_bluetooth_disabled_24dp;
      int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      Drawable d = ContextCompat.getDrawable(getActivity(), res);
      d = DrawableUtil.tintDrawableFromColor(d, ContextCompat.getColor(getContext(), color));
      bluetoothToggle.setImageDrawable(d);
      bluetoothToggle.setOnClickListener(view -> {
        Timber.d("Toggle wifi state");
        bluetoothPresenter.toggleState();
      });

      setBluetoothOnChecked(managed);
    }

    @UiThread private void setBluetoothOnChecked(boolean managed) {
      bluetoothManage.setOnCheckedChangeListener(null);
      bluetoothManage.setChecked(managed);

      final CompoundButton.OnCheckedChangeListener listener =
          new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
              compoundButton.setOnCheckedChangeListener(null);
              compoundButton.setChecked(!b);
              compoundButton.setOnCheckedChangeListener(this);

              Timber.d("Set manage bluetooth");
              bluetoothPresenter.toggleManaged();
            }
          };

      bluetoothManage.setOnCheckedChangeListener(listener);
    }

    @Override public void toggleBluetoothEnabled() {
      Timber.d("Enable bluetooth");
      managerBluetooth.enable(0, false);
    }

    @Override public void toggleBluetoothDisabled() {
      Timber.d("Disable bluetooth");
      managerBluetooth.disable(0, false);
    }

    @Override public void bluetoothStartManaged() {
      // KLUDGE we need this or UI crash
      getActivity().runOnUiThread(() -> {
        Timber.d("Bluetooth is managed");
        setBluetoothOnChecked(true);
      });
    }

    @Override public void bluetoothStopManaged() {
      // KLUDGE we need this or UI crash
      getActivity().runOnUiThread(() -> {
        Timber.d("Bluetooth is not managed");
        setBluetoothOnChecked(false);
      });
    }

    @UiThread private void setDataState(boolean enabled) {
      int res = enabled ? R.drawable.ic_network_cell_24dp : R.drawable.ic_signal_cellular_off_24dp;
      int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      Drawable d = ContextCompat.getDrawable(getActivity(), res);
      d = DrawableUtil.tintDrawableFromColor(d, ContextCompat.getColor(getContext(), color));
      dataToggle.setImageDrawable(d);
    }

    @Override public void dataInitialState(boolean enabled, boolean managed) {
      Timber.d("dataInitialState");
      dataToggle.setOnClickListener(view -> {
        Timber.d("Toggle data state");
        dataPresenter.toggleState();
      });

      setDataState(enabled);
      setDataChecked(managed);
    }

    @Override public void toggleDataEnabled() {
      Timber.d("Enable data");
      managerData.enable(0, false);
    }

    @Override public void toggleDataDisabled() {
      Timber.d("Disable data");
      managerData.disable(0, false);
    }

    @UiThread private void setDataChecked(boolean managed) {
      dataManage.setOnCheckedChangeListener(null);
      dataManage.setChecked(managed);

      final CompoundButton.OnCheckedChangeListener listener =
          new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
              compoundButton.setOnCheckedChangeListener(null);
              compoundButton.setChecked(!b);
              compoundButton.setOnCheckedChangeListener(this);

              Timber.d("Set manage data");
              dataPresenter.toggleManaged();
            }
          };

      dataManage.setOnCheckedChangeListener(listener);
    }

    @Override public void dataStartManaged() {
      // KLUDGE we need this or UI crash
      getActivity().runOnUiThread(() -> {
        Timber.d("Data is managed");
        setDataChecked(true);
      });
    }

    @Override public void dataStopManaged() {
      // KLUDGE we need this or UI crash
      getActivity().runOnUiThread(() -> {
        Timber.d("Data is not managed");
        setDataChecked(false);
      });
    }

    @UiThread private void setSyncChecked(boolean managed) {
      syncManage.setOnCheckedChangeListener(null);
      syncManage.setChecked(managed);

      final CompoundButton.OnCheckedChangeListener listener =
          new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
              compoundButton.setOnCheckedChangeListener(null);
              compoundButton.setChecked(!b);
              compoundButton.setOnCheckedChangeListener(this);

              Timber.d("Set manage sync");
              syncPresenter.toggleManaged();
            }
          };

      syncManage.setOnCheckedChangeListener(listener);
    }

    @Override public void syncInitialState(boolean enabled, boolean managed) {
      Timber.d("syncInitialState");
      int res = enabled ? R.drawable.ic_sync_24dp : R.drawable.ic_sync_disabled_24dp;
      int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      Drawable d = ContextCompat.getDrawable(getActivity(), res);
      d = DrawableUtil.tintDrawableFromColor(d, ContextCompat.getColor(getContext(), color));
      syncToggle.setImageDrawable(d);
      syncToggle.setOnClickListener(view -> {
        Timber.d("Toggle sync state");
        syncPresenter.toggleState();
      });

      setSyncChecked(managed);
    }

    @Override public void toggleSyncEnabled() {
      Timber.d("Enable sync");
      managerSync.enable(0, false);
    }

    @Override public void toggleSyncDisabled() {
      Timber.d("Disable sync");
      managerSync.disable(0, false);
    }

    @Override public void syncStartManaged() {
      // KLUDGE we need this or UI crash
      getActivity().runOnUiThread(() -> {
        Timber.d("Sync is managed");
        setSyncChecked(true);
      });
    }

    @Override public void syncStopManaged() {
      // KLUDGE we need this or UI crash
      getActivity().runOnUiThread(() -> {
        Timber.d("Sync is not managed");
        setSyncChecked(false);
      });
    }

    @Override public void wifiInitialState(boolean enabled, boolean managed) {
      wifiToggle.setOnClickListener(view -> {
        Timber.d("Toggle wifi state");
        wifiPresenter.toggleState();
      });

      setWifiState(enabled);
      setWifiOnChecked(managed);
    }

    @UiThread private void setWifiState(boolean enabled) {
      Timber.d("wifiInitialState");
      int res = enabled ? R.drawable.ic_network_wifi_24dp : R.drawable.ic_signal_wifi_off_24dp;
      int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      Drawable d = ContextCompat.getDrawable(getActivity(), res);
      d = DrawableUtil.tintDrawableFromColor(d, ContextCompat.getColor(getContext(), color));
      wifiToggle.setImageDrawable(d);
    }

    @UiThread private void setWifiOnChecked(boolean managed) {
      wifiManage.setOnCheckedChangeListener(null);
      wifiManage.setChecked(managed);

      final CompoundButton.OnCheckedChangeListener listener =
          new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
              compoundButton.setOnCheckedChangeListener(null);
              compoundButton.setChecked(!b);
              compoundButton.setOnCheckedChangeListener(this);

              Timber.d("Set manage wifi");
              wifiPresenter.toggleManaged();
            }
          };
      wifiManage.setOnCheckedChangeListener(listener);
    }

    @Override public void toggleWifiDisabled() {
      Timber.d("Disable wifi");
      managerWifi.disable(0, false);
    }

    @Override public void toggleWifiEnabled() {
      Timber.d("Enable wifi");
      managerWifi.enable(0, false);
    }

    @Override public void wifiStartManaged() {
      // KLUDGE we need this or UI crash
      getActivity().runOnUiThread(() -> {
        Timber.d("Wifi is managed");
        setWifiOnChecked(true);
      });
    }

    @Override public void wifiStopManaged() {
      // KLUDGE we need this or UI crash
      getActivity().runOnUiThread(() -> {
        Timber.d("Wifi is not managed");
        setWifiOnChecked(false);
      });
    }

    @Override public void onWifiStateEnabled() {
      Timber.d("Wifi state enabled");
      setWifiState(true);
    }

    @Override public void onWifiStateDisabled() {
      Timber.d("Wifi state disabled");
      setWifiState(false);
    }

    @Override public void startManagingWearable() {

    }

    @Override public void stopManagingWearable() {

    }

    @Override public void onDataStateEnabled() {
      Timber.d("Data state enabled");
      setDataState(true);
    }

    @Override public void onDataStateDisabled() {
      Timber.d("Data state disabled");
      setDataState(false);
    }
  }
}
