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

package com.pyamsoft.powermanager.dagger.manager.backend;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.app.receiver.SensorFixReceiver;
import com.pyamsoft.powermanager.dagger.base.BaseJob;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public abstract class DozeJob extends BaseJob {

  @NonNull public static final String DOZE_TAG = "doze_tag";
  private static final int PRIORITY = 5;

  private final boolean doze;
  private final boolean manageSensors;
  private final boolean forceDoze;
  @NonNull private final Scheduler ioScheduler;
  @NonNull private final Scheduler mainScheduler;
  @NonNull private Subscription subscription = Subscriptions.empty();

  // KLUDGE created here with dagger. goes against architecture
  @Nullable private SensorFixReceiver sensorFixReceiver;

  DozeJob(long delay, boolean enable, boolean forceDoze, boolean manageSensors) {
    this(delay, enable, forceDoze, manageSensors, Schedulers.io(), AndroidSchedulers.mainThread());
  }

  DozeJob(long delay, boolean enable, boolean forceDoze, boolean manageSensors,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(new Params(PRIORITY).setRequiresNetwork(false).addTags(DOZE_TAG).setDelayMs(delay));
    this.doze = enable;
    this.forceDoze = forceDoze;
    this.manageSensors = manageSensors;
    this.ioScheduler = ioScheduler;
    this.mainScheduler = mainScheduler;
  }

  @Override public void onRun() throws Throwable {
    unsub();

    // Unregister here manually
    if (sensorFixReceiver != null) {
      sensorFixReceiver.unregister();
    }
    this.sensorFixReceiver = new SensorFixReceiver(getApplicationContext());

    subscription = Observable.defer(() -> Observable.just(doze)).map(doze1 -> {
      Timber.d("Run DozeJob");
      if (doze1) {
        Timber.d("Do doze startDoze");
        if (forceDoze) {
          ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_DOZE_START);
        }
        if (manageSensors) {
          Timber.d("Do sensor restrict");
          ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_SENSOR_RESTRICT);
        }
      } else {
        Timber.d("Do doze stopDoze");
        if (forceDoze) {
          ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_DOZE_END);
        }

        Timber.d("Do sensor enable");
        ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_SENSOR_ENABLE);
      }
      // Ignore
      Timber.d("Run a fix function if doze was stopping and current state was doze");
      return !doze1 && manageSensors;
    }).subscribeOn(ioScheduler).observeOn(mainScheduler).subscribe(shouldRunFix -> {
      if (shouldRunFix) {
        Timber.d("Run hack fix for brightness and rotate");
        sensorFixReceiver.register();
      }
    }, throwable -> {
      Timber.e(throwable, "error executing dumpsys command");
      // TODO
    }, this::unsub);
  }

  private void unsub() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  public static final class EnableJob extends DozeJob {

    public EnableJob(boolean forceDoze) {
      super(100, false, forceDoze, false);
    }
  }

  public static final class DisableJob extends DozeJob {

    public DisableJob(long delay, boolean forceDoze, boolean manageSensors) {
      super(delay, true, forceDoze, manageSensors);
    }
  }
}
