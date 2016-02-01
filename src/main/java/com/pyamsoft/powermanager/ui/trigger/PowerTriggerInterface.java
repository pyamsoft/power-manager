package com.pyamsoft.powermanager.ui.trigger;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;
import com.pyamsoft.powermanager.ui.trigger.dialog.PowerTriggerDialogFragment;

public interface PowerTriggerInterface {

  void onDialogFragmentCreated(PowerTriggerDialogFragment fragment);

  void onItemRemoved(final PowerTrigger removed);

  void onDeleteWarningDialogCreated(AlertDialog dialog);

  void refreshDataSet(final Context context);
}
