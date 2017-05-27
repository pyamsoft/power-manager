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

package com.pyamsoft.powermanager.base.permission

import android.content.Context
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.shell.RootChecker
import com.pyamsoft.powermanager.model.PermissionObserver
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module class PermissionObserverModule {
  @Singleton @Named("obs_root_permission") @Provides fun provideRootPermissionObserver(
      rootChecker: RootChecker, context: Context,
      preferences: RootPreferences): PermissionObserver {
    return RootPermissionObserver(context, preferences, rootChecker)
  }

  @Singleton @Named("obs_doze_permission") @Provides fun provideDozePermissionObserver(
      rootChecker: RootChecker, context: Context,
      preferences: RootPreferences): PermissionObserver {
    return DozePermissionObserver(context, preferences, rootChecker)
  }
}
