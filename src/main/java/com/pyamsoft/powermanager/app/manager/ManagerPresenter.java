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

package com.pyamsoft.powermanager.app.manager;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.pyamsoft.powermanager.app.base.SchedulerPresenter;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerInteractor;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public abstract class ManagerPresenter<I extends ManagerPresenter.ManagerView>
    extends SchedulerPresenter<I> {

  @NonNull private final ManagerInteractor interactor;
  @NonNull private Subscription initialSubscription = Subscriptions.empty();
  @NonNull private Subscription toggleSubscription = Subscriptions.empty();
  @NonNull private Subscription managedSubscription = Subscriptions.empty();

  protected ManagerPresenter(@NonNull ManagerInteractor interactor,
      @NonNull @Named("main") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubInitial();
    unsubToggle();
  }

  private void unsubInitial() {
    if (!initialSubscription.isUnsubscribed()) {
      initialSubscription.unsubscribe();
    }
  }

  public void getCurrentState() {
    unsubInitial();
    initialSubscription = Observable.zip(interactor.isEnabled(), interactor.isManaged(), Pair::new)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(pair -> {
          onCurrentStateReceived(pair.first, pair.second);
        }, throwable -> {
          Timber.e(throwable, "onError");
          // TODO error
        });
  }

  public void toggleState() {
    unsubToggle();
    toggleSubscription = interactor.isEnabled()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(this::onToggle, throwable -> {
          Timber.e(throwable, "onError");
          // TODO error
        });
  }

  public void toggleManaged() {
    unsubManaged();
    managedSubscription = interactor.isManaged()
        .map(managed -> {
          interactor.setManaged(!managed);
          return !managed;
        })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(this::onManaged, throwable -> {
          Timber.e(throwable, "onError");
          // TODO error
        });
  }

  void unsubToggle() {
    if (!toggleSubscription.isUnsubscribed()) {
      toggleSubscription.unsubscribe();
    }
  }

  void unsubManaged() {
    if (!managedSubscription.isUnsubscribed()) {
      managedSubscription.unsubscribe();
    }
  }

  abstract void onCurrentStateReceived(boolean enabled, boolean managed);

  abstract void onToggle(boolean enabled);

  abstract void onManaged(boolean managed);

  public interface ManagerView {

  }
}
