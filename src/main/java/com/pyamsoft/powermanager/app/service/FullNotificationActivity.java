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
import android.os.AsyncTask;
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
import com.pyamsoft.powermanager.app.manager.backend.ManagerBluetooth;
import com.pyamsoft.powermanager.app.manager.backend.ManagerData;
import com.pyamsoft.powermanager.app.manager.backend.ManagerSync;
import com.pyamsoft.powermanager.app.manager.backend.ManagerWifi;
import com.pyamsoft.powermanager.dagger.observer.state.BluetoothStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.DaggerStateObserverComponent;
import com.pyamsoft.powermanager.dagger.observer.state.DataStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.SyncStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.WifiStateObserver;
import com.pyamsoft.powermanager.dagger.service.DaggerFullNotificationComponent;
import com.pyamsoft.pydroid.model.AsyncDrawable;
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
      SyncStateObserver.View {

    @Inject ManagerWifi managerWifi;
    @Inject ManagerData managerData;
    @Inject ManagerBluetooth managerBluetooth;
    @Inject ManagerSync managerSync;

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

    private Unbinder unbinder;
    private AsyncVectorDrawableTask mainTask;
    private AsyncVectorDrawableTask closeTask;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setCancelable(true);

      DaggerStateObserverComponent.builder()
          .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
          .build()
          .inject(this);

      wifiStateObserver.setView(this);
      dataStateObserver.setView(this);
      bluetoothStateObserver.setView(this);
      syncStateObserver.setView(this);

      wifiStateObserver.register();
      dataStateObserver.register();
      bluetoothStateObserver.register();
      syncStateObserver.register();
    }

    private void cancelTask(@Nullable AsyncTask task) {
      if (task != null) {
        if (!task.isCancelled()) {
          Timber.d("Cancel task");
          task.cancel(true);
        }
      }
    }

    @Override public void onDestroy() {
      super.onDestroy();

      managerWifi.cleanup();
      managerData.cleanup();
      managerBluetooth.cleanup();
      managerSync.cleanup();

      wifiStateObserver.unregister();
      dataStateObserver.unregister();
      bluetoothStateObserver.unregister();
      syncStateObserver.unregister();

      cancelTask(mainTask);
      cancelTask(closeTask);

      unbinder.unbind();
    }

    @Override public void onResume() {
      super.onResume();
    }

    @Override public void onPause() {
      super.onPause();
    }

    private void destroy() {
      Timber.d("Destroy FullNotification");
      dismiss();
      FullNotificationPresenter.Bus.get().post(new FullNotificationPresenter.DismissEvent());
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
      @SuppressLint("InflateParams") final android.view.View dialogView =
          LayoutInflater.from(getActivity())
              .inflate(R.layout.dialog_full_notification, null, false);

      unbinder = ButterKnife.bind(this, dialogView);

      mainButton.setOnClickListener(view -> {
        destroy();
        startActivity(new Intent(getActivity(), MainActivity.class).setFlags(
            Intent.FLAG_ACTIVITY_SINGLE_TOP));
      });

      mainTask = new AsyncVectorDrawableTask(mainButton);
      mainTask.execute(new AsyncDrawable(getContext(), R.drawable.ic_settings_24dp));

      closeButton.setOnClickListener(view -> destroy());

      closeTask = new AsyncVectorDrawableTask(closeButton);
      closeTask.execute(new AsyncDrawable(getContext(), R.drawable.ic_close_24dp));

      return new AlertDialog.Builder(getActivity()).setView(dialogView).create();
    }

    @Override public void onCancel(DialogInterface dialog) {
      super.onCancel(dialog);
      destroy();
    }

    @Override public void onDataStateEnabled() {

    }

    @Override public void onDataStateDisabled() {

    }

    @Override public void onBluetoothStateEnabled() {

    }

    @Override public void onBluetoothStateDisabled() {

    }

    @Override public void onSyncStateEnabled() {

    }

    @Override public void onSyncStateDisabled() {

    }

    @Override public void onWifiStateEnabled() {

    }

    @Override public void onWifiStateDisabled() {

    }
  }
}
