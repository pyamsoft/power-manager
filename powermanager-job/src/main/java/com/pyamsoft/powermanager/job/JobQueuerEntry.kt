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

package com.pyamsoft.powermanager.job

import android.support.annotation.CheckResult
import com.google.auto.value.AutoValue

@AutoValue abstract class JobQueuerEntry {

  @CheckResult abstract fun tag(): String

  @CheckResult abstract fun firstRun(): Boolean

  @CheckResult abstract fun oneshot(): Boolean

  @CheckResult abstract fun screenOn(): Boolean

  @CheckResult abstract fun delay(): Long

  @CheckResult abstract fun repeatingOnWindow(): Long

  @CheckResult abstract fun repeatingOffWindow(): Long

  @AutoValue.Builder abstract class Builder {

    @CheckResult protected abstract fun tag(tag: String): Builder

    @CheckResult abstract fun screenOn(screen: Boolean): Builder

    @CheckResult abstract fun firstRun(first: Boolean): Builder

    @CheckResult abstract fun oneshot(oneshot: Boolean): Builder

    @CheckResult abstract fun delay(delay: Long): Builder

    @CheckResult abstract fun repeatingOnWindow(window: Long): Builder

    @CheckResult abstract fun repeatingOffWindow(window: Long): Builder

    @CheckResult abstract fun build(): JobQueuerEntry
  }

  companion object {
    @JvmStatic @CheckResult fun builder(tag: String): Builder {
      return AutoValue_JobQueuerEntry.Builder().tag(tag)
    }
  }
}
