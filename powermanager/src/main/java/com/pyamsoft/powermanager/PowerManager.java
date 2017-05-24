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

package com.pyamsoft.powermanager;

import android.app.Application;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.evernote.android.job.JobManager;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pyamsoft.powermanager.base.PowerManagerModule;
import com.pyamsoft.powermanager.job.JobHandler;
import com.pyamsoft.powermanager.job.JobQueuer;
import com.pyamsoft.powermanager.job.Jobs;
import com.pyamsoft.powermanager.main.MainActivity;
import com.pyamsoft.powermanager.service.ActionToggleService;
import com.pyamsoft.powermanager.service.ForegroundService;
import com.pyamsoft.pydroid.about.Licenses;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import javax.inject.Inject;
import timber.log.Timber;

public class PowerManager extends Application {

  @Inject JobHandler jobHandler;
  @Nullable private RefWatcher refWatcher;

  @CheckResult @NonNull public static RefWatcher getRefWatcher(@NonNull Fragment fragment) {
    final Application application = fragment.getActivity().getApplication();
    if (application instanceof PowerManager) {
      return ((PowerManager) application).getWatcher();
    } else {
      throw new IllegalStateException("Application is not Power Manager");
    }
  }

  @Override public void onCreate() {
    super.onCreate();
    if (LeakCanary.isInAnalyzerProcess(this)) {
      return;
    }

    Licenses.create("SQLBrite", "https://github.com/square/sqlbrite", "licenses/sqlbrite");
    Licenses.create("SQLDelight", "https://github.com/square/sqldelight", "licenses/sqldelight");
    Licenses.create("Android-Job", "https://github.com/evernote/android-job",
        "licenses/androidjob");
    Licenses.create("libsuperuser", "http://su.chainfire.eu/", "licenses/libsuperuser");
    Licenses.create("Dagger", "https://github.com/google/dagger", "licenses/dagger2");
    Licenses.create("Firebase", "https://firebase.google.com", "licenses/firebase");

    String gmsContent = GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(this);
    if (gmsContent != null) {
      Licenses.createWithContent("Google Play Services",
          "https://developers.google.com/android/guides/overview", gmsContent);
    }

    final PowerManagerModule module =
        new PowerManagerModule(this, MainActivity.class, ActionToggleService.class);
    final PowerManagerComponent component =
        DaggerPowerManagerComponent.builder().powerManagerModule(module).build();
    Injector.set(component);

    // Inject the jobHandler
    Injector.get().provideComponent().inject(this);

    // Guarantee JobManager creation
    JobManager.create(this);
    JobManager.instance().addJobCreator(s -> {
      if (JobQueuer.ENABLE_TAG.equals(s) || JobQueuer.DISABLE_TAG.equals(s)) {
        return Jobs.Companion.newJob(jobHandler);
      } else {
        Timber.e("Could not create job for tag: %s", s);
        return null;
      }
    });

    if (BuildConfig.DEBUG) {
      refWatcher = LeakCanary.install(this);
    } else {
      refWatcher = RefWatcher.DISABLED;
    }

    ForegroundService.start(this);
  }

  @NonNull @CheckResult private RefWatcher getWatcher() {
    if (refWatcher == null) {
      throw new IllegalStateException("RefWatcher is NULL");
    }
    return refWatcher;
  }
}
