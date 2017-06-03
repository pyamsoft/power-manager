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

package com.pyamsoft.powermanager.base.logger

import android.content.Context
import android.support.annotation.CheckResult
import com.pyamsoft.powermanager.base.preference.LoggerPreferences
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.DateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class LoggerInteractor @Inject constructor(context: Context,
    private val preferences: LoggerPreferences, internal val logId: String) {
  private val appContext: Context = context.applicationContext
  private var logPath: File? = null

  @CheckResult fun getLogLocation(): File {
    if (logPath == null || !logPath!!.exists()) {
      synchronized(LoggerInteractor::class.java) {
        if (logPath == null || !logPath!!.exists()) {
          val filesDirPath = appContext.filesDir.absolutePath
          val logDir = File(filesDirPath, "logger")
          if (!logDir.exists()) {
            if (!logDir.mkdirs()) {
              Timber.e("Failed to make log dir: %s", logDir.absolutePath)
              Timber.e("Will be unable to log to file")
            }
          }
          val logDirPath = logDir.absolutePath
          logPath = File(logDirPath, logId)
        }
      }
    }

    return logPath!!
  }

  @CheckResult fun log(logType: LogType, fmt: String, vararg args: Any): Completable {
    logWithTimber(logType, fmt, *args)
    return isLoggingEnabled().filter { it }.flatMapCompletable {
      val message = String.format(Locale.getDefault(), fmt, *args)
      val logMessage = String.format(Locale.getDefault(), "%s: %s", logType.name, message)
      val writeAppendResult: Completable
      if (it) {
        writeAppendResult = appendToLog(logMessage)
      } else {
        writeAppendResult = Completable.complete()
      }
      return@flatMapCompletable writeAppendResult
    }
  }

  @CheckResult private fun isLoggingEnabled(): Single<Boolean> {
    return Single.fromCallable { preferences.loggerEnabled }
  }

  private fun logWithTimber(logType: LogType, fmt: String, vararg args: Any) {
    when (logType) {
      LogType.DEBUG -> Timber.d(fmt, *args)
      LogType.INFO -> Timber.i(fmt, *args)
      LogType.WARNING -> Timber.w(fmt, *args)
      LogType.ERROR -> Timber.e(fmt, *args)
      else -> throw IllegalStateException("Invalid LogType: " + logType.name)
    }
  }

  /**
   * public
   */
  val logContents: Observable<String>
    @CheckResult get() = Single.fromCallable { getLogLocation() }.map {
      val fileContents = ArrayList<String>()
      FileInputStream(it).use({
        BufferedInputStream(it).use {
          InputStreamReader(it, StandardCharsets.UTF_8).use {
            BufferedReader(it).use {
              var line: String? = it.readLine()
              while (line != null) {
                fileContents.add(line)
                line = it.readLine()
              }
            }
          }
        }
      })
      return@map fileContents
    }.flatMapObservable { Observable.fromIterable(it) }

  /**
   * public
   */
  @CheckResult fun deleteLog(): Single<Boolean> {
    return Single.fromCallable { getLogLocation() }.map {
      Timber.w("Delete log file: %s", it.absolutePath)
      return@map it.delete()
    }
  }

  @CheckResult fun appendToLog(message: String): Completable {
    return Maybe.fromCallable { getLogLocation() }.flatMapCompletable {
      FileOutputStream(it, true).use({
        BufferedOutputStream(it).use {
          OutputStreamWriter(it, StandardCharsets.UTF_8).use {
            BufferedWriter(it).use {
              val formattedMessage = formatMessage(message)
              it.write(formattedMessage)
              it.newLine()
              it.flush()
            }
          }
        }
      })
      return@flatMapCompletable Completable.complete()
    }
  }

  @CheckResult fun formatMessage(message: String): String {
    val datePrefix = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
    return String.format(Locale.getDefault(), "[%s] - %s", datePrefix, message)
  }

  companion object {
    const val AIRPLANE_LOG_ID = "AIRPLANE"
    const val BLUETOOTH_LOG_ID = "BLUETOOTH"
    const val DATA_LOG_ID = "DATA"
    const val DOZE_LOG_ID = "DOZE"
    const val MANAGER_LOG_ID = "MANAGER"
    const val SYNC_LOG_ID = "SYNC"
    const val TRIGGER_LOG_ID = "TRIGGER"
    const val WIFI_LOG_ID = "WIFI"
  }
}
