package com.pyamsoft.powermanager.ui.trigger.dialog;

import android.content.Context;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;
import com.pyamsoft.pydroid.base.PresenterBase;

public class PowerTriggerDialogPresenter extends PresenterBase<PowerTriggerDialogInterface> {

  private PowerTriggerDialogModel model;

  public void onCreateClicked(final String name, final int level) {
    final PowerTriggerDialogInterface reference = getBoundReference();
    if (reference == null) {
      return;
    }

    if (name == null || level == -1) {
      return;
    }

    final PowerTrigger trigger = model.onCreateClicked(name, level);
    reference.fillInNewTrigger(trigger);
  }

  public void onNewTriggerFilled(final PowerTrigger trigger) {
    final PowerTriggerDialogInterface reference = getBoundReference();
    if (reference == null) {
      return;
    }

    if (model.createNewPowerTrigger(trigger)) {
      reference.onTriggerCreateSuccess();
    } else {
      reference.onTriggerCreateFailed();
    }
  }

  public void bind(Context context, PowerTriggerDialogInterface reference) {
    super.bind(reference);
    model = new PowerTriggerDialogModel(context);
  }

  @Override public void unbind() {
    super.unbind();
    model = null;
  }
}
