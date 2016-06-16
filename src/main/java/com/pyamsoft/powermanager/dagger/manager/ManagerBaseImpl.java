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

package com.pyamsoft.powermanager.dagger.manager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.manager.Manager;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

abstract class ManagerBaseImpl implements Manager {

  @NonNull private final ManagerInteractor interactor;
  @NonNull private Subscription subscription = Subscriptions.empty();
  @NonNull private final Scheduler ioScheduler;
  @NonNull private final Scheduler mainScheduler;

  protected ManagerBaseImpl(@NonNull ManagerInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    this.interactor = interactor;
    this.ioScheduler = ioScheduler;
    this.mainScheduler = mainScheduler;
  }

  @NonNull @CheckResult Scheduler getIoScheduler() {
    return ioScheduler;
  }

  @NonNull @CheckResult Scheduler getMainScheduler() {
    return mainScheduler;
  }

  void setSubscription(@NonNull Subscription subscription) {
    this.subscription = subscription;
  }

  void unsubscribe() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @Override public final void enable(long time) {
    interactor.cancelJobs();
    PowerManager.getInstance().getJobManager().addJobInBackground(interactor.createEnableJob(time));
  }

  @Override public final void disable(long time) {
    interactor.setOriginalState(interactor.isEnabled());
    interactor.cancelJobs();
    PowerManager.getInstance()
        .getJobManager()
        .addJobInBackground(interactor.createDisableJob(time));
  }
}
