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

package com.pyamsoft.powermanager.sync;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.uicore.ManagePreferenceInteractor;
import com.pyamsoft.powermanager.uicore.ManagePreferencePresenter;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import javax.inject.Named;

@Module public class SyncManagePreferenceModule {

  @Provides @Named("sync_manage_pref")
  ManagePreferencePresenter provideSyncManagePreferencePresenter(
      @Named("sync_manage_pref_interactor") ManagePreferenceInteractor interactor,
      @Named("obs") Scheduler obsScheduler, @Named("sub") Scheduler subScheduler) {
    return new ManagePreferencePresenter(interactor, obsScheduler, subScheduler);
  }

  @Provides @Named("sync_manage_pref_interactor")
  ManagePreferenceInteractor provideSyncManagePreferenceInteractor(
      @NonNull PowerManagerPreferences preferences) {
    return new ManagePreferenceInteractor(preferences);
  }
}
