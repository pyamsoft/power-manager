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

package com.pyamsoft.powermanager.dagger.job;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import com.pyamsoft.powermanager.app.wrapper.PowerTriggerDB;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import java.util.List;
import java.util.Locale;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class TriggerJob extends BaseJob {

  @NonNull public static final String TRIGGER_TAG = "trigger";
  private static final int PRIORITY = 2;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerTriggerDB powerTriggerDB;
  @NonNull private final BooleanInterestObserver wifiObserver;
  @NonNull private final BooleanInterestObserver dataObserver;
  @NonNull private final BooleanInterestObserver bluetoothObserver;
  @NonNull private final BooleanInterestObserver syncObserver;
  @NonNull private final BooleanInterestModifier wifiModifier;
  @NonNull private final BooleanInterestModifier dataModifier;
  @NonNull private final BooleanInterestModifier bluetoothModifier;
  @NonNull private final BooleanInterestModifier syncModifier;
  @NonNull private final JobSchedulerCompat jobSchedulerCompat;
  @NonNull private final PowerManagerPreferences preferences;
  @SuppressWarnings("WeakerAccess") @NonNull Subscription runSubscription = Subscriptions.empty();

  TriggerJob(long delay, @NonNull BooleanInterestObserver wifiObserver,
      @NonNull BooleanInterestObserver dataObserver,
      @NonNull BooleanInterestObserver bluetoothObserver,
      @NonNull BooleanInterestObserver syncObserver, @NonNull BooleanInterestModifier wifiModifier,
      @NonNull BooleanInterestModifier dataModifier,
      @NonNull BooleanInterestModifier bluetoothModifier,
      @NonNull BooleanInterestModifier syncModifier, @NonNull JobSchedulerCompat jobSchedulerCompat,
      @NonNull PowerTriggerDB powerTriggerDB, @NonNull PowerManagerPreferences preferences) {
    super(new Params(PRIORITY).setDelayMs(delay).addTags(TRIGGER_TAG));
    this.wifiObserver = wifiObserver;
    this.dataObserver = dataObserver;
    this.bluetoothObserver = bluetoothObserver;
    this.syncObserver = syncObserver;
    this.wifiModifier = wifiModifier;
    this.dataModifier = dataModifier;
    this.bluetoothModifier = bluetoothModifier;
    this.syncModifier = syncModifier;
    this.jobSchedulerCompat = jobSchedulerCompat;
    this.powerTriggerDB = powerTriggerDB;
    this.preferences = preferences;
  }

  @Override public void onRun() throws Throwable {
    final IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    final Intent batteryIntent = getApplicationContext().registerReceiver(null, batteryFilter);

    // Get battery level
    int percent;
    boolean charging;
    if (batteryIntent != null) {
      Timber.d("Retrieve battery info");
      percent = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
      final int state = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS,
          BatteryManager.BATTERY_STATUS_UNKNOWN);
      charging = (state == BatteryManager.BATTERY_STATUS_CHARGING
          || state == BatteryManager.BATTERY_STATUS_FULL);
    } else {
      Timber.d("Null battery intent");
      percent = 0;
      charging = false;
    }

    Timber.d("Run trigger job for percent: %d", percent);
    runTriggerForPercent(percent, charging);
  }

  private void runTriggerForPercent(int percent, boolean charging) {
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
    if (charging) {
      powerTriggerEntryObservable =
          triggerQuery.flatMap(new Func1<List<PowerTriggerEntry>, Observable<Integer>>() {
            @Override public Observable<Integer> call(List<PowerTriggerEntry> powerTriggerEntries) {
              // Not final so we can call merges on it
              Observable<Integer> updateTriggerResult = Observable.empty();

              Timber.i("We are charging, mark any available triggers");
              for (final PowerTriggerEntry entry : powerTriggerEntries) {
                Timber.d("Current entry: %s %d", entry.name(), entry.percent());
                if (entry.percent() <= percent && !entry.available()) {
                  Timber.d("Mark entry available for percent: %d", entry.percent());
                  final PowerTriggerEntry updated = PowerTriggerEntry.updatedAvailable(entry, true);
                  final ContentValues values = PowerTriggerEntry.asContentValues(updated);
                  updateTriggerResult = updateTriggerResult.mergeWith(
                      powerTriggerDB.update(values, updated.percent()));
                }
              }

              return updateTriggerResult;
            }
            // Convert to list so that we interate over all the triggers we have found instead of just first
          }).toList().first().map(integers -> {
            Timber.d("Number of values marked available: %d", integers.size());
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
      }).flatMap(new Func1<PowerTriggerEntry, Observable<PowerTriggerEntry>>() {
        @Override public Observable<PowerTriggerEntry> call(PowerTriggerEntry entry) {
          final Observable<PowerTriggerEntry> updateTriggerResult;
          if (!PowerTriggerEntry.isEmpty(entry)) {
            Timber.d("Mark trigger as unavailable: %s %d", entry.name(), entry.percent());
            final PowerTriggerEntry updated = PowerTriggerEntry.updatedAvailable(entry, false);
            final ContentValues values = PowerTriggerEntry.asContentValues(updated);
            updateTriggerResult = powerTriggerDB.update(values, updated.percent()).map(integer -> {
              Timber.d("Updated trigger: (%d) %s", integer, updated);
              return updated;
            });
          } else {
            Timber.w("No trigger marked, EMPTY result");
            updateTriggerResult = Observable.empty();
          }

          return updateTriggerResult;
        }
      });
    }

    SubscriptionHelper.unsubscribe(runSubscription);
    runSubscription = powerTriggerEntryObservable.subscribe(entry -> {
          if (charging) {
            Timber.w("Do not run Trigger because device is charging");
          } else if (PowerTriggerEntry.isEmpty(entry)) {
            Timber.w("Do not run Trigger because entry specified is EMPTY");
          } else {
            onTriggerRun(entry);
          }

          requeueJob();
        }, throwable -> Timber.e(throwable, "onError"),
        () -> SubscriptionHelper.unsubscribe(runSubscription));
  }

  @SuppressWarnings("WeakerAccess") void requeueJob() {
    Timber.d("Requeue the trigger job");
    JobHelper.queueTriggerJob(jobSchedulerCompat, wifiObserver, dataObserver, bluetoothObserver,
        syncObserver, wifiModifier, dataModifier, bluetoothModifier, syncModifier, powerTriggerDB,
        preferences);
  }

  @SuppressWarnings("WeakerAccess") void onTriggerRun(@NonNull PowerTriggerEntry entry) {
    Timber.d("Run trigger for entry name: %s", entry.name());
    Timber.d("Run trigger for entry percent: %d", entry.percent());
    final String formatted =
        String.format(Locale.getDefault(), "Run trigger: %s [%d]", entry.name(), entry.percent());
    Toast.makeText(getApplicationContext(), formatted, Toast.LENGTH_SHORT).show();

    if (entry.toggleWifi()) {
      Timber.d("Wifi should toggle");
      if (entry.enableWifi()) {
        Timber.d("Wifi should enable");
        if (!wifiObserver.is()) {
          wifiModifier.set();
        }
      } else {
        Timber.d("Wifi should disable");
        if (wifiObserver.is()) {
          wifiModifier.unset();
        }
      }
    }

    if (entry.toggleData()) {
      Timber.d("Data should toggle");
      if (entry.enableData()) {
        Timber.d("Data should enable");
        if (!dataObserver.is()) {
          dataModifier.set();
        }
      } else {
        Timber.d("Data should disable");
        if (dataObserver.is()) {
          dataModifier.unset();
        }
      }
    }

    if (entry.toggleBluetooth()) {
      Timber.d("Bluetooth should toggle");
      if (entry.enableBluetooth()) {
        Timber.d("Bluetooth should enable");
        if (!bluetoothObserver.is()) {
          bluetoothModifier.set();
        }
      } else {
        Timber.d("Bluetooth should disable");
        if (bluetoothObserver.is()) {
          bluetoothModifier.unset();
        }
      }
    }

    if (entry.toggleSync()) {
      Timber.d("Sync should toggle");
      if (entry.enableSync()) {
        Timber.d("Sync should enable");
        if (!syncObserver.is()) {
          syncModifier.set();
        }
      } else {
        Timber.d("Sync should disable");
        if (syncObserver.is()) {
          syncModifier.unset();
        }
      }
    }
  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    super.onCancel(cancelReason, throwable);
    SubscriptionHelper.unsubscribe(runSubscription);
  }
}
