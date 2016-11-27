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

package com.pyamsoft.powermanager.dagger.wrapper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import javax.inject.Inject;
import timber.log.Timber;

class JobQueuerWrapperImpl implements JobQueuerWrapper {

  @NonNull private final Context appContext;
  @NonNull private final AlarmManager alarmManager;

  @Inject JobQueuerWrapperImpl(@NonNull Context appContext) {
    this.appContext = appContext;
    this.alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
  }

  @Override public void cancel(@NonNull Intent intent) {
    Timber.w("Cancel Alarm: %s", intent);
    alarmManager.cancel(PendingIntent.getService(appContext, 0, intent, 0));
  }

  @Override public void set(@NonNull Intent intent, long time) {
    cancel(intent);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Timber.i("Set and allow while idle: %s at %d", intent, time);
      alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time,
          PendingIntent.getService(appContext, 0, intent, 0));
    } else {
      Timber.i("Set: %s at %d", intent, time);
      alarmManager.setExact(AlarmManager.RTC_WAKEUP, time,
          PendingIntent.getService(appContext, 0, intent, 0));
    }
  }

  @Override public void setRepeating(@NonNull Intent intent, long time, long interval) {
    cancel(intent);

    Timber.i("Set repeating: %s at %d window %d", intent, time, interval);
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval,
        PendingIntent.getService(appContext, 0, intent, 0));
  }
}
