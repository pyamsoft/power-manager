package com.pyamsoft.powermanager.ui.trigger;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;
import com.pyamsoft.powermanager.backend.trigger.PowerTriggerDataSource;
import com.pyamsoft.powermanager.ui.trigger.dialog.PowerTriggerDialogFragment;
import java.lang.ref.WeakReference;

public class PowerTriggerModel {

  private WeakReference<Context> weakContext;

  public PowerTriggerModel(final Context context) {
    this.weakContext = new WeakReference<>(context);
  }

  public PowerTriggerDialogFragment createFragment(final PowerTriggerInterface triggerInterface) {
    final Context context = weakContext.get();
    if (context != null) {
      final PowerTriggerDialogFragment fragment = new PowerTriggerDialogFragment();
      fragment.setContext(context);
      fragment.setParentAdapter(triggerInterface);
      return fragment;
    } else {
      return null;
    }
  }

  public boolean removeItem(final PowerTrigger trigger) {
    final Context context = weakContext.get();
    if (context != null) {
      final PowerTriggerDataSource source = PowerTriggerDataSource.with(context);
      source.open();
      boolean removed = false;
      if (source.isOpened()) {
        removed = source.deleteTrigger(trigger);
        source.close();
      }
      return removed;
    }

    return false;
  }

  public AlertDialog.Builder createDeleteWarningDialog(final PowerTrigger trigger) {
    final Context context = weakContext.get();
    if (context == null) {
      return null;
    }

    return new AlertDialog.Builder(context).setCancelable(true)
        .setMessage("Really delete trigger: [" + trigger.getId() + "] " +
            trigger.getName() + "?")
        .setTitle("Delete Trigger");
  }
}
