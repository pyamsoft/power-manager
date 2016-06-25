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

package com.pyamsoft.powermanager.app.trigger;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.trigger.PowerTriggerEntry;
import com.squareup.sqlbrite.BriteDatabase;
import java.util.List;
import rx.Observable;
import timber.log.Timber;

public final class PowerTriggerOpenHelper extends SQLiteOpenHelper {

  private static final int DATABASE_VERSION = 1;

  public PowerTriggerOpenHelper(final @NonNull Context context) {
    super(context.getApplicationContext(), "power_trigger_db", null, DATABASE_VERSION);
  }

  @SuppressLint("NewApi") public static void newTransaction(final @NonNull Context context,
      final @NonNull Runnable runnable) {
    final Context appContext = context.getApplicationContext();
    try (
        final BriteDatabase.Transaction transaction = PowerTriggerDB.with(appContext)
            .newTransaction()) {
      runnable.run();
      transaction.markSuccessful();
    }
  }

  public static void insert(final @NonNull Context context,
      final @NonNull ContentValues contentValues) {
    final Context appContext = context.getApplicationContext();
    PowerTriggerDB.with(appContext).insert(PowerTriggerEntry.TABLE_NAME, contentValues);
  }

  @NonNull @CheckResult
  public static Observable<PowerTriggerEntry> queryWithPercent(final @NonNull Context context,
      final int percent) {
    final Context appContext = context.getApplicationContext();
    return PowerTriggerDB.with(appContext)
        .createQuery(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.WITH_PERCENT,
            Integer.toString(percent))
        .mapToOneOrDefault(PowerTriggerEntry.FACTORY.with_percentMapper()::map,
            PowerTriggerEntry.empty())
        .filter(padLockEntry -> padLockEntry != null);
  }

  @NonNull @CheckResult
  public static Observable<List<PowerTriggerEntry>> queryAll(final @NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    return PowerTriggerDB.with(appContext)
        .createQuery(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.ALL_ENTRIES)
        .mapToList(PowerTriggerEntry.FACTORY.all_entriesMapper()::map)
        .filter(padLockEntries -> padLockEntries != null);
  }

  public static void deleteWithPercent(final @NonNull Context context, final int percent) {
    final Context appContext = context.getApplicationContext();
    PowerTriggerDB.with(appContext)
        .delete(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.DELETE_WITH_PERCENT,
            Integer.toString(percent));
  }

  public static void deleteAll(final @NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    PowerTriggerDB.with(appContext)
        .delete(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.DELETE_ALL);
  }

  @Override public void onCreate(@NonNull SQLiteDatabase sqLiteDatabase) {
    Timber.d("onCreate");
    sqLiteDatabase.execSQL(PowerTriggerEntry.CREATE_TABLE);
  }

  @Override
  public void onUpgrade(@NonNull SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    Timber.d("onUpgrade from old version %d to new %d", oldVersion, newVersion);
  }
}
