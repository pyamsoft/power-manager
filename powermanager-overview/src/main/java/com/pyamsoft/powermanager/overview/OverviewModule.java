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

package com.pyamsoft.powermanager.overview;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class OverviewModule {

  @Provides OverviewItemPresenter provideOverviewItemPresenter(@Named("obs") Scheduler obsScheduler,
      @Named("sub") Scheduler subScheduler) {
    return new OverviewItemPresenter(obsScheduler, subScheduler);
  }

  @Provides OverviewPresenter provideOverviewPresenter(@NonNull OverviewInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler,
      @Named("obs_wifi_manage") BooleanInterestObserver wifiObserver,
      @Named("obs_data_manage") BooleanInterestObserver dataObserver,
      @Named("obs_bluetooth_manage") BooleanInterestObserver bluetoothObserver,
      @Named("obs_sync_manage") BooleanInterestObserver syncObserver,
      @Named("obs_airplane_manage") BooleanInterestObserver airplaneObserver,
      @Named("obs_doze_manage") BooleanInterestObserver dozeObserver,
      @Named("obs_wear_manage") BooleanInterestObserver wearObserver) {
    return new OverviewPresenter(interactor, obsScheduler, subScheduler, wifiObserver, dataObserver,
        bluetoothObserver, syncObserver, airplaneObserver, dozeObserver, wearObserver);
  }

  @Provides OverviewInteractor provideInteractor(@NonNull PowerManagerPreferences preferences) {
    return new OverviewInteractor(preferences);
  }
}
