package com.pyamsoft.powermanager.ui.trigger;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;
import com.pyamsoft.powermanager.ui.trigger.dialog.PowerTriggerDialogFragment;
import com.pyamsoft.pydroid.base.PresenterBase;
import java.lang.ref.WeakReference;

public class PowerTriggerPresenter extends PresenterBase<PowerTriggerInterface> {

  private PowerTriggerModel model;

  @Override public void bind(PowerTriggerInterface reference) {
    throw new IllegalBindException("Needs context");
  }

  public void bind(final Context context, final PowerTriggerInterface reference) {
    super.bind(reference);
    model = new PowerTriggerModel(context);
  }

  public void onLongClick(final PowerTrigger trigger) {
    final PowerTriggerInterface reference = getBoundReference();
    if (reference == null) {
      return;
    }

    final AlertDialog.Builder builder = model.createDeleteWarningDialog(trigger);
    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
      private WeakReference<PowerTrigger> weakTrigger = new WeakReference<>(trigger);
      private WeakReference<PowerTriggerPresenter> weakPresenter =
          new WeakReference<>(PowerTriggerPresenter.this);

      @Override public void onClick(DialogInterface dialog, int which) {
        final PowerTriggerPresenter p = weakPresenter.get();
        if (p != null) {
          final PowerTrigger t = weakTrigger.get();
          if (t != null) {
            p.onItemRemoveClicked(t);
          }
        }
      }
    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });

    reference.onDeleteWarningDialogCreated(builder.create());
  }

  public void onAddButtonClicked() {
    final PowerTriggerInterface reference = getBoundReference();
    if (reference == null) {
      return;
    }

    final PowerTriggerDialogFragment fragment = model.createFragment(reference);
    if (fragment != null) {
      reference.onDialogFragmentCreated(fragment);
    }
  }

  public void onItemRemoveClicked(PowerTrigger trigger) {
    final PowerTriggerInterface reference = getBoundReference();
    if (reference == null) {
      return;
    }

    final boolean removed = model.removeItem(trigger);
    if (removed) {
      reference.onItemRemoved(trigger);
    }
  }
}
