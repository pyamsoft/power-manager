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

package com.pyamsoft.powermanager.dagger.data;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.powermanager.dagger.base.BaseManagePreferenceInteractor;
import com.pyamsoft.powermanager.dagger.base.BaseManagePreferencePresenterImpl;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class DataManagePreferencePresenter extends BaseManagePreferencePresenterImpl {

  @NonNull private final PermissionObserver rootPermissionObserver;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription rootPermissionSubscription =
      Subscriptions.empty();

  @Inject DataManagePreferencePresenter(@NonNull BaseManagePreferenceInteractor manageInteractor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler,
      @NonNull InterestObserver manageObserver,
      @NonNull PermissionObserver rootPermissionObserver) {
    super(manageInteractor, observeScheduler, subscribeScheduler, manageObserver);
    this.rootPermissionObserver = rootPermissionObserver;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(rootPermissionSubscription);
  }

  @Override public void checkManagePermission() {
    SubscriptionHelper.unsubscribe(rootPermissionSubscription);
    rootPermissionSubscription = rootPermissionObserver.hasPermission()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(hasPermission -> {
          Timber.d("Root permission granted? %s", hasPermission);
          getView(view -> view.onManagePermissionCallback(hasPermission));
        }, throwable -> {
          Timber.e(throwable, "onError checkRootPermission");
          getView(view -> view.onManagePermissionCallback(false));
        }, () -> SubscriptionHelper.unsubscribe(rootPermissionSubscription));
  }
}
