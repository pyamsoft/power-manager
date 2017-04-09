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

package com.pyamsoft.powermanager.trigger.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.squareup.sqldelight.SqlDelightStatement;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.schedulers.Schedulers;
import timber.log.Timber;

class PowerTriggerDBImpl implements PowerTriggerDB {

  @SuppressWarnings("WeakerAccess") @NonNull final BriteDatabase briteDatabase;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerTriggerOpenHelper openHelper;
  @SuppressWarnings("WeakerAccess") @NonNull Disposable dbOpenDisposable = Disposables.empty();

  @Inject PowerTriggerDBImpl(@NonNull Context context) {
    openHelper = new PowerTriggerOpenHelper(context);
    briteDatabase = new SqlBrite.Builder().build().wrapDatabaseHelper(openHelper, Schedulers.io());
  }

  @SuppressWarnings("WeakerAccess") synchronized void openDatabase() {
    dbOpenDisposable = DisposableHelper.dispose(dbOpenDisposable);

    // After a 1 minute timeout, close the DB
    dbOpenDisposable =
        Flowable.defer(() -> Flowable.timer(1, TimeUnit.MINUTES)).subscribe(aLong -> {
          Timber.w("PowerTriggerDB is closed");
          briteDatabase.close();
        }, throwable -> Timber.e(throwable, "onError closing database"));
  }

  @Override @CheckResult @NonNull public Flowable<Long> insert(@NonNull PowerTriggerEntry entry) {
    return Flowable.fromCallable(() -> {
      if (PowerTriggerEntry.isEmpty(entry)) {
        throw new IllegalStateException("Cannot insert empty entries");
      }

      Timber.i("DB: INSERT");
      openDatabase();
      final int percent = entry.percent();
      return deleteWithPercentUnguarded(percent);
    }).map(deleted -> {
      Timber.d("Delete result: %d", deleted);
      return PowerTriggerEntry.insertTrigger(openHelper).executeProgram(entry);
    }).onBackpressureBuffer(16, () -> Timber.e("PowerTriggerDBImpl insert backpressure overflow"));
  }

  @NonNull @Override public Flowable<Integer> updateAvailable(boolean available, int percent) {
    return Flowable.fromCallable(() -> {
      Timber.i("DB: UPDATE AVAILABLE");
      openDatabase();
      return PowerTriggerEntry.updateAvailable(openHelper).executeProgram(available, percent);
    })
        .onBackpressureBuffer(16,
            () -> Timber.e("PowerTriggerDBImpl updateAvailable backpressure overflow"));
  }

  @NonNull @Override public Flowable<Integer> updateEnabled(boolean enabled, int percent) {
    return Flowable.fromCallable(() -> {
      Timber.i("DB: UPDATE ENABLED");
      openDatabase();
      return PowerTriggerEntry.updateEnabled(openHelper).executeProgram(enabled, percent);
    })
        .onBackpressureBuffer(16,
            () -> Timber.e("PowerTriggerDBImpl updateEnabled backpressure overflow"));
  }

  @Override @NonNull @CheckResult public Flowable<List<PowerTriggerEntry>> queryAll() {
    return Flowable.defer(() -> {
      Timber.i("DB: QUERY ALL");
      openDatabase();

      SqlDelightStatement statement = PowerTriggerEntry.queryAll();
      return RxJavaInterop.toV2Flowable(
          briteDatabase.createQuery(statement.tables, statement.statement, statement.args)
              .mapToList(PowerTriggerEntry.allEntriesMapper()::map));
    })
        .onBackpressureBuffer(16,
            () -> Timber.e("PowerTriggerDBImpl queryAll backpressure overflow"));
  }

  @Override @NonNull @CheckResult public Flowable<PowerTriggerEntry> queryWithPercent(int percent) {
    return Flowable.defer(() -> {
      Timber.i("DB: QUERY PERCENT");
      openDatabase();

      SqlDelightStatement statement = PowerTriggerEntry.withPercent(percent);
      return RxJavaInterop.toV2Flowable(
          briteDatabase.createQuery(statement.tables, statement.statement, statement.args)
              .mapToOneOrDefault(PowerTriggerEntry.withPercentMapper()::map,
                  PowerTriggerEntry.empty()));
    })
        .onBackpressureBuffer(16,
            () -> Timber.e("PowerTriggerDBImpl queryWithPercent backpressure overflow"));
  }

  @Override @CheckResult @NonNull public Flowable<Integer> deleteWithPercent(int percent) {
    return Flowable.fromCallable(() -> {
      Timber.i("DB: DELETE PERCENT");
      openDatabase();
      return deleteWithPercentUnguarded(percent);
    })
        .onBackpressureBuffer(16,
            () -> Timber.e("PowerTriggerDBImpl deleteWithPercent backpressure overflow"));
  }

  @SuppressWarnings("WeakerAccess") @VisibleForTesting @CheckResult int deleteWithPercentUnguarded(
      int percent) {
    return PowerTriggerEntry.deleteTrigger(openHelper).executeProgram(percent);
  }

  @Override @CheckResult @NonNull public Flowable<Integer> deleteAll() {
    return Flowable.fromCallable(() -> {
      Timber.i("DB: DELETE ALL");
      briteDatabase.execute(PowerTriggerEntry.DELETE_ALL);
      dbOpenDisposable = DisposableHelper.dispose(dbOpenDisposable);
      briteDatabase.close();
      deleteDatabase();
      return 1;
    })
        .onBackpressureBuffer(16,
            () -> Timber.e("PowerTriggerDBImpl deleteAll backpressure overflow"));
  }

  @NonNull @Override public Flowable<Boolean> deleteDatabase() {
    return Flowable.fromCallable(() -> {
      openHelper.deleteDatabase();
      return Boolean.TRUE;
    })
        .onBackpressureBuffer(16,
            () -> Timber.e("PowerTriggerDBImpl deleteDatabase backpressure overflow"));
  }

  private static class PowerTriggerOpenHelper extends SQLiteOpenHelper {

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
