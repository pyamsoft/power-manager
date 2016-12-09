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
import com.pyamsoft.powermanager.app.trigger.TriggerPreferencePresenter;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import javax.inject.Inject;
import rx.Scheduler;

class TriggerPreferencePresenterImpl extends SchedulerPresenter<TriggerPreferencePresenter.Provider>
    implements TriggerPreferencePresenter {

  @NonNull private final TriggerPreferenceInteractor interactor;

  @Inject TriggerPreferencePresenterImpl(@NonNull TriggerPreferenceInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override public void restartService() {
    interactor.restartTriggers();
  }
}
