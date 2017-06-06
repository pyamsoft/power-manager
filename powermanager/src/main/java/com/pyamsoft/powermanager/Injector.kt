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

package com.pyamsoft.powermanager

import android.app.Activity
import android.app.Service
import android.content.Context
import android.support.annotation.CheckResult
import com.pyamsoft.powermanager.base.PowerManagerModule
import com.pyamsoft.pydroid.helper.ThreadSafe.MutableSingleton

class Injector private constructor(private val component: PowerManagerComponent) {

  @CheckResult fun provideComponent(): PowerManagerComponent {
    return component
  }

  companion object {

    private val singleton = MutableSingleton<Injector>(null)

    @JvmStatic internal fun set(context: Context, mainActivityClass: Class<out Activity>,
        toggleServiceClass: Class<out Service>) {
      singleton.assign(Injector(DaggerPowerManagerComponent.builder().powerManagerModule(
          PowerManagerModule(context.applicationContext, mainActivityClass,
              toggleServiceClass)).build()))
    }

    @JvmStatic @CheckResult fun get(): Injector {
      return singleton.access()
    }
  }
}
