/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.manager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.ManagePreferences;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.job.JobQueuerEntry;
import io.reactivex.Single;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.pyamsoft.powermanager.job.JobQueuer.MANAGED_TAG;

@Singleton class ManagerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final JobQueuer jobQueuer;
  @NonNull private final ManagePreferences preferences;

  @Inject ManagerInteractor(@NonNull @Named("instant") JobQueuer jobQueuer,
      @NonNull ManagePreferences preferences) {
    this.jobQueuer = jobQueuer;
    this.preferences = preferences;
  }

  public void destroy() {
    jobQueuer.cancel(MANAGED_TAG);
  }

  /**
   * public
   */
  @NonNull @CheckResult Single<String> cancel() {
    return Single.fromCallable(() -> {
      destroy();
      return MANAGED_TAG;
    });
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<String> queueEnable() {
    return Single.fromCallable(() -> {
      // Queue up an enable job
      jobQueuer.cancel(MANAGED_TAG);
      jobQueuer.queue(JobQueuerEntry.builder(MANAGED_TAG)
          .screenOn(true)
          .delay(0)
          .repeatingOffWindow(0L)
          .repeatingOnWindow(0L)
          .build());
      return MANAGED_TAG;
    });
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<String> queueDisable() {
    return Single.fromCallable(() -> {
      // Queue up a disable job
      jobQueuer.cancel(MANAGED_TAG);
      jobQueuer.queue(JobQueuerEntry.builder(MANAGED_TAG)
          .screenOn(false)
          .delay(getDelayTime() * 1000L)
          .repeatingOffWindow(getPeriodicDisableTime())
          .repeatingOnWindow(getPeriodicEnableTime())
          .build());
      return MANAGED_TAG;
    });
  }

  @SuppressWarnings("WeakerAccess") @CheckResult long getDelayTime() {
    return preferences.getManageDelay();
  }

  @SuppressWarnings("WeakerAccess") @CheckResult long getPeriodicEnableTime() {
    return preferences.getPeriodicEnableTime();
  }

  @SuppressWarnings("WeakerAccess") @CheckResult long getPeriodicDisableTime() {
    return preferences.getPeriodicDisableTime();
  }
}
