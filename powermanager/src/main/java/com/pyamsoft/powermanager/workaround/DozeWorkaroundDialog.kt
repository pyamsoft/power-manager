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

package com.pyamsoft.powermanager.workaround

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.SpannableString
import com.pyamsoft.powermanager.uicore.WatchedDialog
import com.pyamsoft.pydroid.util.AppUtil
import com.pyamsoft.pydroid.util.StringUtil

class DozeWorkaroundDialog : WatchedDialog() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val message = // @formatter:off
    "On newer Android devices running Marshmallow and up, Doze Mode can " +
    "normally only be automatically controlled using a rooted device." +
    "A workaround exists however, but it may not work on every device. " +
    "If successful, this alternative method is safer, faster, and more reliable." +
    // @formatter:on
        """
        |
        | THIS WILL NOT WORK FOR EVERY DEVICE.
        |
        |To enable this workaround, one must do the following.
        |1. Connect to the device directly using the Android Debug Bridge
        |2. Launch a Shell on the device using 'adb shell'
        ${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        // formatter:off
          """|3. Grant the WRITE_SECURE_SETTINGS permission:
        |
        |pm grant com.pyamsoft.powermanager android.permission.WRITE_SECURE_SETTINGS""".trimMargin()
        // formatter:on
        else """|3. Grant the DUMP permission:
        |
        |pm grant com.pyamsoft.powermanager android.permission.DUMP""".trimMargin()}
        |
        |Finally, close and re-open the application.""".trimMargin()
    val messageSpan = SpannableString(message)
    StringUtil.sizeSpan(messageSpan, 0, message.length, AppUtil.convertToDP(activity, 14F).toInt())
    return AlertDialog.Builder(activity).setTitle("Doze Mode Workaround").setMessage(
        messageSpan).setPositiveButton("Ok", { _, _ -> dismiss() }).create()
  }

}

