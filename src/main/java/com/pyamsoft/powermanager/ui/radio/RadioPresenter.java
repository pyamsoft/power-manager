package com.pyamsoft.powermanager.ui.radio;

import android.content.Context;
import com.pyamsoft.powermanager.backend.service.ActiveService;
import com.pyamsoft.pydroid.base.Presenter;
import com.pyamsoft.pydroid.util.LogUtil;

public final class RadioPresenter extends Presenter<RadioInterface> {

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

  public long getDelayTimeData() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.DELAY_RADIO_SIXTY;
    }

    return model.getDelayTimeData();
  }

  public long getDelayTimeBluetooth() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.DELAY_RADIO_SIXTY;
    }

    return model.getDelayTimeBluetooth();
  }

  public long getDelayTimeSync() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }

    return model.getDelayTimeSync();
  }

  public long getReOpenTimeWifi() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getReOpenTimeWifi();
  }

  public long getReOpenTimeData() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN;
    }

    return model.getReOpenTimeData();
  }

  public long getReOpenTimeBluetooth() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }

    return model.getReOpenTimeBluetooth();
  }

  public long getReOpenTimeSync() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }

    return model.getReOpenTimeSync();
  }

  public long getIntervalTimeWifi() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeWifi();
  }

  public long getIntervalTimeData() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeData();
  }

  public long getIntervalTimeBluetooth() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeBluetooth();
  }

  public long getIntervalTimeSync() {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }

    return model.getIntervalTimeSync();
  }

  public void setDelayTimeWifi(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeWifi(value);
    reference.onRadioButtonCheckedChanged(position);
  }

  public void setDelayTimeData(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeData(value);
    reference.onRadioButtonCheckedChanged(position);
  }

  public void setDelayTimeBluetooth(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeBluetooth(value);
    reference.onRadioButtonCheckedChanged(position);
  }

  public void setDelayTimeSync(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setDelayTimeSync(value);
    reference.onRadioButtonCheckedChanged(position);
  }

  public void setIntervalTimeWifi(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeWifi(value);
    reference.onRadioButtonCheckedChanged(position);
  }

  public void setIntervalTimeData(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeData(value);
    reference.onRadioButtonCheckedChanged(position);
  }

  public void setIntervalTimeBluetooth(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeBluetooth(value);
    reference.onRadioButtonCheckedChanged(position);
  }

  public void setIntervalTimeSync(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setIntervalTimeSync(value);
    reference.onRadioButtonCheckedChanged(position);
  }

  public void setReOpenTimeWifi(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeWifi(value);
    reference.onRadioButtonCheckedChanged(position);
  }

  public void setReOpenTimeData(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeData(value);
    reference.onRadioButtonCheckedChanged(position);
  }

  public void setReOpenTimeBluetooth(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeBluetooth(value);
    reference.onRadioButtonCheckedChanged(position);
  }

  public void setReOpenTimeSync(final long value, final int position) {
    final RadioInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }

    model.setReOpenTimeSync(value);
    reference.onRadioButtonCheckedChanged(position);
  }
}
