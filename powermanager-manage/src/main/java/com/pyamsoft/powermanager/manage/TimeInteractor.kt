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

package com.pyamsoft.powermanager.manage

import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.bus.RxBus
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open internal class TimeInteractor @Inject internal constructor(
    private val preferences: TimePreferenceWrapper) {
  private val customInputBus = RxBus.create<String>()

  internal val time: Single<Pair<Boolean, Long>>
    @CheckResult get() = Single.fromCallable { Pair(preferences.isCustom, preferences.time) }

  @CheckResult internal fun setTime(time: Long): Maybe<Long> {
    if (time > 0L) {
      return Completable.fromAction { preferences.setTime(time, false) }.andThen(Maybe.just(time))
    } else {
      return Maybe.empty()
    }
  }

  @CheckResult internal fun listenTimeChanges(): Observable<Long> {
    return Observable.create { emitter ->
      val listener = preferences.registerTimeChanges {
        if (!emitter.isDisposed) {
          emitter.onNext(it)
        }
      }

      emitter.setCancellable {
        Timber.d("Cancel: Stop listening for delay changes")
        preferences.unregisterTimeChanges(listener)
      }
    }
  }

  /**
   * public
   */
  internal fun acceptCustomTimeChange(text: String, instant: Boolean) {
    if (instant) {
      val pair = convertAndSaveCustomTime(text)
      if (pair.first == null) {
        Timber.d("Instant saved custom time: %d", pair.second)
      } else {
        Timber.e("Error instant saving custom time: %s", pair.first)
      }
    } else {
      customInputBus.publish(text)
    }
  }

  @CheckResult internal fun listenCustomTimeChanges(): Observable<Pair<String?, Long>> {
    return customInputBus.listen().debounce(800,
        TimeUnit.MILLISECONDS).distinctUntilChanged().map { convertAndSaveCustomTime(it) }
  }

  @CheckResult internal fun convertAndSaveCustomTime(s: String): Pair<String?, Long> {
    var errorString: String?
    var time: Long
    try {
      errorString = null
      time = java.lang.Long.valueOf(s)!!
    } catch (e: NumberFormatException) {
      Timber.e(e, "Error formatting string to long: %s", s)
      errorString = s
      time = 0
    }

    preferences.setTime(time, true)
    return Pair(errorString, preferences.time)
  }
}
