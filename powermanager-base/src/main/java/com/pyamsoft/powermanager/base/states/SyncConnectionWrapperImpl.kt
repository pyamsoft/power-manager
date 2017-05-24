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

package com.pyamsoft.powermanager.base.states

import android.content.ContentResolver
import com.pyamsoft.powermanager.base.logger.Logger
import com.pyamsoft.powermanager.model.States
import javax.inject.Inject

internal class SyncConnectionWrapperImpl @Inject constructor(
    private val logger: Logger) : DeviceFunctionWrapper {

  private fun toggle(state: Boolean) {
    logger.i("Sync: %s", if (state) "enable" else "disable")
    ContentResolver.setMasterSyncAutomatically(state)
  }

  override fun enable() {
    toggle(true)
  }

  override fun disable() {
    toggle(false)
  }

  override val state: States
    get() = if (ContentResolver.getMasterSyncAutomatically()) States.ENABLED else States.DISABLED
}
