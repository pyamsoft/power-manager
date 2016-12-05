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

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.trigger.TriggerPresenter;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class TriggerPresenterImpl extends SchedulerPresenter<TriggerPresenter.TriggerView>
    implements TriggerPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final TriggerInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription deleteSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription viewSubscription = Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription createSubscription =
      Subscriptions.empty();

  @Inject TriggerPresenterImpl(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull TriggerInteractor interactor) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(viewSubscription, deleteSubscription, createSubscription);
  }

  @Override public void loadTriggerView() {
    SubscriptionHelper.unsubscribe(viewSubscription);
    viewSubscription = interactor.queryAll()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(entry -> getView(triggerView -> {
          Timber.d("Trigger loaded = %s", entry);
          triggerView.onTriggerLoaded(entry);
        }), throwable -> {
          // Todo
          Timber.e(throwable, "onError");
        }, () -> getView(view -> {
          view.onTriggerLoadFinished();
          SubscriptionHelper.unsubscribe(viewSubscription);
        }));
  }

  @Override public void showNewTriggerDialog() {
    // TODO is there anything else we have to do?
    getView(TriggerView::onShowNewTriggerDialog);
  }

  @Override public void createPowerTrigger(@NonNull PowerTriggerEntry entry) {
    Timber.d("Create new power trigger");
    SubscriptionHelper.unsubscribe(createSubscription);
    createSubscription = interactor.put(entry)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(entry1 -> {
          getView(triggerView -> triggerView.onNewTriggerAdded(entry1.percent()));
        }, throwable -> {
          Timber.e(throwable, "onError");
          if (throwable instanceof SQLiteConstraintException) {
            Timber.e("Error inserting into DB");
            getView(TriggerView::onNewTriggerInsertError);
          } else {
            Timber.e("Issue creating trigger");
            getView(TriggerView::onNewTriggerCreateError);
          }
        }, () -> SubscriptionHelper.unsubscribe(createSubscription));
  }

  @Override public void deleteTrigger(int percent) {
    SubscriptionHelper.unsubscribe(deleteSubscription);
    deleteSubscription = interactor.delete(percent)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(position -> {
          getView(triggerView -> triggerView.onTriggerDeleted(position));
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        }, () -> SubscriptionHelper.unsubscribe(deleteSubscription));
  }
}
