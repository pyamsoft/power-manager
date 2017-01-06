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

package com.pyamsoft.powermanager.logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.model.LogType;
import javax.inject.Inject;

class LoggerImpl implements Logger {

  @NonNull private final LoggerPresenter presenter;

  @Inject LoggerImpl(@NonNull LoggerPresenter presenter) {
    this.presenter = presenter;
  }

  // Does not have to be bound
  private void log(@NonNull LogType logType, @NonNull String fmt, @Nullable Object... args) {
    presenter.log(logType, fmt, args);
  }

  @Override public void d(@NonNull String fmt, @Nullable Object... args) {
    log(LogType.DEBUG, fmt, args);
  }

  @Override public void i(@NonNull String fmt, @Nullable Object... args) {
    log(LogType.INFO, fmt, args);
  }

  @Override public void w(@NonNull String fmt, @Nullable Object... args) {
    log(LogType.WARNING, fmt, args);
  }

  @Override public void e(@NonNull String fmt, @Nullable Object... args) {
    log(LogType.ERROR, fmt, args);
  }
}
