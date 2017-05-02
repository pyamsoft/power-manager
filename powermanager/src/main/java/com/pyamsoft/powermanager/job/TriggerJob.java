/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.job;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.evernote.android.job.Job;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.base.logger.Logger;
import com.pyamsoft.powermanager.model.ConnectedStateObserver;
import com.pyamsoft.powermanager.model.StateModifier;
import com.pyamsoft.powermanager.model.StateObserver;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerDB;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class TriggerJob extends Job {

  @SuppressWarnings("WeakerAccess") @NonNull final CompositeDisposable compositeDisposable;
  @SuppressWarnings("WeakerAccess") @Inject PowerTriggerDB powerTriggerDB;
  @SuppressWarnings("WeakerAccess") @Inject @Named("obs_charging_state") StateObserver
      chargingObserver;
  @SuppressWarnings("WeakerAccess") @Inject @Named("logger_trigger") Logger logger;
  @SuppressWarnings("WeakerAccess") @Inject @Named("obs_wifi_state") ConnectedStateObserver wifiObserver;
  @SuppressWarnings("WeakerAccess") @Inject @Named("obs_data_state") StateObserver dataObserver;
  @SuppressWarnings("WeakerAccess") @Inject @Named("obs_bluetooth_state") StateObserver
      bluetoothObserver;
  @SuppressWarnings("WeakerAccess") @Inject @Named("obs_sync_state") StateObserver syncObserver;
  @SuppressWarnings("WeakerAccess") @Inject @Named("mod_wifi_state") StateModifier wifiModifier;
  @SuppressWarnings("WeakerAccess") @Inject @Named("mod_data_state") StateModifier dataModifier;
  @SuppressWarnings("WeakerAccess") @Inject @Named("mod_bluetooth_state") StateModifier
      bluetoothModifier;
  @SuppressWarnings("WeakerAccess") @Inject @Named("mod_sync_state") StateModifier syncModifier;
  @SuppressWarnings("WeakerAccess") @Inject @Named("sub") Scheduler subScheduler;
  @SuppressWarnings("WeakerAccess") @Inject @Named("obs") Scheduler obsScheduler;

  public TriggerJob() {
    Injector.get().provideComponent().plusJobComponent().inject(this);
    compositeDisposable = new CompositeDisposable();
  }

  private void runTriggerForPercent(int percent) {
    final Single<List<PowerTriggerEntry>> triggerQuery =
        powerTriggerDB.queryAll().flatMapObservable(powerTriggerEntries -> {
          Timber.d("Flatten power triggers");
          return Observable.fromIterable(powerTriggerEntries);
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

    final Single<PowerTriggerEntry> powerTriggerEntryObservable;
    if (chargingObserver.enabled()) {
      powerTriggerEntryObservable = triggerQuery.flatMapObservable(powerTriggerEntries -> {
        // Not final so we can call merges on it
        Observable<Integer> updateTriggerResult = Observable.just(0);

        Timber.i("We are charging, mark any available triggers");
        for (final PowerTriggerEntry entry : powerTriggerEntries) {
          Timber.d("Current entry: %s %d", entry.name(), entry.percent());
          if (entry.percent() <= percent && !entry.available()) {
            Timber.d("Mark entry available for percent: %d", entry.percent());
            updateTriggerResult = updateTriggerResult.mergeWith(
                powerTriggerDB.updateAvailable(true, entry.percent()).toObservable());
          }
        }

        return updateTriggerResult;
        // Convert to list so that we iterate over all the triggers we have found instead of just first
      }).toList().map(integers -> {
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
        final Single<PowerTriggerEntry> updateTriggerResult;
        if (!PowerTriggerEntry.isEmpty(entry)) {
          Timber.d("Mark trigger as unavailable: %s %d", entry.name(), entry.percent());
          updateTriggerResult = powerTriggerDB.updateAvailable(entry.available(), entry.percent())
              .toSingleDefault(entry.percent())
              .map(integer -> entry);
        } else {
          Timber.w("No trigger marked, EMPTY result");
          updateTriggerResult = Single.never();
        }

        return updateTriggerResult;
      });
    }

    compositeDisposable.add(powerTriggerEntryObservable.subscribeOn(subScheduler)
        .observeOn(obsScheduler)
        .subscribe(entry -> {
          if (chargingObserver.enabled()) {
            Timber.w("Do not run Trigger because device is charging");
          } else if (PowerTriggerEntry.isEmpty(entry)) {
            Timber.w("Do not run Trigger because entry specified is EMPTY");
          } else {
            onTriggerRun(getContext(), entry);
          }
        }, throwable -> Timber.e(throwable, "onError")));
  }

  @SuppressWarnings("WeakerAccess") void onTriggerRun(@NonNull Context context,
      @NonNull PowerTriggerEntry entry) {
    logger.d("Run trigger for entry name: %s", entry.name());
    logger.d("Run trigger for entry percent: %d", entry.percent());
    final String formatted =
        String.format(Locale.getDefault(), "Run trigger: %s [%d]", entry.name(), entry.percent());
    Toast.makeText(context.getApplicationContext(), formatted, Toast.LENGTH_SHORT).show();

    if (entry.toggleWifi()) {
      Timber.d("Wifi should toggle");
      if (entry.enableWifi()) {
        Timber.d("Wifi should enable");
        if (!wifiObserver.enabled()) {
          logger.i("Trigger job: %s set wifi", entry.name());
          wifiModifier.set();
        }
      } else {
        Timber.d("Wifi should disable");
        if (wifiObserver.enabled()) {
          logger.i("Trigger job: %s unset wifi", entry.name());
          wifiModifier.unset();
        }
      }
    }

    if (entry.toggleData()) {
      Timber.d("Data should toggle");
      if (entry.enableData()) {
        Timber.d("Data should enable");
        if (!dataObserver.enabled()) {
          logger.i("Trigger job: %s set data", entry.name());
          dataModifier.set();
        }
      } else {
        Timber.d("Data should disable");
        if (dataObserver.enabled()) {
          logger.i("Trigger job: %s unset data", entry.name());
          dataModifier.unset();
        }
      }
    }

    if (entry.toggleBluetooth()) {
      Timber.d("Bluetooth should toggle");
      if (entry.enableBluetooth()) {
        Timber.d("Bluetooth should enable");
        if (!bluetoothObserver.enabled()) {
          logger.i("Trigger job: %s set bluetooth", entry.name());
          bluetoothModifier.set();
        }
      } else {
        Timber.d("Bluetooth should disable");
        if (bluetoothObserver.enabled()) {
          logger.i("Trigger job: %s set bluetooth", entry.name());
          bluetoothModifier.unset();
        }
      }
    }

    if (entry.toggleSync()) {
      Timber.d("Sync should toggle");
      if (entry.enableSync()) {
        Timber.d("Sync should enable");
        if (!syncObserver.enabled()) {
          logger.i("Trigger job: %s set sync", entry.name());
          syncModifier.set();
        }
      } else {
        Timber.d("Sync should disable");
        if (syncObserver.enabled()) {
          logger.i("Trigger job: %s unset sync", entry.name());
          syncModifier.unset();
        }
      }
    }
  }

  @NonNull @Override protected Result onRunJob(Params params) {
    final IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    final Intent batteryIntent =
        getContext().getApplicationContext().registerReceiver(null, batteryFilter);

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
    compositeDisposable.clear();
    return Result.SUCCESS;
  }
}
