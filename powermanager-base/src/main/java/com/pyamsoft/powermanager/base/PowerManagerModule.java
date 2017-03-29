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

package com.pyamsoft.powermanager.base;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.support.annotation.NonNull;
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
  @NonNull private final PowerManagerPreferences preferences;
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

  @Singleton @Provides PowerManagerPreferences providePreferences() {
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
