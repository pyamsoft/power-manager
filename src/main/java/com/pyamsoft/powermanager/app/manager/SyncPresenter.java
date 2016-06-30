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
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerInteractor;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;
import timber.log.Timber;

public final class SyncPresenter extends ManagerPresenter<SyncView> {

  @NonNull private final ManagerInteractor interactor;

  @Inject public SyncPresenter(@NonNull @Named("sync") ManagerInteractor interactor,
      @NonNull @Named("main") Scheduler mainScheduler,
      @NonNull @Named("io") Scheduler ioScheduler) {
    super(interactor, mainScheduler, ioScheduler);
    Timber.d("new ManagerSync");
    this.interactor = interactor;
  }

  @Override public void onCurrentStateReceived(boolean enabled, boolean managed) {
    getView().syncInitialState(enabled, managed);
  }

  @Override public void onToggle(boolean currentState) {
    if (currentState) {
      getView().toggleSyncDisabled();
    } else {
      getView().toggleSyncEnabled();
    }
  }
  //public final void isManaged() {
  //  unsubIsManaged();
  //  isManagedSubscription = interactor.isManaged()
  //      .subscribeOn(getSubscribeScheduler())
  //      .observeOn(getObserveScheduler())
  //      .subscribe(managed -> {
  //        if (managed) {
  //          getView().syncStartManaging();
  //        } else {
  //          getView().syncStopManaging();
  //        }
  //      }, throwable -> {
  //        Timber.e(throwable, "onError");
  //         TODO error
  //});
  //}
  //
  //void unsubIsManaged() {
  //  if (!isManagedSubscription.isUnsubscribed()) {
  //    isManagedSubscription.unsubscribe();
  //  }
  //}
}
