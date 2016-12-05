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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroidrx.SubscriptionHelper;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import timber.log.Timber;

class PowerTriggerDBImpl implements PowerTriggerDB {

  @SuppressWarnings("WeakerAccess") @NonNull final BriteDatabase briteDatabase;
  @SuppressWarnings("WeakerAccess") @NonNull final PowerTriggerOpenHelper openHelper;
  @NonNull private final Scheduler dbScheduler;
  @SuppressWarnings("WeakerAccess") @Nullable Subscription dbOpenSubscription;

  @Inject PowerTriggerDBImpl(@NonNull Context context, @NonNull Scheduler scheduler) {
    dbScheduler = scheduler;
    openHelper = new PowerTriggerOpenHelper(context);
    briteDatabase = new SqlBrite.Builder().build().wrapDatabaseHelper(openHelper, scheduler);
  }

  @SuppressWarnings("WeakerAccess") synchronized void openDatabase() {
    SubscriptionHelper.unsubscribe(dbOpenSubscription);

    // After a 1 minute timeout, close the DB
    dbOpenSubscription = Observable.timer(1, TimeUnit.MINUTES)
        .map(aLong -> {
          briteDatabase.close();
          return true;
        })
        .subscribeOn(dbScheduler)
        .observeOn(dbScheduler)
        .subscribe(ignoreMe -> Timber.d("PowerTriggerDB is closed"),
            throwable -> Timber.e(throwable, "onError closing database"),
            () -> SubscriptionHelper.unsubscribe(dbOpenSubscription));
  }

  @Override @CheckResult @NonNull public Observable<Long> insert(@NonNull PowerTriggerEntry entry) {
    return Observable.defer(() -> {
      Timber.i("DB: INSERT");
      openDatabase();
      final int percent = entry.percent();
      return deleteWithPercentUnguarded(percent);
    }).map(deleted -> {
      Timber.d("Delete result: %d", deleted);
      return PowerTriggerEntry.insertTrigger(openHelper).executeProgram(entry);
    });
  }

  @NonNull @Override public Observable<Integer> updateAvailable(boolean available, int percent) {
    return Observable.defer(() -> {
      Timber.i("DB: UPDATE AVAILABLE");
      openDatabase();
      return Observable.fromCallable(
          () -> PowerTriggerEntry.updateAvailable(openHelper).executeProgram(available, percent));
    });
  }

  @NonNull @Override public Observable<Integer> updateEnabled(boolean enabled, int percent) {
    return Observable.defer(() -> {
      Timber.i("DB: UPDATE ENABLED");
      openDatabase();
      return Observable.fromCallable(
          () -> PowerTriggerEntry.updateEnabled(openHelper).executeProgram(enabled, percent));
    });
  }

  @Override @NonNull @CheckResult public Observable<List<PowerTriggerEntry>> queryAll() {
    return Observable.defer(() -> {
      Timber.i("DB: QUERY ALL");
      openDatabase();
      return briteDatabase.createQuery(PowerTriggerEntry.TABLE_NAME, PowerTriggerEntry.ALL_ENTRIES)
          .mapToList(PowerTriggerEntry.ALL_ENTRIES_MAPPER::map);
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
    });
  }

  @Override @CheckResult @NonNull public Observable<Integer> deleteWithPercent(int percent) {
    return Observable.defer(() -> {
      Timber.i("DB: DELETE PERCENT");
      openDatabase();
      return deleteWithPercentUnguarded(percent);
    });
  }

  @SuppressWarnings("WeakerAccess") @VisibleForTesting @NonNull @CheckResult
  Observable<Integer> deleteWithPercentUnguarded(int percent) {
    return Observable.just(PowerTriggerEntry.deleteTrigger(openHelper).executeProgram(percent));
  }

  @Override @CheckResult @NonNull public Observable<Integer> deleteAll() {
    return Observable.defer(() -> {
      Timber.i("DB: DELETE ALL");
      briteDatabase.execute(PowerTriggerEntry.DELETE_ALL);
      SubscriptionHelper.unsubscribe(dbOpenSubscription);
      briteDatabase.close();
      deleteDatabase();
      return Observable.just(1);
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
