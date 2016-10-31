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
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManagerSingleInitProvider;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.wrapper.JobSchedulerCompat;
import com.pyamsoft.powermanager.app.wrapper.PowerTriggerDB;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class TriggerJob extends BaseJob {

  @NonNull public static final String TRIGGER_TAG = "trigger";
  private static final int PRIORITY = 2;
  @Inject @Named("obs_wifi_state") BooleanInterestObserver wifiObserver;
  @Inject @Named("obs_data_state") BooleanInterestObserver dataObserver;
  @Inject @Named("obs_bluetooth_state") BooleanInterestObserver bluetoothObserver;
  @Inject @Named("obs_sync_state") BooleanInterestObserver syncObserver;

  @Inject @Named("mod_wifi_state") BooleanInterestModifier wifiModifier;
  @Inject @Named("mod_data_state") BooleanInterestModifier dataModifier;
  @Inject @Named("mod_bluetooth_state") BooleanInterestModifier bluetoothModifier;
  @Inject @Named("mod_sync_state") BooleanInterestModifier syncModifier;
  @Inject JobSchedulerCompat jobSchedulerCompat;
  @Inject PowerTriggerDB powerTriggerDB;
  @NonNull private Subscription runSubscription = Subscriptions.empty();

  public TriggerJob(long delay) {
    super(new Params(PRIORITY).setDelayMs(delay).addTags(TRIGGER_TAG));
  }

  public static void queue(@NonNull JobSchedulerCompat jobManager, @NonNull TriggerJob job) {
    Timber.d("Cancel trigger jobs");
    jobManager.cancelJobsInBackground(TagConstraint.ANY, TRIGGER_TAG);

    Timber.d("Add new trigger job");
    jobManager.addJobInBackground(job);
  }

  @Override public void onAdded() {
    super.onAdded();
    PowerManagerSingleInitProvider.get().provideComponent().plusTriggerJobComponent().inject(this);
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
    unsubRun();
    runSubscription = powerTriggerDB.queryAll().first().flatMap(powerTriggerEntries -> {
      Timber.d("Flatten and filter");
      return Observable.from(powerTriggerEntries);
    }).filter(entry -> {
      Timber.d("Filter empty triggers");
      return !PowerTriggerEntry.isEmpty(entry);
    })
        // KLUDGE Entries do not implement comparable
        .toSortedList((entry, entry2) -> {
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
        }).flatMap(powerTriggerEntries -> {
          // KLUDGE this is really bad. Is there another way to update in the background?
          Observable<Integer> updatedAvailability = Observable.just(-1);
          PowerTriggerEntry trigger = PowerTriggerEntry.empty();

          if (charging) {
            Timber.d("Mark any available triggers");
            for (final PowerTriggerEntry entry : powerTriggerEntries) {
              Timber.d("Current entry: %s %d", entry.name(), entry.percent());
              if (entry.percent() <= percent && !entry.available()) {
                Timber.d("Mark entry available for percent: %d", entry.percent());
                final PowerTriggerEntry updated = PowerTriggerEntry.updatedAvailable(entry, true);
                final ContentValues values = PowerTriggerEntry.asContentValues(updated);
                updatedAvailability =
                    updatedAvailability.mergeWith(powerTriggerDB.update(values, updated.percent()));
              }
            }
          } else {
            Timber.d("Select best trigger from available");
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

            if (!PowerTriggerEntry.isEmpty(best)) {
              Timber.d("Mark trigger as unavailable: %s %d", best.name(), best.percent());
              final PowerTriggerEntry updated = PowerTriggerEntry.updatedAvailable(best, false);
              final ContentValues values = PowerTriggerEntry.asContentValues(updated);
              updatedAvailability = powerTriggerDB.update(values, updated.percent());
              trigger = updated;
            }
          }

          // KLUDGE just java things
          Timber.d("Finalize trigger so we can kludge");
          final PowerTriggerEntry passOn = trigger;
          return updatedAvailability.toSortedList().first().map(list -> {
            // KLUDGE this is terrible
            Timber.d("Do terrible kludge");
            return passOn;
          });
        })
        // KLUDGE hardcoded schedulers
        // KLUDGE need to subscribe even if we are noop so that the other operations run
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(entry -> {
          if (!charging && !PowerTriggerEntry.isEmpty(entry)) {
            onTriggerRun(entry);
          } else {
            Timber.e("Can't run trigger. Either device is charging or no valid trigger");
          }

          // KLUDGE the show must go on
          Timber.d("Requeue the job");
          queue(jobSchedulerCompat, new TriggerJob(getDelayInMs()));
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  private void onTriggerRun(PowerTriggerEntry entry) {
    Timber.d("Run trigger for entry name: %s", entry.name());
    Timber.d("Run trigger for entry percent: %d", entry.percent());
    final String formatted =
        String.format(Locale.US, "Run trigger: %s [%d]", entry.name(), entry.percent());
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

  private void unsubRun() {
    if (!runSubscription.isUnsubscribed()) {
      runSubscription.unsubscribe();
    }
  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    super.onCancel(cancelReason, throwable);
    unsubRun();
  }
}
