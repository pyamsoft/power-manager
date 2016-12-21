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

package com.pyamsoft.powermanagerpresenter.data;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanagermodel.BooleanInterestObserver;
import com.pyamsoft.powermanagerpresenter.PowerManagerPreferences;
import com.pyamsoft.powermanagerpresenter.base.PeriodPreferenceInteractor;
import com.pyamsoft.powermanagerpresenter.base.PeriodPreferencePresenter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class DataPeriodPreferenceModule {

  @Provides @Named("data_period_pref")
  PeriodPreferencePresenter provideDataManagePreferencePresenter(
      @Named("data_period_pref_interactor") PeriodPreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler,
      @Named("obs_data_periodic") BooleanInterestObserver periodicObserver) {
    return new DataPeriodPreferencePresenterImpl(interactor, obsScheduler, subScheduler,
        periodicObserver);
  }

  @Provides @Named("data_period_pref_interactor")
  PeriodPreferenceInteractor provideDataManagePreferenceInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new DataPeriodPreferenceInteractorImpl(preferences);
  }
}