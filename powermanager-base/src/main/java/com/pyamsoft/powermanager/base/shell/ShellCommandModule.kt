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

package com.pyamsoft.powermanager.base.shell

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import javax.inject.Named
import javax.inject.Singleton

@Module class ShellCommandModule {
  @Volatile private var impl: ShellHandlerImpl? = null

  @Singleton @Provides fun provideShellCommandHelper(@Named("obs") obsScheduler: Scheduler,
      @Named("io") subScheduler: Scheduler): ShellCommandHelper {
    return createImpl(obsScheduler, subScheduler)
  }

  private fun createImpl(obsScheduler: Scheduler, subScheduler: Scheduler): ShellHandlerImpl {
    if (impl == null) {
      synchronized(ShellCommandModule::class.java) {
        if (impl == null) {
          impl = ShellHandlerImpl(obsScheduler, subScheduler)
        }
      }
    }

    return impl!!
  }

  @Singleton @Provides fun provideRootChecker(@Named("obs") obsScheduler: Scheduler,
      @Named("io") subScheduler: Scheduler): RootChecker {
    return createImpl(obsScheduler, subScheduler)
  }
}
