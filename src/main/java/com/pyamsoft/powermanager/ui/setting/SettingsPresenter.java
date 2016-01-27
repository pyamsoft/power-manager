package com.pyamsoft.powermanager.ui.setting;

import android.content.Context;
import com.pyamsoft.pydroid.base.Presenter;
import com.pyamsoft.pydroid.util.LogUtil;
import java.lang.ref.WeakReference;

public final class SettingsPresenter extends Presenter<SettingsInterface> {

  private static final String TAG = SettingsPresenter.class.getSimpleName();
  private SettingsModel model;
  private WeakReference<Context> context;

  @Override public void bind(SettingsInterface reference) {
    throw new IllegalBindException("Cannot bind without position and Context");
  }

  public void bind(final Context context, final SettingsInterface reference, final int position) {
    super.bind(reference);
    this.context = new WeakReference<>(context);
    this.model = new SettingsModel(context, position);
  }

  @Override public void unbind() {
    super.unbind();
    model = null;
    if (context != null) {
      context.clear();
      context = null;
    }
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

  public boolean isViewTypeReset(final int position) {
    return position == SettingsModel.POSITION_RESET;
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

    final Context c = context.get();
    if (c != null) {
      reference.onResetRequested(this, c);
    }
  }

  public void onResetConfirmed() {
    final SettingsInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }
    model.doReset();
  }

  public String getTitle() {
    return model.getTitle();
  }

  public String getExplanation() {
    return model.getExplanation();
  }
}
