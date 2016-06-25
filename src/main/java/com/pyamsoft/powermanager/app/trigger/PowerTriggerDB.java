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

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import rx.Scheduler;
import rx.schedulers.Schedulers;

final class PowerTriggerDB {

  @NonNull private static final Object lock = new Object();

  @Nullable private static volatile PowerTriggerDB instance = null;

  @NonNull private final BriteDatabase briteDatabase;

  private PowerTriggerDB(final @NonNull Context context, final @NonNull Scheduler dbScheduler) {
    final SqlBrite sqlBrite = SqlBrite.create();
    final PowerTriggerOpenHelper openHelper =
        new PowerTriggerOpenHelper(context.getApplicationContext());
    briteDatabase = sqlBrite.wrapDatabaseHelper(openHelper, dbScheduler);
  }

  @CheckResult @NonNull static BriteDatabase with(final @NonNull Context context) {
    return with(context, Schedulers.io());
  }

  @SuppressWarnings("ConstantConditions") @CheckResult @NonNull
  static BriteDatabase with(final @NonNull Context context, final @NonNull Scheduler dbScheduler) {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new PowerTriggerDB(context.getApplicationContext(), dbScheduler);
        }
      }
    }

    // With double checking, this singleton should be guaranteed non-null
    if (instance == null) {
      throw new NullPointerException("PowerTriggerDB instance is NULL");
    } else {
      return instance.briteDatabase;
    }
  }
}
