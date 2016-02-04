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

public final class PowerPlanPresenter extends PresenterBase<PowerPlanInterface> {

  private static final String TAG = PowerPlanPresenter.class.getSimpleName();
  private PowerPlanModel model;

  @Override public void bind(PowerPlanInterface reference) {
    throw new IllegalBindException("Cannot bind without position and Context");
  }

  public void bind(final Context context, final PowerPlanInterface reference) {
    super.bind(reference);
    model = new PowerPlanModel(context);
  }

  @Override public void unbind() {
    super.unbind();
    model = null;
  }

  public String getName(final int position) {
    return model.getName(position);
  }

  public int getIndex(final int position) {
    return model.getIndex(position);
  }

  public boolean isWifiManaged(final int position) {
    return model.isWifiManaged(position);
  }

  public boolean isDataManaged(final int position) {
    return model.isDataManaged(position);
  }

  public boolean isBluetoothManaged(final int position) {
    return model.isBluetoothManaged(position);
  }

  public boolean isSyncManaged(final int position) {
    return model.isSyncManaged(position);
  }

  public boolean isActivePlan(final int position) {
    return model.isActivePlan(position);
  }

  public long getWifiDelay(final int position) {
    return model.getWifiDelay(position);
  }

  public long getDataDelay(final int position) {
    return model.getDataDelay(position);
  }

  public long getBluetoothDelay(final int position) {
    return model.getBluetoothDelay(position);
  }

  public long getSyncDelay(final int position) {
    return model.getSyncDelay(position);
  }

  public boolean isBootEnabled(final int position) {
    return model.isBootEnabled(position);
  }

  public boolean isSuspendEnabled(final int position) {
    return model.isSuspendEnabled(position);
  }

  public boolean isWifiReOpenEnabled(final int position) {
    return model.isWifiReOpenEnabled(position);
  }

  public boolean isDataReOpenEnabled(final int position) {
    return model.isDataReOpenEnabled(position);
  }

  public boolean isBluetoothReOpenEnabled(final int position) {
    return model.isBluetoothReOpenEnabled(position);
  }

  public boolean isSyncReOpenEnabled(final int position) {
    return model.isSyncReOpenEnabled(position);
  }

  public long getWifiReOpenTime(final int position) {
    return model.getWifiReOpenTime(position);
  }

  public long getDataReOpenTime(final int position) {
    return model.getDataReOpenTime(position);
  }

  public long getBluetoothReOpenTime(final int position) {
    return model.getBluetoothReOpenTime(position);
  }

  public long getSyncReOpenTime(final int position) {
    return model.getSyncReOpenTime(position);
  }

  public void setActivePlan(final int position) {
    final PowerPlanInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.d(TAG, "Reference is NULL");
      return;
    }

    model.setActivePlan(position);
    reference.onSetAsActivePlan();
  }
}
