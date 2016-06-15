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

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.powermanager.app.service.job.PowerManagerFrameworkJobSchedulerService;
import com.pyamsoft.powermanager.app.service.job.PowerManagerGCMJobSchedulerService;
import com.pyamsoft.powermanager.dagger.DaggerPowerManagerComponent;
import com.pyamsoft.powermanager.dagger.PowerManagerComponent;
import com.pyamsoft.powermanager.dagger.PowerManagerModule;
import com.pyamsoft.pydroid.base.app.ApplicationBase;
import timber.log.Timber;

public final class PowerManager extends ApplicationBase {

  @Nullable private PowerManagerComponent powerManagerComponent;
  @Nullable private JobManager jobManager;
  @Nullable private volatile static PowerManager instance = null;

  @NonNull @CheckResult public synchronized static PowerManager getInstance() {
    if (instance == null) {
      throw new NullPointerException("PowerManager instance is NULL");
    } else {
      //noinspection ConstantConditions
      return instance;
    }
  }

  public synchronized static void setInstance(@Nullable PowerManager instance) {
    PowerManager.instance = instance;
  }

  @NonNull @CheckResult public synchronized final PowerManagerComponent getPowerManagerComponent() {
    if (powerManagerComponent == null) {
      throw new NullPointerException("PowerManagerComponent is NULL");
    } else {
      return powerManagerComponent;
    }
  }

  @NonNull @CheckResult public synchronized final JobManager getJobManager() {
    if (jobManager == null) {
      throw new NullPointerException("JobManager is NULL");
    } else {
      return jobManager;
    }
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

    // Initialize instance
    powerManagerComponent = DaggerPowerManagerComponent.builder()
        .powerManagerModule(new PowerManagerModule(getApplicationContext()))
        .build();
    initializeJobManager();

    // Set instance
    setInstance(this);

    // Start stuff
    startForegroundService();
  }

  private void startForegroundService() {
    startService(new Intent(this, ForegroundService.class));
  }

  private void initializeJobManager() {
    final JobManager jobManager = createJobManager(this);
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
