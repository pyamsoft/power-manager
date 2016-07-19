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

package com.pyamsoft.powermanager.dagger.trigger;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.sql.PowerTriggerDB;
import com.pyamsoft.powermanager.dagger.base.BaseJob;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class TriggerJob extends BaseJob {

  public static final int PRIORITY = 2;
  @NonNull public static final String TRIGGER_TAG = "trigger";
  @NonNull private final Scheduler subscribeScheduler;
  @NonNull private final Scheduler mainScheduler;
  @NonNull private Subscription runSubscription = Subscriptions.empty();
  @NonNull private Subscription queueSubscription = Subscriptions.empty();

  public TriggerJob(long delay, @NonNull Scheduler subscribeScheduler,
      @NonNull Scheduler mainScheduler) {
    super(new Params(PRIORITY).setDelayMs(delay).addTags(TRIGGER_TAG));
    this.subscribeScheduler = subscribeScheduler;
    this.mainScheduler = mainScheduler;
  }

  @CheckResult @NonNull public static Observable<Boolean> queue(@NonNull TriggerJob job) {
    return Observable.defer(() -> {
      Timber.d("Cancel trigger jobs");
      PowerManager.getInstance().getJobManager().cancelJobs(TagConstraint.ANY, TRIGGER_TAG);

      Timber.d("Add new trigger job");
      PowerManager.getInstance().getJobManager().addJob(job);
      return Observable.just(true);
    }).subscribeOn(job.subscribeScheduler).observeOn(job.mainScheduler);
  }

  @Override public void onRun() throws Throwable {
    final IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    final Intent batteryIntent = getApplicationContext().registerReceiver(null, batteryFilter);

    // Get battery level
    int percent;
    if (batteryIntent != null) {
      Timber.d("Retrieve battery info");
      percent = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
    } else {
      Timber.d("Null battery intent");
      percent = 0;
    }

    Timber.d("Run trigger job for percent: %d", percent);
    runTriggerForPercent(percent);
  }

  private void runTriggerForPercent(int percent) {
    unsubRun();
    runSubscription = PowerTriggerDB.with(getApplicationContext())
        .queryWithPercent(percent)
        .first()
        .filter(entry -> {
          Timber.d("Filter empty triggers");
          return !PowerTriggerEntry.isEmpty(entry);
        })
        .filter(entry -> {
          Timber.d("Filter disabled triggers");
          return entry.enabled();
        })
        .subscribeOn(subscribeScheduler)
        .observeOn(mainScheduler)
        .subscribe(entry -> {
          Timber.d("Run trigger for entry name: %s", entry.name());
          Timber.d("Run trigger for entry percent: %d", entry.percent());
          Timber.d("Requeue the job");

          // KLUDGE nested subs are ugly
          unsubQueue();
          queueSubscription =
              queue(new TriggerJob(getDelayInMs(), subscribeScheduler, mainScheduler)).subscribe(
                  aBoolean -> {
                    Timber.d("New job queued");
                  }, throwable -> {
                    Timber.e(throwable, "onError");
                  });
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  private void unsubRun() {
    if (!runSubscription.isUnsubscribed()) {
      runSubscription.unsubscribe();
    }
  }

  private void unsubQueue() {
    if (!queueSubscription.isUnsubscribed()) {
      queueSubscription.unsubscribe();
    }
  }

  @Override protected void onCancelHook() {
    unsubQueue();
    unsubRun();
  }
}
