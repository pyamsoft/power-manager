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

package com.pyamsoft.powermanager.dagger.modifier.manage;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.modifier.InterestModifier;
import com.pyamsoft.powermanager.app.service.ForegroundService;

abstract class ManageModifier implements InterestModifier {

  @NonNull private final Context appContext;
  @NonNull private final Intent service;
  @NonNull private final Handler handler;
  @NonNull private final PowerManagerPreferences preferences;

  ManageModifier(@NonNull Context context, @NonNull PowerManagerPreferences preferences) {
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
    this.handler = new Handler(Looper.getMainLooper());
    this.service = new Intent(appContext, ForegroundService.class);
  }

  @Override public final void set() {
    handler.removeCallbacksAndMessages(null);
    handler.post(() -> {
      mainThreadSet(appContext, preferences);

      // The notification will be notified when a manage state changes
      appContext.startService(service);
    });
  }

  @Override public final void unset() {
    handler.removeCallbacksAndMessages(null);
    handler.post(() -> {
      mainThreadUnset(appContext, preferences);

      // The notification will be notified when a manage state changes
      appContext.startService(service);
    });
  }


  abstract void mainThreadSet(@NonNull Context context, @NonNull PowerManagerPreferences preferences);

  abstract void mainThreadUnset(@NonNull Context context, @NonNull PowerManagerPreferences preferences);
}

