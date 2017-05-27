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

import android.support.v7.widget.RecyclerView.ViewHolder
import com.mikepenz.fastadapter.items.GenericAbstractItem
import timber.log.Timber

@Suppress(
    "FINITE_BOUNDS_VIOLATION_IN_JAVA") abstract class BaseItem<I : GenericAbstractItem<String, *, VH>, VH : ViewHolder> internal constructor(
    tag: String) : GenericAbstractItem<String, I, VH>(tag) {
  override fun unbindView(holder: VH) {
    super.unbindView(holder)
    Timber.d("UNBIND VIEW HOLDER: %s", model)
  }

  abstract fun unbindItem()
}

