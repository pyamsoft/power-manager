/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.app.trigger;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.pyamsoft.powermanager.bus.DeleteTriggerBus;
import com.pyamsoft.powermanager.model.event.DeleteTriggerEvent;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import timber.log.Timber;

public class DeleteTriggerDialog extends DialogFragment {

  @NonNull private static final String TRIGGER_NAME = "trigger_name";
  @NonNull private static final String TRIGGER_PERCENT = "trigger_percent";
  @SuppressWarnings("WeakerAccess") int percent;
  private String name;

  @CheckResult @NonNull
  public static DeleteTriggerDialog newInstance(@NonNull PowerTriggerEntry trigger) {
    Bundle args = new Bundle();
    DeleteTriggerDialog fragment = new DeleteTriggerDialog();
    args.putString(TRIGGER_NAME, trigger.name());
    args.putInt(TRIGGER_PERCENT, trigger.percent());
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    name = getArguments().getString(TRIGGER_NAME, null);
    percent = getArguments().getInt(TRIGGER_PERCENT, -1);

    if (percent == -1) {
      Timber.e("Invalid percent for DeleteTriggerDialog. Dismiss dialog");
      dismiss();
    }
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setTitle("Delete Trigger")
        .setMessage("Really delete trigger for: " + name + " [" + percent + "%] ?")
        .setNegativeButton("Cancel", (dialogInterface, i) -> {
          dismiss();
        })
        .setPositiveButton("Okay", (dialogInterface, i) -> {
          dismiss();
          DeleteTriggerBus.get().post(DeleteTriggerEvent.create(percent));
        })
        .create();
  }
}
