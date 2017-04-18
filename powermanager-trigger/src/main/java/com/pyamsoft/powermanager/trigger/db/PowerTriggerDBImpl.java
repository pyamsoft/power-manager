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
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.squareup.sqldelight.SqlDelightStatement;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.schedulers.Schedulers;
import timber.log.Timber;

class PowerTriggerDBImpl implements PowerTriggerDB {

  @SuppressWarnings("WeakerAccess") @NonNull final BriteDatabase briteDatabase;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerTriggerOpenHelper openHelper;
  @SuppressWarnings("WeakerAccess") @NonNull final CompositeDisposable compositeDisposable;

  @Inject PowerTriggerDBImpl(@NonNull Context context) {
    openHelper = new PowerTriggerOpenHelper(context);
    briteDatabase = new SqlBrite.Builder().build().wrapDatabaseHelper(openHelper, Schedulers.io());
    compositeDisposable = new CompositeDisposable();
  }

  @SuppressWarnings("WeakerAccess") synchronized void openDatabase() {
    // After a 1 minute timeout, close the DB
    compositeDisposable.add(
        Flowable.defer(() -> Flowable.timer(1, TimeUnit.MINUTES)).subscribe(aLong -> {
          Timber.w("PowerTriggerDB is closed");
          briteDatabase.close();
        }, throwable -> Timber.e(throwable, "onError closing database")));
  }

  @Override @CheckResult @NonNull public Completable insert(@NonNull PowerTriggerEntry entry) {
    return Completable.fromCallable(() -> {
      if (PowerTriggerEntry.isEmpty(entry)) {
        throw new IllegalStateException("Cannot insert empty entries");
      }

      Timber.i("DB: INSERT");
      openDatabase();
      final int percent = entry.percent();
      return deleteWithPercentUnguarded(percent);
    })
        .andThen(Completable.fromCallable(
            () -> PowerTriggerEntry.insertTrigger(openHelper).executeProgram(entry)));
  }

  @NonNull @Override public Completable updateAvailable(boolean available, int percent) {
    return Completable.fromCallable(() -> {
      Timber.i("DB: UPDATE AVAILABLE");
      openDatabase();
      return PowerTriggerEntry.updateAvailable(openHelper).executeProgram(available, percent);
    });
  }

  @NonNull @Override public Completable updateEnabled(boolean enabled, int percent) {
    return Completable.fromCallable(() -> {
      Timber.i("DB: UPDATE ENABLED");
      openDatabase();
      return PowerTriggerEntry.updateEnabled(openHelper).executeProgram(enabled, percent);
    });
  }

  @Override @NonNull @CheckResult public Single<List<PowerTriggerEntry>> queryAll() {
    return Single.defer(() -> {
      Timber.i("DB: QUERY ALL");
      openDatabase();

      SqlDelightStatement statement = PowerTriggerEntry.queryAll();
      return RxJavaInterop.toV2Single(
          briteDatabase.createQuery(statement.tables, statement.statement, statement.args)
              .mapToList(PowerTriggerEntry.allEntriesMapper()::map)
              .firstOrDefault(Collections.emptyList())
              .toSingle());
    });
  }

  @Override @NonNull @CheckResult public Single<PowerTriggerEntry> queryWithPercent(int percent) {
    return Single.defer(() -> {
      Timber.i("DB: QUERY PERCENT");
      openDatabase();

      SqlDelightStatement statement = PowerTriggerEntry.withPercent(percent);
      return RxJavaInterop.toV2Single(
          briteDatabase.createQuery(statement.tables, statement.statement, statement.args)
              .mapToOneOrDefault(PowerTriggerEntry.withPercentMapper()::map,
                  PowerTriggerEntry.empty())
              .firstOrDefault(PowerTriggerEntry.empty())
              .toSingle());
    });
  }

  @Override @CheckResult @NonNull public Completable deleteWithPercent(int percent) {
    return Completable.fromCallable(() -> {
      Timber.i("DB: DELETE PERCENT");
      openDatabase();
      return deleteWithPercentUnguarded(percent);
    });
  }

  @SuppressWarnings("WeakerAccess") @VisibleForTesting @CheckResult int deleteWithPercentUnguarded(
      int percent) {
    return PowerTriggerEntry.deleteTrigger(openHelper).executeProgram(percent);
  }

  @Override @CheckResult @NonNull public Completable deleteAll() {
    return Completable.fromAction(() -> {
      Timber.i("DB: DELETE ALL");
      briteDatabase.execute(PowerTriggerEntry.DELETE_ALL);
      briteDatabase.close();
      compositeDisposable.clear();
    }).andThen(deleteDatabase());
  }

  @NonNull @Override public Completable deleteDatabase() {
    return Completable.fromAction(openHelper::deleteDatabase);
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
