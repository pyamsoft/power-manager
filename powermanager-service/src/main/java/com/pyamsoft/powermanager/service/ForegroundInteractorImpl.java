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

package com.pyamsoft.powermanager.service;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.wrapper.JobQueuerWrapper;
import com.pyamsoft.powermanager.model.Constants;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class ForegroundInteractorImpl extends ActionToggleInteractorImpl implements ForegroundInteractor {

  private static final int PENDING_RC = 1004;
  private static final int TOGGLE_RC = 421;
  @SuppressWarnings("WeakerAccess") @NonNull final NotificationCompat.Builder builder;
  @SuppressWarnings("WeakerAccess") @NonNull final Context appContext;
  @SuppressWarnings("WeakerAccess") @NonNull final Class<? extends Service> toggleServiceClass;
  @NonNull private final Class<? extends Service> triggerRunnerServiceClass;
  @NonNull private final JobQueuerWrapper jobQueuerWrapper;

  @Inject ForegroundInteractorImpl(@NonNull JobQueuerWrapper jobQueuerWrapper,
      @NonNull Context context, @NonNull PowerManagerPreferences preferences,
      @NonNull Class<? extends Activity> mainActivityClass,
      @NonNull Class<? extends Service> toggleServiceClass,
      @NonNull Class<? extends Service> triggerRunnerServiceClass) {
    super(preferences);
    this.jobQueuerWrapper = jobQueuerWrapper;
    appContext = context.getApplicationContext();
    this.toggleServiceClass = toggleServiceClass;
    this.triggerRunnerServiceClass = triggerRunnerServiceClass;

    final Intent intent =
        new Intent(appContext, mainActivityClass).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    final PendingIntent pendingIntent =
        PendingIntent.getActivity(appContext, PENDING_RC, intent, 0);

    builder = new NotificationCompat.Builder(appContext).setContentTitle(
        context.getString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_notification)
        .setColor(ContextCompat.getColor(context, R.color.amber500))
        .setWhen(0)
        .setOngoing(true)
        .setAutoCancel(false)
        .setNumber(0)
        .setContentIntent(pendingIntent);
  }

  @Override public void create() {
    final long delayTime = getPreferences().getTriggerPeriodTime();
    final long triggerPeriod = delayTime * 60 * 1000L;
    final Bundle extras = new Bundle();
    extras.putLong(Constants.TRIGGER_RUNNER_PERIOD, triggerPeriod);

    Timber.d("Trigger period: %d", triggerPeriod);

    jobQueuerWrapper.cancel(triggerRunnerServiceClass);
    jobQueuerWrapper.set(triggerRunnerServiceClass, System.currentTimeMillis() + triggerPeriod);
  }

  @Override public void destroy() {
    Timber.d("Cancel all trigger jobs");
    jobQueuerWrapper.cancel(triggerRunnerServiceClass);
  }

  @SuppressWarnings("WeakerAccess") @NonNull @CheckResult
  Observable<Integer> getNotificationPriority() {
    return Observable.defer(() -> Observable.just(getPreferences().getNotificationPriority()));
  }

  @NonNull @Override public Observable<Notification> createNotification() {
    return isServiceEnabled().flatMap(serviceEnabled -> {
      final String actionName = serviceEnabled ? "Suspend" : "Start";
      final Intent toggleService = new Intent(appContext, toggleServiceClass);
      final PendingIntent actionToggleService =
          PendingIntent.getService(appContext, TOGGLE_RC, toggleService,
              PendingIntent.FLAG_UPDATE_CURRENT);

      final String title =
          serviceEnabled ? "Managing Device Power..." : "Power Management Suspended...";

      return getNotificationPriority().map(priority -> {
        // Clear all of the Actions
        builder.mActions.clear();
        return builder.setPriority(priority)
            .setContentText(title)
            .addAction(R.drawable.ic_notification, actionName, actionToggleService)
            .build();
      });
    });
  }
}