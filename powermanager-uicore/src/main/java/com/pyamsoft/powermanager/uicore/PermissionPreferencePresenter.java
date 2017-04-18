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
import com.pyamsoft.pydroid.helper.Checker;
import io.reactivex.Scheduler;
import javax.inject.Inject;
import timber.log.Timber;

public class PermissionPreferencePresenter extends ManagePreferencePresenter {

  @NonNull private final PermissionPreferenceInteractor interactor;

  @Inject public PermissionPreferencePresenter(@NonNull PermissionPreferenceInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(interactor, observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @CallSuper @Override
  public void checkManagePermission(@NonNull ManagePermissionCallback callback) {
    ManagePermissionCallback permissionCallback = Checker.checkNonNull(callback);

    disposeOnStop(interactor.hasPermission()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onComplete)
        .doOnSubscribe(disposable -> permissionCallback.onBegin())
        .subscribe(hasPermission -> {
          Timber.d("Permission granted? %s", hasPermission);
          permissionCallback.onManagePermissionCallback(hasPermission);
        }, throwable -> {
          Timber.e(throwable, "onError checkManagePermission");
          permissionCallback.onManagePermissionCallback(false);
        }));
  }
}
