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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.MainActivity;
import javax.inject.Inject;

final class ForegroundInteractorImpl implements ForegroundInteractor {

  private static final int PENDING_RC = 1004;
  @NonNull private final Context appContext;
  @NonNull private final PowerManagerPreferences preferences;

  @Inject ForegroundInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    this.preferences = preferences;
    this.appContext = context.getApplicationContext();
  }

  @NonNull @Override public NotificationCompat.Builder createNotificationBuilder() {
    final Intent intent = new Intent(appContext, MainActivity.class);
    final PendingIntent pendingIntent =
        PendingIntent.getActivity(appContext, PENDING_RC, intent, 0);
    return new NotificationCompat.Builder(appContext).setContentTitle(
        appContext.getString(R.string.app_name))
        .setContentIntent(pendingIntent)
        .setPriority(preferences.getNotificationPriority());
  }
}
