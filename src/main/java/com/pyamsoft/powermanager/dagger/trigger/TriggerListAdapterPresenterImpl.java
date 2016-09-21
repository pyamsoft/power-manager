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
import com.pyamsoft.powermanager.app.trigger.TriggerListAdapterPresenter;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class TriggerListAdapterPresenterImpl
    extends SchedulerPresenter<TriggerListAdapterPresenter.TriggerListAdapterView>
    implements TriggerListAdapterPresenter {

  @NonNull private final TriggerListAdapterInteractor interactor;
  @NonNull private Subscription updateSubscription = Subscriptions.empty();

  @Inject TriggerListAdapterPresenterImpl(@NonNull TriggerListAdapterInteractor adapterInteractor,
      @NonNull @Named("main") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = adapterInteractor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubUpdateSubscription();
  }

  @Override @CheckResult public int size() {
    return interactor.size().toBlocking().first();
  }

  @Override @CheckResult @NonNull public PowerTriggerEntry get(int position) {
    return interactor.get(position).toBlocking().first();
  }

  @Override @CheckResult public int getPositionForPercent(int percent) {
    return interactor.getPosition(percent).toBlocking().first();
  }

  @Override
  public void toggleEnabledState(int position, @NonNull PowerTriggerEntry entry, boolean enabled) {
    unsubUpdateSubscription();
    updateSubscription = interactor.update(entry, enabled)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(result -> {
          getView(triggerListAdapterView -> triggerListAdapterView.updateViewHolder(position));
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  @SuppressWarnings("WeakerAccess") void unsubUpdateSubscription() {
    if (!updateSubscription.isUnsubscribed()) {
      updateSubscription.unsubscribe();
    }
  }
}
