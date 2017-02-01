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

package com.pyamsoft.powermanager.main;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.model.PermissionObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class MainModule {

  @Provides MainPresenter provideMainPresenter(@NonNull MainInteractor interactor,
      @NonNull @Named("obs") Scheduler obsScheduler, @NonNull @Named("sub") Scheduler subScheduler,
      @Named("obs_root_permission") PermissionObserver rootPermissionObserver) {
    return new MainPresenterImpl(interactor, obsScheduler, subScheduler, rootPermissionObserver);
  }

  @Provides MainInteractor provideMainInteractor(@NonNull PowerManagerPreferences preferences) {
    return new MainInteractorImpl(preferences);
  }
}
