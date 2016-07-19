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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.MainActivity;
import com.pyamsoft.powermanager.dagger.modifier.manage.BluetoothManageModifier;
import com.pyamsoft.powermanager.dagger.modifier.manage.DaggerManageModifierComponent;
import com.pyamsoft.powermanager.dagger.modifier.manage.DataManageModifier;
import com.pyamsoft.powermanager.dagger.modifier.manage.SyncManageModifier;
import com.pyamsoft.powermanager.dagger.modifier.manage.WifiManageModifier;
import com.pyamsoft.powermanager.dagger.modifier.state.BluetoothStateModifier;
import com.pyamsoft.powermanager.dagger.modifier.state.DaggerStateModifierComponent;
import com.pyamsoft.powermanager.dagger.modifier.state.DataStateModifier;
import com.pyamsoft.powermanager.dagger.modifier.state.SyncStateModifier;
import com.pyamsoft.powermanager.dagger.modifier.state.WifiStateModifier;
import com.pyamsoft.powermanager.dagger.observer.manage.BluetoothManageObserver;
import com.pyamsoft.powermanager.dagger.observer.manage.DaggerManageObserverComponent;
import com.pyamsoft.powermanager.dagger.observer.manage.DataManageObserver;
import com.pyamsoft.powermanager.dagger.observer.manage.SyncManageObserver;
import com.pyamsoft.powermanager.dagger.observer.manage.WifiManageObserver;
import com.pyamsoft.powermanager.dagger.observer.state.BluetoothStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.DaggerStateObserverComponent;
import com.pyamsoft.powermanager.dagger.observer.state.DataStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.SyncStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.WifiStateObserver;
import com.pyamsoft.powermanager.dagger.service.DaggerFullNotificationComponent;
import com.pyamsoft.pydroid.model.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncTaskMap;
import com.pyamsoft.pydroid.tool.AsyncVectorDrawableTask;
import com.pyamsoft.pydroid.util.AppUtil;
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
      implements WifiStateObserver.View, DataStateObserver.View, BluetoothStateObserver.View,
      SyncStateObserver.View, WifiManageObserver.View, DataManageObserver.View,
      BluetoothManageObserver.View, SyncManageObserver.View {

    @NonNull private final AsyncTaskMap taskMap = new AsyncTaskMap();
    @BindView(R.id.full_notification_wifi_manage) SwitchCompat wifiManage;
    @BindView(R.id.full_notification_wifi_toggle) ImageButton wifiToggle;
    @BindView(R.id.full_notification_bluetooth_manage) SwitchCompat bluetoothManage;
    @BindView(R.id.full_notification_bluetooth_toggle) ImageButton bluetoothToggle;
    @BindView(R.id.full_notification_data_manage) SwitchCompat dataManage;
    @BindView(R.id.full_notification_data_toggle) ImageButton dataToggle;
    @BindView(R.id.full_notification_sync_manage) SwitchCompat syncManage;
    @BindView(R.id.full_notification_sync_toggle) ImageButton syncToggle;

    @BindView(R.id.full_notification_main) ImageView mainButton;
    @BindView(R.id.full_notification_close) ImageView closeButton;

    @Inject WifiStateObserver wifiStateObserver;
    @Inject DataStateObserver dataStateObserver;
    @Inject BluetoothStateObserver bluetoothStateObserver;
    @Inject SyncStateObserver syncStateObserver;

    @Inject WifiManageObserver wifiManageObserver;
    @Inject DataManageObserver dataManageObserver;
    @Inject BluetoothManageObserver bluetoothManageObserver;
    @Inject SyncManageObserver syncManageObserver;

    @Inject WifiStateModifier wifiStateModifier;
    @Inject DataStateModifier dataStateModifier;
    @Inject BluetoothStateModifier bluetoothStateModifier;
    @Inject SyncStateModifier syncStateModifier;

    @Inject WifiManageModifier wifiManageModifier;
    @Inject DataManageModifier dataManageModifier;
    @Inject BluetoothManageModifier bluetoothManageModifier;
    @Inject SyncManageModifier syncManageModifier;

    private Unbinder unbinder;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setCancelable(true);
      Timber.d("onCreate");

      DaggerStateObserverComponent.builder()
          .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
          .build()
          .inject(this);

      DaggerManageObserverComponent.builder()
          .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
          .build()
          .inject(this);

      DaggerStateModifierComponent.builder()
          .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
          .build()
          .inject(this);

      DaggerManageModifierComponent.builder()
          .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
          .build()
          .inject(this);

      wifiStateObserver.setView(this);
      dataStateObserver.setView(this);
      bluetoothStateObserver.setView(this);
      syncStateObserver.setView(this);

      wifiManageObserver.setView(this);
      dataManageObserver.setView(this);
      bluetoothManageObserver.setView(this);
      syncManageObserver.setView(this);

      wifiStateObserver.register();
      dataStateObserver.register();
      bluetoothStateObserver.register();
      syncStateObserver.register();

      wifiManageObserver.register();
      dataManageObserver.register();
      bluetoothManageObserver.register();
      syncManageObserver.register();
    }

    @Override public void onDestroy() {
      super.onDestroy();
      Timber.d("onDestroy");

      wifiStateObserver.unregister();
      dataStateObserver.unregister();
      bluetoothStateObserver.unregister();
      syncStateObserver.unregister();

      wifiManageObserver.unregister();
      dataManageObserver.unregister();
      bluetoothManageObserver.unregister();
      syncManageObserver.unregister();

      taskMap.clear();
      unbinder.unbind();
    }

    private void destroy() {
      Timber.d("Destroy FullNotification");
      dismiss();
      FullNotificationPresenter.Bus.get().post(new FullNotificationPresenter.DismissEvent());
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
      Timber.d("onCreateDialog");
      @SuppressLint("InflateParams") final android.view.View dialogView =
          LayoutInflater.from(getActivity())
              .inflate(R.layout.dialog_full_notification, null, false);

      unbinder = ButterKnife.bind(this, dialogView);

      mainButton.setOnClickListener(view -> {
        destroy();
        startActivity(new Intent(getActivity(), MainActivity.class).setFlags(
            Intent.FLAG_ACTIVITY_SINGLE_TOP));
      });

      final AsyncVectorDrawableTask mainTask = new AsyncVectorDrawableTask(mainButton);
      mainTask.execute(new AsyncDrawable(getContext(), R.drawable.ic_settings_24dp));
      taskMap.put("main", mainTask);

      closeButton.setOnClickListener(view -> destroy());

      final AsyncVectorDrawableTask closeTask = new AsyncVectorDrawableTask(closeButton);
      closeTask.execute(new AsyncDrawable(getContext(), R.drawable.ic_close_24dp));
      taskMap.put("close", closeTask);

      wifiToggle.setOnClickListener(view -> {
        Timber.d("Wifi clicked");
        if (wifiStateObserver.is()) {
          Timber.d("Disable wifi");
          wifiStateModifier.unset();
        } else {
          Timber.d("Enable wifi");
          wifiStateModifier.set();
        }
      });

      dataToggle.setOnClickListener(view -> {
        Timber.d("Data clicked");
        if (dataStateObserver.is()) {
          Timber.d("Disable data");
          dataStateModifier.unset();
        } else {
          Timber.d("Enable data");
          dataStateModifier.set();
        }
      });

      bluetoothToggle.setOnClickListener(view -> {
        Timber.d("Bluetooth clicked");
        if (bluetoothStateObserver.is()) {
          Timber.d("Disable bluetooth");
          bluetoothStateModifier.unset();
        } else {
          Timber.d("Enable bluetooth");
          bluetoothStateModifier.set();
        }
      });

      syncToggle.setOnClickListener(view -> {
        Timber.d("Sync clicked");
        if (syncStateObserver.is()) {
          Timber.d("Disable sync");
          syncStateModifier.unset();
        } else {
          Timber.d("Enable sync");
          syncStateModifier.set();
        }
      });

      setWifiToggleState(wifiStateObserver.is());
      setDataToggleState(dataStateObserver.is());
      setBluetoothToggleState(bluetoothStateObserver.is());
      setSyncToggleState(syncStateObserver.is());

      setWifiManageState(wifiManageObserver.is());
      setDataManageState(dataManageObserver.is());
      setBluetoothManageState(bluetoothManageObserver.is());
      setSyncManageState(syncManageObserver.is());

      return new AlertDialog.Builder(getActivity()).setView(dialogView).create();
    }

    @Override public void onCancel(DialogInterface dialog) {
      super.onCancel(dialog);
      destroy();
    }

    private void setWifiToggleState(boolean enabled) {
      int icon = enabled ? R.drawable.ic_network_wifi_24dp : R.drawable.ic_signal_wifi_off_24dp;
      int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      final AsyncVectorDrawableTask task = new AsyncVectorDrawableTask(wifiToggle, color);
      task.execute(new AsyncDrawable(getActivity(), icon));
      taskMap.put("wifi", task);
    }

    private void setDataToggleState(boolean enabled) {
      int icon = enabled ? R.drawable.ic_network_cell_24dp : R.drawable.ic_signal_cellular_off_24dp;
      int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      final AsyncVectorDrawableTask task = new AsyncVectorDrawableTask(dataToggle, color);
      task.execute(new AsyncDrawable(getActivity(), icon));
      taskMap.put("data", task);
    }

    private void setBluetoothToggleState(boolean enabled) {
      int icon = enabled ? R.drawable.ic_bluetooth_24dp : R.drawable.ic_bluetooth_disabled_24dp;
      int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      final AsyncVectorDrawableTask task = new AsyncVectorDrawableTask(bluetoothToggle, color);
      task.execute(new AsyncDrawable(getActivity(), icon));
      taskMap.put("bluetooth", task);
    }

    private void setSyncToggleState(boolean enabled) {
      int icon = enabled ? R.drawable.ic_sync_24dp : R.drawable.ic_sync_disabled_24dp;
      int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      final AsyncVectorDrawableTask task = new AsyncVectorDrawableTask(syncToggle, color);
      task.execute(new AsyncDrawable(getActivity(), icon));
      taskMap.put("sync", task);
    }

    private void setWifiManageState(boolean state) {
      wifiManage.setOnCheckedChangeListener(null);
      wifiManage.setChecked(state);
      wifiManage.setOnCheckedChangeListener((compoundButton, b) -> {
        Timber.d("Wifi Manage click");
        if (b) {
          wifiManageModifier.set();
        } else {
          wifiManageModifier.unset();
        }
      });
    }

    private void setDataManageState(boolean state) {
      dataManage.setOnCheckedChangeListener(null);
      dataManage.setChecked(state);
      dataManage.setOnCheckedChangeListener((compoundButton, b) -> {
        Timber.d("Data Manage click");
        if (b) {
          dataManageModifier.set();
        } else {
          dataManageModifier.unset();
        }
      });
    }

    private void setBluetoothManageState(boolean state) {
      bluetoothManage.setOnCheckedChangeListener(null);
      bluetoothManage.setChecked(state);
      bluetoothManage.setOnCheckedChangeListener((compoundButton, b) -> {
        Timber.d("Bluetooth Manage click");
        if (b) {
          bluetoothManageModifier.set();
        } else {
          bluetoothManageModifier.unset();
        }
      });
    }

    private void setSyncManageState(boolean state) {
      syncManage.setOnCheckedChangeListener(null);
      syncManage.setChecked(state);
      syncManage.setOnCheckedChangeListener((compoundButton, b) -> {
        Timber.d("Sync Manage click");
        if (b) {
          syncManageModifier.set();
        } else {
          syncManageModifier.unset();
        }
      });
    }

    @Override public void onDataStateEnabled() {
      Timber.d("Data enabled");
      setDataToggleState(true);
    }

    @Override public void onDataStateDisabled() {
      Timber.d("Data disabled");
      setDataToggleState(false);
    }

    @Override public void onBluetoothStateEnabled() {
      Timber.d("Bluetooth enabled");
      setBluetoothToggleState(true);
    }

    @Override public void onBluetoothStateDisabled() {
      Timber.d("Bluetooth disabled");
      setBluetoothToggleState(false);
    }

    @Override public void onSyncStateEnabled() {
      Timber.d("Sync enabled");
      setSyncToggleState(true);
    }

    @Override public void onSyncStateDisabled() {
      Timber.d("Sync disabled");
      setSyncToggleState(false);
    }

    @Override public void onWifiStateEnabled() {
      Timber.d("Wifi enabled");
      setWifiToggleState(true);
    }

    @Override public void onWifiStateDisabled() {
      Timber.d("Wifi disabled");
      setWifiToggleState(false);
    }

    @Override public void onWifiManageEnabled() {
      Timber.d("Wifi managed");
      setWifiManageState(true);
    }

    @Override public void onWifiManageDisabled() {
      Timber.d("Wifi not managed");
      setWifiManageState(false);
    }

    @Override public void onDataManageEnabled() {
      Timber.d("Data managed");
      setDataManageState(true);
    }

    @Override public void onDataManageDisabled() {
      Timber.d("Data not managed");
      setDataManageState(false);
    }

    @Override public void onBluetoothManageEnabled() {
      Timber.d("Bluetooth managed");
      setBluetoothManageState(true);
    }

    @Override public void onBluetoothManageDisabled() {
      Timber.d("Bluetooth not managed");
      setBluetoothManageState(false);
    }

    @Override public void onSyncManageEnabled() {
      Timber.d("Sync managed");
      setSyncManageState(true);
    }

    @Override public void onSyncManageDisabled() {
      Timber.d("Sync not managed");
      setSyncManageState(false);
    }
  }
}
