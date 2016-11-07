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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.MainActivity;
import com.pyamsoft.powermanager.app.service.ActionToggleService;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import com.pyamsoft.powermanager.dagger.job.TriggerJob;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

class ForegroundInteractorImpl extends BaseServiceInteractorImpl implements ForegroundInteractor {

  private static final int PENDING_RC = 1004;
  private static final int TOGGLE_RC = 421;
  @SuppressWarnings("WeakerAccess") @NonNull final NotificationCompat.Builder builder;
  @SuppressWarnings("WeakerAccess") @NonNull final Context appContext;
  @NonNull private final JobSchedulerCompat jobManager;

  @Inject ForegroundInteractorImpl(@NonNull JobSchedulerCompat jobManager, @NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    super(preferences);
    appContext = context.getApplicationContext();
    this.jobManager = jobManager;

    final Intent intent =
        new Intent(appContext, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
    // TODO
    // For now, trigger every 5 minutes
    //TriggerJob.queue(jobManager, new TriggerJob(5 * 60 * 1000, wifiObserver, dataObserver,
    //    bluetoothObserver, syncObserver, wifiModifier, dataModifier, bluetoothModifier,
    //    syncModifier, jobSchedulerCompat, powerTriggerDB));
  }

  @Override public void destroy() {
    Timber.d("Cancel all trigger jobs");
    jobManager.cancelJobsInBackground(TagConstraint.ANY, TriggerJob.TRIGGER_TAG);
  }

  @SuppressWarnings("WeakerAccess") @NonNull @CheckResult
  Observable<Integer> getNotificationPriority() {
    return Observable.defer(() -> Observable.just(getPreferences().getNotificationPriority()));
  }

  @NonNull @Override public Observable<Notification> createNotification() {
    return Observable.defer(() -> Observable.just(getPreferences().isForegroundServiceEnabled()))
        .flatMap(new Func1<Boolean, Observable<Notification>>() {
          @Override public Observable<Notification> call(Boolean serviceEnabled) {
            final String actionName = serviceEnabled ? "Suspend" : "Start";
            final Intent toggleService = new Intent(appContext, ActionToggleService.class);
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
          }
        });
  }
}
