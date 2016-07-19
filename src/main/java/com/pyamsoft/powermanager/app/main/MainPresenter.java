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

package com.pyamsoft.powermanager.app.main;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.base.SchedulerPresenter;
import com.pyamsoft.powermanager.app.overview.OverviewSelectionBus;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class MainPresenter extends SchedulerPresenter<MainPresenter.MainView> {

  @NonNull private Subscription overviewBusSubscription = Subscriptions.empty();
  @NonNull private Subscription fabColorBusSubscription = Subscriptions.empty();

  @Inject public MainPresenter(@NonNull @Named("main") Scheduler mainScheduler,
      @NonNull @Named("io") Scheduler ioScheduler) {
    super(mainScheduler, ioScheduler);
  }

  @Override protected void onResume(@NonNull MainView view) {
    super.onResume(view);
    registerToOverviewBus();
  }

  @Override protected void onPause(@NonNull MainView view) {
    super.onPause(view);
    unregisterFromOverviewBus();
  }

  @Override protected void onStart(@NonNull MainView view) {
    super.onStart(view);
    registerToFabColorBus();
  }

  @Override protected void onStop(@NonNull MainView view) {
    super.onStop(view);
    unregisterFromFabColorBus();
  }

  void registerToOverviewBus() {
    unregisterFromOverviewBus();

    overviewBusSubscription = OverviewSelectionBus.get()
        .register()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(overviewSelectionEvent -> {
          Timber.d("Load fragment %s", overviewSelectionEvent.getType());
          getView().loadFragmentFromOverview(overviewSelectionEvent.getType());
        }, throwable -> {
          Timber.e(throwable, "onError");
          getView().overviewEventError();
        });
  }

  void registerToFabColorBus() {
    unregisterFromFabColorBus();
    fabColorBusSubscription = FabColorBus.get()
        .register()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(fabColorEvent -> {
          Timber.d("Set fab coloring");
          getView().loadFabColoring(fabColorEvent.icon(), fabColorEvent.onClick());
        }, throwable -> {
          // TODO different error
          Timber.e(throwable, "onError");
          getView().overviewEventError();
        });
  }

  void unregisterFromOverviewBus() {
    if (!overviewBusSubscription.isUnsubscribed()) {
      overviewBusSubscription.unsubscribe();
    }
  }

  void unregisterFromFabColorBus() {
    if (!fabColorBusSubscription.isUnsubscribed()) {
      fabColorBusSubscription.unsubscribe();
    }
  }

  public interface MainView {

    void loadFragmentFromOverview(@NonNull String type);

    void overviewEventError();

    void loadFabColoring(@DrawableRes int icon, @NonNull Runnable runnable);
  }
}
