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

package com.pyamsoft.powermanager.app.manager.backend;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerInteractor;
import com.pyamsoft.powermanager.dagger.manager.backend.WearableManagerInteractor;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

abstract class WearableManager<I extends WearableManager.WearableView> extends Manager<I> {

  @NonNull private final WearableManagerInteractor interactor;
  @NonNull private Subscription managedSubscription = Subscriptions.empty();

  WearableManager(@NonNull WearableManagerInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(interactor, ioScheduler, mainScheduler);
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

  @CheckResult @NonNull final Observable<ManagerInteractor> zipWithWearableManagedState(
      @NonNull Observable<ManagerInteractor> observable) {
    final Observable<Boolean> connectedObservable =
        interactor.isWearableManaged().flatMap(managed -> {
          if (managed) {
            return interactor.isWearableConnected();
          } else {
            return Observable.just(managed);
          }
        });

    return Observable.zip(observable, connectedObservable, (managerInteractor, shouldPass) -> {
      if (shouldPass) {
        Timber.d("Wearable is managed and connected, return NULL");
        return null;
      } else {
        Timber.d("Wearable is not managed or not connected, return NORMAL");
        return managerInteractor;
      }
    });
  }

  public final void onWearableManageChanged() {
    unsubManaged();
    managedSubscription =  interactor.isWearableManaged().subscribeOn(getSubscribeScheduler())
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

  @Override public void disable() {
    disable(zipWithWearableManagedState(baseDisableObservable()));
  }

  public interface WearableView extends ManagerView {

    void startManagingWearable();

    void stopManagingWearable();
  }
}
