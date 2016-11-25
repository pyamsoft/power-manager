/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.dagger;

import android.app.AlarmManager;
import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.wrapper.PowerTriggerDB;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module public class PowerManagerModule {

  @NonNull private final Context appContext;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final PowerTriggerDB powerTriggerDB;
  @NonNull private final AlarmManager alarmManager;

  public PowerManagerModule(final @NonNull Context context) {
    appContext = context.getApplicationContext();
    preferences = new PowerManagerPreferencesImpl(appContext);
    powerTriggerDB = new PowerTriggerDBImpl(appContext, Schedulers.io());
    alarmManager = appContext.getSystemService(AlarmManager.class);
  }

  @Singleton @Provides Context provideContext() {
    return appContext;
  }

  @Singleton @Provides PowerTriggerDB providePowerTriggerDB() {
    return powerTriggerDB;
  }

  @Singleton @Provides PowerManagerPreferences providePreferences() {
    return preferences;
  }

  @Singleton @Provides AlarmManager provideAlarmManager() {
    return alarmManager;
  }

  @Singleton @Provides @Named("sub") Scheduler provideIOScheduler() {
    return Schedulers.io();
  }

  @Singleton @Provides @Named("obs") Scheduler provideMainThreadScheduler() {
    return AndroidSchedulers.mainThread();
  }
}
