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

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.SqlDelightStatement;

@AutoValue public abstract class PowerTriggerEntry implements PowerTriggerModel {

  @NonNull public static final String EMPTY_NAME =
      PowerTriggerEntry.class.getName() + ".__TRIGGER_NAME_EMPTY";
  public static final int EMPTY_PERCENT = -1;
  @SuppressWarnings("StaticInitializerReferencesSubClass") @NonNull
  public static final PowerTriggerEntry EMPTY =
      new AutoValue_PowerTriggerEntry(EMPTY_PERCENT, EMPTY_NAME, false, false, false, false, false,
          false, false, false, false, false);
  @SuppressWarnings("StaticInitializerReferencesSubClass") @NonNull
  private static final Factory<PowerTriggerEntry> FACTORY =
      new Factory<>(AutoValue_PowerTriggerEntry::new);
  @NonNull public static final Creator<PowerTriggerEntry> CREATOR = FACTORY.creator;
  @NonNull public static final Mapper<PowerTriggerEntry> ALL_ENTRIES_MAPPER =
      FACTORY.all_entriesMapper();
  @NonNull public static final Mapper<PowerTriggerEntry> WITH_PERCENT_MAPPER =
      FACTORY.with_percentMapper();

  @CheckResult public static boolean isEmpty(@NonNull PowerTriggerEntry entry) {
    return EMPTY == entry;
  }

  @CheckResult @NonNull public static SqlDelightStatement queryAll() {
    return FACTORY.all_entries();
  }

  @CheckResult @NonNull public static SqlDelightStatement withPercent(int percent) {
    return FACTORY.with_percent(percent);
  }

  @CheckResult @NonNull
  public static InsertManager insertTrigger(@NonNull SQLiteOpenHelper openHelper) {
    return new InsertManager(openHelper);
  }

  @CheckResult @NonNull
  public static DeleteTriggerManager deleteTrigger(@NonNull SQLiteOpenHelper openHelper) {
    return new DeleteTriggerManager(openHelper);
  }

  @CheckResult @NonNull
  public static UpdateAvailabeManager updateAvailable(@NonNull SQLiteOpenHelper openHelper) {
    return new UpdateAvailabeManager(openHelper);
  }

  @CheckResult @NonNull
  public static UpdateEnabledManager updateEnabled(@NonNull SQLiteOpenHelper openHelper) {
    return new UpdateEnabledManager(openHelper);
  }

  @SuppressWarnings("WeakerAccess") public static class DeleteTriggerManager {
    @NonNull private final Delete_trigger deleteTrigger;

    DeleteTriggerManager(@NonNull SQLiteOpenHelper openHelper) {
      this.deleteTrigger = new Delete_trigger(openHelper.getWritableDatabase());
    }

    @CheckResult public int executeProgram(int percent) {
      deleteTrigger.bind(percent);
      return deleteTrigger.program.executeUpdateDelete();
    }
  }

  @SuppressWarnings("WeakerAccess") public static class InsertManager {
    @NonNull private final Insert_trigger insertTrigger;

    InsertManager(@NonNull SQLiteOpenHelper openHelper) {
      this.insertTrigger = new Insert_trigger(openHelper.getWritableDatabase());
    }

    @CheckResult public long executeProgram(@NonNull PowerTriggerEntry newEntry) {
      insertTrigger.bind(newEntry.percent(), newEntry.name(), newEntry.enabled(),
          newEntry.available(), newEntry.toggleWifi(), newEntry.toggleData(),
          newEntry.toggleBluetooth(), newEntry.toggleSync(), newEntry.enableWifi(),
          newEntry.enableData(), newEntry.enableBluetooth(), newEntry.enableSync());
      return insertTrigger.program.executeInsert();
    }
  }

  @SuppressWarnings("WeakerAccess") public static class UpdateAvailabeManager {
    @NonNull private final Update_available updateAvailable;

    UpdateAvailabeManager(@NonNull SQLiteOpenHelper openHelper) {
      this.updateAvailable = new Update_available(openHelper.getWritableDatabase());
    }

    @CheckResult public int executeProgram(boolean available, int percent) {
      updateAvailable.bind(available, percent);
      return updateAvailable.program.executeUpdateDelete();
    }
  }

  @SuppressWarnings("WeakerAccess") public static class UpdateEnabledManager {
    @NonNull private final Update_enabled updateEnabled;

    UpdateEnabledManager(@NonNull SQLiteOpenHelper openHelper) {
      this.updateEnabled = new Update_enabled(openHelper.getWritableDatabase());
    }

    @CheckResult public int executeProgram(boolean enabled, int percent) {
      updateEnabled.bind(enabled, percent);
      return updateEnabled.program.executeUpdateDelete();
    }
  }
}
