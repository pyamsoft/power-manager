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

import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.presenter.base.OverviewPagerPresenter;
import com.pyamsoft.powermanager.presenter.base.OverviewPagerPresenterImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class AirplaneOverviewModule {

  @Provides @Named("airplane_overview")
  OverviewPagerPresenter provideAirplaneOverviewPagerPresenter(
      @Named("mod_airplane_state") BooleanInterestModifier stateModifier,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new OverviewPagerPresenterImpl(obsScheduler, subScheduler, stateModifier);
  }
}