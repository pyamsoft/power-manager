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
import com.pyamsoft.powermanager.base.preference.ManagePreferences.TimeChangeListener
import com.pyamsoft.pydroid.bus.EventBus
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TimeInteractor @Inject internal constructor(val preferences: TimePreferenceWrapper) {
  private val customInputBus: EventBus = EventBus.newLocalBus()

  val delayTime: Single<Pair<Boolean, Long>>
    @CheckResult get() = Single.fromCallable { Pair(preferences.isCustom, preferences.time) }

  @CheckResult fun setTime(time: Long): Completable {
    return Completable.fromAction { preferences.setTime(time, false) }
  }

  @CheckResult fun listenTimeChanges(): Flowable<Long> {
    return Flowable.create<Long>({
      val listener = preferences.registerTimeChanges(object : TimeChangeListener {
        override fun onTimeChanged(time: Long) {
          it.onNext(time)
        }
      })

      it.setCancellable {
        Timber.d("Stop listening for delay changes")
        preferences.unregisterTimeChanges(listener)
      }

      it.setDisposable(object : Disposable {

        private var disposed = false

        override fun dispose() {
          Timber.d("Dispose: Stop listening for delay changes")
          preferences.unregisterTimeChanges(listener)
          disposed = true
        }

        override fun isDisposed(): Boolean {
          return disposed
        }

      })
    }, BackpressureStrategy.BUFFER)
  }

  /**
   * public
   */
  fun acceptCustomTimeChange(text: String, instant: Boolean) {
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

  @CheckResult fun listenCustomTimeChanges(): Observable<Pair<String?, Long>> {
    return customInputBus.listen(String::class.java).debounce(800,
        TimeUnit.MILLISECONDS).distinctUntilChanged().map { convertAndSaveCustomTime(it) }
  }

  @CheckResult fun convertAndSaveCustomTime(s: String): Pair<String?, Long> {
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
