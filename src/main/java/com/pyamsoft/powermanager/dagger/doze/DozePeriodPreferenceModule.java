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

package com.pyamsoft.powermanager.dagger.doze;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.base.PeriodPreferencePresenter;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.base.PeriodPreferenceInteractor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class DozePeriodPreferenceModule {

  @Provides @Named("doze_period_pref")
  PeriodPreferencePresenter provideDozeManagePreferencePresenter(
      @Named("doze_period_pref_interactor") PeriodPreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler,
      @Named("obs_doze_periodic") BooleanInterestObserver periodicObserver) {
    return new DozePeriodPreferencePresenterImpl(interactor, obsScheduler, subScheduler,
        periodicObserver);
  }

  @Provides @Named("doze_period_pref_interactor")
  PeriodPreferenceInteractor provideDozeManagePreferenceInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new DozePeriodPreferenceInteractorImpl(preferences);
  }
}
