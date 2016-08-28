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
import com.pyamsoft.powermanager.app.bus.DeleteTriggerBus;
import com.pyamsoft.powermanager.app.bus.TriggerBus;
import com.pyamsoft.powermanager.app.trigger.TriggerPresenter;
import com.pyamsoft.pydroid.base.presenter.SchedulerPresenter;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class TriggerPresenterImpl extends SchedulerPresenter<TriggerPresenter.TriggerView>
    implements TriggerPresenter {

  @NonNull final TriggerInteractor interactor;
  @NonNull Subscription viewSubscription = Subscriptions.empty();
  @NonNull Subscription deleteTriggerBusSubscription = Subscriptions.empty();
  @NonNull Subscription createTriggerBusSubscription = Subscriptions.empty();
  @NonNull Subscription deleteSubscription = Subscriptions.empty();
  @NonNull Subscription createSubscription = Subscriptions.empty();

  @Inject public TriggerPresenterImpl(@NonNull @Named("main") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler, @NonNull TriggerInteractor interactor) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onBind(@NonNull TriggerView view) {
    super.onBind(view);
    registerOnDeleteTriggerBus();
    registerOnCreateTriggerBus();
  }

  @Override protected void onUnbind(@NonNull TriggerView view) {
    super.onUnbind(view);
    unregisterFromDeleteTriggerBus();
    unregisterFromCreateTriggerBus();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unsubViewSubscription();
    unsubDeleteSubscription();
    unsubCreateSubscription();
  }

  void unsubViewSubscription() {
    if (!viewSubscription.isUnsubscribed()) {
      viewSubscription.unsubscribe();
    }
  }

  void unsubDeleteSubscription() {
    if (!deleteSubscription.isUnsubscribed()) {
      deleteSubscription.unsubscribe();
    }
  }

  void unsubCreateSubscription() {
    if (!createSubscription.isUnsubscribed()) {
      createSubscription.unsubscribe();
    }
  }

  @Override public void loadTriggerView() {
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

  @Override public void showNewTriggerDialog() {
    // TODO is there anything else we have to do?
    getView().onShowNewTriggerDialog();
  }

  void createPowerTrigger(@NonNull ContentValues values) {
    Timber.d("Create new power trigger");
    unsubCreateSubscription();
    createSubscription = interactor.put(values)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(entry1 -> {
          getView().onNewTriggerAdded(entry1.percent());
        }, throwable -> {
          Timber.e(throwable, "onError");
          if (throwable instanceof SQLiteConstraintException) {
            Timber.e("Error inserting into DB");
            getView().onNewTriggerInsertError();
          } else {
            Timber.e("Issue creating trigger");
            getView().onNewTriggerCreateError();
          }
        });
  }

  void registerOnDeleteTriggerBus() {
    unregisterFromDeleteTriggerBus();
    deleteTriggerBusSubscription = DeleteTriggerBus.get()
        .register()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(deleteTriggerEvent -> {
          // KLUDGE nested subs are ugly
          unsubDeleteSubscription();
          deleteSubscription = interactor.delete(deleteTriggerEvent.percent())
              .subscribeOn(getSubscribeScheduler())
              .observeOn(getObserveScheduler())
              .subscribe(position -> {
                getView().onTriggerDeleted(position);
              }, throwable -> {
                // TODO
                Timber.e(throwable, "onError");
              });
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  void registerOnCreateTriggerBus() {
    unregisterFromCreateTriggerBus();
    createTriggerBusSubscription = TriggerBus.get()
        .register()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(this::createPowerTrigger, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  void unregisterFromDeleteTriggerBus() {
    if (!deleteTriggerBusSubscription.isUnsubscribed()) {
      deleteTriggerBusSubscription.unsubscribe();
    }
  }

  void unregisterFromCreateTriggerBus() {
    if (!createTriggerBusSubscription.isUnsubscribed()) {
      createTriggerBusSubscription.unsubscribe();
    }
  }
}