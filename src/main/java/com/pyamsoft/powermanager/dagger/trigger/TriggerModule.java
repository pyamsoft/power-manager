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

package com.pyamsoft.powermanager.dagger.trigger;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.trigger.TriggerPresenter;
import com.pyamsoft.powermanager.dagger.PowerTriggerDB;
import com.pyamsoft.pydroid.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class TriggerModule {

  @ActivityScope @Provides TriggerPresenter provideTriggerPresenter(
      @NonNull @Named("main") Scheduler mainScheduler, @NonNull @Named("computation") Scheduler ioScheduler,
      @NonNull TriggerInteractor interactor) {
    return new TriggerPresenterImpl(mainScheduler, ioScheduler, interactor);
  }

  @ActivityScope @Provides TriggerInteractor provideTriggerInteractor(
      PowerTriggerDB powerTriggerDB) {
    return new TriggerInteractorImpl(powerTriggerDB);
  }
}
