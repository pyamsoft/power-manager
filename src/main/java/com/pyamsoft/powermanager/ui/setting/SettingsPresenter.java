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

import android.content.Context;
import com.pyamsoft.pydroid.base.PresenterBase;
import com.pyamsoft.pydroid.util.LogUtil;

public final class SettingsPresenter extends PresenterBase<SettingsInterface> {

  private static final String TAG = SettingsPresenter.class.getSimpleName();
  private SettingsModel model;

  @Override public void bind(SettingsInterface reference) {
    throw new IllegalBindException("Cannot bind without position and Context");
  }

  public void bind(final Context context, final SettingsInterface reference) {
    super.bind(reference);
    this.model = new SettingsModel(context);
  }

  @Override public void unbind() {
    super.unbind();
    model = null;
  }

  public boolean isBootEnabled() {
    return model.isBootEnabled();
  }

  public boolean isSuspendEnabled() {
    return model.isSuspendEnabled();
  }

  public boolean isNotificationEnabled() {
    return model.isNotificationEnabled();
  }

  public boolean isForegroundEnabled() {
    return model.isForegroundEnabled();
  }

  public boolean isBootClickable() {
    return true;
  }

  public boolean isSuspendClickable() {
    return true;
  }

  public boolean isNotificationClickable() {
    return true;
  }

  public boolean isForegroundClickable() {
    return model.isForegroundClickable();
  }

  public void onBootClicked(final boolean isChecked) {
    final SettingsInterface reference = getBoundReference();
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

  public void onSuspendClicked(final boolean isChecked) {
    final SettingsInterface reference = getBoundReference();
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

  public void onNotificationClicked(final boolean isChecked) {
    final SettingsInterface reference = getBoundReference();
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

  public void onForegroundClicked(final boolean isChecked) {
    final SettingsInterface reference = getBoundReference();
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

  public void onResetClicked() {
    final SettingsInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }

    reference.onResetRequested(this, model.provideResetContext());
  }

  public void onResetConfirmed() {
    final SettingsInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }
    model.doReset();
  }
}
