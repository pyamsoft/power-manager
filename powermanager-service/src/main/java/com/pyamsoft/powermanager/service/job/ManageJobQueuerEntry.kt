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

package com.pyamsoft.powermanager.service.job

import com.evernote.android.job.util.support.PersistableBundleCompat
import com.pyamsoft.powermanager.job.JobQueuerEntry

class ManageJobQueuerEntry(tag: String, delay: Long, internal val firstRun: Boolean,
    internal val oneShot: Boolean, internal val screenOn: Boolean,
    internal val repeatingOnWindow: Long, internal val repeatingOffWindow: Long) : JobQueuerEntry(
    tag, delay) {

  override fun getOptions(): PersistableBundleCompat {
    val extras = PersistableBundleCompat()
    extras.putBoolean(KEY_SCREEN, screenOn)
    extras.putLong(KEY_ON_WINDOW, repeatingOnWindow)
    extras.putLong(KEY_OFF_WINDOW, repeatingOffWindow)
    extras.putBoolean(KEY_ONESHOT, oneShot)
    extras.putBoolean(KEY_FIRST_RUN, firstRun)
    return extras
  }

  companion object {

    const val KEY_ON_WINDOW = "extra_key__on_window"
    const val KEY_OFF_WINDOW = "extra_key__off_window"
    const val KEY_SCREEN = "extra_key__screen"
    const val KEY_ONESHOT = "extra_key__once"
    const val KEY_FIRST_RUN = "extra_key__first"
  }

}
