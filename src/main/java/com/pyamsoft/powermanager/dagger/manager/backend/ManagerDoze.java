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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import com.pyamsoft.powermanager.app.manager.backend.Manager;
import com.pyamsoft.powermanager.app.receiver.SensorFixReceiver;
import com.pyamsoft.powermanager.dagger.base.SchedulerPresenter;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class ManagerDoze extends SchedulerPresenter<ManagerDoze.DozeView> implements Manager {
  @NonNull private static final String DUMPSYS_DOZE_START = "deviceidle force-idle deep";
  @NonNull private static final String DUMPSYS_DOZE_END = "deviceidle step";
  @NonNull private static final String DUMPSYS_SENSOR_ENABLE = "sensorservice enable";
  @NonNull private static final String DUMPSYS_SENSOR_RESTRICT =
      "sensorservice restrict com.pyamsoft.powermanager";
  @NonNull private final ManagerDozeInteractor interactor;
  @NonNull private Subscription subscription = Subscriptions.empty();
  @NonNull private Subscription dozeEndSubscription = Subscriptions.empty();
  @NonNull private Subscription dozeStartSubscription = Subscriptions.empty();
  @NonNull private Subscription sensorEnableSubscription = Subscriptions.empty();
  @NonNull private Subscription sensorRestrictSubscription = Subscriptions.empty();
  @NonNull private Subscription stateChangeSubscription = Subscriptions.empty();
  @Nullable private SensorFixReceiver sensorFixReceiver;

  @Inject ManagerDoze(@NonNull ManagerDozeInteractor interactor,
      @NonNull @Named("io") Scheduler ioScheduler,
      @NonNull @Named("main") Scheduler mainScheduler) {
    super(mainScheduler, ioScheduler);
    this.interactor = interactor;
  }

  public void commandSensorEnable() {
    unsubSensorEnable();
    sensorEnableSubscription = executeDumpsys(DUMPSYS_SENSOR_ENABLE).flatMap(enable -> {
      Timber.d("Force enable sensors");
      return interactor.createSensorFixReceiver();
    })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(sensorFixReceiver1 -> {
              Timber.d(
                  "Run sensor fix which resets both auto brightness and rotation after sensor dump");
              setSensorFixReceiver(sensorFixReceiver1);
              sensorFixReceiver1.register();
            }, throwable -> Timber.e(throwable, "onError commandSensorEnable"),
            this::unsubSensorEnable);
  }

  void setSensorFixReceiver(@NonNull SensorFixReceiver receiver) {
    if (sensorFixReceiver != null) {
      sensorFixReceiver.unregister();
    }
    this.sensorFixReceiver = receiver;
  }

  void unsubSensorEnable() {
    if (!sensorEnableSubscription.isUnsubscribed()) {
      sensorEnableSubscription.unsubscribe();
    }
  }

  public void commandSensorRestrict() {
    unsubSensorRestrict();
    sensorEnableSubscription =
        executeDumpsys(DUMPSYS_SENSOR_RESTRICT).subscribeOn(getSubscribeScheduler())
            .observeOn(getObserveScheduler())
            .subscribe(aBoolean -> Timber.d("Force restrict sensors"),
                throwable -> Timber.e(throwable, "onError commandSensorRestrict"),
                this::unsubSensorRestrict);
  }

  void unsubSensorRestrict() {
    if (!sensorRestrictSubscription.isUnsubscribed()) {
      sensorRestrictSubscription.unsubscribe();
    }
  }

  public void commandDozeEnd() {
    unsubDozeEnd();
    dozeEndSubscription = executeDumpsys(DUMPSYS_DOZE_END).subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Force out of doze"),
            throwable -> Timber.e(throwable, "onError commandDozeEnd"), this::unsubDozeEnd);
  }

  void unsubDozeEnd() {
    if (!dozeEndSubscription.isUnsubscribed()) {
      dozeEndSubscription.unsubscribe();
    }
  }

  public void commandDozeStart() {
    unsubDozeStart();
    dozeStartSubscription = executeDumpsys(DUMPSYS_DOZE_START).subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Force into doze"),
            throwable -> Timber.e(throwable, "onError commandDozeStart"), this::unsubDozeStart);
  }

  void unsubDozeStart() {
    if (!dozeStartSubscription.isUnsubscribed()) {
      dozeStartSubscription.unsubscribe();
    }
  }

  @CheckResult @NonNull Observable<Boolean> executeDumpsys(@NonNull String cmd) {
    return interactor.isDozeEnabled().map(isEnabled -> {
      if (!isEnabled) {
        Timber.e("Does not have permission to call dumpsys");
        return false;
      }

      interactor.executeDumpsys(cmd);
      return true;
    });
  }

  @CheckResult @NonNull Observable<Boolean> baseObservable() {
    return Observable.defer(() -> {
      Timber.d("Cancel old doze jobs");
      return interactor.cancelJobs();
    }).flatMap(managerInteractor -> interactor.isDozeEnabled()).filter(aBoolean -> {
      Timber.d("filter Doze not enabled");
      return aBoolean;
    });
  }

  // KLUDGE blocking obs
  @CheckResult public boolean isDozeEnabled() {
    return interactor.isDozeEnabled().toBlocking().first();
  }

  // KLUDGE blocking obs
  @CheckResult public boolean isDozeAvailable() {
    return interactor.isDozeAvailable().toBlocking().first();
  }

  // KLUDGE blocking obs
  @CheckResult public boolean isSensorsManaged() {
    return isDozeEnabled() && interactor.isManageSensors().toBlocking().first();
  }

  @Override public void enable() {
    unsubSubscription();
    subscription = baseObservable().flatMap(isEnabled -> {
      if (isEnabled) {
        Timber.d("Doze is enabled");
        return Observable.just(100L);
      } else {
        Timber.e("Doze is not available on this platform");
        return Observable.empty();
      }
    })
        .flatMap(delay -> interactor.createEnableJob(delay, false))
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(interactor::queueJob, throwable -> Timber.e(throwable, "onError"),
            this::unsubSubscription);
  }

  void unsubSubscription() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @Override public void disable(boolean charging) {
    unsubSubscription();
    final Observable<Long> delayObservable = baseObservable().flatMap(isEnabled -> {
      if (isEnabled) {
        return interactor.isDozeIgnoreCharging();
      } else {
        return Observable.empty();
      }
    }).filter(ignore -> {
      Timber.d("Filter out if ignore doze and device is charging");
      return !(ignore && charging);
    }).flatMap(shouldIgnore -> {
      Timber.d("Get doze delay");
      return interactor.getDelayTime();
    });

    final Observable<Boolean> sensorsObservable = interactor.isManageSensors();

    subscription = Observable.zip(delayObservable, sensorsObservable, Pair::new)
        .flatMap(pair -> interactor.createDisableJob(pair.first * 1000L, pair.second))
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(interactor::queueJob, throwable -> Timber.e(throwable, "onError"),
            this::unsubSubscription);
  }

  @Override public void cleanup() {
    unsubSubscription();
    unsubDozeStart();
    unsubDozeEnd();
    unsubSensorEnable();
    unsubSensorRestrict();
    unsubStateChange();
  }

  public void handleDozeStateChange(boolean currentState, boolean charging) {
    unsubStateChange();
    stateChangeSubscription = interactor.isDozeEnabled()
        .flatMap(isEnabled -> {
          if (!isEnabled) {
            Timber.e("Doze is not enabled");
            return Observable.just(false);
          } else {
            Timber.d("Holds DUMP permission");
            if (currentState) {
              Timber.d("Device has entered Doze mode");
              return interactor.isForceOutOfDoze().map(forceOut -> {
                if (forceOut) {
                  Timber.w("Forcing device out of Doze mode");
                  enable();
                } else {
                  Timber.d("Attempt device sensor restrict");
                  disable(charging);
                }
                return true;
              });
            } else {
              Timber.d("Device has exited Doze mode, Attempt device sensors enable");
              enable();
              return Observable.just(true);
            }
          }
        })
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(aBoolean -> Timber.d("Handled Doze state change: %s", aBoolean),
            throwable -> Timber.e(throwable, "onError handleDozeStateChange"),
            this::unsubStateChange);
  }

  void unsubStateChange() {
    if (!stateChangeSubscription.isUnsubscribed()) {
      stateChangeSubscription.unsubscribe();
    }
  }

  public interface DozeView {
  }
}
