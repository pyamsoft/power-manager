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

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.overview.OverviewSelectionBus;
import com.pyamsoft.pydroid.base.Presenter;
import javax.inject.Inject;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class MainPresenter extends Presenter<MainPresenter.MainView> {

  @NonNull private Subscription overviewBusSubscription = Subscriptions.empty();
  @NonNull private Subscription fabColorBusSubscription = Subscriptions.empty();

  @Inject public MainPresenter() {
  }

  @Override public void onResume() {
    super.onResume();
    registerToOverviewBus();
    registerToFabColorBus();
  }

  @Override public void onPause() {
    super.onPause();
    unregisterFromOverviewBus();
    unregisterFromFabColorBus();
  }

  void registerToOverviewBus() {
    unregisterFromOverviewBus();

    overviewBusSubscription =
        OverviewSelectionBus.get().register().subscribe(overviewSelectionEvent -> {
          Timber.d("Load fragment %s", overviewSelectionEvent.getType());
          getView().loadFragmentFromOverview(overviewSelectionEvent.getType());
        }, throwable -> {
          Timber.e(throwable, "onError");
          getView().overviewEventError();
        });
  }

  void registerToFabColorBus() {
    unregisterFromFabColorBus();
    fabColorBusSubscription = FabColorBus.get().register().subscribe(fabColorEvent -> {
      Timber.d("Set fab coloring");
      getView().loadFabColoring(fabColorEvent.icon(), fabColorEvent.backgroundColor());
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

    void loadFabColoring(@DrawableRes int icon, @ColorRes int backgroundColor);
  }
}
