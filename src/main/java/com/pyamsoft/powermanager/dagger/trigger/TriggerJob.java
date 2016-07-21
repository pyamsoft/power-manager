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

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.sql.PowerTriggerDB;
import com.pyamsoft.powermanager.dagger.base.BaseJob;
import com.pyamsoft.powermanager.dagger.modifier.state.BluetoothStateModifier;
import com.pyamsoft.powermanager.dagger.modifier.state.DaggerStateModifierComponent;
import com.pyamsoft.powermanager.dagger.modifier.state.DataStateModifier;
import com.pyamsoft.powermanager.dagger.modifier.state.SyncStateModifier;
import com.pyamsoft.powermanager.dagger.modifier.state.WifiStateModifier;
import com.pyamsoft.powermanager.dagger.observer.state.BluetoothStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.DataStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.SyncStateObserver;
import com.pyamsoft.powermanager.dagger.observer.state.WifiStateObserver;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class TriggerJob extends BaseJob {

  public static final int PRIORITY = 2;
  @NonNull public static final String TRIGGER_TAG = "trigger";
  @Inject WifiStateObserver wifiObserver;
  @Inject DataStateObserver dataObserver;
  @Inject BluetoothStateObserver bluetoothObserver;
  @Inject SyncStateObserver syncObserver;

  @Inject WifiStateModifier wifiModifier;
  @Inject DataStateModifier dataModifier;
  @Inject BluetoothStateModifier bluetoothModifier;
  @Inject SyncStateModifier syncModifier;
  @NonNull private Subscription runSubscription = Subscriptions.empty();

  public TriggerJob(long delay) {
    super(new Params(PRIORITY).setDelayMs(delay).addTags(TRIGGER_TAG));

    DaggerStateModifierComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build()
        .inject(this);
  }

  public static void queue(@NonNull TriggerJob job) {
    Timber.d("Cancel trigger jobs");
    PowerManager.getInstance()
        .getJobManager()
        .cancelJobsInBackground(null, TagConstraint.ANY, TRIGGER_TAG);

    Timber.d("Add new trigger job");
    PowerManager.getInstance().getJobManager().addJobInBackground(job, null);
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
      charging = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS,
          BatteryManager.BATTERY_STATUS_UNKNOWN) == (BatteryManager.BATTERY_STATUS_CHARGING
          | BatteryManager.BATTERY_STATUS_FULL);
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
    runSubscription = PowerTriggerDB.with(getApplicationContext())
        .queryAll()
        .first()
        .flatMap(powerTriggerEntries -> {
          Timber.d("Flatten and filter");
          return Observable.from(powerTriggerEntries);
        })
        .filter(entry -> {
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
        })
        .flatMap(powerTriggerEntries -> {
          // KLUDGE this is really bad. Is there another way to update in the background?
          Observable<Integer> updatedAvailability = Observable.just(-1);
          PowerTriggerEntry trigger = PowerTriggerEntry.empty();

          if (charging) {
            Timber.d("Mark any available triggers");
            for (final PowerTriggerEntry entry : powerTriggerEntries) {
              if (entry.percent() <= percent && !entry.available()) {
                Timber.d("Mark entry available for percent: %d", entry.percent());
                final PowerTriggerEntry updated = PowerTriggerEntry.updatedAvailable(entry, true);
                final ContentValues values = PowerTriggerEntry.asContentValues(updated);
                updatedAvailability = updatedAvailability.mergeWith(
                    PowerTriggerDB.with(getApplicationContext())
                        .update(values, updated.percent(), updated.available()));
              }
            }
          } else {
            Timber.d("Select best trigger from available");
            PowerTriggerEntry best = PowerTriggerEntry.empty();
            for (final PowerTriggerEntry entry : powerTriggerEntries) {
              if (entry.available() && entry.enabled()) {
                final int bestDiff = best.percent() - percent;
                final int currentDiff = entry.percent() - percent;
                if (currentDiff < bestDiff) {
                  Timber.d("Best entry for %d: %s [%d]", percent, entry.name(), entry.percent());
                  best = entry;
                }

                if (best.percent() == percent) {
                  Timber.d("Found exact");
                  break;
                }
              }
            }

            if (!PowerTriggerEntry.isEmpty(best)) {
              Timber.d("Mark trigger as unavailable: %s", best.name());
              final PowerTriggerEntry updated = PowerTriggerEntry.updatedAvailable(best, false);
              final ContentValues values = PowerTriggerEntry.asContentValues(updated);
              updatedAvailability = PowerTriggerDB.with(getApplicationContext())
                  .update(values, updated.percent(), updated.available());
              trigger = updated;
            }
          }

          // KLUDGE just java things
          final PowerTriggerEntry passOn = trigger;
          return updatedAvailability.toSortedList().first().map(integer -> {
            // KLUDGE this is terrible
            Timber.d("Do terrible kludge");
            return passOn;
          });
        })
        // KLUDGE hardcoded schedulers
        // KLUDGE need to subscribe even if we are noop so that the other operations run
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(entry -> {
          if (!charging && !PowerTriggerEntry.isEmpty(entry)) {
            onTriggerRun(entry);
          } else {
            Timber.e("Can't run trigger. Either device is charging or no valid trigger");
          }

          // KLUDGE the show must go on
          Timber.d("Requeue the job");
          queue(new TriggerJob(getDelayInMs()));
        }, throwable -> {
          // TODO
          Timber.e(throwable, "onError");
        });
  }

  private void onTriggerRun(PowerTriggerEntry entry) {
    Timber.d("Run trigger for entry name: %s", entry.name());
    Timber.d("Run trigger for entry percent: %d", entry.percent());

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

  @Override protected void onCancelHook() {
    unsubRun();
  }
}
