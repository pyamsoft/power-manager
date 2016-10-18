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

package com.pyamsoft.powermanager.app.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

public class ConfirmationDialog extends DialogFragment {
  @NonNull private static final String WHICH = "which_type";

  @SuppressWarnings("WeakerAccess") int which;

  public static ConfirmationDialog newInstance(final int which) {
    final ConfirmationDialog fragment = new ConfirmationDialog();
    final Bundle args = new Bundle();
    args.putInt(WHICH, which);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    which = getArguments().getInt(WHICH, 0);
  }

  @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setMessage(which == 0
        ? "Really clear entire database?\n\nYou will have to re-configure all triggers again"
        : "Really clear all application settings?")
        .setPositiveButton("Yes", (dialogInterface, i) -> {
          dialogInterface.dismiss();
          sendConfirmationEvent(which);
        })
        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
        .create();
  }

  void sendConfirmationEvent(int which) {
    final FragmentManager fragmentManager = getFragmentManager();
    final Fragment settingsPreferenceFragment =
        fragmentManager.findFragmentByTag(SettingsPreferenceFragment.TAG);
    if (settingsPreferenceFragment instanceof SettingsPreferenceFragment) {
      ((SettingsPreferenceFragment) settingsPreferenceFragment).getPresenter()
          .processClearRequest(which);
    } else {
      throw new ClassCastException("Fragment is not SettingsPreferenceFragment");
    }
  }
}
