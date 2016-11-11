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

package com.pyamsoft.powermanager.dagger.logger;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.logger.LoggerPresenter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Scheduler;

@Module public class LoggerModule {

  @Singleton @Provides @Named("logger_manager") Logger provideManagerLogger(
      @NonNull @Named("logger_presenter_manager") LoggerPresenter loggerPresenter) {
    return new ManagerLogger(loggerPresenter);
  }

  @Singleton @Provides @Named("logger_presenter_manager")
  LoggerPresenter provideManagerLoggerPresenter(
      @NonNull @Named("logger_interactor_manager") LoggerInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new LoggerPresenterImpl(interactor, obsScheduler, subScheduler);
  }

  @Singleton @Provides @Named("logger_interactor_manager")
  LoggerInteractor provideManagerLoggerInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new ManageLoggerInteractor(context, preferences);
  }
}