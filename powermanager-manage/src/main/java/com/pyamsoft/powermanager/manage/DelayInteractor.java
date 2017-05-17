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

package com.pyamsoft.powermanager.manage;

import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.pyamsoft.powermanager.base.preference.ManagePreferences;
import com.pyamsoft.pydroid.bus.EventBus;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton class DelayInteractor {

  @NonNull final ManagePreferences preferences;
  @NonNull private final EventBus customInputBus;

  @Inject DelayInteractor(@NonNull ManagePreferences preferences) {
    this.preferences = preferences;
    customInputBus = EventBus.newLocalBus();
  }

  @CheckResult @NonNull Single<Pair<Boolean, Long>> getDelayTime() {
    return Single.fromCallable(
        () -> new Pair<>(preferences.isCustomManageDelay(), preferences.getManageDelay()));
  }

  @CheckResult @NonNull Completable setDelayTime(long time) {
    return Completable.fromAction(() -> preferences.setManageDelay(time))
        .andThen(Completable.fromAction(() -> preferences.setCustomManageDelay(false)));
  }

  @CheckResult @NonNull Flowable<Long> listenTimeChanges() {
    return Flowable.create(emitter -> {
      SharedPreferences.OnSharedPreferenceChangeListener listener =
          preferences.registerDelayChanges(time -> {
            Timber.d("On delay changed");
            emitter.onNext(time);
          });

      emitter.setCancellable(() -> {
        Timber.d("Stop listening for delay changes");
        preferences.unregisterDelayChanges(listener);
      });
    }, BackpressureStrategy.BUFFER);
  }

  /**
   * public
   */
  void acceptCustomTimeChange(@NonNull String text, boolean instant) {
    if (instant) {
      Pair<String, Long> pair = convertAndSaveCustomTime(text);
      if (pair.first == null) {
        Timber.d("Instant saved custom time: %d", pair.second);
      } else {
        Timber.e("Error instant saving custom time: %s", pair.first);
      }
    } else {
      customInputBus.publish(text);
    }
  }

  @CheckResult @NonNull Observable<Pair<String, Long>> listenCustomTimeChanges() {
    return customInputBus.listen(String.class)
        .debounce(800, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .map(this::convertAndSaveCustomTime);
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  Pair<String, Long> convertAndSaveCustomTime(@NonNull String s) {
    String errorString;
    long time;
    try {
      errorString = null;
      time = Long.valueOf(s);
    } catch (NumberFormatException e) {
      Timber.e(e, "Error formatting string to long: %s", s);
      errorString = s;
      time = 0;
    }
    preferences.setManageDelay(time);
    preferences.setCustomManageDelay(true);
    return new Pair<>(errorString, time);
  }
}
