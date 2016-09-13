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

package com.pyamsoft.powermanager.app.service.notification;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.MainActivity;
import com.pyamsoft.powermanager.bus.FullNotificationBus;
import com.pyamsoft.powermanager.model.event.DismissEvent;
import com.pyamsoft.pydroid.base.PersistLoader;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncDrawableMap;
import com.pyamsoft.pydroid.util.PersistentCache;
import rx.Subscription;
import timber.log.Timber;

public class NotificationDialog extends DialogFragment implements NotificationDialogPresenter.View {

  @NonNull private static final String KEY_PRESENTER = "key_dialog_notification_presenter";
  @NonNull final AsyncDrawableMap taskMap = new AsyncDrawableMap();
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

  NotificationDialogPresenter presenter;

  private Unbinder unbinder;
  private long loadedKey;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(true);
    Timber.d("onCreate");

    loadedKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<NotificationDialogPresenter>() {
              @NonNull @Override public PersistLoader<NotificationDialogPresenter> createLoader() {
                return new NotificationDialogPresenterLoader(getContext());
              }

              @Override
              public void onPersistentLoaded(@NonNull NotificationDialogPresenter persist) {
                presenter = persist;
              }
            });
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Timber.d("onDestroy");

    taskMap.clear();
    unbinder.unbind();

    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_PRESENTER, loadedKey);
    super.onSaveInstanceState(outState);
  }

  private void destroy() {
    Timber.d("Destroy FullNotification");
    dismiss();
    FullNotificationBus.get().post(DismissEvent.create());
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    Timber.d("onCreateDialog");
    @SuppressLint("InflateParams") final android.view.View dialogView =
        LayoutInflater.from(getActivity()).inflate(R.layout.dialog_full_notification, null, false);

    unbinder = ButterKnife.bind(this, dialogView);

    mainButton.setOnClickListener(view -> {
      destroy();
      startActivity(
          new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    });

    final Subscription mainTask =
        AsyncDrawable.with(getContext()).load(R.drawable.ic_settings_24dp).into(mainButton);
    taskMap.put("main", mainTask);

    closeButton.setOnClickListener(view -> destroy());

    final Subscription closeTask =
        AsyncDrawable.with(getContext()).load(R.drawable.ic_close_24dp).into(closeButton);
    taskMap.put("close", closeTask);

    wifiToggle.setOnClickListener(view -> presenter.wifiToggleClicked());

    dataToggle.setOnClickListener(view -> presenter.dataToggleClicked());

    bluetoothToggle.setOnClickListener(view -> presenter.bluetoothToggleClicked());

    syncToggle.setOnClickListener(view -> presenter.syncToggleClicked());

    return new AlertDialog.Builder(getActivity()).setView(dialogView).create();
  }

  @Override public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);
    destroy();
  }

  @Override public void setWifiToggleState(boolean enabled) {
    @DrawableRes final int icon =
        enabled ? R.drawable.ic_network_wifi_24dp : R.drawable.ic_signal_wifi_off_24dp;
    @ColorRes final int color = enabled ? R.color.lightblueA200 : android.R.color.black;
    final Subscription task =
        AsyncDrawable.with(getContext()).load(icon).tint(color).into(wifiToggle);
    taskMap.put("wifi", task);
  }

  @Override public void setDataToggleState(boolean enabled) {
    @DrawableRes final int icon =
        enabled ? R.drawable.ic_network_cell_24dp : R.drawable.ic_signal_cellular_off_24dp;
    @ColorRes final int color = enabled ? R.color.lightblueA200 : android.R.color.black;
    final Subscription task =
        AsyncDrawable.with(getContext()).load(icon).tint(color).into(dataToggle);
    taskMap.put("data", task);
  }

  @Override public void setBluetoothToggleState(boolean enabled) {
    @DrawableRes final int icon =
        enabled ? R.drawable.ic_bluetooth_24dp : R.drawable.ic_bluetooth_disabled_24dp;
    @ColorRes final int color = enabled ? R.color.lightblueA200 : android.R.color.black;
    final Subscription task =
        AsyncDrawable.with(getContext()).load(icon).tint(color).into(bluetoothToggle);
    taskMap.put("bluetooth", task);
  }

  @Override public void setSyncToggleState(boolean enabled) {
    @DrawableRes final int icon =
        enabled ? R.drawable.ic_sync_24dp : R.drawable.ic_sync_disabled_24dp;
    @ColorRes final int color = enabled ? R.color.lightblueA200 : android.R.color.black;
    final Subscription task =
        AsyncDrawable.with(getContext()).load(icon).tint(color).into(syncToggle);
    taskMap.put("sync", task);
  }

  @Override public void setWifiManageState(boolean state) {
    wifiManage.setOnCheckedChangeListener(null);
    wifiManage.setChecked(state);
    wifiManage.setOnCheckedChangeListener((compoundButton, b) -> presenter.wifiManageClicked());
  }

  @Override public void setDataManageState(boolean state) {
    dataManage.setOnCheckedChangeListener(null);
    dataManage.setChecked(state);
    dataManage.setOnCheckedChangeListener((compoundButton, b) -> presenter.dataManageClicked());
  }

  @Override public void setBluetoothManageState(boolean state) {
    bluetoothManage.setOnCheckedChangeListener(null);
    bluetoothManage.setChecked(state);
    bluetoothManage.setOnCheckedChangeListener((compoundButton, b) -> presenter.bluetoothManageClicked());
  }

  @Override public void setSyncManageState(boolean state) {
    syncManage.setOnCheckedChangeListener(null);
    syncManage.setChecked(state);
    syncManage.setOnCheckedChangeListener((compoundButton, b) -> presenter.syncManageClicked());
  }
}
