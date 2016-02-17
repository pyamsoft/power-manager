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

final class RadioPresenter extends PresenterBase<RadioInterface> {

  private static final String TAG = RadioPresenter.class.getSimpleName();
  private final RadioModel model;

  RadioPresenter(final Context context, RadioInterface iface) {
    super(iface);
    model = new RadioModel(context);
  }

  long getDelayTimeWifi() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }

    return model.getDelayTimeWifi();
  }

  void setDelayTimeWifi(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeWifi(value);
    reference.onDelayTimeChanged();
  }

  long getDelayTimeData() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.DELAY_RADIO_SIXTY;
    }

    return model.getDelayTimeData();
  }

  void setDelayTimeData(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeData(value);
    reference.onDelayTimeChanged();
  }

  long getDelayTimeBluetooth() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.DELAY_RADIO_SIXTY;
    }

    return model.getDelayTimeBluetooth();
  }

  void setDelayTimeBluetooth(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeBluetooth(value);
    reference.onDelayTimeChanged();
  }

  long getDelayTimeSync() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }

    return model.getDelayTimeSync();
  }

  void setDelayTimeSync(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeSync(value);
    reference.onDelayTimeChanged();
  }

  long getReOpenTimeWifi() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getReOpenTimeWifi();
  }

  void setReOpenTimeWifi(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeWifi(value);
    reference.onReOpenTimeChanged();
  }

  long getReOpenTimeData() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN;
    }

    return model.getReOpenTimeData();
  }

  void setReOpenTimeData(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeData(value);
    reference.onReOpenTimeChanged();
  }

  long getReOpenTimeBluetooth() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }

    return model.getReOpenTimeBluetooth();
  }

  void setReOpenTimeBluetooth(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeBluetooth(value);
    reference.onReOpenTimeChanged();
  }

  long getReOpenTimeSync() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }

    return model.getReOpenTimeSync();
  }

  void setReOpenTimeSync(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeSync(value);
    reference.onReOpenTimeChanged();
  }

  long getIntervalTimeWifi() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeWifi();
  }

  void setIntervalTimeWifi(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeWifi(value);
    reference.onIntervalTimeChanged();
  }

  long getIntervalTimeData() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeData();
  }

  void setIntervalTimeData(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeData(value);
    reference.onIntervalTimeChanged();
  }

  long getIntervalTimeBluetooth() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeBluetooth();
  }

  void setIntervalTimeBluetooth(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeBluetooth(value);
    reference.onIntervalTimeChanged();
  }

  long getIntervalTimeSync() {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeSync();
  }

  void setIntervalTimeSync(final long value) {
    final RadioInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeSync(value);
    reference.onIntervalTimeChanged();
  }
}
