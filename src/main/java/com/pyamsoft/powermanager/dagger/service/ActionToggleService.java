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

package com.pyamsoft.powermanager.dagger.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import timber.log.Timber;

public class ActionToggleService extends IntentService {

  public ActionToggleService() {
    super(ActionToggleService.class.getName());
  }

  // KLUDGE: Raw preference access from service
  private void setForegroundServiceEnabled(boolean state) {
    final SharedPreferences preferences =
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    preferences.edit().putBoolean(ForegroundService.POWER_MANAGER_SERVICE_ENABLED, state).apply();
    if (state) {
      ForegroundService.start(getApplicationContext());
    } else {
      ForegroundService.stop(getApplicationContext());
    }
  }

  @Override protected void onHandleIntent(Intent intent) {
    Timber.d("Action: Toggle Service");
    setForegroundServiceEnabled(!ForegroundService.isEnabled(getApplicationContext()));
  }
}
