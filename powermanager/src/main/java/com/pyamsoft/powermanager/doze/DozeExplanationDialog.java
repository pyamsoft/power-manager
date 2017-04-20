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

package com.pyamsoft.powermanager.doze;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.pydroid.util.StringUtil;

public class DozeExplanationDialog extends DialogFragment {

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setTitle("Enable Doze permission")
        .setMessage(createDozeMessage())
        .setPositiveButton("Okay", (dialogInterface, i) -> dialogInterface.dismiss())
        .create();
  }

  @NonNull @CheckResult private Spannable createDozeMessage() {
    final String message;
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
      message = "ANDROID 6 (MARSHMALLOW) ONLY!\n"
          + "In order to allow Power Manager to control Doze on your device, \n"
          + "you must enable a special permission. You must connect your device \n"
          + "a computer which has the Android Debug Bridge (adb) program installed \n"
          + "and enter the following command as one line: \n\n"
          + "adb -d shell pm grant com.pyamsoft.powermanager android.permission.DUMP \n\n"
          + "This will allow Power Manager to control the Doze state of your device.";
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      message = "ANDROID 7 (NOUGAT) ONLY!\n"
          + "In order to allow Power Manager to control Doze on your device, \n"
          + "you must be on a rooted device and grant Power Manager root permissions to\n"
          + "run the following command on your device: \n"
          + "\n"
          + " # su -c dumpsys deviceidle force-idle deep\n"
          + " # su -c dumpsys deviceidle unforce\n\n"
          + "This will allow Power Manager to control the Doze state of your device.";
    } else {
      message = "This version of Android does not have Doze features";
    }

    final Spannable spannable = StringUtil.createBuilder(message);
    final int textSize =
        StringUtil.getTextSizeFromAppearance(getContext(), android.R.attr.textAppearanceSmall);
    if (textSize != 0) {
      StringUtil.sizeSpan(spannable, 0, spannable.length(), textSize);
    }

    return spannable;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    PowerManager.getRefWatcher(this).watch(this);
  }
}
