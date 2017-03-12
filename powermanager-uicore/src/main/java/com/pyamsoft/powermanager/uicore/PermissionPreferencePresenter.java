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

package com.pyamsoft.powermanager.uicore;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.InterestObserver;
import com.pyamsoft.powermanager.model.PermissionObserver;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import timber.log.Timber;

public class PermissionPreferencePresenter extends ManagePreferencePresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final PermissionObserver permissionObserver;
  @NonNull private Disposable permissionDisposable = Disposables.empty();

  @Inject public PermissionPreferencePresenter(@NonNull ManagePreferenceInteractor manageInteractor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler,
      @NonNull InterestObserver manageObserver, @NonNull PermissionObserver permissionObserver) {
    super(manageInteractor, observeScheduler, subscribeScheduler, manageObserver);
    this.permissionObserver = permissionObserver;
  }

  @CallSuper @Override protected void onUnbind() {
    super.onUnbind();
    permissionDisposable = DisposableHelper.unsubscribe(permissionDisposable);
  }

  @CallSuper @Override
  public void checkManagePermission(@NonNull ManagePermissionCallback callback) {
    permissionDisposable = DisposableHelper.unsubscribe(permissionDisposable);
    permissionDisposable = Observable.fromCallable(permissionObserver::hasPermission)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(hasPermission -> {
          Timber.d("Permission granted? %s", hasPermission);
          callback.onManagePermissionCallback(hasPermission);
        }, throwable -> {
          Timber.e(throwable, "onError checkManagePermission");
          callback.onManagePermissionCallback(false);
        });
  }
}
