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
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.exceptions.Exceptions;
import timber.log.Timber;

abstract class LoggerInteractorImpl implements LoggerInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final Context appContext;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;
  @SuppressWarnings("WeakerAccess") @NonNull final String logPath;

  LoggerInteractorImpl(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;

    final String type = getLogType();
    final String filesDirPath = appContext.getFilesDir().getAbsolutePath();
    final String logDirPath = new File(filesDirPath, "logger").getAbsolutePath();
    logPath = new File(logDirPath, type).getAbsolutePath();
    Timber.d("%s Log location: %s", type, logPath);
  }

  @NonNull @Override public Observable<Boolean> isLoggingEnabled() {
    return Observable.defer(() -> {
      // TODO get from preferences
      Timber.d("isLoggingEnabled: return hardcoded TRUE");
      return Observable.just(true);
    });
  }

  @NonNull @Override public Observable<String> getLogContents() {
    return Observable.defer(() -> Observable.just(logPath)).flatMap(logLocation -> {
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
    return Observable.defer(() -> Observable.just(logPath)).map(logLocation -> {
      try (
          final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
              appContext.openFileOutput(logLocation, Context.MODE_APPEND));
          final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(bufferedOutputStream,
              StandardCharsets.UTF_8);
          final BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
        bufferedWriter.write(message);
        bufferedWriter.flush();
      } catch (IOException e) {
        throw Exceptions.propagate(e);
      }
      return true;
    });
  }
}
