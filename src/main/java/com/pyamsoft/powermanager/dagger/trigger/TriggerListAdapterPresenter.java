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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.base.SchedulerPresenter;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class TriggerListAdapterPresenter
    extends SchedulerPresenter<TriggerListAdapterPresenter.TriggerListAdapterView> {

  @NonNull private final TriggerListAdapterInteractor interactor;
  @NonNull private Subscription updateSubscription = Subscriptions.empty();

  @Inject TriggerListAdapterPresenter(@NonNull @Named("main") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler,
      @NonNull TriggerListAdapterInteractor adapterInteractor) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = adapterInteractor;
  }

  @Override protected void onUnbind(@NonNull TriggerListAdapterView view) {
    super.onUnbind(view);
    unsubUpdateSubscription();
  }

  @CheckResult public int size() {
    return interactor.size().toBlocking().first();
  }

  @CheckResult @NonNull public PowerTriggerEntry get(int position) {
    return interactor.get(position).toBlocking().first();
  }

  @CheckResult public int getPositionForPercent(int percent) {
    return interactor.getPosition(percent).toBlocking().first();
  }

  public void toggleEnabledState(int position, @NonNull PowerTriggerEntry entry, boolean enabled) {
    unsubUpdateSubscription();
    updateSubscription = interactor.update(entry, enabled)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(result -> {
          getView().updateViewHolder(position);
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  void unsubUpdateSubscription() {
    if (!updateSubscription.isUnsubscribed()) {
      updateSubscription.unsubscribe();
    }
  }

  public interface TriggerListAdapterView {

    void updateViewHolder(int position);
  }
}