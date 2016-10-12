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

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.trigger.TriggerPresenter;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class TriggerPresenterImpl extends SchedulerPresenter<TriggerPresenter.TriggerView>
    implements TriggerPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final TriggerInteractor interactor;
  @NonNull private Subscription deleteSubscription = Subscriptions.empty();
  @NonNull private Subscription viewSubscription = Subscriptions.empty();
  @NonNull private Subscription createSubscription = Subscriptions.empty();

  @Inject TriggerPresenterImpl(@NonNull @Named("obs") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler, @NonNull TriggerInteractor interactor) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubViewSubscription();
    unsubDeleteSubscription();
    unsubCreateSubscription();
  }

  @SuppressWarnings("WeakerAccess") void unsubViewSubscription() {
    if (!viewSubscription.isUnsubscribed()) {
      viewSubscription.unsubscribe();
    }
  }

  @SuppressWarnings("WeakerAccess") void unsubDeleteSubscription() {
    if (!deleteSubscription.isUnsubscribed()) {
      deleteSubscription.unsubscribe();
    }
  }

  @SuppressWarnings("WeakerAccess") void unsubCreateSubscription() {
    if (!createSubscription.isUnsubscribed()) {
      createSubscription.unsubscribe();
    }
  }

  @Override public void loadTriggerView() {
    unsubViewSubscription();
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
          unsubViewSubscription();
        }));
  }

  @Override public void showNewTriggerDialog() {
    // TODO is there anything else we have to do?
    getView(TriggerView::onShowNewTriggerDialog);
  }

  @Override public void createPowerTrigger(@NonNull ContentValues values) {
    Timber.d("Create new power trigger");
    unsubCreateSubscription();
    createSubscription = interactor.put(values)
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
        });
  }

  @Override public void deleteTrigger(int percent) {
    unsubDeleteSubscription();
    deleteSubscription = interactor.delete(percent)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(position -> {
          getView(triggerView -> triggerView.onTriggerDeleted(position));
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }
}
