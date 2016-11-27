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
  @Nullable Subscription runSubscription;

  @Inject @Named("sub") Scheduler subScheduler;
  @Inject @Named("obs_charging_state") BooleanInterestObserver chargingObserver;
  @Inject JobQueuerWrapper jobQueuerWrapper;

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

    final QueuerType queuerType = QueuerType.valueOf(type);
    getLogger().d("Run long queue job: %s", getJobTag());

    runJob(queuerType, ignoreCharging);
    return START_NOT_STICKY;
  }

  private void runJob(@NonNull QueuerType queuerType, int ignoreCharging) {
    SubscriptionHelper.unsubscribe(runSubscription);
    runSubscription = Observable.defer(() -> {
      QueueRunner.run(getJobTag(), queuerType, getStateObserver(), getStateModifier(),
          chargingObserver, getLogger(), ignoreCharging);
      return Observable.just(true);
    })
        .subscribeOn(subScheduler)
        .observeOn(subScheduler)
        .subscribe(ignore -> Timber.d("Finished running long job: %s", getJobTag()),
            throwable -> Timber.e(throwable, "onError runJob %s", getJobTag()), () -> {
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

  @CheckResult @NonNull abstract String getJobTag();

  @CheckResult @NonNull abstract Logger getLogger();

  @CheckResult @Named abstract BooleanInterestObserver getStateObserver();

  @CheckResult @Named abstract BooleanInterestModifier getStateModifier();

  abstract void injectDependencies();
}
