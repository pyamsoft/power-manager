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

package com.pyamsoft.powermanagerpresenter.doze;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanagermodel.InterestObserver;
import com.pyamsoft.powermanagermodel.PermissionObserver;
import com.pyamsoft.powermanagerpresenter.base.ManagePreferenceInteractor;
import com.pyamsoft.powermanagerpresenter.base.PermissionManagePreferencePresenterImpl;
import javax.inject.Inject;
import rx.Scheduler;

class DozeManagePreferencePresenterImpl extends PermissionManagePreferencePresenterImpl {

  @Inject DozeManagePreferencePresenterImpl(@NonNull ManagePreferenceInteractor manageInteractor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler,
      @NonNull InterestObserver manageObserver,
      @NonNull PermissionObserver dozePermissionObserver) {
    super(manageInteractor, observeScheduler, subscribeScheduler, manageObserver,
        dozePermissionObserver);
  }
}
