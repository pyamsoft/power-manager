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
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
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
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.app.main.MainActivity;
import com.pyamsoft.powermanager.app.modifier.InterestModifier;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.service.FullNotificationPresenter;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncDrawableMap;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Subscription;
import timber.log.Timber;

public class FullNotificationActivity extends AppCompatActivity
    implements FullNotificationPresenter.FullNotificationView {

  @Inject FullNotificationPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Singleton.Dagger.with(this).plusFullNotificationComponent().inject(this);

    presenter.bindView(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.unbindView();
  }

  @Override protected void onResume() {
    super.onResume();

    Timber.d("resume");
    presenter.resume();
  }

  @Override protected void onPause() {
    super.onPause();

    Timber.d("pause");
    presenter.pause();
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

  public static final class FullDialog extends DialogFragment {

    @NonNull private final AsyncDrawableMap taskMap = new AsyncDrawableMap();
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

    @Inject @Named("obs_wifi_state") InterestObserver wifiStateObserver;
    @Inject @Named("obs_data_state") InterestObserver dataStateObserver;
    @Inject @Named("obs_bluetooth_state") InterestObserver bluetoothStateObserver;
    @Inject @Named("obs_sync_state") InterestObserver syncStateObserver;

    @Inject @Named("obs_wifi_manage") InterestObserver wifiManageObserver;
    @Inject @Named("obs_data_manage") InterestObserver dataManageObserver;
    @Inject @Named("obs_bluetooth_manage") InterestObserver bluetoothManageObserver;
    @Inject @Named("obs_sync_manage") InterestObserver syncManageObserver;

    @Inject @Named("mod_wifi_state") InterestModifier wifiStateModifier;
    @Inject @Named("mod_data_state") InterestModifier dataStateModifier;
    @Inject @Named("mod_bluetooth_state") InterestModifier bluetoothStateModifier;
    @Inject @Named("mod_sync_state") InterestModifier syncStateModifier;

    @Inject @Named("mod_wifi_manage") InterestModifier wifiManageModifier;
    @Inject @Named("mod_data_manage") InterestModifier dataManageModifier;
    @Inject @Named("mod_bluetooth_manage") InterestModifier bluetoothManageModifier;
    @Inject @Named("mod_sync_manage") InterestModifier syncManageModifier;

    private Unbinder unbinder;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setCancelable(true);
      Timber.d("onCreate");

      Singleton.Dagger.with(getContext()).plusFullDialogComponent().inject(this);

      wifiStateObserver.register(() -> setWifiToggleState(true), () -> setWifiToggleState(false));
      dataStateObserver.register(() -> setDataToggleState(true), () -> setDataToggleState(false));
      bluetoothStateObserver.register(() -> setBluetoothToggleState(true),
          () -> setBluetoothToggleState(false));
      syncStateObserver.register(() -> setSyncToggleState(true), () -> setSyncToggleState(false));

      wifiManageObserver.register(() -> setWifiManageState(true), () -> setWifiManageState(false));
      dataManageObserver.register(() -> setDataManageState(true), () -> setDataManageState(false));
      bluetoothManageObserver.register(() -> setBluetoothManageState(true),
          () -> setBluetoothManageState(false));
      syncManageObserver.register(() -> setSyncManageState(true), () -> setSyncManageState(false));
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

      final Subscription mainTask =
          AsyncDrawable.with(getContext()).load(R.drawable.ic_settings_24dp).into(mainButton);
      taskMap.put("main", mainTask);

      closeButton.setOnClickListener(view -> destroy());

      final Subscription closeTask =
          AsyncDrawable.with(getContext()).load(R.drawable.ic_close_24dp).into(closeButton);
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
      @DrawableRes final int icon =
          enabled ? R.drawable.ic_network_wifi_24dp : R.drawable.ic_signal_wifi_off_24dp;
      @ColorRes final int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      final Subscription task =
          AsyncDrawable.with(getContext()).load(icon).tint(color).into(wifiToggle);
      taskMap.put("wifi", task);
    }

    private void setDataToggleState(boolean enabled) {
      @DrawableRes final int icon =
          enabled ? R.drawable.ic_network_cell_24dp : R.drawable.ic_signal_cellular_off_24dp;
      @ColorRes final int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      final Subscription task =
          AsyncDrawable.with(getContext()).load(icon).tint(color).into(dataToggle);
      taskMap.put("data", task);
    }

    private void setBluetoothToggleState(boolean enabled) {
      @DrawableRes final int icon =
          enabled ? R.drawable.ic_bluetooth_24dp : R.drawable.ic_bluetooth_disabled_24dp;
      @ColorRes final int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      final Subscription task =
          AsyncDrawable.with(getContext()).load(icon).tint(color).into(bluetoothToggle);
      taskMap.put("bluetooth", task);
    }

    private void setSyncToggleState(boolean enabled) {
      @DrawableRes final int icon =
          enabled ? R.drawable.ic_sync_24dp : R.drawable.ic_sync_disabled_24dp;
      @ColorRes final int color = enabled ? R.color.lightblueA200 : android.R.color.black;
      final Subscription task =
          AsyncDrawable.with(getContext()).load(icon).tint(color).into(syncToggle);
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
  }
}
