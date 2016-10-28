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

package com.pyamsoft.powermanager.dagger.doze;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.doze.DozeOnlyPresenter;
import com.pyamsoft.powermanager.app.observer.PermissionObserver;
import com.pyamsoft.pydroidrx.SchedulerPresenter;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class DozeOnlyPresenterImpl extends SchedulerPresenter<DozeOnlyPresenter.View>
    implements DozeOnlyPresenter {

  @NonNull private final PermissionObserver dozePermissionObserver;
  @NonNull private Subscription dozePermissionSubscription = Subscriptions.empty();

  @Inject DozeOnlyPresenterImpl(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler, @NonNull PermissionObserver dozePermissionObserver) {
    super(observeScheduler, subscribeScheduler);
    this.dozePermissionObserver = dozePermissionObserver;
  }

  @SuppressWarnings("WeakerAccess") void unsubDoze() {
    if (!dozePermissionSubscription.isUnsubscribed()) {
      dozePermissionSubscription.unsubscribe();
    }
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubDoze();
  }

  @Override public void checkDozePermission() {
    unsubDoze();
    dozePermissionSubscription = dozePermissionObserver.hasPermission()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(hasPermission -> {
          Timber.d("Doze permission granted? %s", hasPermission);
          getView(view -> view.onDozePermissionCallback(hasPermission));
        }, throwable -> {
          Timber.e(throwable, "onError checkDozePermission");
          getView(view -> view.onDozePermissionCallback(false));
        }, this::unsubDoze);
  }
}
