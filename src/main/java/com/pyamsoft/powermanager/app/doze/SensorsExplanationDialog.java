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
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import com.pyamsoft.pydroid.util.StringUtil;

public class SensorsExplanationDialog extends DialogFragment {

  @Nullable private Intent writeSettings;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      writeSettings = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
      writeSettings.setData(Uri.fromParts("package", getContext().getPackageName(), null));
      writeSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      writeSettings.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    } else {
      writeSettings = null;
    }
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setTitle("Enable System Settings permission")
        .setMessage(createSensorsMessage())
        .setPositiveButton("Take Me", (dialogInterface, i) -> {
          dialogInterface.dismiss();
          launchSettingsActivity();
        })
        .setNegativeButton("No Thanks", (dialogInterface, i) -> dialogInterface.dismiss())
        .create();
  }

  void launchSettingsActivity() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      getActivity().startActivity(writeSettings);
    }
  }

  @NonNull @CheckResult private Spannable createSensorsMessage() {
    final String message = "In order to allow Power Manager to control Sensors on your device, \n"
        + "you must enable a the write permission. \n"
        + "Click the button below to go to the permissions screen";
    final Spannable spannable = StringUtil.createBuilder(message);
    final int textSize =
        StringUtil.getTextSizeFromAppearance(getContext(), android.R.attr.textAppearanceSmall);
    if (textSize != 0) {
      StringUtil.sizeSpan(spannable, 0, spannable.length(), textSize);
    }

    return spannable;
  }
}
