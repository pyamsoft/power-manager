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

package com.pyamsoft.powermanager.manage

import android.support.annotation.CheckResult
import io.reactivex.Completable
import io.reactivex.Single

abstract class ExceptionInteractor {

  @CheckResult internal abstract fun setIgnoreCharging(state: Boolean): Completable

  @get:CheckResult internal abstract val isIgnoreCharging: Single<Pair<Boolean, Boolean>>

  @CheckResult internal abstract fun setIgnoreWear(state: Boolean): Completable

  @get:CheckResult internal abstract val isIgnoreWear: Single<Pair<Boolean, Boolean>>
}
