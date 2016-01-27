package com.pyamsoft.powermanager.ui.fragment;

import android.content.Context;
import com.pyamsoft.powermanager.ui.adapter.SettingsContentAdapter;
import com.pyamsoft.pydroid.base.Presenter;
import com.pyamsoft.pydroid.util.LogUtil;
import java.lang.ref.WeakReference;

public final class SettingsPresenter extends Presenter<SettingsInterface> {

  private static final String TAG = SettingsPresenter.class.getSimpleName();
  private SettingsModel model;
  private WeakReference<SettingsContentAdapter.ViewHolder> holder;
  private Context context;
  private int position = -1;

  @Override public void bind(SettingsInterface reference) {
    throw new IllegalBindException("Cannot bind without position and Context");
  }

  public void bind(final SettingsContentAdapter.ViewHolder holder,
      final SettingsInterface reference, final int position) {
    super.bind(reference);
    this.holder = new WeakReference<>(holder);
    this.context = holder.itemView.getContext().getApplicationContext();
    this.position = position;
    this.model = new SettingsModel(context, position);
  }

  @Override public void unbind() {
    super.unbind();
    model = null;
    context = null;
    position = -1;
  }

  public void onClick() {
    onResetClicked();
  }

  public void onClick(final boolean isChecked) {
    LogUtil.d(TAG, "onClick. isChecked: ", isChecked);
    switch (position) {
      case SettingsModel.POSITION_BOOT:
        onBootClicked(isChecked);
        break;
      case SettingsModel.POSITION_SUSPEND:
        onSuspendClicked(isChecked);
        break;
      case SettingsModel.POSITION_NOTIFICATION:
        onNotificationClicked(isChecked);
        break;
      case SettingsModel.POSITION_FOREGROUND:
        onForegroundClicked(isChecked);
        break;
    }
  }

  public boolean isEnabled() {
    boolean b;
    switch (position) {
      case SettingsModel.POSITION_BOOT:
        b = true;
        break;
      case SettingsModel.POSITION_SUSPEND:
        b = true;
        break;
      case SettingsModel.POSITION_NOTIFICATION:
        b = true;
        break;
      case SettingsModel.POSITION_FOREGROUND:
        b = model.isForegroundClickable();
        break;
      case SettingsModel.POSITION_RESET:
        b = true;
        break;
      default:
        b = false;
    }
    return b;
  }

  public boolean isChecked() {
    boolean b;
    switch (position) {
      case SettingsModel.POSITION_BOOT:
        b = model.isBootEnabled();
        break;
      case SettingsModel.POSITION_SUSPEND:
        b = model.isSuspendEnabled();
        break;
      case SettingsModel.POSITION_NOTIFICATION:
        b = model.isNotificationEnabled();
        break;
      case SettingsModel.POSITION_FOREGROUND:
        b = model.isForegroundEnabled();
        break;
      case SettingsModel.POSITION_RESET:
        b = false;
        break;
      default:
        b = false;
    }
    return b;
  }

  public boolean isViewTypeReset() {
    return position == SettingsModel.POSITION_RESET;
  }

  private void onBootClicked(final boolean isChecked) {
    final SettingsInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }

    model.setBootEnabled(isChecked);
    final SettingsContentAdapter.ViewHolder h = holder.get();
    if (h != null) {
      if (isChecked) {
        reference.onBootEnabled(h);
      } else {
        reference.onBootDisabled(h);
      }
    }
  }

  private void onSuspendClicked(final boolean isChecked) {
    final SettingsInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }

    model.setSuspendEnabled(isChecked);
    final SettingsContentAdapter.ViewHolder h = holder.get();
    if (h != null) {
      if (isChecked) {
        reference.onSuspendEnabled(h);
      } else {
        reference.onSuspendDisabled(h);
      }
    }
  }

  private void onNotificationClicked(final boolean isChecked) {
    final SettingsInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }

    model.setNotificationEnabled(isChecked);
    final SettingsContentAdapter.ViewHolder h = holder.get();
    if (h != null) {
      if (isChecked) {
        reference.onNotificationEnabled(h);
      } else {
        reference.onNotificationDisabled(h);
      }
    }
  }

  private void onForegroundClicked(final boolean isChecked) {
    final SettingsInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }

    model.setForegroundEnabled(isChecked);
    final SettingsContentAdapter.ViewHolder h = holder.get();
    if (h != null) {
      if (isChecked) {
        reference.onForegroundEnabled(h);
      } else {
        reference.onForegroundDisabled(h);
      }
    }
  }

  private void onResetClicked() {
    final SettingsInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "Reference is NULL");
      return;
    }

    final SettingsContentAdapter.ViewHolder h = holder.get();
    if (h != null) {
      reference.onResetRequested(this, h);
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
