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

package com.pyamsoft.powermanager.dagger.base;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import rx.Observable;

public abstract class BasePeriodPreferenceInteractorImpl implements BasePeriodPreferenceInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;
  @NonNull private final BooleanInterestModifier periodModifier;

  protected BasePeriodPreferenceInteractorImpl(@NonNull BooleanInterestModifier periodModifier,
      @NonNull PowerManagerPreferences preferences) {
    this.periodModifier = periodModifier;
    this.preferences = preferences;
  }

  @Override public void updatePeriodic(boolean state) {
    if (state) {
      periodModifier.set();
    } else {
      periodModifier.unset();
    }
  }

  @NonNull @Override public Observable<Boolean> hasShownOnboarding() {
    return Observable.defer(() -> Observable.just(preferences.isPeriodicOnboardingShown()));
  }

  @Override public void setOnboarding() {
    preferences.setPeriodicOnboardingShown();
  }
}
