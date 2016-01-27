package com.pyamsoft.powermanager.ui.setting;

import android.content.Context;

public interface SettingsInterface {

  void onResetRequested(final SettingsPresenter presenter, final Context context);

  void onBootEnabled();

  void onBootDisabled();

  void onSuspendEnabled();

  void onSuspendDisabled();

  void onNotificationEnabled();

  void onNotificationDisabled();

  void onForegroundEnabled();

  void onForegroundDisabled();
}
