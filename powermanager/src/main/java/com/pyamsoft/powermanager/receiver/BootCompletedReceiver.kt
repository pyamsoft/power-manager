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

package com.pyamsoft.powermanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.service.Manager
import timber.log.Timber
import javax.inject.Inject

class BootCompletedReceiver : BroadcastReceiver() {

  @field:Inject lateinit internal var manager: Manager

  override fun onReceive(context: Context, intent: Intent?) {
    if (intent != null) {
      val action = intent.action
      if (Intent.ACTION_BOOT_COMPLETED == action) {
        Timber.d("Boot completed")
        Injector.with(context) {
          it.inject(this)
        }

        manager.cleanup()
      }
    }
  }
}
