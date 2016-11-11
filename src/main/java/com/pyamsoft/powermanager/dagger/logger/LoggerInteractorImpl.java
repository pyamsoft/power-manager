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

package com.pyamsoft.powermanager.dagger.logger;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import rx.Observable;
import rx.exceptions.Exceptions;
import timber.log.Timber;

abstract class LoggerInteractorImpl implements LoggerInteractor {

  @NonNull private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();
  private static final int MAX_LINE_LENGTH = 80;
  @SuppressWarnings("WeakerAccess") @NonNull final Context appContext;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;
  @Nullable private String logPath;

  LoggerInteractorImpl(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
  }

  @SuppressWarnings("WeakerAccess") @NonNull @CheckResult String getLogPath() {
    final String type = getLogType();

    if (logPath == null) {
      final String filesDirPath = appContext.getFilesDir().getAbsolutePath();

      final File logDir = new File(filesDirPath, "logger");
      if (!logDir.mkdirs()) {
        Timber.e("Failed to make log dir: %s", logDir.getAbsolutePath());
        Timber.e("Will be unable to log to file");
      }

      final String logDirPath = logDir.getAbsolutePath();
      logPath = new File(logDirPath, type).getAbsolutePath();
    }

    Timber.d("%s Log location: %s", type, logPath);
    return logPath;
  }

  @NonNull @Override public Observable<Boolean> isLoggingEnabled() {
    return Observable.defer(() -> {
      // TODO get from preferences
      Timber.d("isLoggingEnabled: return hardcoded TRUE");
      return Observable.just(true);
    });
  }

  @NonNull @Override public Observable<String> getLogContents() {
    return Observable.defer(() -> Observable.just(getLogPath())).flatMap(logLocation -> {
      final List<String> fileContents = new ArrayList<>();
      try (
          final BufferedInputStream bufferedInputStream = new BufferedInputStream(
              appContext.openFileInput(logLocation));
          final InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream,
              StandardCharsets.UTF_8);
          final BufferedReader reader = new BufferedReader(inputStreamReader)) {
        String line = reader.readLine();
        while (line != null) {
          fileContents.add(line);
          line = reader.readLine();
        }
      } catch (IOException e) {
        throw Exceptions.propagate(e);
      }

      return Observable.from(fileContents);
    });
  }

  @NonNull @Override public Observable<Boolean> appendToLog(@NonNull String message) {
    return Observable.defer(() -> Observable.just(getLogPath())).map(logLocation -> {
      try (
          final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
              appContext.openFileOutput(logLocation, Context.MODE_APPEND));
          final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(bufferedOutputStream,
              StandardCharsets.UTF_8);
          final BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
        final String formattedMessage = formatMessage(message);
        bufferedWriter.write(formattedMessage);
        bufferedWriter.flush();
      } catch (IOException e) {
        throw Exceptions.propagate(e);
      }
      return true;
    });
  }

  @NonNull @CheckResult private String formatMessage(String message) {
    final String datePrefix = DATE_FORMAT.format(Calendar.getInstance().getTime());
    return String.format(Locale.getDefault(), "[%s] - %s", datePrefix, addLinebreaks(message));
  }

  // Bug: if a word in the input is longer than maxLineLength it will be appended to the current
  // line instead of on a too-long line of its own. I assume line length is something like
  // 80 or 120 characters, in which case this is unlikely to be a problem.
  @NonNull @CheckResult private String addLinebreaks(@NonNull String input) {
    final StringTokenizer tok = new StringTokenizer(input, " ");
    final StringBuilder output = new StringBuilder(input.length());
    int lineLen = 0;
    while (tok.hasMoreTokens()) {
      final String word = tok.nextToken();

      if (lineLen + word.length() > MAX_LINE_LENGTH) {
        output.append("\n");
        lineLen = 0;
      }

      output.append(word);
      lineLen += word.length();
    }

    return output.toString();
  }
}
