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

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
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
      implements WifiView, DataView, BluetoothView, SyncView {

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
    }

    @Override public void onDestroy() {
      super.onDestroy();

      wifiPresenter.unbindView();
      dataPresenter.unbindView();
      bluetoothPresenter.unbindView();
      syncPresenter.unbindView();

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
      final View dialogView = LayoutInflater.from(getActivity())
          .inflate(R.layout.dialog_full_notification, null, false);

      // TODO init view
      unbinder = ButterKnife.bind(this, dialogView);
      wifiPresenter.getCurrentState();

      return new AlertDialog.Builder(getActivity()).setView(dialogView).create();
    }

    @Override public void onCancel(DialogInterface dialog) {
      super.onCancel(dialog);
      destroy();
    }

    @Override public void bluetoothInitialState(boolean enabled, boolean managed) {

    }

    @Override public void toggleBluetoothEnabled() {

    }

    @Override public void toggleBluetoothDisabled() {

    }

    @Override public void dataInitialState(boolean enabled, boolean managed) {

    }

    @Override public void toggleDataEnabled() {

    }

    @Override public void toggleDataDisabled() {

    }

    @Override public void syncInitialState(boolean enabled, boolean managed) {

    }

    @Override public void toggleSyncEnabled() {

    }

    @Override public void toggleSyncDisabled() {

    }

    @Override public void wifiInitialState(boolean enabled, boolean managed) {
      Timber.d("wifiInitialState");
      int res = enabled ? R.drawable.ic_network_wifi_24dp : R.drawable.ic_signal_wifi_off_24dp;
      int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      Drawable d = ContextCompat.getDrawable(getActivity(), res);
      d = DrawableUtil.tintDrawableFromColor(d, ContextCompat.getColor(getContext(), color));
      wifiToggle.setImageDrawable(d);
      wifiToggle.setOnClickListener(view -> {
        Timber.d("Toggle wifi state");
        wifiPresenter.toggleState();
      });

      wifiManage.setOnCheckedChangeListener(null);
      wifiManage.setChecked(managed);
      wifiManage.setOnCheckedChangeListener((compoundButton, b) -> {
        // TODO set managed
        Timber.d("Set manage wifi");
      });
    }

    @Override public void toggleWifiDisabled() {
      Timber.d("Disable wifi");
      managerWifi.disable(0, false);
    }

    @Override public void toggleWifiEnabled() {
      Timber.d("Enable wifi");
      managerWifi.enable(0, false);
    }

    @Override public void startManagingWearable() {

    }

    @Override public void stopManagingWearable() {

    }
  }
}
