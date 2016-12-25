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

package com.pyamsoft.powermanager.presenter.airplane;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanagermodel.BooleanInterestObserver;
import com.pyamsoft.powermanager.presenter.PowerManagerPreferences;
import com.pyamsoft.powermanager.presenter.base.PeriodPreferenceInteractor;
import com.pyamsoft.powermanager.presenter.base.PeriodPreferenceInteractorImpl;
import com.pyamsoft.powermanager.presenter.base.PeriodPreferencePresenter;
import com.pyamsoft.powermanager.presenter.base.PeriodPreferencePresenterImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class AirplanePeriodPreferenceModule {

  @Provides @Named("airplane_period_pref")
  PeriodPreferencePresenter provideAirplaneManagePreferencePresenter(
      @Named("airplane_period_pref_interactor") PeriodPreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler,
      @Named("obs_airplane_periodic") BooleanInterestObserver periodicObserver) {
    return new PeriodPreferencePresenterImpl(interactor, obsScheduler, subScheduler,
        periodicObserver);
  }

  @Provides @Named("airplane_period_pref_interactor")
  PeriodPreferenceInteractor provideAirplaneManagePreferenceInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new PeriodPreferenceInteractorImpl(preferences);
  }
}
