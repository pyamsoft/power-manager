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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.logger.LoggerPresenter;
import javax.inject.Inject;

class ManagerLogger implements Logger {

  @NonNull private final LoggerPresenter presenter;

  @Inject ManagerLogger(@NonNull LoggerPresenter presenter) {
    this.presenter = presenter;
  }

  // Does not have to be bound
  @Override public void log(@NonNull String fmt, @Nullable Object... args) {
    presenter.log(fmt, args);
  }
}
