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

package com.pyamsoft.powermanager.overview;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton class OverviewInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;

  @Inject OverviewInteractor(@NonNull PowerManagerPreferences preferences) {
    this.preferences = preferences;
  }

  @NonNull @CheckResult public Observable<Boolean> hasShownOnboarding() {
    return Observable.fromCallable(preferences::isOverviewOnboardingShown)
        .delay(1, TimeUnit.SECONDS);
  }

  public void setShownOnboarding() {
    preferences.setOverviewOnboardingShown();
  }
}
