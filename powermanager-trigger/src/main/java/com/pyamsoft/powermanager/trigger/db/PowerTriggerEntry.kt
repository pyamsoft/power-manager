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

package com.pyamsoft.powermanager.trigger.db

import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.CheckResult
import com.google.auto.value.AutoValue
import com.pyamsoft.powermanager.trigger.db.PowerTriggerModel.Creator
import com.pyamsoft.powermanager.trigger.db.PowerTriggerModel.Mapper
import com.squareup.sqldelight.SqlDelightStatement

@AutoValue abstract class PowerTriggerEntry : PowerTriggerModel {

  internal class DeleteTriggerManager internal constructor(openHelper: SQLiteOpenHelper) {
    private val deleteTrigger: PowerTriggerModel.Delete_trigger = PowerTriggerModel.Delete_trigger(
        openHelper.writableDatabase)

    @CheckResult internal fun executeProgram(percent: Int): Int {
      deleteTrigger.bind(percent)
      return deleteTrigger.program.executeUpdateDelete()
    }
  }

  internal class InsertManager internal constructor(openHelper: SQLiteOpenHelper) {
    private val insertTrigger: PowerTriggerModel.Insert_trigger = PowerTriggerModel.Insert_trigger(
        openHelper.writableDatabase)

    @CheckResult internal fun executeProgram(newEntry: PowerTriggerEntry): Long {
      insertTrigger.bind(newEntry.percent(), newEntry.name(), newEntry.enabled(),
          newEntry.stateWifi(), newEntry.stateData(), newEntry.stateBluetooth(),
          newEntry.stateSync())
      return insertTrigger.program.executeInsert()
    }
  }

  internal class UpdateEnabledManager internal constructor(openHelper: SQLiteOpenHelper) {
    private val updateEnabled: PowerTriggerModel.Update_enabled = PowerTriggerModel.Update_enabled(
        openHelper.writableDatabase)

    @CheckResult internal fun executeProgram(enabled: Boolean, percent: Int): Int {
      updateEnabled.bind(enabled, percent)
      return updateEnabled.program.executeUpdateDelete()
    }
  }

  @CheckResult fun updateEnabled(enabled: Boolean): PowerTriggerEntry {
    return creator.create(percent(), name(), enabled, stateWifi(), stateData(), stateBluetooth(),
        stateSync())
  }

  companion object {

    const val STATE_NONE = 0
    const val STATE_ENABLE = 1
    const val STATE_DISABLE = 2

    const val EMPTY_NAME = "PowerTriggerEntry.__TRIGGER_NAME_EMPTY"
    const val EMPTY_PERCENT = -1

    private val factory: PowerTriggerModel.Factory<PowerTriggerEntry> by lazy {
      PowerTriggerModel.Factory(
          PowerTriggerModel.Creator<PowerTriggerEntry> { percent, name, enabled, stateWifi, stateData, stateBluetooth, stateSync ->
            AutoValue_PowerTriggerEntry(percent, name, enabled, stateWifi, stateData,
                stateBluetooth, stateSync)
          })
    }
    internal val empty: PowerTriggerEntry by lazy {
      AutoValue_PowerTriggerEntry(EMPTY_PERCENT, EMPTY_NAME, false, STATE_NONE, STATE_NONE,
          STATE_NONE, STATE_NONE)
    }
    internal val allEntriesMapper: Mapper<PowerTriggerEntry> by lazy {
      factory.all_entriesMapper()
    }
    internal val withPercentMapper: Mapper<PowerTriggerEntry> by lazy {
      factory.with_percentMapper()
    }
    internal val creator: Creator<PowerTriggerEntry> by lazy {
      factory.creator
    }

    private var insertManager: InsertManager? = null
    private var deleteManager: DeleteTriggerManager? = null
    private var updateManager: UpdateEnabledManager? = null

    @JvmStatic @CheckResult internal fun isEmpty(entry: PowerTriggerEntry): Boolean {
      return empty === entry
    }

    @JvmStatic @CheckResult internal fun queryAll(): SqlDelightStatement {
      return factory.all_entries()
    }

    @JvmStatic @CheckResult internal fun withPercent(percent: Int): SqlDelightStatement {
      return factory.with_percent(percent)
    }

    @JvmStatic @CheckResult internal fun insertTrigger(
        openHelper: SQLiteOpenHelper): InsertManager {
      var obj = insertManager
      if (obj == null) {
        val im = InsertManager(openHelper)
        insertManager = im
        obj = im
      }
      return obj
    }

    @JvmStatic @CheckResult internal fun deleteTrigger(
        openHelper: SQLiteOpenHelper): DeleteTriggerManager {
      var obj = deleteManager
      if (obj == null) {
        val dm = DeleteTriggerManager(openHelper)
        deleteManager = dm
        obj = dm
      }
      return obj
    }

    @JvmStatic @CheckResult internal fun updateEnabled(
        openHelper: SQLiteOpenHelper): UpdateEnabledManager {
      var obj = updateManager
      if (obj == null) {
        val um = UpdateEnabledManager(openHelper)
        updateManager = um
        obj = um
      }
      return obj
    }
  }
}
