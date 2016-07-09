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
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.powermanager.model.sql.PowerTriggerModel;
import java.util.Random;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class TriggerPresenter extends SchedulerPresenter<TriggerPresenter.TriggerView> {

  @NonNull private static final Random RANDOM_PERCENT = new Random();
  @NonNull private final TriggerInteractor interactor;
  @NonNull private Subscription viewSubscription = Subscriptions.empty();
  @NonNull private Subscription deleteTriggerBusSubscription = Subscriptions.empty();
  @NonNull private Subscription deleteSubscription = Subscriptions.empty();

  @Inject public TriggerPresenter(@NonNull @Named("main") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler, @NonNull TriggerInteractor interactor) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubViewSubscription();
    unsubDeleteSubscription();
  }

  @Override public void onResume() {
    super.onResume();
    registerOnDeleteTriggerBus();
  }

  @Override public void onPause() {
    super.onPause();
    unregisterFromDeleteTriggerBus();
  }

  private void unsubViewSubscription() {
    if (!viewSubscription.isUnsubscribed()) {
      viewSubscription.unsubscribe();
    }
  }

  private void unsubDeleteSubscription() {
    if (!deleteSubscription.isUnsubscribed()) {
      deleteSubscription.unsubscribe();
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

    // TODO move this into a dialog which creates the trigger on completion
    final PowerTriggerModel.Marshal marshal = PowerTriggerEntry.FACTORY.marshal();
    marshal.name("TESTING 1")
        .percent(RANDOM_PERCENT.nextInt(101))
        .enabled(false)
        .toggleWifi(false)
        .toggleData(false)
        .toggleBluetooth(false)
        .toggleSync(false)
        .enableWifi(false)
        .enableData(false)
        .enableBluetooth(false)
        .enableSync(false);
    interactor.put(marshal.asContentValues())
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(entry1 -> {
          getView().onNewTriggerAdded(entry1.percent());
        }, throwable -> {
          Timber.e(throwable, "onError");
        });
  }

  void registerOnDeleteTriggerBus() {
    unregisterFromDeleteTriggerBus();
    deleteTriggerBusSubscription = DeleteTriggerDialog.Bus.get()
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

  private void unregisterFromDeleteTriggerBus() {
    if (!deleteTriggerBusSubscription.isUnsubscribed()) {
      deleteTriggerBusSubscription.unsubscribe();
    }
  }

  public interface TriggerView {

    void onTriggerDeleted(int position);

    void onNewTriggerAdded(int percent);

    void loadEmptyView();

    void loadListView();
  }
}
