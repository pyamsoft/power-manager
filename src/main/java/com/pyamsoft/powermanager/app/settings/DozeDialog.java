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
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DozeDialog extends DialogFragment {

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setCancelable(false)
        .setPositiveButton("Okay", (dialogInterface, i) -> {
          dismiss();
        })
        .setTitle("Agressive Doze on Marshmallow")
        .setMessage(
            "Doze mode is a feature on Android Marshmallow that allows the device to enter a power saving mode after 30 minutes."
                + "\n"
                + "In order to enable Power Manager to force the device to enter Doze mode more aggressively, you must do the following."
                + "\n"
                + "You must connect your Android device to a computer which has the adb program installed and then run the following command (all one line):"
                + "\n\n"
                + "adb -d shell pm grant com.pyamsoft.powermanager android.permission.DUMP"
                + "\n\n"
                + "This will grant Power Manager the ability to call the dumpsys command which is critical to enabling Doze mode"
                + "\n"
                + "This commands the Android package manager to grant the DUMP permission to Power Manager so that it may force Doze mode")
        .create();
  }
}
