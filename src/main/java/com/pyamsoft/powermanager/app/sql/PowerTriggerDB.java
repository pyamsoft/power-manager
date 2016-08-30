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
import java.util.concurrent.atomic.AtomicInteger;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PowerTriggerDB {
  @NonNull private static final Object lock = new Object();
  private static volatile PowerTriggerDB instance = null;

  @NonNull private final SqlBrite sqlBrite;
  @NonNull private final PowerTriggerOpenHelper openHelper;
  @NonNull private final Scheduler dbScheduler;
  @NonNull private final AtomicInteger openCount;
  @SuppressWarnings("WeakerAccess") volatile BriteDatabase briteDatabase;

  private PowerTriggerDB(final @NonNull Context context, final @NonNull Scheduler scheduler) {
    sqlBrite = SqlBrite.create();
    openHelper = new PowerTriggerOpenHelper(context.getApplicationContext());
    dbScheduler = scheduler;
    openCount = new AtomicInteger(0);
  }

  public static void setDB(@Nullable PowerTriggerDB db) {
    instance = db;
  }

  @CheckResult @NonNull public static PowerTriggerDB with(@NonNull Context context) {
    return with(context, Schedulers.io());
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull
  public static PowerTriggerDB with(@NonNull Context context, @NonNull Scheduler scheduler) {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new PowerTriggerDB(context, scheduler);
        }
      }
    }

    return instance;
  }

  @SuppressWarnings("WeakerAccess") synchronized void openDatabase() {
    if (briteDatabase == null) {
      Timber.d("Open new Database instance");
      briteDatabase = sqlBrite.wrapDatabaseHelper(openHelper, dbScheduler);
    }

    Timber.d("Increment open count to: %d", openCount.incrementAndGet());
  }

  @SuppressWarnings("WeakerAccess") synchronized void closeDatabase() {
    Timber.d("Decrement open count to: %d", openCount.decrementAndGet());

    if (openCount.get() == 0) {
      close();
    }
  }

  @CheckResult @NonNull public Observable<Long> insert(final @NonNull ContentValues contentValues) {
    // KLUDGE ugly
    final int percent = PowerTriggerEntry.asTrigger(contentValues).percent();
    return deleteWithPercent(percent).map(deleted -> {
      Timber.d("Delete result: %d", deleted);

      openDatabase();
      final long result = briteDatabase.insert(PowerTriggerEntry.TABLE_NAME, contentValues);
      closeDatabase();
      return result;
    });
  }

  @CheckResult @NonNull
  public Observable<Integer> update(final @NonNull ContentValues contentValues, final int percent) {
    openDatabase();

    return Observable.defer(() -> {
      final int result = briteDatabase.update(PowerTriggerEntry.TABLE_NAME, contentValues,
          PowerTriggerEntry.UPDATE_WITH_PERCENT, String.valueOf(percent));
      closeDatabase();
      return Observable.just(result);
    });
  }

  @NonNull @CheckResult public Observable<List<PowerTriggerEntry>> queryAll() {
    openDatabase();

    return briteDatabase.createQuery(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.ALL_ENTRIES)
        .mapToList(PowerTriggerEntry.FACTORY.all_entriesMapper()::map)
        .map(powerTriggerEntries -> {
          closeDatabase();
          return powerTriggerEntries;
        })
        .filter(padLockEntries -> padLockEntries != null);
  }

  @CheckResult @NonNull public Observable<Integer> deleteWithPercent(final int percent) {
    openDatabase();

    return Observable.defer(() -> {
      final int result =
          briteDatabase.delete(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.DELETE_WITH_PERCENT,
              Integer.toString(percent));
      closeDatabase();
      return Observable.just(result);
    });
  }

  @CheckResult @NonNull public Observable<Integer> deleteAll() {
    openDatabase();

    return Observable.defer(() -> {
      final int result =
          briteDatabase.delete(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.DELETE_ALL);
      closeDatabase();
      return Observable.just(result);
    });
  }

  public void close() {
    Timber.d("Close and recycle database connection");
    openCount.set(0);
    if (briteDatabase != null) {
      briteDatabase.close();
      briteDatabase = null;
    }
  }
}
