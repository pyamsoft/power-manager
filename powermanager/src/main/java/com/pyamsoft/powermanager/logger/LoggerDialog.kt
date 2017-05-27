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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.base.logger.LoggerPresenter
import com.pyamsoft.powermanager.uicore.WatchedDialog
import timber.log.Timber

class LoggerDialog : WatchedDialog(), LoggerPresenter.DeleteCallback, LoggerPresenter.LogCallback {
  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.dialog_logger, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    addOptionsPreferenceFragment()
  }

  private fun addOptionsPreferenceFragment() {
    val fragmentManager = childFragmentManager
    if (fragmentManager.findFragmentByTag(LoggerPreferenceFragment.TAG) == null) {
      fragmentManager.beginTransaction().replace(R.id.dialog_logger_options_container,
          LoggerPreferenceFragment(), LoggerPreferenceFragment.TAG).commit()
    }
  }

  override fun onPrepareLogContentRetrieval() {
    Timber.d("onPrepareLogContentRetrieval")
  }

  override fun onLogContentRetrieved(logLine: String) {
    Timber.d("onLogContentRetrieved: %s", logLine)
  }

  override fun onAllLogContentsRetrieved() {
    Timber.d("onAllLogContentsRetrieved")
  }

  override fun onLogDeleted(logId: String) {
    Timber.d("onLogDeleted: %s", logId)
  }
}
