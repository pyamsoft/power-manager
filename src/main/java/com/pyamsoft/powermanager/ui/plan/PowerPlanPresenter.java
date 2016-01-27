package com.pyamsoft.powermanager.ui.plan;

import android.content.Context;
import com.pyamsoft.pydroid.base.Presenter;
import com.pyamsoft.pydroid.util.LogUtil;

public final class PowerPlanPresenter extends Presenter<PowerPlanInterface> {

  private static final String TAG = PowerPlanPresenter.class.getSimpleName();
  private PowerPlanModel model;

  @Override public void bind(PowerPlanInterface reference) {
    throw new IllegalBindException("Cannot bind without position and Context");
  }

  public void bind(final Context context, final PowerPlanInterface reference, final int position) {
    super.bind(reference);
    model = new PowerPlanModel(context, position);
  }

  @Override public void unbind() {
    super.unbind();
    model = null;
  }

  public String getName() {
    return model.getName();
  }

  public int getIndex() {
    return model.getIndex();
  }

  public boolean isWifiManaged() {
    return model.isWifiManaged();
  }

  public boolean isDataManaged() {
    return model.isDataManaged();
  }

  public boolean isBluetoothManaged() {
    return model.isBluetoothManaged();
  }

  public boolean isSyncManaged() {
    return model.isSyncManaged();
  }

  public boolean isActivePlan() {
    return model.isActivePlan();
  }

  public long getWifiDelay() {
    return model.getWifiDelay();
  }

  public long getDataDelay() {
    return model.getDataDelay();
  }

  public long getBluetoothDelay() {
    return model.getBluetoothDelay();
  }

  public long getSyncDelay() {
    return model.getSyncDelay();
  }

  public boolean isBootEnabled() {
    return model.isBootEnabled();
  }

  public boolean isSuspendEnabled() {
    return model.isSuspendEnabled();
  }

  public boolean isWifiReOpenEnabled() {
    return model.isWifiReOpenEnabled();
  }

  public boolean isDataReOpenEnabled() {
    return model.isDataReOpenEnabled();
  }

  public boolean isBluetoothReOpenEnabled() {
    return model.isBluetoothReOpenEnabled();
  }

  public boolean isSyncReOpenEnabled() {
    return model.isSyncReOpenEnabled();
  }

  public long getWifiReOpenTime() {
    return model.getWifiReOpenTime();
  }

  public long getDataReOpenTime() {
    return model.getDataReOpenTime();
  }

  public long getBluetoothReOpenTime() {
    return model.getBluetoothReOpenTime();
  }

  public long getSyncReOpenTime() {
    return model.getSyncReOpenTime();
  }

  public void setActivePlan() {
    final PowerPlanInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.d(TAG, "Reference is NULL");
      return;
    }

    model.setActivePlan();
    reference.onSetAsActivePlan();
  }
}
