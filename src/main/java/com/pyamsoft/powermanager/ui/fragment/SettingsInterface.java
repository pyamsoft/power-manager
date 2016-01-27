package com.pyamsoft.powermanager.ui.fragment;

import android.content.Context;
import com.pyamsoft.powermanager.ui.adapter.SettingsContentAdapter;

public interface SettingsInterface {

  void onResetRequested(final SettingsPresenter presenter, final SettingsContentAdapter.ViewHolder holder);

  void onBootEnabled(final SettingsContentAdapter.ViewHolder holder);

  void onBootDisabled(final SettingsContentAdapter.ViewHolder holder);

  void onSuspendEnabled(final SettingsContentAdapter.ViewHolder holder);

  void onSuspendDisabled(final SettingsContentAdapter.ViewHolder holder);

  void onNotificationEnabled(final SettingsContentAdapter.ViewHolder holder);

  void onNotificationDisabled(final SettingsContentAdapter.ViewHolder holder);

  void onForegroundEnabled(final SettingsContentAdapter.ViewHolder holder);

  void onForegroundDisabled(final SettingsContentAdapter.ViewHolder holder);
}
