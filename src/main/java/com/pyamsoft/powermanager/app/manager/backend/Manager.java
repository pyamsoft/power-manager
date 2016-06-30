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
import android.support.v4.util.Pair;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerInteractor;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

abstract class Manager {

  @NonNull private final ManagerInteractor interactor;
  @NonNull private final Scheduler mainScheduler;
  @NonNull private final Scheduler ioScheduler;
  @NonNull private Subscription subscription = Subscriptions.empty();
  @NonNull private Subscription disableJobSubscription = Subscriptions.empty();
  @NonNull private Subscription enableJobSubscription = Subscriptions.empty();

  protected Manager(@NonNull ManagerInteractor interactor,
      @NonNull @Named("main") Scheduler mainScheduler,
      @NonNull @Named("io") Scheduler ioScheduler) {
    this.interactor = interactor;
    this.ioScheduler = ioScheduler;
    this.mainScheduler = mainScheduler;
  }

  public final void cleanup() {
    unsubscribe();
    unsubsDisable();
    unsubsEnable();
  }

  private void unsubscribe() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  private void unsubsDisable() {
    if (!disableJobSubscription.isUnsubscribed()) {
      disableJobSubscription.unsubscribe();
    }
  }

  private void unsubsEnable() {
    if (!enableJobSubscription.isUnsubscribed()) {
      enableJobSubscription.unsubscribe();
    }
  }

  @CheckResult @NonNull final Observable<ManagerInteractor> baseEnableObservable() {
    return Observable.defer(() -> Observable.just(interactor)).map(managerInteractor -> {
      Timber.d("Cancel any running jobs");
      managerInteractor.cancelJobs();
      return managerInteractor;
    }).zipWith(interactor.isManaged(), (managerInteractor, managed) -> {
      Timber.d("Check that manager isManaged");
      if (managed) {
        return managerInteractor;
      } else {
        return null;
      }
    }).filter(managerInteractor -> {
      Timber.d("Filter out nulls");
      return managerInteractor != null;
    });
  }

  @CheckResult @NonNull
  final Observable<ManagerInteractor> baseDisableObservable(boolean charging) {
    final Observable<Boolean> ignoreChargingObservable =
        interactor.isChargingIgnore().map(ignoreCharding -> ignoreCharding && charging);
    return Observable.defer(() -> Observable.just(interactor)).map(managerInteractor -> {
      Timber.d("Cancel any running jobs");
      managerInteractor.cancelJobs();
      return managerInteractor;
    }).zipWith(ignoreChargingObservable, (managerInteractor, ignoreCharging) -> {
      Timber.d("Check that manager ignoreCharging");
      if (ignoreCharging) {
        return null;
      } else {
        return managerInteractor;
      }
    }).filter(managerInteractor -> {
      Timber.d("Filter out nulls");
      return managerInteractor != null;
    }).zipWith(interactor.isManaged(), (managerInteractor, managed) -> {
      Timber.d("Check that manager isManaged");
      if (managed) {
        return managerInteractor;
      } else {
        return null;
      }
    }).filter(managerInteractor -> {
      Timber.d("Filter out nulls");
      return managerInteractor != null;
    });
  }

  public final void enable(long time, boolean periodic, boolean explicit) {
    unsubsEnable();
    if (explicit) {
      interactor.setOriginalState(true);
    }
    enableJobSubscription = interactor.createEnableJob(time, periodic)
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .subscribe(deviceJob -> {
          PowerManager.getInstance().getJobManager().addJobInBackground(deviceJob);
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  public final void disable(long time, boolean periodic, boolean explicit) {
    unsubsDisable();
    disableJobSubscription = interactor.isEnabled().flatMap(enabled -> {
      interactor.setOriginalState(enabled || explicit);
      return interactor.createDisableJob(time, periodic);
    }).subscribeOn(ioScheduler).observeOn(mainScheduler).subscribe(deviceJob -> {
      PowerManager.getInstance().getJobManager().addJobInBackground(deviceJob);
    }, throwable -> {
      // TODO
      Timber.e(throwable, "onError");
    });
  }

  protected void enable(@NonNull Observable<ManagerInteractor> observable) {
    unsubscribe();
    subscription = observable.filter(managerInteractor -> managerInteractor != null)
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .subscribe(managerInteractor -> {
          Timber.d("Queue enable");
          enable(0, false, false);
        }, throwable -> Timber.e(throwable, "onError"), () -> {
          Timber.d("onComplete");
          interactor.setOriginalState(false);
        });
  }

  public void enable() {
    enable(baseEnableObservable());
  }

  protected void disable(@NonNull Observable<ManagerInteractor> observable) {
    unsubscribe();
    subscription = observable.filter(managerInteractor -> managerInteractor != null)
        .flatMap(ManagerInteractor::isPeriodic)
        .zipWith(interactor.getDelayTime(), (periodic, delayTime) -> {
          return new Pair<>(periodic, delayTime * 1000);
        })
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .subscribe(pair -> {
          Timber.d("Queue disable");
          disable(pair.second, pair.first, false);
        }, throwable -> Timber.e(throwable, "onError"), () -> Timber.d("onComplete"));
  }

  public void disable(boolean charging) {
    disable(baseDisableObservable(charging));
  }

  abstract void onEnableComplete();

  abstract void onDisableComplete();
}
