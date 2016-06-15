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
import com.pyamsoft.powermanager.app.service.ForegroundService;
import javax.inject.Inject;
import timber.log.Timber;

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
    final Intent wearIntent =
        new Intent(appContext, ForegroundService.class).putExtra(ForegroundService.EXTRA_WEARABLE,
            true);
    final PendingIntent pendingIntent =
        PendingIntent.getActivity(appContext, PENDING_RC, intent, 0);
    final PendingIntent wearAction =
        PendingIntent.getService(appContext, PENDING_RC + 4, wearIntent, 0);
    return new NotificationCompat.Builder(appContext).setContentTitle(
        appContext.getString(R.string.app_name))
        .setContentIntent(pendingIntent)
        .setPriority(preferences.getNotificationPriority())
        .addAction(R.drawable.ic_watch_black_24dp, "Wear", wearAction);
  }

  @Override public void updateWearablePreferenceStatus() {
    final boolean state = preferences.isWearableManaged();
    preferences.setWearableManaged(!state);
    Timber.d("Update wearable managed from %s to %s", state, !state);
  }
}
