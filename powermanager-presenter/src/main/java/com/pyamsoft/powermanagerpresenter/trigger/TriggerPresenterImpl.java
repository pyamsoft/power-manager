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

package com.pyamsoft.powermanagerpresenter.trigger;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanagermodel.sql.PowerTriggerEntry;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class TriggerPresenterImpl extends SchedulerPresenter<TriggerPresenter.TriggerView>
    implements TriggerPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final TriggerInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull final List<PowerTriggerEntry> powerTriggerEntryCached;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription deleteSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription viewSubscription = Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription createSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") @NonNull Subscription updateSubscription =
      Subscriptions.empty();
  @SuppressWarnings("WeakerAccess") boolean refreshing;

  @Inject TriggerPresenterImpl(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull TriggerInteractor interactor) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
    powerTriggerEntryCached = new ArrayList<>();
    refreshing = false;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(deleteSubscription, createSubscription, updateSubscription);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    refreshing = false;
    SubscriptionHelper.unsubscribe(viewSubscription);
    powerTriggerEntryCached.clear();
  }

  @Override public void loadTriggerView() {
    if (refreshing) {
      Timber.d("List is currently refreshing, do nothing");
      return;
    }

    refreshing = true;
    final Observable<PowerTriggerEntry> freshData = interactor.queryAll().map(entry -> {
      powerTriggerEntryCached.add(entry);
      return entry;
    });

    final Observable<PowerTriggerEntry> dataSource;
    if (powerTriggerEntryCached.isEmpty()) {
      dataSource = freshData;
    } else {
      dataSource = Observable.defer(() -> Observable.from(powerTriggerEntryCached));
    }

    SubscriptionHelper.unsubscribe(viewSubscription);
    viewSubscription = dataSource.subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(entry -> getView(triggerView -> triggerView.onTriggerLoaded(entry)),
            throwable -> {
              // Todo
              Timber.e(throwable, "onError");
            }, () -> getView(view -> {
              refreshing = false;
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
        .map(entry12 -> {
          powerTriggerEntryCached.add(entry12);
          return entry12;
        })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(entry1 -> getView(triggerView -> triggerView.onNewTriggerAdded(entry1)),
            throwable -> {
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
        .map(result -> {
          PowerTriggerEntry removeMe = null;
          for (final PowerTriggerEntry entry : powerTriggerEntryCached) {
            if (entry.percent() == percent) {
              removeMe = entry;
              break;
            }
          }
          if (removeMe != null) {
            powerTriggerEntryCached.remove(removeMe);
          }
          return result;
        })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(position -> getView(triggerView -> triggerView.onTriggerDeleted(position)),
            throwable -> {
              // TODO
              Timber.e(throwable, "onError");
            }, () -> SubscriptionHelper.unsubscribe(deleteSubscription));
  }

  @Override
  public void toggleEnabledState(int position, @NonNull PowerTriggerEntry entry, boolean enabled) {
    SubscriptionHelper.unsubscribe(updateSubscription);
    updateSubscription = interactor.update(entry, enabled)
        .flatMap(updated -> interactor.get(entry.percent()))
        .map(newEntry -> {
          int updateIndex = -1;
          final int size = powerTriggerEntryCached.size();
          for (int i = 0; i < size; ++i) {
            final PowerTriggerEntry checkEntry = powerTriggerEntryCached.get(i);
            if (checkEntry.percent() == newEntry.percent()) {
              updateIndex = i;
              break;
            }
          }
          if (updateIndex != -1) {
            powerTriggerEntryCached.set(updateIndex, newEntry);
          }

          return newEntry;
        })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(result -> getView(
            triggerListAdapterView -> triggerListAdapterView.updateViewHolder(position, result)),
            throwable -> {
              // TODO
              Timber.e(throwable, "onError");
            }, () -> SubscriptionHelper.unsubscribe(updateSubscription));
  }
}
