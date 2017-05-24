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

package com.pyamsoft.powermanager.base

import android.app.Activity
import android.app.Service
import android.content.Context
import com.pyamsoft.powermanager.base.preference.AirplanePreferences
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences
import com.pyamsoft.powermanager.base.preference.ClearPreferences
import com.pyamsoft.powermanager.base.preference.DataPreferences
import com.pyamsoft.powermanager.base.preference.DozePreferences
import com.pyamsoft.powermanager.base.preference.LoggerPreferences
import com.pyamsoft.powermanager.base.preference.ManagePreferences
import com.pyamsoft.powermanager.base.preference.OnboardingPreferences
import com.pyamsoft.powermanager.base.preference.RootPreferences
import com.pyamsoft.powermanager.base.preference.ServicePreferences
import com.pyamsoft.powermanager.base.preference.SyncPreferences
import com.pyamsoft.powermanager.base.preference.TriggerPreferences
import com.pyamsoft.powermanager.base.preference.WearablePreferences
import com.pyamsoft.powermanager.base.preference.WifiPreferences
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton

@Module class PowerManagerModule(context: Context,
    private val mainActivityClass: Class<out Activity>,
    private val toggleServiceClass: Class<out Service>) {
  private val appContext: Context = context.applicationContext
  private val preferences: PowerManagerPreferencesImpl

  init {
    preferences = PowerManagerPreferencesImpl(appContext)
  }

  @Singleton @Provides @Named("data_uri") internal fun provideMobileDataUri(): String {
    return SETTINGS_URI_MOBILE_DATA
  }

  @Singleton @Provides @Named("main") internal fun provideMainClass(): Class<out Activity> {
    return mainActivityClass
  }

  @Singleton @Provides @Named(
      "toggle") internal fun provideToggleServiceClass(): Class<out Service> {
    return toggleServiceClass
  }

  @Singleton @Provides internal fun provideContext(): Context {
    return appContext
  }

  @Singleton @Provides internal fun provideWifiPreferences(): WifiPreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideClearPreferences(): ClearPreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideWearablePreferences(): WearablePreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideAirplanePreferences(): AirplanePreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideBluetoothPreferences(): BluetoothPreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideDataPreferences(): DataPreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideDozePreferences(): DozePreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideSyncPreferences(): SyncPreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideOnboardingPreferences(): OnboardingPreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideTriggerPreferences(): TriggerPreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideServicePreferences(): ServicePreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideRootPreferences(): RootPreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideLoggerPreferences(): LoggerPreferences {
    return preferences
  }

  @Singleton @Provides internal fun provideManagePreferences(): ManagePreferences {
    return preferences
  }

  @Singleton @Provides @Named("sub") internal fun provideSubScheduler(): Scheduler {
    return Schedulers.computation()
  }

  @Singleton @Provides @Named("io") internal fun provideIoScheduler(): Scheduler {
    return Schedulers.io()
  }

  @Singleton @Provides @Named("obs") internal fun provideObsScheduler(): Scheduler {
    return AndroidSchedulers.mainThread()
  }

  companion object {

    private val SETTINGS_URI_MOBILE_DATA = "mobile_data"
  }
}
