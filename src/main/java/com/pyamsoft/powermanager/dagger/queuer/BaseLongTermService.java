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

package com.pyamsoft.powermanager.dagger.queuer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.wrapper.JobQueuerWrapper;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import timber.log.Timber;

public abstract class BaseLongTermService extends Service {

  @NonNull static final String EXTRA_JOB_TYPE = "extra_job_queue_type";
  @NonNull static final String EXTRA_IGNORE_CHARGING = "extra_ignore_charging";
  @NonNull static final String EXTRA_PERIODIC = "extra_periodic";
  @NonNull static final String EXTRA_PERIODIC_ENABLE = "extra_periodic_enable";
  @NonNull static final String EXTRA_PERIODIC_DISABLE = "extra_periodic_disable";
  @Nullable Subscription runSubscription;

  @Inject @Named("sub") Scheduler subScheduler;
  @Inject @Named("obs_charging_state") BooleanInterestObserver chargingObserver;
  @Inject JobQueuerWrapper jobQueuerWrapper;

  @CheckResult @NonNull
  static Intent buildIntent(@NonNull Intent intent, @NonNull QueuerType type, int ignoreCharging,
      int periodic, long periodicEnableTime, long periodicDisableTime) {
    intent.putExtra(BaseLongTermService.EXTRA_IGNORE_CHARGING, ignoreCharging);
    intent.putExtra(BaseLongTermService.EXTRA_JOB_TYPE, type.name());
    intent.putExtra(BaseLongTermService.EXTRA_PERIODIC, periodic);
    intent.putExtra(BaseLongTermService.EXTRA_PERIODIC_ENABLE, periodicEnableTime);
    intent.putExtra(BaseLongTermService.EXTRA_PERIODIC_DISABLE, periodicDisableTime);
    return intent;
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

    final String type = intent.getStringExtra(EXTRA_JOB_TYPE);
    if (type == null) {
      getLogger().e("QueuerType extra is NULL. Skip");
      return START_NOT_STICKY;
    }

    final int ignoreCharging = intent.getIntExtra(EXTRA_IGNORE_CHARGING, -1);
    if (ignoreCharging < 0) {
      getLogger().e("Ignore Charging was not passed with Intent. Skip");
      return START_NOT_STICKY;
    }

    final int periodic = intent.getIntExtra(EXTRA_PERIODIC, -1);
    if (periodic < 0) {
      getLogger().e("Periodic was not passed with Intent. Skip");
      return START_NOT_STICKY;
    }

    final long periodicEnableTime = intent.getLongExtra(EXTRA_PERIODIC_ENABLE, -1);
    if (periodicEnableTime < 60) {
      getLogger().e("Periodic Enable time was not passed with Intent. Skip");
      return START_NOT_STICKY;
    }

    final long periodicDisableTime = intent.getLongExtra(EXTRA_PERIODIC_DISABLE, -1);
    if (periodicDisableTime < 60) {
      getLogger().e("Periodic Disable time was not passed with Intent. Skip");
      return START_NOT_STICKY;
    }

    getLogger().d("Run long queue job");
    runJob(intent, QueuerType.valueOf(type), ignoreCharging, periodic, periodicEnableTime,
        periodicDisableTime);
    return START_NOT_STICKY;
  }

  private void runJob(@NonNull Intent originalIntent, @NonNull QueuerType queuerType,
      int ignoreCharging, int periodic, long periodicEnableTime, long periodicDisableTime) {
    SubscriptionHelper.unsubscribe(runSubscription);
    runSubscription = Observable.defer(() -> {
      QueueRunner.builder()
          .setJobQueueWrapper(jobQueuerWrapper)
          .setType(queuerType)
          .setObserver(getStateObserver())
          .setModifier(getStateModifier())
          .setCharging(chargingObserver)
          .setIgnoreCharging(ignoreCharging)
          .setLogger(getLogger())
          .setPeriodic(periodic)
          .setPeriodicEnableTime(periodicEnableTime)
          .setPeriodicDisableTime(periodicDisableTime)
          .setIntent(new Intent(originalIntent))
          .build()
          .run();
      return Observable.just(true);
    })
        .subscribeOn(subScheduler)
        .observeOn(subScheduler)
        .subscribe(ignore -> Timber.d("Finished running long job"),
            throwable -> Timber.e(throwable, "onError runJob"), () -> {
              SubscriptionHelper.unsubscribe(runSubscription);
              stopSelf();
            });
  }

  @Override public void onDestroy() {
    super.onDestroy();
    SubscriptionHelper.unsubscribe(runSubscription);
  }

  private void inject() {
    Injector.get().provideComponent().plusQueuerComponent().inject(this);
    injectDependencies();
  }

  @CheckResult @NonNull abstract Logger getLogger();

  @CheckResult @Named abstract BooleanInterestObserver getStateObserver();

  @CheckResult @Named abstract BooleanInterestModifier getStateModifier();

  abstract void injectDependencies();
}
