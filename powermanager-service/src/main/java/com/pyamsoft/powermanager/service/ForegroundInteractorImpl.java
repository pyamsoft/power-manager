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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.db.PowerTriggerDB;
import com.pyamsoft.powermanager.base.jobs.JobQueuer;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.Logger;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class ForegroundInteractorImpl extends ActionToggleInteractorImpl implements ForegroundInteractor {

  private static final int PENDING_RC = 1004;
  private static final int TOGGLE_RC = 421;
  @SuppressWarnings("WeakerAccess") @NonNull final NotificationCompat.Builder builder;
  @SuppressWarnings("WeakerAccess") @NonNull final Context appContext;
  @SuppressWarnings("WeakerAccess") @NonNull final Class<? extends Service> toggleServiceClass;
  @NonNull private final PowerTriggerDB powerTriggerDB;
  @NonNull private final BooleanInterestObserver chargingObserver;
  @NonNull private final JobQueuer jobQueuer;
  @NonNull private final Logger triggerLogger;
  @NonNull private final BooleanInterestObserver wifiObserver;
  @NonNull private final BooleanInterestObserver dataObserver;
  @NonNull private final BooleanInterestObserver bluetoothObserver;
  @NonNull private final BooleanInterestObserver syncObserver;
  @NonNull private final BooleanInterestModifier wifiModifier;
  @NonNull private final BooleanInterestModifier dataModifier;
  @NonNull private final BooleanInterestModifier bluetoothModifier;
  @NonNull private final BooleanInterestModifier syncModifier;

  @Inject ForegroundInteractorImpl(@NonNull JobQueuer jobQueuer, @NonNull Context context,
      @NonNull PowerManagerPreferences preferences,
      @NonNull Class<? extends Activity> mainActivityClass,
      @NonNull Class<? extends Service> toggleServiceClass,
      @NonNull PowerTriggerDB powerTriggerDB,
      @NonNull BooleanInterestObserver chargingObserver, @NonNull Logger triggerLogger,
      @NonNull BooleanInterestObserver wifiObserver, @NonNull BooleanInterestObserver dataObserver,
      @NonNull BooleanInterestObserver bluetoothObserver,
      @NonNull BooleanInterestObserver syncObserver, @NonNull BooleanInterestModifier wifiModifier,
      @NonNull BooleanInterestModifier dataModifier,
      @NonNull BooleanInterestModifier bluetoothModifier,
      @NonNull BooleanInterestModifier syncModifier) {
    super(preferences);
    this.jobQueuer = jobQueuer;
    appContext = context.getApplicationContext();
    this.toggleServiceClass = toggleServiceClass;
    this.powerTriggerDB = powerTriggerDB;
    this.chargingObserver = chargingObserver;
    this.triggerLogger = triggerLogger;
    this.wifiObserver = wifiObserver;
    this.dataObserver = dataObserver;
    this.bluetoothObserver = bluetoothObserver;
    this.syncObserver = syncObserver;
    this.wifiModifier = wifiModifier;
    this.dataModifier = dataModifier;
    this.bluetoothModifier = bluetoothModifier;
    this.syncModifier = syncModifier;

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
    jobQueuer.cancel(JobQueuer.TRIGGER_JOB_TAG);
    jobQueuer.queueTrigger(triggerPeriod, triggerLogger, powerTriggerDB, wifiObserver, dataObserver,
        bluetoothObserver, syncObserver, wifiModifier, dataModifier, bluetoothModifier,
        syncModifier, chargingObserver);
  }

  @Override public void destroy() {
    Timber.d("Cancel all trigger jobs");
    jobQueuer.cancel(JobQueuer.TRIGGER_JOB_TAG);
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
