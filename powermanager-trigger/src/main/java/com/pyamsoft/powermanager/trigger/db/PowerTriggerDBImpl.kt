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

package com.pyamsoft.powermanager.trigger.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.CheckResult
import android.support.annotation.VisibleForTesting
import com.pyamsoft.pydroid.helper.DisposableHelper
import com.squareup.sqlbrite.BriteDatabase
import com.squareup.sqlbrite.SqlBrite
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Completable
import io.reactivex.Single
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class PowerTriggerDBImpl @Inject constructor(context: Context) : PowerTriggerDB {

  private val briteDatabase: BriteDatabase
  private val openHelper = PowerTriggerOpenHelper(context.applicationContext)
  private var timerDisposable = DisposableHelper.dispose(null)

  init {
    briteDatabase = SqlBrite.Builder().build().wrapDatabaseHelper(openHelper, Schedulers.io())
  }

  @Synchronized fun openDatabase() {
    // After a 1 minute timeout, close the DB
    timerDisposable = DisposableHelper.dispose(timerDisposable)
    timerDisposable = Single.defer { Single.timer(1, TimeUnit.MINUTES) }.doAfterTerminate {
      timerDisposable = DisposableHelper.dispose(timerDisposable)
    }.subscribe({
      Timber.w("PowerTriggerDB is closed")
      briteDatabase.close()
    }, { Timber.e(it, "onError closing database") })
  }

  @CheckResult override fun insert(entry: PowerTriggerEntry): Completable {
    return Completable.fromCallable {
      if (PowerTriggerEntry.isEmpty(entry)) {
        throw IllegalStateException("Cannot insert empty entries")
      }

      Timber.i("DB: INSERT")
      openDatabase()
      val percent = entry.percent()
      return@fromCallable deleteWithPercentUnguarded(percent)
    }.andThen(Completable.fromCallable {
      PowerTriggerEntry.insertTrigger(openHelper).executeProgram(entry)
    })
  }

  override fun updateAvailable(entry: PowerTriggerEntry): Completable {
    return Completable.fromCallable {
      Timber.i("DB: UPDATE AVAILABLE")
      openDatabase()
      return@fromCallable PowerTriggerEntry.updateAvailable(openHelper).executeProgram(
          entry.available(), entry.percent())
    }
  }

  override fun updateEnabled(entry: PowerTriggerEntry): Completable {
    return Completable.fromCallable {
      Timber.i("DB: UPDATE ENABLED")
      openDatabase()
      return@fromCallable PowerTriggerEntry.updateEnabled(openHelper).executeProgram(
          entry.enabled(), entry.percent())
    }
  }

  @CheckResult override fun queryAll(): Single<List<PowerTriggerEntry>> {
    return Single.defer {
      Timber.i("DB: QUERY ALL")
      openDatabase()

      val statement = PowerTriggerEntry.queryAll()
      return@defer RxJavaInterop.toV2Single(
          briteDatabase.createQuery(statement.tables, statement.statement,
              *statement.args).mapToList {
            PowerTriggerEntry.allEntriesMapper.map(it)
          }.firstOrDefault(emptyList()).toSingle())
    }
  }

  @CheckResult override fun queryWithPercent(percent: Int): Single<PowerTriggerEntry> {
    return Single.defer {
      Timber.i("DB: QUERY PERCENT")
      openDatabase()

      val statement = PowerTriggerEntry.withPercent(percent)
      return@defer RxJavaInterop.toV2Single(
          briteDatabase.createQuery(statement.tables, statement.statement,
              *statement.args).mapToOneOrDefault({ PowerTriggerEntry.withPercentMapper.map(it) },
              PowerTriggerEntry.empty).firstOrDefault(PowerTriggerEntry.empty).toSingle())
    }
  }

  @CheckResult override fun deleteWithPercent(percent: Int): Completable {
    return Completable.fromCallable {
      Timber.i("DB: DELETE PERCENT")
      openDatabase()
      return@fromCallable deleteWithPercentUnguarded(percent)
    }
  }

  @VisibleForTesting @CheckResult fun deleteWithPercentUnguarded(percent: Int): Int {
    return PowerTriggerEntry.deleteTrigger(openHelper).executeProgram(percent)
  }

  @CheckResult override fun deleteAll(): Completable {
    return Completable.fromAction {
      Timber.i("DB: DELETE ALL")
      briteDatabase.execute(PowerTriggerModel.DELETE_ALL)
      briteDatabase.close()
      timerDisposable = DisposableHelper.dispose(timerDisposable)
    }.andThen(deleteDatabase())
  }

  override fun deleteDatabase(): Completable {
    return Completable.fromAction { openHelper.deleteDatabase() }
  }

  private class PowerTriggerOpenHelper internal constructor(context: Context) : SQLiteOpenHelper(
      context.applicationContext, PowerTriggerDBImpl.PowerTriggerOpenHelper.DB_NAME, null,
      PowerTriggerDBImpl.PowerTriggerOpenHelper.DATABASE_VERSION) {
    private val appContext: Context = context.applicationContext

    internal fun deleteDatabase() {
      appContext.deleteDatabase(DB_NAME)
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
      Timber.d("onCreate")
      sqLiteDatabase.execSQL(PowerTriggerModel.CREATE_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
      Timber.d("onUpgrade from old version %d to new %d", oldVersion, newVersion)
    }

    companion object {

      private const val DB_NAME = "power_trigger_db"
      private const val DATABASE_VERSION = 1
    }
  }
}
