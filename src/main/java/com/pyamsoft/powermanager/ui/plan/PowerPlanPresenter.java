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

package com.pyamsoft.powermanager.ui.plan;

import android.content.Context;
import com.pyamsoft.pydroid.base.PresenterBase;
import com.pyamsoft.pydroid.util.LogUtil;

final class PowerPlanPresenter extends PresenterBase<PowerPlanInterface> {

  private static final String TAG = PowerPlanPresenter.class.getSimpleName();
  private final PowerPlanModel model;

  PowerPlanPresenter(Context context, PowerPlanInterface iface) {
    super(iface);
    model = new PowerPlanModel(context);
  }

  String getName(final int position) {
    return model.getName(position);
  }

  int getIndex(final int position) {
    return model.getIndex(position);
  }

  boolean isWifiManaged(final int position) {
    return model.isWifiManaged(position);
  }

  boolean isDataManaged(final int position) {
    return model.isDataManaged(position);
  }

  boolean isBluetoothManaged(final int position) {
    return model.isBluetoothManaged(position);
  }

  boolean isSyncManaged(final int position) {
    return model.isSyncManaged(position);
  }

  boolean isActivePlan(final int position) {
    return model.isActivePlan(position);
  }

  long getWifiDelay(final int position) {
    return model.getWifiDelay(position);
  }

  long getDataDelay(final int position) {
    return model.getDataDelay(position);
  }

  long getBluetoothDelay(final int position) {
    return model.getBluetoothDelay(position);
  }

  long getSyncDelay(final int position) {
    return model.getSyncDelay(position);
  }

  boolean isBootEnabled(final int position) {
    return model.isBootEnabled(position);
  }

  boolean isSuspendEnabled(final int position) {
    return model.isSuspendEnabled(position);
  }

  boolean isWifiReOpenEnabled(final int position) {
    return model.isWifiReOpenEnabled(position);
  }

  boolean isDataReOpenEnabled(final int position) {
    return model.isDataReOpenEnabled(position);
  }

  boolean isBluetoothReOpenEnabled(final int position) {
    return model.isBluetoothReOpenEnabled(position);
  }

  boolean isSyncReOpenEnabled(final int position) {
    return model.isSyncReOpenEnabled(position);
  }

  long getWifiReOpenTime(final int position) {
    return model.getWifiReOpenTime(position);
  }

  long getDataReOpenTime(final int position) {
    return model.getDataReOpenTime(position);
  }

  long getBluetoothReOpenTime(final int position) {
    return model.getBluetoothReOpenTime(position);
  }

  long getSyncReOpenTime(final int position) {
    return model.getSyncReOpenTime(position);
  }

  void setActivePlan(final int position) {
    final PowerPlanInterface iface = getInterface();
    if (iface == null) {
      LogUtil.d(TAG, "Reference is NULL");
      return;
    }

    model.setActivePlan(position);
    iface.onSetAsActivePlan();
  }
}
