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

package com.pyamsoft.powermanager.ui.radio;

import android.content.Context;
import com.pyamsoft.powermanager.backend.service.ActiveService;
import com.pyamsoft.pydroid.base.PresenterBase;
import com.pyamsoft.pydroid.util.LogUtil;

public final class RadioPresenter extends PresenterBase<RadioInterface> {

  private static final String TAG = RadioPresenter.class.getSimpleName();
  private RadioModel model;

  @Override public void bind(RadioInterface reference) {
    throw new IllegalBindException("Need context to bind");
  }

  public void bind(final Context context, final RadioInterface reference) {
    super.bind(reference);
    model = new RadioModel(context);
  }

  @Override public void unbind() {
    super.unbind();
    model = null;
  }

  public long getDelayTimeWifi() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }

    return model.getDelayTimeWifi();
  }

  public void setDelayTimeWifi(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeWifi(value);
    reference.onDelayTimeChanged();
  }

  public long getDelayTimeData() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.DELAY_RADIO_SIXTY;
    }

    return model.getDelayTimeData();
  }

  public void setDelayTimeData(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeData(value);
    reference.onDelayTimeChanged();
  }

  public long getDelayTimeBluetooth() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.DELAY_RADIO_SIXTY;
    }

    return model.getDelayTimeBluetooth();
  }

  public void setDelayTimeBluetooth(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeBluetooth(value);
    reference.onDelayTimeChanged();
  }

  public long getDelayTimeSync() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }

    return model.getDelayTimeSync();
  }

  public void setDelayTimeSync(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeSync(value);
    reference.onDelayTimeChanged();
  }

  public long getReOpenTimeWifi() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getReOpenTimeWifi();
  }

  public void setReOpenTimeWifi(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeWifi(value);
    reference.onReOpenTimeChanged();
  }

  public long getReOpenTimeData() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN;
    }

    return model.getReOpenTimeData();
  }

  public void setReOpenTimeData(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeData(value);
    reference.onReOpenTimeChanged();
  }

  public long getReOpenTimeBluetooth() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }

    return model.getReOpenTimeBluetooth();
  }

  public void setReOpenTimeBluetooth(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeBluetooth(value);
    reference.onReOpenTimeChanged();
  }

  public long getReOpenTimeSync() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }

    return model.getReOpenTimeSync();
  }

  public void setReOpenTimeSync(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeSync(value);
    reference.onReOpenTimeChanged();
  }

  public long getIntervalTimeWifi() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeWifi();
  }

  public void setIntervalTimeWifi(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeWifi(value);
    reference.onIntervalTimeChanged();
  }

  public long getIntervalTimeData() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeData();
  }

  public void setIntervalTimeData(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeData(value);
    reference.onIntervalTimeChanged();
  }

  public long getIntervalTimeBluetooth() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeBluetooth();
  }

  public void setIntervalTimeBluetooth(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeBluetooth(value);
    reference.onIntervalTimeChanged();
  }

  public long getIntervalTimeSync() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeSync();
  }

  public void setIntervalTimeSync(final long value) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeSync(value);
    reference.onIntervalTimeChanged();
  }
}
