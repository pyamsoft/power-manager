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

package com.pyamsoft.powermanager.logger

import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v7.preference.Preference
import android.view.View
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.base.logger.LoggerPreferencePresenter
import com.pyamsoft.powermanager.base.logger.LoggerPresenter
import com.pyamsoft.powermanager.uicore.WatchedPreferenceFragment
import javax.inject.Inject
import javax.inject.Named

class LoggerPreferenceFragment : WatchedPreferenceFragment() {
  @field:[Inject Named("logger_presenter_wifi")] lateinit internal var loggerWifi: LoggerPresenter
  @field:[Inject Named("logger_presenter_data")] lateinit internal var loggerData: LoggerPresenter
  @field:[Inject Named(
      "logger_presenter_bluetooth")] lateinit internal var loggerBluetooth: LoggerPresenter
  @field:[Inject Named("logger_presenter_sync")] lateinit internal var loggerSync: LoggerPresenter
  @field:[Inject Named(
      "logger_presenter_airplane")] lateinit internal var loggerAirplane: LoggerPresenter
  @field:[Inject Named("logger_presenter_doze")] lateinit internal var loggerDoze: LoggerPresenter
  @field:[Inject Named(
      "logger_presenter_trigger")] lateinit internal var loggerTrigger: LoggerPresenter
  @field:[Inject Named(
      "logger_presenter_manager")] lateinit internal var loggerManager: LoggerPresenter
  private lateinit var loggingEnabled: Preference
  @Inject internal lateinit var loggerPrefrencePresenter: LoggerPreferencePresenter

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    addPreferencesFromResource(R.xml.logger)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.with(context) {
      it.inject(this)
    }
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    loggingEnabled = findPreference(getString(R.string.logger_enabled))
  }

  internal fun deleteAllPreviousLogs() {
    loggerWifi.deleteLog(this::onLogDeleted)
    loggerData.deleteLog(this::onLogDeleted)
    loggerBluetooth.deleteLog(this::onLogDeleted)
    loggerSync.deleteLog(this::onLogDeleted)
    loggerAirplane.deleteLog(this::onLogDeleted)
    loggerDoze.deleteLog(this::onLogDeleted)
    loggerManager.deleteLog(this::onLogDeleted)
    loggerTrigger.deleteLog(this::onLogDeleted)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    loggingEnabled.onPreferenceChangeListener = null
  }

  override fun onStart() {
    super.onStart()
    loggerWifi.retrieveLogContents(this::onPrepareLogContentRetrieval, this::onLogContentRetrieved,
        this::onAllLogContentsRetrieved)
    loggerData.retrieveLogContents(this::onPrepareLogContentRetrieval, this::onLogContentRetrieved,
        this::onAllLogContentsRetrieved)
    loggerBluetooth.retrieveLogContents(this::onPrepareLogContentRetrieval,
        this::onLogContentRetrieved, this::onAllLogContentsRetrieved)
    loggerSync.retrieveLogContents(this::onPrepareLogContentRetrieval, this::onLogContentRetrieved,
        this::onAllLogContentsRetrieved)
    loggerAirplane.retrieveLogContents(this::onPrepareLogContentRetrieval,
        this::onLogContentRetrieved, this::onAllLogContentsRetrieved)
    loggerDoze.retrieveLogContents(this::onPrepareLogContentRetrieval, this::onLogContentRetrieved,
        this::onAllLogContentsRetrieved)
    loggerManager.retrieveLogContents(this::onPrepareLogContentRetrieval,
        this::onLogContentRetrieved, this::onAllLogContentsRetrieved)
    loggerTrigger.retrieveLogContents(this::onPrepareLogContentRetrieval,
        this::onLogContentRetrieved, this::onAllLogContentsRetrieved)

    loggerPrefrencePresenter.clickEvent(loggingEnabled, {
      deleteAllPreviousLogs()
    })
  }

  override fun onStop() {
    super.onStop()
    loggerWifi.stop()
    loggerData.stop()
    loggerBluetooth.stop()
    loggerSync.stop()
    loggerAirplane.stop()
    loggerDoze.stop()
    loggerManager.stop()
    loggerTrigger.stop()

    loggerPrefrencePresenter.stop()
  }

  override fun onDestroy() {
    super.onDestroy()
    loggerWifi.destroy()
    loggerData.destroy()
    loggerBluetooth.destroy()
    loggerSync.destroy()
    loggerAirplane.destroy()
    loggerDoze.destroy()
    loggerManager.destroy()
    loggerTrigger.destroy()


    loggerPrefrencePresenter.destroy()
  }

  private val loggerDialog: LoggerDialog
    @CheckResult get() {
      val loggerDialog = parentFragment
      if (loggerDialog is LoggerDialog) {
        return loggerDialog
      } else {
        throw RuntimeException("Parent is not LoggerDialog")
      }
    }

  fun onPrepareLogContentRetrieval() {
    loggerDialog.onPrepareLogContentRetrieval()
  }

  fun onLogContentRetrieved(logLine: String) {
    loggerDialog.onLogContentRetrieved(logLine)
  }

  fun onAllLogContentsRetrieved() {
    loggerDialog.onAllLogContentsRetrieved()
  }

  fun onLogDeleted(logId: String) {
    loggerDialog.onLogDeleted(logId)
  }

  companion object {
    const val TAG = "LoggerPreferenceFragment"
  }
}
