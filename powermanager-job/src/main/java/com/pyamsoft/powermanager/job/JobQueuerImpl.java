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

package com.pyamsoft.powermanager.job;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.JobQueuerEntry;
import com.pyamsoft.powermanager.model.Logger;
import javax.inject.Inject;
import timber.log.Timber;

class JobQueuerImpl implements JobQueuer {

  @NonNull final static String KEY_IGNORE_CHARGING = "extra_key__ignore_charging";
  @NonNull final static String KEY_PERIODIC = "extra_key__periodic";
  @NonNull final static String KEY_ON_WINDOW = "extra_key__on_window";
  @NonNull final static String KEY_OFF_WINDOW = "extra_key__off_window";
  @NonNull final static String KEY_QUEUE_TYPE = "extra_key__type";

  @NonNull private final JobManager jobManager;

  @NonNull private final Logger loggerWifi;
  @NonNull private final Logger loggerData;
  @NonNull private final Logger loggerBluetooth;
  @NonNull private final Logger loggerSync;
  @NonNull private final Logger loggerDoze;
  @NonNull private final Logger loggerAirplane;

  @NonNull private final BooleanInterestObserver stateObserverWifi;
  @NonNull private final BooleanInterestObserver stateObserverData;
  @NonNull private final BooleanInterestObserver stateObserverBluetooth;
  @NonNull private final BooleanInterestObserver stateObserverSync;
  @NonNull private final BooleanInterestObserver stateObserverDoze;
  @NonNull private final BooleanInterestObserver stateObserverAirplane;

  @NonNull private final BooleanInterestModifier stateModifierWifi;
  @NonNull private final BooleanInterestModifier stateModifierData;
  @NonNull private final BooleanInterestModifier stateModifierBluetooth;
  @NonNull private final BooleanInterestModifier stateModifierSync;
  @NonNull private final BooleanInterestModifier stateModifierDoze;
  @NonNull private final BooleanInterestModifier stateModifierAirplane;

  @Inject JobQueuerImpl(@NonNull JobManager jobManager, @NonNull Logger loggerWifi,
      @NonNull Logger loggerData, @NonNull Logger loggerBluetooth, @NonNull Logger loggerSync,
      @NonNull Logger loggerDoze, @NonNull Logger loggerAirplane,
      @NonNull BooleanInterestObserver stateObserverWifi,
      @NonNull BooleanInterestObserver stateObserverData,
      @NonNull BooleanInterestObserver stateObserverBluetooth,
      @NonNull BooleanInterestObserver stateObserverSync,
      @NonNull BooleanInterestObserver stateObserverDoze,
      @NonNull BooleanInterestObserver stateObserverAirplane,
      @NonNull BooleanInterestModifier stateModifierWifi,
      @NonNull BooleanInterestModifier stateModifierData,
      @NonNull BooleanInterestModifier stateModifierBluetooth,
      @NonNull BooleanInterestModifier stateModifierSync,
      @NonNull BooleanInterestModifier stateModifierDoze,
      @NonNull BooleanInterestModifier stateModifierAirplane) {
    this.jobManager = jobManager;
    this.loggerWifi = loggerWifi;
    this.loggerData = loggerData;
    this.loggerBluetooth = loggerBluetooth;
    this.loggerSync = loggerSync;
    this.loggerDoze = loggerDoze;
    this.loggerAirplane = loggerAirplane;
    this.stateObserverWifi = stateObserverWifi;
    this.stateObserverData = stateObserverData;
    this.stateObserverBluetooth = stateObserverBluetooth;
    this.stateObserverSync = stateObserverSync;
    this.stateObserverDoze = stateObserverDoze;
    this.stateObserverAirplane = stateObserverAirplane;
    this.stateModifierWifi = stateModifierWifi;
    this.stateModifierData = stateModifierData;
    this.stateModifierBluetooth = stateModifierBluetooth;
    this.stateModifierSync = stateModifierSync;
    this.stateModifierDoze = stateModifierDoze;
    this.stateModifierAirplane = stateModifierAirplane;
  }

  @Override public void cancel(@NonNull String tag) {
    Timber.w("Cancel all jobs for tag: %s", tag);
    jobManager.cancelAllForTag(tag);
  }

  @CheckResult @NonNull private PersistableBundleCompat createExtras(JobQueuerEntry entry) {
    final PersistableBundleCompat extras = new PersistableBundleCompat();
    extras.putString(KEY_QUEUE_TYPE, entry.type().name());
    extras.putLong(KEY_ON_WINDOW, entry.repeatingOnWindow());
    extras.putLong(KEY_OFF_WINDOW, entry.repeatingOffWindow());
    extras.putBoolean(KEY_PERIODIC, entry.repeating());
    extras.putBoolean(KEY_IGNORE_CHARGING, entry.ignoreIfCharging());
    return extras;
  }

  @Override public void queue(@NonNull JobQueuerEntry entry) {
    final PersistableBundleCompat extras = createExtras(entry);
    if (entry.delay() == 0) {
      runDirectJob(entry.tag(), extras);
    } else {
      new JobRequest.Builder(entry.tag()).setExact(entry.delay())
          .setPersisted(false)
          .setExtras(extras)
          .setRequiresCharging(false)
          .setRequiresDeviceIdle(false)
          .build()
          .schedule();
    }
  }

  @Override public void queueRepeating(@NonNull JobQueuerEntry entry) {
    final PersistableBundleCompat extras = createExtras(entry);
    new JobRequest.Builder(entry.tag()).setPeriodic(entry.delay())
        .setPersisted(false)
        .setExtras(extras)
        .setRequiresCharging(false)
        .setRequiresDeviceIdle(false)
        .build()
        .schedule();
  }

  private void runDirectJob(@NonNull String tag, @NonNull PersistableBundleCompat extras) {
    final BaseJob job;
    switch (tag) {
      case JobQueuer.WIFI_JOB_TAG:
        job = new WifiJob(loggerWifi, stateObserverWifi, stateModifierWifi);
        break;
      case JobQueuer.DATA_JOB_TAG:
        job = new DataJob(loggerData, stateObserverData, stateModifierData);
        break;
      case JobQueuer.BLUETOOTH_JOB_TAG:
        job = new BluetoothJob(loggerBluetooth, stateObserverBluetooth, stateModifierBluetooth);
        break;
      case JobQueuer.SYNC_JOB_TAG:
        job = new SyncJob(loggerSync, stateObserverSync, stateModifierSync);
        break;
      case JobQueuer.AIRPLANE_JOB_TAG:
        job = new AirplaneJob(loggerAirplane, stateObserverAirplane, stateModifierAirplane);
        break;
      case JobQueuer.DOZE_JOB_TAG:
        job = new DozeJob(loggerDoze, stateObserverDoze, stateModifierDoze);
        break;
      default:
        job = null;
    }

    if (job != null) {
      job.run(tag, extras);
    }
  }
}
