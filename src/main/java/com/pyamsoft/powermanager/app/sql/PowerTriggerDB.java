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

package com.pyamsoft.powermanager.app.sql;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import java.util.List;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public final class PowerTriggerDB {
  @NonNull private final BriteDatabase briteDatabase;
  private static volatile Delegate instance = null;

  private PowerTriggerDB(final @NonNull Context context, final @NonNull Scheduler dbScheduler) {
    final SqlBrite sqlBrite = SqlBrite.create();
    final PowerTriggerOpenHelper openHelper =
        new PowerTriggerOpenHelper(context.getApplicationContext());
    briteDatabase = sqlBrite.wrapDatabaseHelper(openHelper, dbScheduler);
  }

  @CheckResult @NonNull public final BriteDatabase getDatabase() {
    return briteDatabase;
  }

  public static void setDelegate(@Nullable Delegate delegate) {
    instance = delegate;
  }

  @CheckResult @NonNull public static Delegate with(@NonNull Context context) {
    return with(context, Schedulers.io());
  }

  @CheckResult @NonNull
  public static Delegate with(@NonNull Context context, @NonNull Scheduler scheduler) {
    if (instance == null) {
      synchronized (Delegate.class) {
        if (instance == null) {
          instance = new Delegate(context, scheduler);
        }
      }
    }

    return instance;
  }

  public static class Delegate {

    @NonNull private final PowerTriggerDB database;

    public Delegate(@NonNull Context context) {
      this(context, Schedulers.io());
    }

    public Delegate(@NonNull Context context, @NonNull Scheduler scheduler) {
      this.database = new PowerTriggerDB(context.getApplicationContext(), scheduler);
    }

    public void insert(final @NonNull ContentValues contentValues) {
      database.getDatabase().insert(PowerTriggerEntry.TABLE_NAME, contentValues);
    }

    @CheckResult public int update(final @NonNull ContentValues contentValues, final int percent) {
      return database.getDatabase()
          .update(PowerTriggerEntry.TABLE_NAME, contentValues,
              PowerTriggerEntry.UPDATE_WITH_PERCENT, String.valueOf(percent));
    }

    @NonNull @CheckResult public Observable<PowerTriggerEntry> queryWithPercent(final int percent) {
      return database.getDatabase()
          .createQuery(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.WITH_PERCENT,
              Integer.toString(percent))
          .mapToOneOrDefault(PowerTriggerEntry.FACTORY.with_percentMapper()::map,
              PowerTriggerEntry.empty())
          .filter(padLockEntry -> padLockEntry != null);
    }

    @NonNull @CheckResult public Observable<List<PowerTriggerEntry>> queryAll() {
      return database.getDatabase()
          .createQuery(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.ALL_ENTRIES)
          .mapToList(PowerTriggerEntry.FACTORY.all_entriesMapper()::map)
          .filter(padLockEntries -> padLockEntries != null);
    }

    public void deleteWithPercent(final int percent) {
      database.getDatabase()
          .delete(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.DELETE_WITH_PERCENT,
              Integer.toString(percent));
    }

    public void deleteAll() {
      database.getDatabase().delete(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.DELETE_ALL);
    }
  }
}
