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

package com.pyamsoft.powermanager.app.manager.period;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.manager.ManagerSettingsPresenter;
import com.pyamsoft.powermanager.dagger.manager.period.ManagerPeriodicInteractor;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public final class ManagerPeriodicPresenter extends ManagerSettingsPresenter<ManagerPeriodicView> {

  @NonNull private final ManagerPeriodicInteractor interactor;
  @NonNull private Subscription managedSubscription = Subscriptions.empty();
  @NonNull private Subscription customEnableSubscription = Subscriptions.empty();
  @NonNull private Subscription customDisableSubscription = Subscriptions.empty();

  @Inject public ManagerPeriodicPresenter(@NonNull ManagerPeriodicInteractor interactor,
      @Named("main") Scheduler mainScheduler, @Named("io") Scheduler ioScheduler) {
    super(interactor, mainScheduler, ioScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubManaged();
    unsubCustomDisable();
    unsubCustomEnable();
  }

  private void unsubManaged() {
    if (!managedSubscription.isUnsubscribed()) {
      managedSubscription.unsubscribe();
    }
  }

  private void unsubCustomEnable() {
    if (!customEnableSubscription.isUnsubscribed()) {
      customEnableSubscription.unsubscribe();
    }
  }

  private void unsubCustomDisable() {
    if (!customDisableSubscription.isUnsubscribed()) {
      customDisableSubscription.unsubscribe();
    }
  }

  public final void setPeriodicFromPreference(@NonNull String key) {
    unsubManaged();
    managedSubscription = interactor.isManaged(key)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(enabled -> {
          if (enabled) {
            getView().enablePeriodic();
          } else {
            getView().disablePeriodic();
          }
        }, throwable -> {
          // TODO
        });
  }

  public final void setCustomPeriodicDisableTimeStateFromPreference(@NonNull String managedKey,
      @NonNull String key, boolean isPeriodic) {
    unsubCustomDisable();
    customDisableSubscription = interactor.isCustomPeriodicDisableTime(key)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(customTime -> {
          updateCustomPeriodicDisableTimeView(managedKey, customTime && isPeriodic);
        }, throwable -> {
          // TODO
        });
  }

  public final void updateCustomPeriodicDisableTimeView(@NonNull String managedKey,
      boolean newState) {
    unsubManaged();
    managedSubscription = interactor.isManaged(managedKey)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(isManaged -> {
          if (newState && isManaged) {
            getView().enablePeriodicDisableTime();
          } else {
            getView().disablePeriodicDisableTime();
          }
        }, throwable -> {
          // TODO
        });
  }

  public final void setCustomPeriodicEnableTimeStateFromPreference(@NonNull String managedKey,
      @NonNull String key, boolean isPeriodic) {
    unsubCustomEnable();
    customEnableSubscription = interactor.isCustomPeriodicEnableTime(key)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(customTime -> {
          updateCustomPeriodicEnableTimeView(managedKey, customTime && isPeriodic);
        }, throwable -> {
          // TODO
        });
  }

  public final void updateCustomPeriodicEnableTimeView(@NonNull String managedKey,
      boolean newState) {
    managedSubscription = interactor.isManaged(managedKey)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(isManaged -> {
          if (newState && isManaged) {
            getView().enablePeriodicEnableTime();
          } else {
            getView().disablePeriodicEnableTime();
          }
        }, throwable -> {
          // TODO
        });
  }
}
