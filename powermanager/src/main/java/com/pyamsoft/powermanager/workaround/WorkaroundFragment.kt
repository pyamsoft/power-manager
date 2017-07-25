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

package com.pyamsoft.powermanager.workaround

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.support.annotation.CheckResult
import android.support.v7.preference.Preference
import android.view.View
import com.pyamsoft.powermanager.Injector
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.uicore.WatchedPreferenceFragment
import com.pyamsoft.pydroid.ui.util.DialogUtil
import timber.log.Timber
import javax.inject.Inject

class WorkaroundFragment : WatchedPreferenceFragment() {

  @field:Inject internal lateinit var presenter: WorkaroundPresenter
  private lateinit var dataHowTo: Preference
  private lateinit var dataWorkaround: Preference

  private lateinit var dozeHowTo: Preference
  private lateinit var dozeWorkaround: Preference

  private lateinit var stability: Preference
  private lateinit var androidPowerManager: PowerManager
  private var whitelistIntent: Intent? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.with(context) {
      it.inject(this)
    }

    androidPowerManager = context.applicationContext.getSystemService(
        Context.POWER_SERVICE) as PowerManager
  }

  @CheckResult private fun hasDataWorkaroundRuntimePermission(): Boolean {
    return context.applicationContext.checkCallingOrSelfPermission(
        Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
  }

  @CheckResult private fun hasDozeWorkaroundRuntimePermission(): Boolean {
    return context.applicationContext.checkCallingOrSelfPermission(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Manifest.permission.WRITE_SECURE_SETTINGS
        else Manifest.permission.DUMP) == PackageManager.PERMISSION_GRANTED
  }

  override fun onCreatePreferences(p0: Bundle?, p1: String?) {
    addPreferencesFromResource(R.xml.workarounds)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    dataHowTo = findPreference(getString(R.string.key_workaround_howto_data))
    dataWorkaround = findPreference(getString(R.string.key_workaround_data))

    dozeHowTo = findPreference(getString(R.string.key_workaround_howto_doze))
    dozeWorkaround = findPreference(getString(R.string.key_workaround_doze))

    stability = findPreference(getString(R.string.key_workaround_howto_stability))
  }

  override fun onStart() {
    super.onStart()
    presenter.clickEvent(dataHowTo, {
      DialogUtil.guaranteeSingleDialogFragment(activity, DataWorkaroundDialog(), "data_workaround")
    })

    presenter.clickEvent(dataWorkaround, { Timber.d("Data workaround clicked") },
        { hasDataWorkaroundRuntimePermission() })

    presenter.clickEvent(dozeHowTo, {
      DialogUtil.guaranteeSingleDialogFragment(activity, DozeWorkaroundDialog(), "doze_workaround")
    })

    presenter.clickEvent(dozeWorkaround, { Timber.d("Doze workaround clicked") },
        { hasDozeWorkaroundRuntimePermission() })

    presenter.clickEvent(stability, {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (androidPowerManager.isIgnoringBatteryOptimizations(context.packageName)) {
          Timber.i("Power Manager is already ignoring optimization")
          // TODO show dialog with message
        } else {

          if (whitelistIntent == null) {
            val intent: Intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            intent.data = Uri.parse("package:${context.packageName}")
            whitelistIntent = intent
          }

          val obj = whitelistIntent
          if (obj != null) {
            context.startActivity(whitelistIntent)
          } else {
            Timber.e("Could not start battery activity, intent is NULL")
          }
        }
      } else {
        Timber.i("no Doze before marshmallow")
        // TODO show dialog with message
      }
    })
  }

  override fun onResume() {
    super.onResume()
    dataWorkaround.isEnabled = hasDataWorkaroundRuntimePermission()
    dozeWorkaround.isEnabled = hasDozeWorkaroundRuntimePermission()
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.destroy()
  }

  companion object {

    const val TAG = "WorkaroundFragment"
  }
}

