package com.pyamsoft.powermanager.ui.trigger.dialog;

import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;

public interface PowerTriggerDialogInterface {

  void onTriggerCreateFailed();

  void onTriggerCreateSuccess();

  void fillInNewTrigger(PowerTrigger trigger);
}
