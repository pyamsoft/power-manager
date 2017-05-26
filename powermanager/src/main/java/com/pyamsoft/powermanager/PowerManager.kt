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

package com.pyamsoft.powermanager

import android.app.Application
import android.support.annotation.CheckResult
import android.support.v4.app.Fragment
import com.evernote.android.job.JobManager
import com.google.android.gms.common.GoogleApiAvailability
import com.pyamsoft.powermanager.base.PowerManagerModule
import com.pyamsoft.powermanager.job.JobHandler
import com.pyamsoft.powermanager.job.JobQueuer
import com.pyamsoft.powermanager.job.Jobs
import com.pyamsoft.powermanager.main.MainActivity
import com.pyamsoft.powermanager.service.ActionToggleService
import com.pyamsoft.powermanager.service.ForegroundService
import com.pyamsoft.powermanager.uicore.WatchedDialog
import com.pyamsoft.powermanager.uicore.WatchedFragment
import com.pyamsoft.powermanager.uicore.WatchedPreferenceFragment
import com.pyamsoft.pydroid.about.Licenses
import com.pyamsoft.pydroid.ui.PYDroid
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import timber.log.Timber
import javax.inject.Inject

class PowerManager : Application() {

  @field:Inject lateinit internal var jobHandler: JobHandler
  private lateinit var refWatcher: RefWatcher

  override fun onCreate() {
    super.onCreate()
    if (LeakCanary.isInAnalyzerProcess(this)) {
      return
    }
    PYDroid.initialize(this, BuildConfig.DEBUG)
    Licenses.create("SQLBrite", "https://github.com/square/sqlbrite", "licenses/sqlbrite")
    Licenses.create("SQLDelight", "https://github.com/square/sqldelight", "licenses/sqldelight")
    Licenses.create("Android-Job", "https://github.com/evernote/android-job", "licenses/androidjob")
    Licenses.create("libsuperuser", "http://su.chainfire.eu/", "licenses/libsuperuser")
    Licenses.create("Dagger", "https://github.com/google/dagger", "licenses/dagger2")
    Licenses.create("Firebase", "https://firebase.google.com", "licenses/firebase")
    Licenses.create("Fast Adapter", "https://github.com/mikepenz/FastAdapter",
        "licenses/fastadapter")
    Licenses.create("Leak Canary", "https://github.com/square/leakcanary", "licenses/leakcanary")

    val gmsContent = GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(this)
    if (gmsContent != null) {
      Licenses.createWithContent("Google Play Services",
          "https://developers.google.com/android/guides/overview", gmsContent)
    }

    val module = PowerManagerModule(this, MainActivity::class.java, ActionToggleService::class.java)
    val component = DaggerPowerManagerComponent.builder().powerManagerModule(module).build()
    Injector.set(component)

    // Inject the jobHandler
    Injector.get().provideComponent().inject(this)

    // Guarantee JobManager creation
    JobManager.create(this)
    JobManager.instance().addJobCreator {
      if (JobQueuer.ENABLE_TAG == it || JobQueuer.DISABLE_TAG == it) {
        return@addJobCreator Jobs.newJob(jobHandler)
      } else {
        Timber.e("Could not create job for tag: %s", it)
        return@addJobCreator null
      }
    }

    if (BuildConfig.DEBUG) {
      refWatcher = LeakCanary.install(this)
    } else {
      refWatcher = RefWatcher.DISABLED
    }

    ForegroundService.start(this)
  }

  private val watcher: RefWatcher
    @CheckResult get() {
      return refWatcher
    }

  companion object {

    @JvmStatic @CheckResult fun getRefWatcher(fragment: WatchedDialog): RefWatcher {
      return getRefWatcherInternal(fragment)
    }

    @JvmStatic @CheckResult fun getRefWatcher(fragment: WatchedPreferenceFragment): RefWatcher {
      return getRefWatcherInternal(fragment)
    }

    @JvmStatic @CheckResult fun getRefWatcher(fragment: WatchedFragment): RefWatcher {
      return getRefWatcherInternal(fragment)
    }

    @JvmStatic @CheckResult private fun getRefWatcherInternal(fragment: Fragment): RefWatcher {
      val application = fragment.activity.application
      if (application is PowerManager) {
        return application.watcher
      } else {
        throw IllegalStateException("Application is not Power Manager")
      }
    }
  }
}
