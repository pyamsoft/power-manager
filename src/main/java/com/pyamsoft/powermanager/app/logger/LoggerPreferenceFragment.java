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

package com.pyamsoft.powermanager.app.logger;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import javax.inject.Inject;
import javax.inject.Named;

public class LoggerPreferenceFragment extends PreferenceFragmentCompat
    implements LoggerPresenter.Provider {

  @NonNull public static final String TAG = "LoggerPreferenceFragment";
  @Inject @Named("logger_presenter_wifi") LoggerPresenter loggerWifi;
  @Inject @Named("logger_presenter_data") LoggerPresenter loggerData;
  @Inject @Named("logger_presenter_bluetooth") LoggerPresenter loggerBluetooth;
  @Inject @Named("logger_presenter_sync") LoggerPresenter loggerSync;
  @Inject @Named("logger_presenter_airplane") LoggerPresenter loggerAirplane;
  @Inject @Named("logger_presenter_doze") LoggerPresenter loggerDoze;
  @Inject @Named("logger_presenter_trigger") LoggerPresenter loggerTrigger;
  @Inject @Named("logger_presenter_manager") LoggerPresenter loggerManager;
  @Nullable private Preference loggingEnabled;

  @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.logger);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Injector.get().provideComponent().plusLoggerComponent().inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loggingEnabled = findPreference(getString(R.string.logger_enabled));
    if (loggingEnabled == null) {
      throw new NullPointerException("LoggingEnabled view is NULL");
    }

    loggingEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof Boolean) {
        deleteAllPreviousLogs();
        return true;
      }
      return false;
    });
  }

  void deleteAllPreviousLogs() {
    loggerWifi.deleteLog();
    loggerData.deleteLog();
    loggerBluetooth.deleteLog();
    loggerSync.deleteLog();
    loggerAirplane.deleteLog();
    loggerDoze.deleteLog();
    loggerManager.deleteLog();
    loggerTrigger.deleteLog();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (loggingEnabled != null) {
      loggingEnabled.setOnPreferenceChangeListener(null);
    }
  }

  @Override public void onStart() {
    super.onStart();
    loggerWifi.bindView(this);
    loggerData.bindView(this);
    loggerBluetooth.bindView(this);
    loggerSync.bindView(this);
    loggerAirplane.bindView(this);
    loggerDoze.bindView(this);
    loggerManager.bindView(this);
    loggerTrigger.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    loggerWifi.unbindView();
    loggerData.unbindView();
    loggerBluetooth.unbindView();
    loggerSync.unbindView();
    loggerAirplane.unbindView();
    loggerDoze.unbindView();
    loggerManager.unbindView();
    loggerTrigger.unbindView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    loggerWifi.destroy();
    loggerData.destroy();
    loggerBluetooth.destroy();
    loggerSync.destroy();
    loggerAirplane.destroy();
    loggerDoze.destroy();
    loggerManager.destroy();
    loggerTrigger.destroy();
  }

  @CheckResult @NonNull LoggerDialog getLoggerDialog() {
    final Fragment loggerDialog = getParentFragment();
    if (loggerDialog instanceof LoggerDialog) {
      return (LoggerDialog) loggerDialog;
    } else {
      throw new RuntimeException("Parent is not LoggerDialog");
    }
  }

  @Override public void onPrepareLogContentRetrieval() {
    getLoggerDialog().onPrepareLogContentRetrieval();
  }

  @Override public void onLogContentRetrieved(@NonNull String logLine) {
    getLoggerDialog().onLogContentRetrieved(logLine);
  }

  @Override public void onAllLogContentsRetrieved() {
    getLoggerDialog().onAllLogContentsRetrieved();
  }

  @Override public void onLogDeleted(@NonNull String logId) {
    getLoggerDialog().onLogDeleted(logId);
  }
}