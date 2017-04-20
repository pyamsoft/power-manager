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

package com.pyamsoft.powermanager.uicore.preference;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.concurrent.TimeUnit;

public abstract class CustomTimePreferenceInteractor<T> {

  @SuppressWarnings("WeakerAccess") @NonNull final T preferences;

  protected CustomTimePreferenceInteractor(@NonNull T preferences) {
    this.preferences = preferences;
  }

  /**
   * public
   */
  @NonNull @CheckResult Single<Long> saveTime(long time, long delay) {
    return Completable.fromAction(() -> saveTimeToPreferences(preferences, time))
        .andThen(getTime())
        .delay(delay, TimeUnit.MILLISECONDS);
  }

  @NonNull @CheckResult public Single<Long> getTime() {
    return Single.fromCallable(() -> getTimeFromPreferences(preferences));
  }

  protected abstract void saveTimeToPreferences(@NonNull T preferences, long time);

  @CheckResult protected abstract long getTimeFromPreferences(@NonNull T preferences);
}
