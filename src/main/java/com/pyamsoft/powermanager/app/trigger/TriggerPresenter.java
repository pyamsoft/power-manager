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

package com.pyamsoft.powermanager.app.trigger;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.base.SchedulerPresenter;
import com.pyamsoft.powermanager.dagger.trigger.TriggerInteractor;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class TriggerPresenter extends SchedulerPresenter<TriggerPresenter.TriggerView> {

  @NonNull private final TriggerInteractor interactor;
  @NonNull private Subscription viewSubscription = Subscriptions.empty();

  @Inject public TriggerPresenter(@NonNull @Named("main") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler, @NonNull TriggerInteractor interactor) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubViewSubscription();
  }

  private void unsubViewSubscription() {
    if (!viewSubscription.isUnsubscribed()) {
      viewSubscription.unsubscribe();
    }
  }

  public void loadTriggerView() {
    unsubViewSubscription();
    viewSubscription = interactor.size()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(size -> {
          Timber.d("Trigger size = %d", size);
          if (size == 0) {
            getView().loadEmptyView();
          } else {
            getView().loadListView();
          }
        }, throwable -> {
          // Todo
          Timber.e(throwable, "onError");
        });
  }

  public void createPowerTrigger() {
    Timber.d("Create new power trigger");
  }

  public interface TriggerView {

    void loadEmptyView();

    void loadListView();
  }
}
