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

package com.pyamsoft.powermanager.trigger.db;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.pyamsoft.pydroid.helper.Checker;
import com.squareup.sqldelight.SqlDelightStatement;

@AutoValue public abstract class PowerTriggerEntry implements PowerTriggerModel {

  @NonNull public static final String EMPTY_NAME =
      PowerTriggerEntry.class.getName() + ".__TRIGGER_NAME_EMPTY";
  public static final int EMPTY_PERCENT = -1;

  @Nullable private static volatile PowerTriggerEntry empty;
  @Nullable private static volatile Factory<PowerTriggerEntry> factory;
  @Nullable private static volatile Mapper<PowerTriggerEntry> allEntriesMapper;
  @Nullable private static volatile Mapper<PowerTriggerEntry> withPercentMapper;

  @CheckResult @NonNull public static PowerTriggerEntry empty() {
    if (empty == null) {
      synchronized (PowerTriggerEntry.class) {
        if (empty == null) {
          empty =
              new AutoValue_PowerTriggerEntry(EMPTY_PERCENT, EMPTY_NAME, false, false, false, false,
                  false, false, false, false, false, false);
        }
      }
    }

    return Checker.checkNonNull(empty);
  }

  @CheckResult @NonNull private static Factory<PowerTriggerEntry> factory() {
    if (factory == null) {
      synchronized (PowerTriggerEntry.class) {
        if (factory == null) {
          factory = new Factory<>(AutoValue_PowerTriggerEntry::new);
        }
      }
    }

    return Checker.checkNonNull(factory);
  }

  /**
   * public
   */
  @CheckResult @NonNull static Mapper<PowerTriggerEntry> allEntriesMapper() {
    if (allEntriesMapper == null) {
      synchronized (PowerTriggerEntry.class) {
        if (allEntriesMapper == null) {
          allEntriesMapper = factory().all_entriesMapper();
        }
      }
    }

    return Checker.checkNonNull(allEntriesMapper);
  }

  /**
   * public
   */
  @CheckResult @NonNull static Mapper<PowerTriggerEntry> withPercentMapper() {
    if (withPercentMapper == null) {
      synchronized (PowerTriggerEntry.class) {
        if (withPercentMapper == null) {
          withPercentMapper = factory().with_percentMapper();
        }
      }
    }

    return Checker.checkNonNull(withPercentMapper);
  }

  @CheckResult @NonNull public static Creator<PowerTriggerEntry> creator() {
    return factory().creator;
  }

  @CheckResult public static boolean isEmpty(@NonNull PowerTriggerEntry entry) {
    return empty() == entry;
  }

  /**
   * public
   */
  @CheckResult @NonNull static SqlDelightStatement queryAll() {
    return factory().all_entries();
  }

  /**
   * public
   */
  @CheckResult @NonNull static SqlDelightStatement withPercent(int percent) {
    return factory().with_percent(percent);
  }

  /**
   * public
   */
  @CheckResult @NonNull static InsertManager insertTrigger(@NonNull SQLiteOpenHelper openHelper) {
    return new InsertManager(openHelper);
  }

  /**
   * public
   */
  @CheckResult @NonNull static DeleteTriggerManager deleteTrigger(
      @NonNull SQLiteOpenHelper openHelper) {
    return new DeleteTriggerManager(openHelper);
  }

  /**
   * public
   */
  @CheckResult @NonNull static UpdateAvailabeManager updateAvailable(
      @NonNull SQLiteOpenHelper openHelper) {
    return new UpdateAvailabeManager(openHelper);
  }

  /**
   * public
   */
  @CheckResult @NonNull static UpdateEnabledManager updateEnabled(
      @NonNull SQLiteOpenHelper openHelper) {
    return new UpdateEnabledManager(openHelper);
  }

  @SuppressWarnings("WeakerAccess") static class DeleteTriggerManager {
    @NonNull private final Delete_trigger deleteTrigger;

    DeleteTriggerManager(@NonNull SQLiteOpenHelper openHelper) {
      this.deleteTrigger = new Delete_trigger(openHelper.getWritableDatabase());
    }

    @CheckResult public int executeProgram(int percent) {
      deleteTrigger.bind(percent);
      return deleteTrigger.program.executeUpdateDelete();
    }
  }

  @SuppressWarnings("WeakerAccess") static class InsertManager {
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

  @SuppressWarnings("WeakerAccess") static class UpdateAvailabeManager {
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
