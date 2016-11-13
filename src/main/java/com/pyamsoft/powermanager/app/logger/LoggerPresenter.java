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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.presenter.Presenter;

public interface LoggerPresenter extends Presenter<LoggerPresenter.Provider> {

  void log(@NonNull LogType logType, @NonNull String fmt, @Nullable Object... args);

  void d(@NonNull String fmt, @Nullable Object... args);

  void i(@NonNull String fmt, @Nullable Object... args);

  void w(@NonNull String fmt, @Nullable Object... args);

  void e(@NonNull String fmt, @Nullable Object... args);

  void deleteLog();

  interface Provider {

    void onPrepareLogContentRetrieval();

    void onLogContentRetrieved(@NonNull String logLine);

    void onAllLogContentsRetrieved();

    void onLogDeleted();
  }
}
