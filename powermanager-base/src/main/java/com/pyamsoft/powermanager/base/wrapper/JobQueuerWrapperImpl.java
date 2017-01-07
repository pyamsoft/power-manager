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

package com.pyamsoft.powermanager.base.wrapper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Date;
import javax.inject.Inject;
import timber.log.Timber;

class JobQueuerWrapperImpl implements JobQueuerWrapper {

  @NonNull private final AlarmManager alarmManager;
  @NonNull private final Context appContext;

  @Inject JobQueuerWrapperImpl(@NonNull Context context) {
    appContext = context.getApplicationContext();
    this.alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
  }

  @CheckResult @NonNull
  private PendingIntent createPendingIntent(@NonNull Class<? extends Service> serviceClass,
      @Nullable Bundle extras) {
    final Intent serviceIntent = new Intent(appContext, serviceClass);
    if (extras != null) {
      serviceIntent.putExtra(JOB_EXTRAS, extras);
    }
    return PendingIntent.getService(appContext, 0, serviceIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);
  }

  @Override public void cancel(@NonNull Class<? extends Service> serviceClass) {
    Timber.w("Cancel Alarm: %s", serviceClass.getName());
    final PendingIntent pendingIntent = createPendingIntent(serviceClass, null);
    alarmManager.cancel(pendingIntent);
    pendingIntent.cancel();
  }

  @Override public void set(@NonNull Class<? extends Service> serviceClass, long time) {
    set(serviceClass, time, null);
  }

  @Override public void set(@NonNull Class<? extends Service> serviceClass, long time,
      @Nullable Bundle extras) {
    final Date date = new Date(time);
    final PendingIntent pendingIntent = createPendingIntent(serviceClass, extras);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Timber.i("Set and allow while idle: %s at %s", serviceClass.getName(), date);
      alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    } else {
      Timber.i("Set: %s at %s", serviceClass.getName(), date);
      alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }
  }
}
