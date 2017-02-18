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
import com.pyamsoft.pydroid.helper.SubscriptionHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class TriggerPresenter extends SchedulerPresenter<Presenter.Empty> {

  @SuppressWarnings("WeakerAccess") @NonNull final TriggerInteractor interactor;
  @SuppressWarnings("WeakerAccess") boolean needsRefresh;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription deleteSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription viewSubscription = Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription createSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription updateSubscription =
      Subscriptions.empty();

  @Inject TriggerPresenter(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull TriggerInteractor interactor) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
    needsRefresh = false;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(deleteSubscription, createSubscription, updateSubscription,
        viewSubscription);
  }

  public void loadTriggerView(@NonNull TriggerLoadCallback callback) {
    SubscriptionHelper.unsubscribe(viewSubscription);
    viewSubscription = interactor.queryAll(needsRefresh)
        .doOnTerminate(callback::onTriggerLoadFinished)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onTriggerLoaded, throwable -> {
          Timber.e(throwable, "onError");
        }, () -> {
          needsRefresh = false;
          SubscriptionHelper.unsubscribe(viewSubscription);
        });
  }

  public void showNewTriggerDialog(@NonNull ShowTriggerDialogCallback callback) {
    callback.onShowNewTriggerDialog();
  }

  public void createPowerTrigger(@NonNull PowerTriggerEntry entry,
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
        }, () -> {
          needsRefresh = true;
          SubscriptionHelper.unsubscribe(createSubscription);
        });
  }

  public void deleteTrigger(int percent, @NonNull TriggerDeleteCallback callback) {
    SubscriptionHelper.unsubscribe(deleteSubscription);
    deleteSubscription = interactor.delete(percent)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onTriggerDeleted, throwable -> {
          Timber.e(throwable, "onError");
        }, () -> {
          needsRefresh = true;
          SubscriptionHelper.unsubscribe(deleteSubscription);
        });
  }

  public void toggleEnabledState(int position, @NonNull PowerTriggerEntry entry, boolean enabled,
      @NonNull TriggerToggleCallback callback) {
    SubscriptionHelper.unsubscribe(updateSubscription);
    updateSubscription = interactor.update(entry, enabled)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(result -> callback.updateViewHolder(position, result), throwable -> {
          Timber.e(throwable, "onError");
        }, () -> {
          needsRefresh = true;
          SubscriptionHelper.unsubscribe(updateSubscription);
        });
  }

  interface TriggerLoadCallback {

    void onTriggerLoaded(@NonNull PowerTriggerEntry entry);

    void onTriggerLoadFinished();
  }

  interface ShowTriggerDialogCallback {
    void onShowNewTriggerDialog();
  }

  interface TriggerDeleteCallback {

    void onTriggerDeleted(int position);
  }

  interface TriggerCreateCallback {

    void onNewTriggerAdded(@NonNull PowerTriggerEntry entry);

    void onNewTriggerCreateError();

    void onNewTriggerInsertError();
  }

  interface TriggerToggleCallback {

    void updateViewHolder(int position, @NonNull PowerTriggerEntry entry);
  }
}
