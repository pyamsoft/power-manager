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

package com.pyamsoft.powermanagerpresenter.trigger;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.pyamsoft.powermanagermodel.BooleanInterestModifier;
import com.pyamsoft.powermanagermodel.BooleanInterestObserver;
import com.pyamsoft.powermanagermodel.sql.PowerTriggerEntry;
import com.pyamsoft.powermanagerpresenter.Injector;
import com.pyamsoft.powermanagerpresenter.logger.Logger;
import com.pyamsoft.powermanagerpresenter.wrapper.JobQueuerWrapper;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Func1;
import timber.log.Timber;

public class TriggerRunnerService extends Service {

  @NonNull public static final String EXTRA_DELAY_PERIOD = "extra_trigger_delay";

  @Inject JobQueuerWrapper jobQueuerWrapper;
  @Inject @Named("logger_trigger") Logger logger;
  @Inject PowerTriggerDB powerTriggerDB;
  @Inject @Named("obs_wifi_state") BooleanInterestObserver wifiObserver;
  @Inject @Named("obs_data_state") BooleanInterestObserver dataObserver;
  @Inject @Named("obs_bluetooth_state") BooleanInterestObserver bluetoothObserver;
  @Inject @Named("obs_sync_state") BooleanInterestObserver syncObserver;
  @Inject @Named("mod_wifi_state") BooleanInterestModifier wifiModifier;
  @Inject @Named("mod_data_state") BooleanInterestModifier dataModifier;
  @Inject @Named("mod_bluetooth_state") BooleanInterestModifier bluetoothModifier;
  @Inject @Named("mod_sync_state") BooleanInterestModifier syncModifier;
  @Inject @Named("obs") Scheduler obsScheduler;
  @Inject @Named("sub") Scheduler subScheduler;

  @SuppressWarnings("WeakerAccess") @Nullable Subscription runSubscription;

  @Override public void onCreate() {
    super.onCreate();
    Injector.get().provideComponent().plusTriggerRunnerComponent().inject(this);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    final IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    final Intent batteryIntent = getApplicationContext().registerReceiver(null, batteryFilter);

    // Get battery level
    final int percent;
    final boolean charging;
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
    requeueTriggerJob(intent);
    return START_NOT_STICKY;
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
            }
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
      }).flatMap(new Func1<PowerTriggerEntry, Observable<PowerTriggerEntry>>() {
        @Override public Observable<PowerTriggerEntry> call(PowerTriggerEntry entry) {
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
        }
      });
    }

    SubscriptionHelper.unsubscribe(runSubscription);
    runSubscription = powerTriggerEntryObservable.subscribeOn(subScheduler)
        .observeOn(obsScheduler)
        .subscribe(entry -> {
          if (charging) {
            Timber.w("Do not run Trigger because device is charging");
          } else if (PowerTriggerEntry.isEmpty(entry)) {
            Timber.w("Do not run Trigger because entry specified is EMPTY");
          } else {
            onTriggerRun(entry);
          }
        }, throwable -> Timber.e(throwable, "onError"), () -> {
          SubscriptionHelper.unsubscribe(runSubscription);
          stopSelf();
        });
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

  private void requeueTriggerJob(@NonNull Intent intent) {
    final long delay = intent.getLongExtra(EXTRA_DELAY_PERIOD, -1);
    if (delay < 0) {
      logger.e("Invalid delay period passed. Not requeuing trigger job");
      return;
    }

    final Intent newIntent = new Intent(intent);
    intent.putExtra(EXTRA_DELAY_PERIOD, delay);

    jobQueuerWrapper.cancel(newIntent);
    jobQueuerWrapper.set(newIntent, System.currentTimeMillis() + delay);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    SubscriptionHelper.unsubscribe(runSubscription);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
}
