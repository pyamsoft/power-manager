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

package com.pyamsoft.powermanager.base.logger;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.base.preference.LoggerPreferences;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import timber.log.Timber;

class LoggerInteractor {

  @NonNull static final String AIRPLANE_LOG_ID = "AIRPLANE";
  @NonNull static final String BLUETOOTH_LOG_ID = "BLUETOOTH";
  @NonNull static final String DATA_LOG_ID = "DATA";
  @NonNull static final String DOZE_LOG_ID = "DOZE";
  @NonNull static final String MANAGER_LOG_ID = "MANAGER";
  @NonNull static final String SYNC_LOG_ID = "SYNC";
  @NonNull static final String TRIGGER_LOG_ID = "TRIGGER";
  @NonNull static final String WIFI_LOG_ID = "WIFI";

  @SuppressWarnings("WeakerAccess") @NonNull final Context appContext;
  @SuppressWarnings("WeakerAccess") @NonNull final LoggerPreferences preferences;
  @NonNull private final String logId;
  @Nullable private File logPath;

  @Inject LoggerInteractor(@NonNull Context context, @NonNull LoggerPreferences preferences,
      @NonNull String logId) {
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
    this.logId = logId;
  }

  /**
   * public
   */
  @CheckResult @NonNull String getLogId() {
    return logId;
  }

  @SuppressWarnings("WeakerAccess") @NonNull @CheckResult File getLogLocation() {
    final String type = getLogId();

    if (logPath == null || !logPath.exists()) {
      final String filesDirPath = appContext.getFilesDir().getAbsolutePath();

      final File logDir = new File(filesDirPath, "logger");
      if (!logDir.exists()) {
        if (!logDir.mkdirs()) {
          Timber.e("Failed to make log dir: %s", logDir.getAbsolutePath());
          Timber.e("Will be unable to log to file");
        }
      }

      final String logDirPath = logDir.getAbsolutePath();
      logPath = new File(logDirPath, type);
    }

    return logPath;
  }

  @CheckResult @NonNull
  public Completable log(@NonNull LogType logType, @NonNull String fmt, @Nullable Object... args) {
    logWithTimber(logType, fmt, args);
    return isLoggingEnabled().filter(enabled -> enabled).flatMapCompletable(loggingEnabled -> {
      final String message = String.format(Locale.getDefault(), fmt, args);
      final String logMessage =
          String.format(Locale.getDefault(), "%s: %s", logType.name(), message);

      final Completable writeAppendResult;
      if (loggingEnabled) {
        writeAppendResult = appendToLog(logMessage);
      } else {
        writeAppendResult = Completable.complete();
      }
      return writeAppendResult;
    });
  }

  @CheckResult @NonNull private Single<Boolean> isLoggingEnabled() {
    return Single.fromCallable(preferences::isLoggerEnabled);
  }

  private void logWithTimber(@NonNull LogType logType, @NonNull String fmt,
      @Nullable Object... args) {
    switch (logType) {
      case DEBUG:
        Timber.d(fmt, args);
        break;
      case INFO:
        Timber.i(fmt, args);
        break;
      case WARNING:
        Timber.w(fmt, args);
        break;
      case ERROR:
        Timber.e(fmt, args);
        break;
      default:
        throw new IllegalStateException("Invalid LogType: " + logType.name());
    }
  }

  /**
   * public
   */
  @CheckResult @NonNull Observable<String> getLogContents() {
    return Single.fromCallable(this::getLogLocation).map(logLocation -> {
      final List<String> fileContents = new ArrayList<>();
      try (final FileInputStream fileInputStream = new FileInputStream(logLocation);
           final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
           final InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream,
               StandardCharsets.UTF_8);
           final BufferedReader reader = new BufferedReader(inputStreamReader)) {
        String line = reader.readLine();
        while (line != null) {
          fileContents.add(line);
          line = reader.readLine();
        }
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }

      return fileContents;
    }).flatMapObservable(Observable::fromIterable);
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<Boolean> deleteLog() {
    return Single.fromCallable(this::getLogLocation).map(file -> {
      Timber.w("Delete log file: %s", file.getAbsolutePath());
      return file.delete();
    });
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull Completable appendToLog(
      @NonNull String message) {
    return Maybe.fromCallable(this::getLogLocation).flatMapCompletable(logLocation -> {
      try (final FileOutputStream fileOutputStream = new FileOutputStream(logLocation, true);
           final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
               fileOutputStream);
           final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
               bufferedOutputStream, StandardCharsets.UTF_8);
           final BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
        final String formattedMessage = formatMessage(message);
        bufferedWriter.write(formattedMessage);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }

      return Completable.complete();
    });
  }

  @SuppressWarnings("WeakerAccess") @NonNull @CheckResult String formatMessage(
      @NonNull String message) {
    final String datePrefix =
        DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    return String.format(Locale.getDefault(), "[%s] - %s", datePrefix, message);
  }
}
