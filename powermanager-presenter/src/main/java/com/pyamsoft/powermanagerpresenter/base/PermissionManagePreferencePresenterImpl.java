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

package com.pyamsoft.powermanagerpresenter.base;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanagermodel.InterestObserver;
import com.pyamsoft.powermanagermodel.PermissionObserver;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public abstract class PermissionManagePreferencePresenterImpl
    extends ManagePreferencePresenterImpl {

  @SuppressWarnings("WeakerAccess") @NonNull final PermissionObserver permissionObserver;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription permissionSubscription =
      Subscriptions.empty();

  protected PermissionManagePreferencePresenterImpl(
      @NonNull ManagePreferenceInteractor manageInteractor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull InterestObserver manageObserver,
      @NonNull PermissionObserver permissionObserver) {
    super(manageInteractor, observeScheduler, subscribeScheduler, manageObserver);
    this.permissionObserver = permissionObserver;
  }

  @CallSuper @Override protected void onUnbind() {
    super.onUnbind();
    SubscriptionHelper.unsubscribe(permissionSubscription);
  }

  @CallSuper @Override public void checkManagePermission() {
    SubscriptionHelper.unsubscribe(permissionSubscription);
    permissionSubscription =
        Observable.defer(() -> Observable.just(permissionObserver.hasPermission()))
            .subscribeOn(getSubscribeScheduler())
            .observeOn(getObserveScheduler())
            .subscribe(hasPermission -> {
              Timber.d("Permission granted? %s", hasPermission);
              getView(view -> view.onManagePermissionCallback(hasPermission));
            }, throwable -> {
              Timber.e(throwable, "onError checkManagePermission");
              getView(view -> view.onManagePermissionCallback(false));
            }, () -> SubscriptionHelper.unsubscribe(permissionSubscription));
  }
}
