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

package com.pyamsoft.powermanager.base;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.AirplanePreferences;
import com.pyamsoft.powermanager.base.preference.BluetoothPreferences;
import com.pyamsoft.powermanager.base.preference.ClearPreferences;
import com.pyamsoft.powermanager.base.preference.DataPreferences;
import com.pyamsoft.powermanager.base.preference.DozePreferences;
import com.pyamsoft.powermanager.base.preference.LoggerPreferences;
import com.pyamsoft.powermanager.base.preference.OnboardingPreferences;
import com.pyamsoft.powermanager.base.preference.RootPreferences;
import com.pyamsoft.powermanager.base.preference.ServicePreferences;
import com.pyamsoft.powermanager.base.preference.SyncPreferences;
import com.pyamsoft.powermanager.base.preference.TriggerPreferences;
import com.pyamsoft.powermanager.base.preference.WearablePreferences;
import com.pyamsoft.powermanager.base.preference.WifiPreferences;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class PowerManagerModule {

  @NonNull private static final String SETTINGS_URI_MOBILE_DATA = "mobile_data";
  @NonNull private final Context appContext;
  @NonNull private final PowerManagerPreferencesImpl preferences;
  @NonNull private final Class<? extends Activity> mainActivityClass;
  @NonNull private final Class<? extends Service> toggleServiceClass;

  public PowerManagerModule(@NonNull Context context,
      @NonNull Class<? extends Activity> mainActivityClass,
      @NonNull Class<? extends Service> toggleServiceClass) {
    appContext = context.getApplicationContext();
    this.mainActivityClass = mainActivityClass;
    this.toggleServiceClass = toggleServiceClass;
    preferences = new PowerManagerPreferencesImpl(appContext);
  }

  @Singleton @Provides @Named("data_uri") String provideMobileDataUri() {
    return SETTINGS_URI_MOBILE_DATA;
  }

  @Singleton @Provides @Named("main") Class<? extends Activity> provideMainClass() {
    return mainActivityClass;
  }

  @Singleton @Provides @Named("toggle") Class<? extends Service> provideToggleServiceClass() {
    return toggleServiceClass;
  }

  @Singleton @Provides Context provideContext() {
    return appContext;
  }

  @Singleton @Provides WifiPreferences provideWifiPreferences() {
    return preferences;
  }

  @Singleton @Provides ClearPreferences provideClearPreferences() {
    return preferences;
  }

  @Singleton @Provides WearablePreferences provideWearablePreferences() {
    return preferences;
  }

  @Singleton @Provides AirplanePreferences provideAirplanePreferences() {
    return preferences;
  }

  @Singleton @Provides BluetoothPreferences provideBluetoothPreferences() {
    return preferences;
  }

  @Singleton @Provides DataPreferences provideDataPreferences() {
    return preferences;
  }

  @Singleton @Provides DozePreferences provideDozePreferences() {
    return preferences;
  }

  @Singleton @Provides SyncPreferences provideSyncPreferences() {
    return preferences;
  }

  @Singleton @Provides OnboardingPreferences provideOnboardingPreferences() {
    return preferences;
  }

  @Singleton @Provides TriggerPreferences provideTriggerPreferences() {
    return preferences;
  }

  @Singleton @Provides ServicePreferences provideServicePreferences() {
    return preferences;
  }

  @Singleton @Provides RootPreferences provideRootPreferences() {
    return preferences;
  }

  @Singleton @Provides LoggerPreferences provideLoggerPreferences() {
    return preferences;
  }

  @Singleton @Provides @Named("sub") Scheduler provideSubScheduler() {
    return Schedulers.computation();
  }

  @Singleton @Provides @Named("io") Scheduler provideIoScheduler() {
    return Schedulers.io();
  }

  @Singleton @Provides @Named("obs") Scheduler provideObsScheduler() {
    return AndroidSchedulers.mainThread();
  }
}
