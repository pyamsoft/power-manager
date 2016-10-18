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

package com.pyamsoft.powermanager.app.doze;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import com.pyamsoft.pydroid.util.StringUtil;

public class DozeExplanationDialog extends DialogFragment {

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setTitle("Enable Doze permission")
        .setMessage(createDozeMessage())
        .setPositiveButton("Okay", (dialogInterface, i) -> dialogInterface.dismiss())
        .create();
  }

  @NonNull @CheckResult private Spannable createDozeMessage() {
    final String message = "ANDROID 6 (MARSHMALLOW) ONLY!\n"
        + "In order to allow Power Manager to control Doze on your device, \n"
        + "you must enable a special permission. You must connect your device \n"
        + "a computer which has the Android Debug Bridge (adb) program installed \n"
        + "and enter the following command as one line: \n\n"
        + "adb -d shell pm grant com.pyamsoft.powermanager android.permission.DUMP \n\n"
        + "This will allow Power Manager to control the Doze state of your device.";
    final Spannable spannable = StringUtil.createBuilder(message);
    final int textSize =
        StringUtil.getTextSizeFromAppearance(getContext(), android.R.style.TextAppearance_Small);
    if (textSize != 0) {
      StringUtil.sizeSpan(spannable, 0, spannable.length(), textSize);
    }

    return spannable;
  }
}
