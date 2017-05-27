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
import com.squareup.sqldelight.SqlDelightStatement

@AutoValue abstract class PowerTriggerEntry : PowerTriggerModel {

  class DeleteTriggerManager internal constructor(openHelper: SQLiteOpenHelper) {
    private val deleteTrigger: PowerTriggerModel.Delete_trigger = PowerTriggerModel.Delete_trigger(
        openHelper.writableDatabase)

    @CheckResult fun executeProgram(percent: Int): Int {
      deleteTrigger.bind(percent)
      return deleteTrigger.program.executeUpdateDelete()
    }
  }

  class InsertManager internal constructor(openHelper: SQLiteOpenHelper) {
    private val insertTrigger: PowerTriggerModel.Insert_trigger = PowerTriggerModel.Insert_trigger(
        openHelper.writableDatabase)

    @CheckResult fun executeProgram(newEntry: PowerTriggerEntry): Long {
      insertTrigger.bind(newEntry.percent(), newEntry.name(), newEntry.enabled(),
          newEntry.available(), newEntry.toggleWifi(), newEntry.toggleData(),
          newEntry.toggleBluetooth(), newEntry.toggleSync(), newEntry.enableWifi(),
          newEntry.enableData(), newEntry.enableBluetooth(), newEntry.enableSync())
      return insertTrigger.program.executeInsert()
    }
  }

  class UpdateAvailabeManager internal constructor(openHelper: SQLiteOpenHelper) {
    private val updateAvailable: PowerTriggerModel.Update_available = PowerTriggerModel.Update_available(
        openHelper.writableDatabase)

    @CheckResult fun executeProgram(available: Boolean, percent: Int): Int {
      updateAvailable.bind(available, percent)
      return updateAvailable.program.executeUpdateDelete()
    }
  }

  class UpdateEnabledManager internal constructor(openHelper: SQLiteOpenHelper) {
    private val updateEnabled: PowerTriggerModel.Update_enabled = PowerTriggerModel.Update_enabled(
        openHelper.writableDatabase)

    @CheckResult fun executeProgram(enabled: Boolean, percent: Int): Int {
      updateEnabled.bind(enabled, percent)
      return updateEnabled.program.executeUpdateDelete()
    }
  }

  companion object {

    @JvmField val EMPTY_NAME = PowerTriggerEntry::class.java.name + ".__TRIGGER_NAME_EMPTY"
    @JvmField val EMPTY_PERCENT = -1

    @Volatile private var empty: PowerTriggerEntry? = null
    @Volatile private var factory: PowerTriggerModel.Factory<PowerTriggerEntry>? = null
    @Volatile private var allEntriesMapper: PowerTriggerModel.Mapper<PowerTriggerEntry>? = null
    @Volatile private var withPercentMapper: PowerTriggerModel.Mapper<PowerTriggerEntry>? = null

    @JvmStatic @CheckResult fun empty(): PowerTriggerEntry {
      if (empty == null) {
        synchronized(PowerTriggerEntry::class.java) {
          if (empty == null) {
            empty = AutoValue_PowerTriggerEntry(EMPTY_PERCENT, EMPTY_NAME, false, false, false,
                false, false, false, false, false, false, false)
          }
        }
      }

      return empty!!
    }

    @JvmStatic @CheckResult private fun factory(): PowerTriggerModel.Factory<PowerTriggerEntry> {
      if (factory == null) {
        synchronized(PowerTriggerEntry::class.java) {
          if (factory == null) {
            factory = PowerTriggerModel.Factory(
                PowerTriggerModel.Creator<PowerTriggerEntry> { percent, name, enabled, available, toggleWifi, toggleData, toggleBluetooth, toggleSync, enableWifi, enableData, enableBluetooth, enableSync ->
                  AutoValue_PowerTriggerEntry(percent, name, enabled, available, toggleWifi,
                      toggleData, toggleBluetooth, toggleSync, enableWifi, enableData,
                      enableBluetooth, enableSync)
                })
          }
        }
      }

      return factory!!
    }

    /**
     * public
     */
    @JvmStatic @CheckResult internal fun allEntriesMapper(): PowerTriggerModel.Mapper<PowerTriggerEntry> {
      if (allEntriesMapper == null) {
        synchronized(PowerTriggerEntry::class.java) {
          if (allEntriesMapper == null) {
            allEntriesMapper = factory().all_entriesMapper()
          }
        }
      }

      return allEntriesMapper!!
    }

    /**
     * public
     */
    @JvmStatic @CheckResult internal fun withPercentMapper(): PowerTriggerModel.Mapper<PowerTriggerEntry> {
      if (withPercentMapper == null) {
        synchronized(PowerTriggerEntry::class.java) {
          if (withPercentMapper == null) {
            withPercentMapper = factory().with_percentMapper()
          }
        }
      }

      return withPercentMapper!!
    }

    @JvmStatic @CheckResult fun creator(): PowerTriggerModel.Creator<PowerTriggerEntry> {
      return factory().creator
    }

    @JvmStatic @CheckResult fun isEmpty(entry: PowerTriggerEntry): Boolean {
      return empty() === entry
    }

    /**
     * public
     */
    @JvmStatic @CheckResult internal fun queryAll(): SqlDelightStatement {
      return factory().all_entries()
    }

    /**
     * public
     */
    @JvmStatic @CheckResult internal fun withPercent(percent: Int): SqlDelightStatement {
      return factory().with_percent(percent)
    }

    /**
     * public
     */
    @JvmStatic @CheckResult internal fun insertTrigger(
        openHelper: SQLiteOpenHelper): InsertManager {
      return InsertManager(openHelper)
    }

    /**
     * public
     */
    @JvmStatic @CheckResult internal fun deleteTrigger(
        openHelper: SQLiteOpenHelper): DeleteTriggerManager {
      return DeleteTriggerManager(openHelper)
    }

    /**
     * public
     */
    @JvmStatic @CheckResult internal fun updateAvailable(
        openHelper: SQLiteOpenHelper): UpdateAvailabeManager {
      return UpdateAvailabeManager(openHelper)
    }

    /**
     * public
     */
    @JvmStatic @CheckResult internal fun updateEnabled(
        openHelper: SQLiteOpenHelper): UpdateEnabledManager {
      return UpdateEnabledManager(openHelper)
    }
  }
}