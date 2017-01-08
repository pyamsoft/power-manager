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

package com.pyamsoft.powermanager.base.jobs;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.pyamsoft.powermanager.base.db.PowerTriggerDB;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.Logger;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.rx.SubscriptionHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

class TriggerJob extends Job {

  @SuppressWarnings("WeakerAccess") @NonNull final PowerTriggerDB powerTriggerDB;
  @SuppressWarnings("WeakerAccess") @NonNull final BooleanInterestObserver chargingObserver;
  @NonNull private final JobQueuer jobQueuer;
  @NonNull private final Logger logger;
  @NonNull private final BooleanInterestObserver wifiObserver;
  @NonNull private final BooleanInterestObserver dataObserver;
  @NonNull private final BooleanInterestObserver bluetoothObserver;
  @NonNull private final BooleanInterestObserver syncObserver;
  @NonNull private final BooleanInterestModifier wifiModifier;
  @NonNull private final BooleanInterestModifier dataModifier;
  @NonNull private final BooleanInterestModifier bluetoothModifier;
  @NonNull private final BooleanInterestModifier syncModifier;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription runSubscription;

  TriggerJob(long delayTime, @NonNull JobQueuer jobQueuer, @NonNull Logger logger,
      @NonNull PowerTriggerDB powerTriggerDB, @NonNull BooleanInterestObserver wifiObserver,
      @NonNull BooleanInterestObserver dataObserver,
      @NonNull BooleanInterestObserver bluetoothObserver,
      @NonNull BooleanInterestObserver syncObserver, @NonNull BooleanInterestModifier wifiModifier,
      @NonNull BooleanInterestModifier dataModifier,
      @NonNull BooleanInterestModifier bluetoothModifier,
      @NonNull BooleanInterestModifier syncModifier,
      @NonNull BooleanInterestObserver chargingObserver) {
    super(new Params(2).setDelayMs(delayTime)
        .addTags(JobQueuer.ALL_JOB_TAG, JobQueuer.TRIGGER_JOB_TAG)
        .setRequiresNetwork(false)
        .setRequiresUnmeteredNetwork(false));
    this.jobQueuer = jobQueuer;
    this.logger = logger;
    this.powerTriggerDB = powerTriggerDB;
    this.wifiObserver = wifiObserver;
    this.dataObserver = dataObserver;
    this.bluetoothObserver = bluetoothObserver;
    this.syncObserver = syncObserver;
    this.wifiModifier = wifiModifier;
    this.dataModifier = dataModifier;
    this.bluetoothModifier = bluetoothModifier;
    this.syncModifier = syncModifier;
    this.chargingObserver = chargingObserver;
  }

  private void runTriggerForPercent(int percent) {
    final Observable<List<PowerTriggerEntry>> triggerQuery =
        powerTriggerDB.queryAll().first().flatMap(powerTriggerEntries -> {
          Timber.d("Flatten power triggers");
          return Observable.from(powerTriggerEntries);
        }).filter(entry -> {
          Timber.d("Filter empty power triggers");
          return !PowerTriggerEntry.isEmpty(entry);
        }).toSortedList((entry, entry2) -> {
          Timber.d("Sort entries");
          final int p1 = entry.percent();
          final int p2 = entry2.percent();

          if (p1 < p2) {
            return -1;
          } else if (p1 > p2) {
            return 1;
          } else {
            return 0;
          }
        });

    final Observable<PowerTriggerEntry> powerTriggerEntryObservable;
    if (chargingObserver.is()) {
      powerTriggerEntryObservable = triggerQuery.flatMap(powerTriggerEntries -> {
        // Not final so we can call merges on it
        Observable<Integer> updateTriggerResult = Observable.just(-1);

        Timber.i("We are charging, mark any available triggers");
        for (final PowerTriggerEntry entry : powerTriggerEntries) {
          Timber.d("Current entry: %s %d", entry.name(), entry.percent());
          if (entry.percent() <= percent && !entry.available()) {
            Timber.d("Mark entry available for percent: %d", entry.percent());
            updateTriggerResult = updateTriggerResult.mergeWith(
                powerTriggerDB.updateAvailable(true, entry.percent()));
          }
        }

        return updateTriggerResult;
        // Convert to list so that we iterate over all the triggers we have found instead of just first
      }).toList().first().map(integers -> {
        Timber.d("Number of values marked available: %d", integers.size() - 1);
        Timber.d("Return an empty trigger");
        return PowerTriggerEntry.empty();
      });
    } else {
      powerTriggerEntryObservable = triggerQuery.map(powerTriggerEntries -> {
        Timber.i("Not charging, select best available trigger");
        PowerTriggerEntry best = PowerTriggerEntry.empty();

        for (final PowerTriggerEntry entry : powerTriggerEntries) {
          Timber.d("Current entry: %s %d", entry.name(), entry.percent());
          if (entry.available()
              && entry.enabled()
              && entry.percent() >= percent
              && entry.percent() <= percent + 5) {
            if (PowerTriggerEntry.isEmpty(best)) {
              Timber.d("Mark first valid entry as best");
              best = entry;
            }

            if (entry.percent() < best.percent()) {
              Timber.d("Mark current entry as new best");
              best = entry;
            }

            if (best.percent() == percent) {
              Timber.d("Found exact");
              break;
            }
          }
        }

        return best;
      }).flatMap(entry -> {
        final Observable<PowerTriggerEntry> updateTriggerResult;
        if (!PowerTriggerEntry.isEmpty(entry)) {
          Timber.d("Mark trigger as unavailable: %s %d", entry.name(), entry.percent());
          updateTriggerResult =
              powerTriggerDB.updateAvailable(entry.available(), entry.percent()).map(integer -> {
                Timber.d("Updated trigger: (%d) unavailable", integer);
                return entry;
              });
        } else {
          Timber.w("No trigger marked, EMPTY result");
          updateTriggerResult = Observable.empty();
        }

        return updateTriggerResult;
      });
    }

    SubscriptionHelper.unsubscribe(runSubscription);
    runSubscription = powerTriggerEntryObservable.subscribe(entry -> {
          if (chargingObserver.is()) {
            Timber.w("Do not run Trigger because device is charging");
          } else if (PowerTriggerEntry.isEmpty(entry)) {
            Timber.w("Do not run Trigger because entry specified is EMPTY");
          } else {
            onTriggerRun(entry);
          }
        }, throwable -> Timber.e(throwable, "onError"),
        () -> SubscriptionHelper.unsubscribe(runSubscription));
  }

