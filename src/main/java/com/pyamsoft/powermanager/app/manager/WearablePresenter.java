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

package com.pyamsoft.powermanager.app.manager;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.manager.backend.WearableManagerInteractor;
import javax.inject.Named;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public abstract class WearablePresenter<I extends WearablePresenter.WearableView>
    extends ManagerPresenter<I> {

  @NonNull private final WearableManagerInteractor interactor;
  @NonNull private Subscription managedSubscription = Subscriptions.empty();

  protected WearablePresenter(@NonNull WearableManagerInteractor interactor,
      @NonNull @Named("main") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubManaged();
  }

  private void unsubManaged() {
    if (!managedSubscription.isUnsubscribed()) {
      managedSubscription.unsubscribe();
    }
  }

  public final void onWearableManageChanged() {
    unsubManaged();
    managedSubscription = interactor.isWearableManaged()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(managed -> {
          if (managed) {
            getView().startManagingWearable();
          } else {
            getView().stopManagingWearable();
          }
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  public interface WearableView extends ManagerPresenter.ManagerView {

    void startManagingWearable();

    void stopManagingWearable();
  }
}
