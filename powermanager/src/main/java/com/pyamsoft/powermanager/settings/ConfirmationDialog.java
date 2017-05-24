/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.settings.bus.ConfirmEvent;
import com.pyamsoft.pydroid.bus.EventBus;

public class ConfirmationDialog extends DialogFragment {
  @NonNull private static final String WHICH = "which_type";

  @SuppressWarnings("WeakerAccess") ConfirmEvent.Type clearType;

  public static ConfirmationDialog newInstance(@NonNull ConfirmEvent.Type codes) {
    final ConfirmationDialog fragment = new ConfirmationDialog();
    final Bundle args = new Bundle();
    args.putString(WHICH, codes.name());
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String code = getArguments().getString(WHICH);
    if (code == null) {
      throw new RuntimeException("Cannot show dialog without ClearCode");
    }

    clearType = ConfirmEvent.Type.valueOf(code);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    PowerManager.getRefWatcher(this).watch(this);
  }

  @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setMessage(clearType == ConfirmEvent.Type.DATABASE
        ? "Really clear entire database?\n\nYou will have to re-configure all triggers again"
        : "Really clear all application settings?")
        .setPositiveButton("Yes",
            (dialogInterface, i) -> EventBus.get().publish(new ConfirmEvent(clearType)))
        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
        .create();
  }
}
