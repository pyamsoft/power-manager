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

package com.pyamsoft.powermanager.app.manager.manage;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.manager.ManagerSettingsPresenter;
import com.pyamsoft.powermanager.dagger.manager.manage.ManagerManageInteractor;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class ManagerManagePresenter extends ManagerSettingsPresenter<ManagerManageView> {

  @NonNull private final ManagerManageInteractor interactor;
  @NonNull private Subscription managedSubscription = Subscriptions.empty();
  @NonNull private Subscription customDelaySubscription = Subscriptions.empty();

  @Inject public ManagerManagePresenter(@NonNull ManagerManageInteractor interactor,
      @Named("main") Scheduler mainScheduler, @Named("io") Scheduler ioScheduler) {
    super(mainScheduler, ioScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind(@NonNull ManagerManageView view) {
    super.onUnbind(view);
    unsubManaged();
    unsubCustomDelay();
  }

  private void unsubManaged() {
    if (!managedSubscription.isUnsubscribed()) {
      managedSubscription.unsubscribe();
    }
  }

  private void unsubCustomDelay() {
    if (!customDelaySubscription.isUnsubscribed()) {
      customDelaySubscription.unsubscribe();
    }
  }

  public final void setManagedFromPreference(@NonNull String key) {
    unsubManaged();
    managedSubscription = interactor.isManaged(key)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(enabled -> {
          if (enabled) {
            getView().enableManaged();
          } else {
            getView().disableManaged();
          }
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  public final void setCustomDelayTimeStateFromPreference(@NonNull String key, boolean isManaged) {
    unsubCustomDelay();
    customDelaySubscription = interactor.isCustomDelayTime(key)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(customTime -> {
          updateCustomDelayTimeView(customTime && isManaged);
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  public final void updateCustomDelayTimeView(boolean newState) {
    if (newState) {
      getView().enableCustomDelayTime();
    } else {
      getView().disableCustomDelayTime();
    }
  }

  public final void updateNotificationOnManageStateChange() {
    interactor.updateNotificationOnManageStateChange();
  }
}
