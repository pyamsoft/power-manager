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
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.app.manager.backend.ManagerDoze;
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
  @NonNull private final Scheduler ioScheduler;
  @NonNull private final Scheduler mainScheduler;
  @NonNull private Subscription subscription = Subscriptions.empty();

  DozeJob(long delay, boolean enable) {
    this(delay, enable, Schedulers.io(), AndroidSchedulers.mainThread());
  }

  DozeJob(long delay, boolean enable, @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(new Params(PRIORITY).setRequiresNetwork(false).addTags(DOZE_TAG).setDelayMs(delay));
    this.doze = enable;
    this.ioScheduler = ioScheduler;
    this.mainScheduler = mainScheduler;
  }

  @Override public void onRun() throws Throwable {
    unsub();
    subscription = Observable.defer(() -> Observable.just(doze)).map(doze1 -> {
      Timber.d("Run DozeJob");
      if (doze1) {
        Timber.d("Do doze startDoze");
        ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_DOZE_START);
        ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_SENSOR_RESTRICT);
      } else {
        Timber.d("Do doze stopDoze");
        ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_DOZE_END);
        ManagerDoze.executeDumpsys(getApplicationContext(), ManagerDoze.DUMPSYS_SENSOR_ENABLE);
      }
      // Ignore
      Timber.d("Run a fix function if doze was stopping and current state was doze");
      return !doze1;
    }).subscribeOn(ioScheduler).observeOn(mainScheduler).subscribe(shouldRunFix -> {
      if (shouldRunFix) {
        Timber.d("Run hack fix for brightness and rotate");
        ManagerDoze.fixSensorDisplayRotationBug(getApplicationContext());
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

    public EnableJob() {
      super(100, false);
    }
  }

  public static final class DisableJob extends DozeJob {

    public DisableJob(long delay) {
      super(delay, true);
    }
  }
}
