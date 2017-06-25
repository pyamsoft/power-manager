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

package com.pyamsoft.powermanager.main

import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.CheckResult
import android.support.v4.view.ViewCompat
import android.support.v7.preference.PreferenceManager
import android.view.KeyEvent
import android.view.MenuItem
import com.pyamsoft.powermanager.BuildConfig
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.databinding.ActivityMainBinding
import com.pyamsoft.powermanager.logger.LoggerDialog
import com.pyamsoft.powermanager.service.ForegroundService
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.rating.RatingDialog
import com.pyamsoft.pydroid.ui.sec.TamperActivity
import com.pyamsoft.pydroid.ui.util.DialogUtil
import com.pyamsoft.pydroid.util.AppUtil
import timber.log.Timber
import javax.inject.Inject

class MainActivity : TamperActivity() {
  private lateinit var binding: ActivityMainBinding
  private val longPressBackRunnable = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) null else Runnable { this.handleBackLongPress() }
  private val mainHandler = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) null else Handler(
      Looper.getMainLooper())
  @field:Inject lateinit internal var presenter: MainPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_PowerManager_Light)
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    Injector.with(this) {
      it.inject(this)
    }
    setupPreferenceDefaults()
    setupAppBar()

    if (hasNoActiveFragment()) {
      supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MainFragment(),
          MainFragment.TAG).commit()
    }
  }

  private fun setupPreferenceDefaults() {
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    PreferenceManager.setDefaultValues(this, R.xml.workarounds, false)
  }

  private fun setupAppBar() {
    setSupportActionBar(binding.mainToolbar)
    binding.mainToolbar.title = getString(R.string.app_name)
    ViewCompat.setElevation(binding.mainToolbar, AppUtil.convertToDP(this, 4F))
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.destroy()
  }

  override fun onBackPressed() {
    val fragmentManager = supportFragmentManager
    if (fragmentManager.backStackEntryCount > 0) {
      fragmentManager.popBackStackImmediate()
    } else {
      super.onBackPressed()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val handled: Boolean
    when (item.itemId) {
      android.R.id.home -> {
        onBackPressed()
        handled = true
      }
      else -> handled = false
    }
    return handled || super.onOptionsItemSelected(item)
  }

  @CheckResult private fun hasNoActiveFragment(): Boolean {
    return supportFragmentManager.findFragmentByTag(
        AboutLibrariesFragment.TAG) == null && supportFragmentManager.findFragmentByTag(
        MainFragment.TAG) == null
  }

  override fun onPostResume() {
    super.onPostResume()
    RatingDialog.showRatingDialog(this, this, false)
  }

  override val safePackageName: String
    get() = "com.pyamsoft.powermanager"
  override val changeLogLines: Array<String>
    get() {
      val line1 = "BUGFIX: Bugfixes and improvements"
      val line2 = "BUGFIX: Removed all Advertisements"
      val line3 = "BUGFIX: Faster loading of Open Source Licenses page"
      return arrayOf(line1, line2, line3)
    }
  override val versionName: String
    get() = BuildConfig.VERSION_NAME
  override val applicationIcon: Int
    get() = R.mipmap.ic_launcher

  override fun provideApplicationName(): String {
    return "Power Manager"
  }

  override val currentApplicationVersion: Int
    get() = BuildConfig.VERSION_CODE

  override fun onStart() {
    super.onStart()

    presenter.startServiceWhenOpen({
      Timber.d("Should refresh service when opened")
      ForegroundService.start(applicationContext)
    }, {})
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  // https://github.com/mozilla/gecko-dev/blob/master/mobile/android/base/java/org/mozilla/gecko/BrowserApp.java
  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && keyCode == KeyEvent.KEYCODE_BACK) {
      if (mainHandler != null && longPressBackRunnable != null) {
        mainHandler.removeCallbacksAndMessages(null)
        mainHandler.postDelayed(longPressBackRunnable, 1400L)
      }
    }

    return super.onKeyDown(keyCode, event)
  }

  override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && keyCode == KeyEvent.KEYCODE_BACK) {
      if (handleBackLongPress()) {
        return true
      }
    }

    return super.onKeyLongPress(keyCode, event)
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && keyCode == KeyEvent.KEYCODE_BACK) {
      mainHandler?.removeCallbacksAndMessages(null)
    }

    return super.onKeyUp(keyCode, event)
  }

  internal fun handleBackLongPress(): Boolean {
    val tag = "logger_dialog"
    val fragmentManager = supportFragmentManager
    if (fragmentManager.findFragmentByTag(tag) == null) {
      Timber.d("Show logger dialog")
      DialogUtil.guaranteeSingleDialogFragment(this, LoggerDialog(), tag)
      return true
    } else {
      Timber.w("Logger dialog is already shown")
      return false
    }
  }
}
