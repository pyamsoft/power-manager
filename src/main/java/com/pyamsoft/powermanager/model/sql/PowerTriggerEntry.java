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

package com.pyamsoft.powermanager.model.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;

@AutoValue public abstract class PowerTriggerEntry implements PowerTriggerModel {

  @NonNull public static final String EMPTY_NAME =
      PowerTriggerEntry.class.getName() + ".__TRIGGER_NAME_EMPTY";
  public static final int EMPTY_PERCENT = -1;
  // SQLDelight does not yet support delete strings
  @NonNull public static final String DELETE_WITH_PERCENT = "percent = ?";
  @NonNull public static final String DELETE_ALL = "1=1";
  // SQLDelight does not yet support update strings
  @NonNull public static final String UPDATE_WITH_PERCENT = "percent = ?";

  @SuppressWarnings("StaticInitializerReferencesSubClass") @NonNull
  private static final Factory<PowerTriggerEntry> FACTORY =
      new Factory<>(AutoValue_PowerTriggerEntry::new);

  @NonNull public static final Creator<PowerTriggerEntry> CREATOR = FACTORY.creator;
  @NonNull public static final Mapper<PowerTriggerEntry> ALL_ENTRIES_MAPPER =
      FACTORY.all_entriesMapper();
  @NonNull public static final Mapper<PowerTriggerEntry> WITH_PERCENT_MAPPER =
      FACTORY.with_percentMapper();

  @NonNull @CheckResult public static PowerTriggerEntry asTrigger(@NonNull ContentValues values) {
    final int percent = values.getAsInteger(PowerTriggerEntry.PERCENT);
    final String name = values.getAsString(PowerTriggerEntry.NAME);
    final boolean enabled = values.getAsBoolean(PowerTriggerEntry.ENABLED);
    final boolean available = values.getAsBoolean(PowerTriggerEntry.AVAILABLE);
    final boolean toggleWifi = values.getAsBoolean(PowerTriggerEntry.TOGGLEWIFI);
    final boolean toggleData = values.getAsBoolean(PowerTriggerEntry.TOGGLEDATA);
    final boolean toggleBluetooth = values.getAsBoolean(PowerTriggerEntry.TOGGLEBLUETOOTH);
    final boolean toggleSync = values.getAsBoolean(PowerTriggerEntry.TOGGLESYNC);
    final boolean enableWifi = values.getAsBoolean(PowerTriggerEntry.ENABLEWIFI);
    final boolean enableData = values.getAsBoolean(PowerTriggerEntry.ENABLEDATA);
    final boolean enableBluetooth = values.getAsBoolean(PowerTriggerEntry.ENABLEBLUETOOTH);
    final boolean enableSync = values.getAsBoolean(PowerTriggerEntry.ENABLESYNC);
    return CREATOR.create(percent, name, enabled, available, toggleWifi, toggleData,
        toggleBluetooth, toggleSync, enableWifi, enableData, enableBluetooth, enableSync);
  }

  @NonNull @CheckResult
  public static ContentValues asContentValues(@NonNull PowerTriggerEntry entry) {
    return new Marshal(entry).asContentValues();
  }

  @CheckResult @NonNull public static PowerTriggerEntry empty() {
    return new AutoValue_PowerTriggerEntry(EMPTY_PERCENT, EMPTY_NAME, false, false, false, false,
        false, false, false, false, false, false);
  }

  @CheckResult public static boolean isEmpty(@NonNull PowerTriggerEntry entry) {
    return entry.percent() == EMPTY_PERCENT || EMPTY_NAME.equals(entry.name());
  }

  @CheckResult public static boolean isEmpty(@NonNull ContentValues values) {
    final int percent = values.getAsInteger(PowerTriggerEntry.PERCENT);
    final String name = values.getAsString(PowerTriggerEntry.NAME);
    return percent == EMPTY_PERCENT || EMPTY_NAME.equals(name) || name.isEmpty();
  }

  @CheckResult @NonNull
  public static PowerTriggerEntry updatedEnabled(@NonNull PowerTriggerEntry old, boolean enabled) {
    return updated(old, enabled, old.available());
  }

  @CheckResult @NonNull
  public static PowerTriggerEntry updatedAvailable(@NonNull PowerTriggerEntry old,
      boolean available) {
    return updated(old, old.enabled(), available);
  }

  @CheckResult @NonNull
  private static PowerTriggerEntry updated(@NonNull PowerTriggerEntry old, boolean enabled,
      boolean available) {
    return new AutoValue_PowerTriggerEntry(old.percent(), old.name(), enabled, available,
        old.toggleWifi(), old.toggleData(), old.toggleBluetooth(), old.toggleSync(),
        old.enableWifi(), old.enableData(), old.enableBluetooth(), old.enableSync());
  }

  @CheckResult @NonNull
  public static InsertManager insertNewTrigger(@NonNull SQLiteOpenHelper openHelper) {
    return new InsertManager(openHelper);
  }

  public static class InsertManager {
    @NonNull private final PowerTriggerEntry.Insert_trigger insertTrigger;

    InsertManager(@NonNull SQLiteOpenHelper openHelper) {
      this.insertTrigger = new PowerTriggerEntry.Insert_trigger(openHelper.getWritableDatabase());
    }

    @CheckResult public long executeProgram(@NonNull PowerTriggerEntry newEntry) {
      insertTrigger.bind(newEntry.percent(), newEntry.name(), newEntry.enabled(),
          newEntry.available(), newEntry.toggleWifi(), newEntry.toggleData(),
          newEntry.toggleBluetooth(), newEntry.toggleSync(), newEntry.enableWifi(),
          newEntry.enableData(), newEntry.enableBluetooth(), newEntry.enableSync());
      return insertTrigger.program.executeInsert();
    }
  }
}
