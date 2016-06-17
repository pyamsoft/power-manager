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

package com.pyamsoft.powermanager.dagger.main;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.main.MainPresenter;
import com.pyamsoft.powermanager.app.overview.OverviewSelectionBus;
import com.pyamsoft.pydroid.base.PresenterImpl;
import javax.inject.Inject;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

final class MainPresenterImpl extends PresenterImpl<MainPresenter.MainView>
    implements MainPresenter {

  @NonNull private Subscription overviewBusSubscription = Subscriptions.empty();

  @Inject protected MainPresenterImpl() {
  }

  @Override public void onResume() {
    super.onResume();
    registerToOverviewBus();
  }

  @Override public void onPause() {
    super.onPause();
    unregisterFromOverviewBus();
  }

  private void registerToOverviewBus() {
    unregisterFromOverviewBus();

    overviewBusSubscription =
        OverviewSelectionBus.get().register().subscribe(overviewSelectionEvent -> {
          getView().loadFragmentFromOverview(overviewSelectionEvent.getType());
        }, throwable -> {
          Timber.e("onError");
          getView().overviewEventError();
        });
  }

  private void unregisterFromOverviewBus() {
    if (!overviewBusSubscription.isUnsubscribed()) {
      overviewBusSubscription.unsubscribe();
    }
  }
}