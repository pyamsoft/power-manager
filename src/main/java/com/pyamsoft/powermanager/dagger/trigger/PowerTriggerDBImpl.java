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

package com.pyamsoft.powermanager.dagger.trigger;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import rx.Observable;
import rx.Scheduler;
import timber.log.Timber;

class PowerTriggerDBImpl implements PowerTriggerDB {

  @SuppressWarnings("WeakerAccess") @NonNull final BriteDatabase briteDatabase;
  @NonNull private final AtomicInteger openCount;
  @NonNull private final PowerTriggerOpenHelper openHelper;

  @Inject PowerTriggerDBImpl(@NonNull Context context, @NonNull Scheduler scheduler) {
    openHelper = new PowerTriggerOpenHelper(context);
    openCount = new AtomicInteger(0);
    briteDatabase = new SqlBrite.Builder().build().wrapDatabaseHelper(openHelper, scheduler);
  }

  @SuppressWarnings("WeakerAccess") @VisibleForTesting @CheckResult int getOpenCount() {
    return openCount.get();
  }

  @SuppressWarnings("WeakerAccess") synchronized void openDatabase() {
    Timber.d("Increment open count to: %d", openCount.incrementAndGet());
  }

  @SuppressWarnings("WeakerAccess") synchronized void closeDatabase() {
    if (openCount.get() > 0) {
      Timber.d("Decrement open count to: %d", openCount.decrementAndGet());
    }

    if (openCount.get() == 0) {
      Timber.d("Close and recycle database connection");
      briteDatabase.close();
    }
  }

  @Override @CheckResult @NonNull public Observable<Long> insert(@NonNull PowerTriggerEntry entry) {
    return Observable.defer(() -> {
      Timber.i("DB: INSERT");
      openDatabase();
      final int percent = entry.percent();
      return deleteWithPercentUnguarded(percent);
    }).map(deleted -> {
      Timber.d("Delete result: %d", deleted);
      final long result = PowerTriggerEntry.insertTrigger(openHelper).executeProgram(entry);
      closeDatabase();
      return result;
    });
  }

  @Override @CheckResult @NonNull
  public Observable<Integer> update(final @NonNull ContentValues contentValues, final int percent) {
    return Observable.defer(() -> {
      Timber.i("DB: UPDATE");
      openDatabase();
      final int result = briteDatabase.update(PowerTriggerEntry.TABLE_NAME, contentValues,
          PowerTriggerEntry.UPDATE_WITH_PERCENT, String.valueOf(percent));
      closeDatabase();
      return Observable.just(result);
    });
  }

  @Override @NonNull @CheckResult public Observable<List<PowerTriggerEntry>> queryAll() {
    return Observable.defer(() -> {
      Timber.i("DB: QUERY ALL");
      openDatabase();
      return briteDatabase.createQuery(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.ALL_ENTRIES)
          .mapToList(PowerTriggerEntry.ALL_ENTRIES_MAPPER::map);
    }).map(result -> {
      closeDatabase();
      return result;
    });
  }

  @Override @NonNull @CheckResult
  public Observable<PowerTriggerEntry> queryWithPercent(int percent) {
    return Observable.defer(() -> {
      Timber.i("DB: QUERY PERCENT");
      openDatabase();
      return briteDatabase.createQuery(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.WITH_PERCENT,
          Integer.toString(percent))
          .mapToOneOrDefault(PowerTriggerEntry.WITH_PERCENT_MAPPER::map, PowerTriggerEntry.empty());
    }).map(result -> {
      closeDatabase();
      return result;
    });
  }

  @Override @CheckResult @NonNull public Observable<Integer> deleteWithPercent(final int percent) {
    return Observable.defer(() -> {
      Timber.i("DB: DELETE PERCENT");
      openDatabase();
      final Observable<Integer> result = deleteWithPercentUnguarded(percent);
      closeDatabase();
      return result;
    });
  }

  @SuppressWarnings("WeakerAccess") @VisibleForTesting @NonNull @CheckResult
  Observable<Integer> deleteWithPercentUnguarded(int percent) {
    return Observable.just(PowerTriggerEntry.deleteTrigger(openHelper).executeProgram(percent));
  }

  @Override @CheckResult @NonNull public Observable<Integer> deleteAll() {
    return Observable.defer(() -> {
      Timber.i("DB: DELETE ALL");
      openDatabase();

      final int result =
          briteDatabase.delete(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.DELETE_ALL);

      closeDatabase();
      return Observable.just(result);
    });
  }

  @Override public void deleteDatabase() {
    openHelper.deleteDatabase();
  }

  @SuppressWarnings("WeakerAccess") static class PowerTriggerOpenHelper extends SQLiteOpenHelper {

    @NonNull private static final String DB_NAME = "power_trigger_db";
    private static final int DATABASE_VERSION = 1;
    @NonNull private final Context appContext;

    PowerTriggerOpenHelper(final @NonNull Context context) {
      super(context.getApplicationContext(), DB_NAME, null, DATABASE_VERSION);
      appContext = context.getApplicationContext();
    }

    void deleteDatabase() {
      appContext.deleteDatabase(DB_NAME);
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
}
