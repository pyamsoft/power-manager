/*
 * Copyright 2013 - 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.ui.setting;

import android.support.v7.app.AppCompatActivity;
import com.pyamsoft.pydroid.base.PresenterBase;
import com.pyamsoft.pydroid.util.LogUtil;

final class SettingsPresenter extends PresenterBase<SettingsInterface> {

  private static final String TAG = SettingsPresenter.class.getSimpleName();
  private final SettingsModel model;

  SettingsPresenter(final AppCompatActivity activity, SettingsInterface iface) {
    super(iface);
    model = new SettingsModel(activity);
  }

  boolean isBootEnabled() {
    return model.isBootEnabled();
  }

  boolean isSuspendEnabled() {
    return model.isSuspendEnabled();
  }

  boolean isNotificationEnabled() {
    return model.isNotificationEnabled();
  }

  boolean isForegroundEnabled() {
    return model.isForegroundEnabled();
  }

  boolean isBootClickable() {
    return true;
  }

  boolean isSuspendClickable() {
    return true;
  }

  boolean isNotificationClickable() {
    return true;
  }

  boolean isForegroundClickable() {
    return model.isForegroundClickable();
  }

  void onBootClicked(final boolean isChecked) {
    final SettingsInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }

    model.setBootEnabled(isChecked);
    if (isChecked) {
      reference.onBootEnabled();
    } else {
      reference.onBootDisabled();
    }
  }

  void onSuspendClicked(final boolean isChecked) {
    final SettingsInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }

    model.setSuspendEnabled(isChecked);
    if (isChecked) {
      reference.onSuspendEnabled();
    } else {
      reference.onSuspendDisabled();
    }
  }

  void onNotificationClicked(final boolean isChecked) {
    final SettingsInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }

    model.setNotificationEnabled(isChecked);
    if (isChecked) {
      reference.onNotificationEnabled();
    } else {
      reference.onNotificationDisabled();
    }
  }

  void onForegroundClicked(final boolean isChecked) {
    final SettingsInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }

    model.setForegroundEnabled(isChecked);
    if (isChecked) {
      reference.onForegroundEnabled();
    } else {
      reference.onForegroundDisabled();
    }
  }

  void onResetClicked() {
    final SettingsInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }

    reference.onResetRequested(this, model.provideResetContext());
  }

  void onResetConfirmed() {
    final SettingsInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }
    model.doReset();
  }
}
