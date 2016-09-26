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
import com.pyamsoft.powermanager.dagger.job.TriggerJob;
import com.pyamsoft.powermanager.dagger.wrapper.JobSchedulerCompat;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class ForegroundInteractorImpl implements ForegroundInteractor {

  private static final int PENDING_RC = 1004;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerManagerPreferences preferences;
  @SuppressWarnings("WeakerAccess") @NonNull final NotificationCompat.Builder builder;
  @NonNull private final JobSchedulerCompat jobManager;

  @Inject ForegroundInteractorImpl(@NonNull JobSchedulerCompat jobManager, @NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    this.jobManager = jobManager;
    this.preferences = preferences;

    final Intent intent =
        new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    final PendingIntent pendingIntent = PendingIntent.getActivity(context, PENDING_RC, intent, 0);
    builder = new NotificationCompat.Builder(context).setContentTitle(
        context.getString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_notification)
        .setColor(ContextCompat.getColor(context, R.color.amber500))
        .setContentText("Managing Power...")
        .setWhen(0)
        .setOngoing(true)
        .setAutoCancel(false)
        .setNumber(0)
        .setContentIntent(pendingIntent);
  }

  @Override public void create() {
    // For now, trigger every 5 minutes
    TriggerJob.queue(jobManager, new TriggerJob(5 * 60 * 1000));
  }

  @Override public void destroy() {
    Timber.d("Cancel all trigger jobs");
    jobManager.cancelJobsInBackground(TagConstraint.ANY, TriggerJob.TRIGGER_TAG);
  }

  @NonNull @CheckResult private Observable<Integer> getNotificationPriority() {
    return Observable.defer(() -> Observable.just(preferences.getNotificationPriority()));
  }

  @NonNull @Override public Observable<Notification> createNotification() {
    return getNotificationPriority().map(priority -> builder.setPriority(priority).build());
  }
}
