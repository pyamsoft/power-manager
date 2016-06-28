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

package com.pyamsoft.powermanager.app.base;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.base.Presenter;
import javax.inject.Named;
import rx.Scheduler;

public abstract class SchedulerPresenter<I> extends Presenter<I> {

  @NonNull private final Scheduler observeScheduler;
  @NonNull private final Scheduler subscribeScheduler;

  protected SchedulerPresenter(@NonNull @Named("main") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler) {
    this.observeScheduler = observeScheduler;
    this.subscribeScheduler = subscribeScheduler;
  }

  @NonNull protected final Scheduler getObserveScheduler() {
    return observeScheduler;
  }

  @NonNull protected final Scheduler getSubscribeScheduler() {
    return subscribeScheduler;
  }
}
