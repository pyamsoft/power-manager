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

package com.pyamsoft.powermanager.manager.queuer;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.base.Injector;
import com.pyamsoft.powermanager.base.logger.Logger;
import com.pyamsoft.powermanager.base.wrapper.JobQueuerWrapper;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.QueuerType;
import javax.inject.Inject;
import javax.inject.Named;

abstract class BaseLongTermService extends Service {

  @NonNull private static final String EXTRA_JOB_TYPE = "extra_job_queue_type";
  @NonNull private static final String EXTRA_IGNORE_CHARGING = "extra_ignore_charging";
  @NonNull private static final String EXTRA_PERIODIC = "extra_periodic";
  @NonNull private static final String EXTRA_PERIODIC_ENABLE = "extra_periodic_enable";
  @NonNull private static final String EXTRA_PERIODIC_DISABLE = "extra_periodic_disable";
  @Inject @Named("obs_charging_state") BooleanInterestObserver chargingObserver;

  @CheckResult @NonNull
  static Bundle buildExtrasBundle(@NonNull QueuerType type, int ignoreCharging, int periodic,
      long periodicEnableTime, long periodicDisableTime) {
    final Bundle bundle = new Bundle();
    bundle.putInt(BaseLongTermService.EXTRA_IGNORE_CHARGING, ignoreCharging);
    bundle.putString(BaseLongTermService.EXTRA_JOB_TYPE, type.name());
    bundle.putInt(BaseLongTermService.EXTRA_PERIODIC, periodic);
    bundle.putLong(BaseLongTermService.EXTRA_PERIODIC_ENABLE, periodicEnableTime);
    bundle.putLong(BaseLongTermService.EXTRA_PERIODIC_DISABLE, periodicDisableTime);
    return bundle;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    inject();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent == null) {
      getLogger().e("Intent is NULL. Skip");
      return START_NOT_STICKY;
    }

    final Bundle extras = intent.getBundleExtra(JobQueuerWrapper.JOB_EXTRAS);
    if (extras == null) {
      getLogger().e("No extras passed with intent");
      return START_NOT_STICKY;
    }

    final String type = extras.getString(EXTRA_JOB_TYPE);
    if (type == null) {
      getLogger().e("QueuerType extra is unset. Skip");
      return START_NOT_STICKY;
    }

    final int ignoreCharging = extras.getInt(EXTRA_IGNORE_CHARGING, -1);
    if (ignoreCharging < 0) {
      getLogger().e("Ignore Charging was not passed with Intent. Skip");
      return START_NOT_STICKY;
    }

    final int periodic = extras.getInt(EXTRA_PERIODIC, -1);
    if (periodic < 0) {
      getLogger().e("Periodic was not passed with Intent. Skip");
      return START_NOT_STICKY;
    }

    final long periodicEnableTime = extras.getLong(EXTRA_PERIODIC_ENABLE, -1);
    if (periodicEnableTime < 60) {
      getLogger().e("Periodic Enable time was not passed with Intent. Skip");
      return START_NOT_STICKY;
    }

    final long periodicDisableTime = extras.getLong(EXTRA_PERIODIC_DISABLE, -1);
    if (periodicDisableTime < 60) {
      getLogger().e("Periodic Disable time was not passed with Intent. Skip");
      return START_NOT_STICKY;
    }

    final QueuerType queuerType = QueuerType.valueOf(type);
    run(queuerType, ignoreCharging);
    requeue(queuerType, ignoreCharging, periodic, periodicEnableTime, periodicDisableTime);
    return START_NOT_STICKY;
  }

  private void run(@NonNull QueuerType queuerType, int ignoreCharging) {
    final HandlerThread handlerThread =
        new HandlerThread("Long Queue: " + queuerType.name(), Process.THREAD_PRIORITY_BACKGROUND);
    handlerThread.start();

    final Handler backgroundHandler = new Handler(handlerThread.getLooper());
    backgroundHandler.post(() -> {
      getLogger().d("Run long term job: %s", queuerType);
      QueueRunner.builder()
          .setType(queuerType)
          .setObserver(getStateObserver())
          .setModifier(getStateModifier())
          .setCharging(chargingObserver)
          .setIgnoreCharging(ignoreCharging)
          .setLogger(getLogger())
          .build()
          .run();

      handlerThread.quitSafely();
      stopSelf();
    });
  }

  private void requeue(@NonNull QueuerType queuerType, int ignoreCharging, int periodic,
      long periodicEnableTime, long periodicDisableTime) {
    final QueuerType newType = queuerType.flip();
    final long newDelayTime;
    if (newType == QueuerType.SCREEN_ON_ENABLE || newType == QueuerType.SCREEN_ON_DISABLE) {
      newDelayTime = periodicDisableTime * 1000L;
    } else {
      newDelayTime = periodicEnableTime * 1000L;
    }

    getQueuer().cancel();
    getQueuer().setType(newType)
        .setDelayTime(newDelayTime)
        .setIgnoreCharging(ignoreCharging == 1)
        .setPeriodic(periodic == 1)
        .setPeriodicEnableTime(periodicEnableTime)
        .setPeriodicDisableTime(periodicDisableTime)
        .queue();
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  private void inject() {
    DaggerQueuerComponent.builder()
        .powerManagerComponent(Injector.get().provideComponent())
        .build()
        .inject(this);
    injectDependencies();
  }

  @CheckResult @NonNull abstract Logger getLogger();

  @CheckResult @Named abstract BooleanInterestObserver getStateObserver();

  @CheckResult @Named abstract BooleanInterestModifier getStateModifier();

  @CheckResult @Named abstract Queuer getQueuer();

  @CheckResult @NonNull abstract Class<? extends BaseLongTermService> getScreenOnServiceClass();

  @CheckResult @NonNull abstract Class<? extends BaseLongTermService> getScreenOffServiceClass();

  abstract void injectDependencies();
}