  @SuppressWarnings("WeakerAccess") void onTriggerRun(@NonNull PowerTriggerEntry entry) {
    logger.d("Run trigger for entry name: %s", entry.name());
    logger.d("Run trigger for entry percent: %d", entry.percent());
    final String formatted =
        String.format(Locale.getDefault(), "Run trigger: %s [%d]", entry.name(), entry.percent());
    Toast.makeText(getApplicationContext(), formatted, Toast.LENGTH_SHORT).show();

    if (entry.toggleWifi()) {
      Timber.d("Wifi should toggle");
      if (entry.enableWifi()) {
        Timber.d("Wifi should enable");
        if (!wifiObserver.is()) {
          logger.i("Trigger job: %s set wifi", entry.name());
          wifiModifier.set();
        }
      } else {
        Timber.d("Wifi should disable");
        if (wifiObserver.is()) {
          logger.i("Trigger job: %s unset wifi", entry.name());
          wifiModifier.unset();
        }
      }
    }

    if (entry.toggleData()) {
      Timber.d("Data should toggle");
      if (entry.enableData()) {
        Timber.d("Data should enable");
        if (!dataObserver.is()) {
          logger.i("Trigger job: %s set data", entry.name());
          dataModifier.set();
        }
      } else {
        Timber.d("Data should disable");
        if (dataObserver.is()) {
          logger.i("Trigger job: %s unset data", entry.name());
          dataModifier.unset();
        }
      }
    }

    if (entry.toggleBluetooth()) {
      Timber.d("Bluetooth should toggle");
      if (entry.enableBluetooth()) {
        Timber.d("Bluetooth should enable");
        if (!bluetoothObserver.is()) {
          logger.i("Trigger job: %s set bluetooth", entry.name());
          bluetoothModifier.set();
        }
      } else {
        Timber.d("Bluetooth should disable");
        if (bluetoothObserver.is()) {
          logger.i("Trigger job: %s set bluetooth", entry.name());
          bluetoothModifier.unset();
        }
      }
    }

    if (entry.toggleSync()) {
      Timber.d("Sync should toggle");
      if (entry.enableSync()) {
        Timber.d("Sync should enable");
        if (!syncObserver.is()) {
          logger.i("Trigger job: %s set sync", entry.name());
          syncModifier.set();
        }
      } else {
        Timber.d("Sync should disable");
        if (syncObserver.is()) {
          logger.i("Trigger job: %s unset sync", entry.name());
          syncModifier.unset();
        }
      }
    }
  }

  @CheckResult @NonNull private String getJobTagString() {
    final String tagString;
    final Set<String> tags = getTags();
    if (tags == null) {
      tagString = "[NO TAGS]";
    } else {
      tagString = Arrays.toString(tags.toArray());
    }
    return tagString;
  }

  @CallSuper @Override public void onAdded() {
    logger.d("Added job with tags: %s, delay: %d", getJobTagString(), getDelayInMs());
  }

  @Override public void onRun() throws Throwable {
    final IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    final Intent batteryIntent = getApplicationContext().registerReceiver(null, batteryFilter);

    // Get battery level
    final int percent;
    if (batteryIntent != null) {
      Timber.d("Retrieve battery info");
      percent = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
    } else {
      Timber.d("Null battery intent");
      percent = 0;
    }

    logger.d("Run trigger job for percent: %d", percent);
    runTriggerForPercent(percent);
    requeueJob();
  }

  private void requeueJob() {
    jobQueuer.cancel(JobQueuer.TRIGGER_JOB_TAG);
    jobQueuer.queueTrigger(getDelayInMs(), logger, powerTriggerDB, wifiObserver, dataObserver,
        bluetoothObserver, syncObserver, wifiModifier, dataModifier, bluetoothModifier,
        syncModifier, chargingObserver);
  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    SubscriptionHelper.unsubscribe(runSubscription);
  }

  @Override
  protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount,
      int maxRunCount) {
    return RetryConstraint.CANCEL;
  }
}
