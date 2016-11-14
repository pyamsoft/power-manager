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
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import javax.inject.Inject;

class TriggerLoggerInteractor extends LoggerInteractorImpl {

  @NonNull private static final String LOG_ID = "TRIGGER";

  @Inject TriggerLoggerInteractor(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    super(context, preferences);
  }

  @NonNull @Override public String getLogId() {
    return LOG_ID;
  }
}
