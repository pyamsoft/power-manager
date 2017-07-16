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

package com.pyamsoft.powermanager.uicore

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.Window
import com.pyamsoft.powermanager.PowerManager
import com.pyamsoft.powermanager.R

abstract class WatchedBottomSheet : BottomSheetDialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = BottomSheetDialog(activity, R.style.Theme_PowerManager_Light_BottomSheet)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    return dialog
  }

  @CallSuper override fun onDestroy() {
    super.onDestroy()
    PowerManager.getRefWatcher(this).watch(this)
  }
}
