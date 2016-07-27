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

package com.pyamsoft.powermanager.app.manager.manage;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class LollipopDataDialog extends DialogFragment {

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setCancelable(false)
        .setPositiveButton("Okay", (dialogInterface, i) -> {
          dismiss();
        })
        .setTitle("Toggle Data on Lollipop+")
        .setMessage(
            "Due to changes in Android on Lollipop and Marshmallow, it became more difficult to toggle the state of mobile data on the device."
                + "\n"
                + "In order to enable Power Manager to toggle mobile data on your Android device you must run the following command via the Android Debug Bridge (adb)."
                + "\n"
                + "You must connect your Android device to a computer which has the adb program installed and then run the following command (all one line):"
                + "\n\n"
                + "adb -d shell pm grant com.pyamsoft.powermanager android.permission.WRITE_SECURE_SETTINGS"
                + "\n\n"
                + "This will grant Power Manager the ability to change system settings that are normally not available to it."
                + "\n"
                + "This commands the Android package manager to grant the WRITE_SECURE_SETTINGS to Power Manager so that it may change the state of the"
                + "\n"
                + "Cellular Data system setting.")
        .create();
  }
}
