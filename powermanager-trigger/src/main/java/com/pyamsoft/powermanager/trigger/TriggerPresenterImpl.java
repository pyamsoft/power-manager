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

package com.pyamsoft.powermanager.trigger;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.rx.SchedulerPresenter;
import com.pyamsoft.pydroid.rx.SubscriptionHelper;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class TriggerPresenterImpl extends SchedulerPresenter<Presenter.Empty> implements TriggerPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final TriggerInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription deleteSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription viewSubscription = Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription createSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription updateSubscription =
      Subscriptions.empty();

  @Inject TriggerPresenterImpl(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull TriggerInteractor interactor) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(deleteSubscription, createSubscription, updateSubscription,
        viewSubscription);
  }

  @Override public void loadTriggerView(@NonNull TriggerLoadCallback callback) {
    SubscriptionHelper.unsubscribe(viewSubscription);
    viewSubscription = interactor.queryAll()
        .sorted((entry, entry2) -> {
          if (entry.percent() == entry2.percent()) {
            return 0;
          } else if (entry.percent() < entry2.percent()) {
            return -1;
          } else {
            return 1;
          }
        })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onTriggerLoaded, throwable -> {
          // Todo
          Timber.e(throwable, "onError");
        }, () -> {
          callback.onTriggerLoadFinished();
          SubscriptionHelper.unsubscribe(viewSubscription);
        });
  }

  @Override public void showNewTriggerDialog(@NonNull ShowTriggerDialogCallback callback) {
    callback.onShowNewTriggerDialog();
  }

  @Override public void createPowerTrigger(@NonNull PowerTriggerEntry entry,
      @NonNull TriggerCreateCallback callback) {
    Timber.d("Create new power trigger");
    SubscriptionHelper.unsubscribe(createSubscription);
    createSubscription = interactor.put(entry)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onNewTriggerAdded, throwable -> {
          Timber.e(throwable, "onError");
          if (throwable instanceof SQLiteConstraintException) {
            Timber.e("Error inserting into DB");
            callback.onNewTriggerInsertError();
          } else {
            Timber.e("Issue creating trigger");
            callback.onNewTriggerCreateError();
          }
        }, () -> SubscriptionHelper.unsubscribe(createSubscription));
  }

  @Override public void deleteTrigger(int percent, @NonNull TriggerDeleteCallback callback) {
    SubscriptionHelper.unsubscribe(deleteSubscription);
    deleteSubscription = interactor.delete(percent)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onTriggerDeleted, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        }, () -> SubscriptionHelper.unsubscribe(deleteSubscription));
  }

  @Override
  public void toggleEnabledState(int position, @NonNull PowerTriggerEntry entry, boolean enabled,
      @NonNull TriggerToggleCallback callback) {
    SubscriptionHelper.unsubscribe(updateSubscription);
    updateSubscription = interactor.update(entry, enabled)
        .flatMap(updated -> interactor.get(entry.percent()))
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(result -> callback.updateViewHolder(position, result), throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        }, () -> SubscriptionHelper.unsubscribe(updateSubscription));
  }
}
