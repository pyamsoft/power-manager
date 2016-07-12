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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import com.pyamsoft.powermanager.dagger.manager.DaggerManagerSettingsComponent;
import com.pyamsoft.powermanager.dagger.service.DaggerFullNotificationComponent;
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
      implements WifiView, DataView, BluetoothView, SyncView {

    @Inject WifiPresenter wifiPresenter;
    @Inject DataPresenter dataPresenter;
    @Inject BluetoothPresenter bluetoothPresenter;
    @Inject SyncPresenter syncPresenter;

    @BindView(R.id.full_notification_wifi_manage) SwitchCompat wifiManage;
    @BindView(R.id.full_notification_wifi_toggle) ImageButton wifiToggl;

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

    }

    @Override public void toggleWifiDisabled() {

    }

    @Override public void toggleWifiEnabled() {

    }

    @Override public void startManagingWearable() {

    }

    @Override public void stopManagingWearable() {

    }
  }
}
