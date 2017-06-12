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

package com.pyamsoft.powermanager.base.preference

import android.content.SharedPreferences
import android.support.annotation.CheckResult

interface ManagePreferences {
  var manageDelay: Long
    @CheckResult get
  var customManageDelay: Boolean
    @CheckResult get
  var customDisableTime: Boolean
    @CheckResult get
  var periodicDisableTime: Long
    @CheckResult get
  val periodicEnableTime: Long
    @CheckResult get

  @CheckResult fun registerDelayChanges(
      listener: (Long) -> Unit): SharedPreferences.OnSharedPreferenceChangeListener

  fun unregisterDelayChanges(listener: SharedPreferences.OnSharedPreferenceChangeListener)

  @CheckResult fun registerDisableChanges(
      listener: (Long) -> Unit): SharedPreferences.OnSharedPreferenceChangeListener

  fun unregisterDisableChanges(listener: SharedPreferences.OnSharedPreferenceChangeListener)
}
