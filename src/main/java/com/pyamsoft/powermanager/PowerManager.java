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

package com.pyamsoft.powermanager;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pyamsoft.powermanager.app.receiver.ScreenOnOffReceiver;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.powermanager.app.service.PowerManagerFrameworkJobSchedulerService;
import com.pyamsoft.powermanager.app.service.PowerManagerGCMJobSchedulerService;
import com.pyamsoft.powermanager.dagger.DaggerPowerManagerComponent;
import com.pyamsoft.powermanager.dagger.PowerManagerComponent;
import com.pyamsoft.powermanager.dagger.PowerManagerModule;
import com.pyamsoft.pydroid.base.app.ApplicationBase;
import timber.log.Timber;

public final class PowerManager extends ApplicationBase {

  @Nullable private PowerManagerComponent powerManagerComponent;
  @Nullable private ScreenOnOffReceiver screenOnOffReceiver;
  @Nullable private JobManager jobManager;

  @NonNull @CheckResult
  public static PowerManagerComponent powerManagerComponent(@NonNull Application application) {
    if (application instanceof PowerManager) {
      final PowerManager powerManager = (PowerManager) application;
      final PowerManagerComponent component = powerManager.powerManagerComponent;

      assert component != null;
      return component;
    } else {
      throw new ClassCastException("Cannot cast Application to PowerManager");
    }
  }

  @NonNull @CheckResult
  public static PowerManagerComponent powerManagerComponent(@NonNull Activity activity) {
    return powerManagerComponent(activity.getApplication());
  }

  @NonNull @CheckResult
  public static PowerManagerComponent powerManagerComponent(@NonNull Fragment fragment) {
    return powerManagerComponent(fragment.getActivity());
  }

  @NonNull @CheckResult
  public static PowerManagerComponent powerManagerComponent(@NonNull Service service) {
    return powerManagerComponent(service.getApplication());
  }

  @CheckResult @NonNull public static JobManager getJobManager(@NonNull Application application) {
    if (application instanceof PowerManager) {
      final PowerManager powerManager = (PowerManager) application;
      if (powerManager.jobManager == null) {
        synchronized (PowerManager.class) {
          if (powerManager.jobManager == null) {
            powerManager.jobManager = createJobManager(powerManager);
          }
        }
      }
      return powerManager.jobManager;
    } else {
      throw new ClassCastException("Cannot cast Application to PowerManager");
    }
  }

  @CheckResult @NonNull public static JobManager getJobManager(@NonNull Activity activity) {
    return getJobManager(activity.getApplication());
  }

  @CheckResult @NonNull public static JobManager getJobManager(@NonNull Fragment fragment) {
    return getJobManager(fragment.getActivity());
  }

  @CheckResult @NonNull public static JobManager getJobManager(@NonNull Service service) {
    return getJobManager(service.getApplication());
  }

  @CheckResult @NonNull
  private static JobManager createJobManager(@NonNull Application application) {
    final Configuration.Builder builder = new Configuration.Builder(application).minConsumerCount(1)
        .maxConsumerCount(4)
        .loadFactor(4)
        .consumerKeepAlive(120);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      builder.scheduler(FrameworkJobSchedulerService.createSchedulerFor(application,
          PowerManagerFrameworkJobSchedulerService.class));
    } else {
      final int googleAvailable =
          GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(application);
      if (googleAvailable == ConnectionResult.SUCCESS) {
        Timber.d("Create scheduler using Google play services");

        // Batch by default
        builder.scheduler(GcmJobSchedulerService.createSchedulerFor(application,
            PowerManagerGCMJobSchedulerService.class));
      } else {
        Timber.e("Could not create a scheduler to use with the JobScheduler");
      }
    }

    Timber.d("Create a new JobManager");
    return new JobManager(builder.build());
  }

  @Override public void onCreate() {
    super.onCreate();

    powerManagerComponent = DaggerPowerManagerComponent.builder()
        .powerManagerModule(new PowerManagerModule(getApplicationContext()))
        .build();

    initializeJobManager();
    registerScreenOnOffReceiver();
    startForegroundService();
  }

  private void startForegroundService() {
    startService(new Intent(this, ForegroundService.class));
  }

  private void registerScreenOnOffReceiver() {
    screenOnOffReceiver = new ScreenOnOffReceiver(this);
    screenOnOffReceiver.register();
  }

  private void initializeJobManager() {
    final JobManager jobManager = getJobManager(this);
    Timber.d("Created new JobManager with scheduler: %s", jobManager.getScheduler());
  }

  @Override protected boolean buildConfigDebug() {
    return BuildConfig.DEBUG;
  }

  @NonNull @Override public String appName() {
    return getString(R.string.app_name);
  }

  @NonNull @Override public String buildConfigApplicationId() {
    return BuildConfig.APPLICATION_ID;
  }

  @NonNull @Override public String buildConfigVersionName() {
    return BuildConfig.VERSION_NAME;
  }

  @Override public int buildConfigVersionCode() {
    return BuildConfig.VERSION_CODE;
  }

  @NonNull @Override public String getApplicationPackageName() {
    return getPackageName();
  }
}
