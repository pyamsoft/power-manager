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

package com.pyamsoft.powermanager.app.manager.backend;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerInteractor;
import com.pyamsoft.pydroid.base.Presenter;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

abstract class Manager<I extends ManagerView> extends Presenter<I> {

  @NonNull private final ManagerInteractor interactor;
  @NonNull private final Scheduler ioScheduler;
  @NonNull private final Scheduler mainScheduler;
  @NonNull private Subscription subscription = Subscriptions.empty();

  protected Manager(@NonNull ManagerInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    this.interactor = interactor;
    this.ioScheduler = ioScheduler;
    this.mainScheduler = mainScheduler;
  }

  @NonNull @CheckResult final Scheduler getIoScheduler() {
    return ioScheduler;
  }

  @NonNull @CheckResult final Scheduler getMainScheduler() {
    return mainScheduler;
  }

  final void setSubscription(@NonNull Subscription subscription) {
    this.subscription = subscription;
  }

  final void unsubscribe() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @CheckResult @NonNull final Observable<ManagerInteractor> baseEnableObservable() {
    return Observable.defer(() -> Observable.just(interactor)).map(managerInteractor -> {
      Timber.d("Cancel any running jobs");
      managerInteractor.cancelJobs();
      return managerInteractor;
    }).filter(managerInteractor -> {
      Timber.d("Check that manager isManaged");
      return managerInteractor.isManaged();
    });
  }

  @CheckResult @NonNull final Observable<ManagerInteractor> baseDisableObservable() {
    return Observable.defer(() -> Observable.just(interactor)).map(managerInteractor -> {
      Timber.d("Cancel any running jobs");
      managerInteractor.cancelJobs();
      return managerInteractor;
    }).filter(managerInteractor -> {
      Timber.d("Check that manager isManaged");
      return managerInteractor.isManaged();
    });
  }

  public final void enable(long time) {
    PowerManager.getInstance().getJobManager().addJobInBackground(interactor.createEnableJob(time));
  }

  public final void disable(long time) {
    interactor.setOriginalState(interactor.isEnabled());
    PowerManager.getInstance()
        .getJobManager()
        .addJobInBackground(interactor.createDisableJob(time));
  }

  @CheckResult final boolean isEnabled() {
    return interactor.isEnabled();
  }

  @CheckResult final boolean isManaged() {
    return interactor.isManaged();
  }

  public final void onStateChanged() {
    if (isEnabled()) {
      getView().stateEnabled();
    } else {
      getView().stateDisabled();
    }
  }

  public final void onManagedChanged() {
    if (isManaged()) {
      getView().startManaging();
    } else {
      getView().stopManaging();
    }
  }

  public abstract void enable();

  public abstract void disable();
}
