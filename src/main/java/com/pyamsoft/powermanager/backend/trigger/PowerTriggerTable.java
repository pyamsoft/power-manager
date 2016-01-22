/*
 * Copyright 2013 - 2016 Peter Kenji Yamanaka
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
package com.pyamsoft.powermanager.backend.trigger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.pyamsoft.pydroid.util.LogUtil;

final class PowerTriggerTable extends SQLiteOpenHelper {

  public static final String TABLE_NAME = "trigger_table";
  private static final String SQL_DROP_TABLE =
      "DROP TABLE IF EXISTS " + PowerTriggerTable.TABLE_NAME;
  private static final String NON_NULL_INT = " INTEGER NOT NULL, ";
  private static final String NON_NULL_TEXT = " TEXT NOT NULL, ";
  private static final String SQL_CREATE_TABLE = "CREATE TABLE " +
      PowerTriggerTable.TABLE_NAME + "(" +
      Entry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
      Entry.COLUMN_NAME + NON_NULL_TEXT +
      Entry.COLUMN_LEVEL + NON_NULL_INT +
      Entry.COLUMN_WIFI_MANAGE + NON_NULL_INT +
      Entry.COLUMN_DATA_MANAGE + NON_NULL_INT +
      Entry.COLUMN_BLUETOOTH_MANAGE + NON_NULL_INT +
      Entry.COLUMN_SYNC_MANAGE + NON_NULL_INT +
      Entry.COLUMN_WIFI_STATE + NON_NULL_INT +
      Entry.COLUMN_DATA_STATE + NON_NULL_INT +
      Entry.COLUMN_BLUETOOTH_STATE + NON_NULL_INT +
      Entry.COLUMN_SYNC_STATE + NON_NULL_INT +
      Entry.COLUMN_WIFI_REOPEN + NON_NULL_INT +
      Entry.COLUMN_DATA_REOPEN + NON_NULL_INT +
      Entry.COLUMN_BLUETOOTH_REOPEN + NON_NULL_INT +
      Entry.COLUMN_SYNC_REOPEN + NON_NULL_INT +
      Entry.COLUMN_AUTO_BRIGHTNESS + NON_NULL_INT +
      Entry.COLUMN_BRIGHTNESS_LEVEL + NON_NULL_INT +
      Entry.COLUMN_VOLUME + NON_NULL_INT +
      Entry.COLUMN_ENABLED + NON_NULL_INT +
      Entry.COLUMN_AVAILABLE + " INTEGER NOT NULL" +
      ");";
  private static final int DATABASE_VERSION = 3;
  private static final String DATABASE_NAME = "TriggerTableHelper.db";
  private static final String[] allColumns = {
      Entry.COLUMN_ID, Entry.COLUMN_NAME, Entry.COLUMN_LEVEL, Entry.COLUMN_WIFI_MANAGE,
      Entry.COLUMN_DATA_MANAGE, Entry.COLUMN_BLUETOOTH_MANAGE, Entry.COLUMN_SYNC_MANAGE,
      Entry.COLUMN_WIFI_STATE, Entry.COLUMN_DATA_STATE, Entry.COLUMN_BLUETOOTH_STATE,
      Entry.COLUMN_SYNC_STATE, Entry.COLUMN_WIFI_REOPEN, Entry.COLUMN_DATA_REOPEN,
      Entry.COLUMN_BLUETOOTH_REOPEN, Entry.COLUMN_SYNC_REOPEN, Entry.COLUMN_AUTO_BRIGHTNESS,
      Entry.COLUMN_BRIGHTNESS_LEVEL, Entry.COLUMN_VOLUME, Entry.COLUMN_ENABLED,
      Entry.COLUMN_AVAILABLE
  };
  private static final String TAG = PowerTriggerTable.class.getSimpleName();

  public PowerTriggerTable(final Context c) {
    super(c.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
  }

  static String[] getAllColumns() {
    return allColumns.clone();
  }

  @Override public final void onCreate(final SQLiteDatabase db) {
    LogUtil.d(TAG, "onCreate: Create new SQL db");
    db.execSQL(PowerTriggerTable.SQL_CREATE_TABLE);
  }

  @Override
  public final void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    LogUtil.d(TAG, "onUpgrade: Drop Existing SQL db");
    db.execSQL(PowerTriggerTable.SQL_DROP_TABLE);
    onCreate(db);
  }

  public static final class Entry implements BaseColumns {

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_WIFI_MANAGE = "wifi_manage";
    public static final String COLUMN_DATA_MANAGE = "data_manage";
    public static final String COLUMN_BLUETOOTH_MANAGE = "bluetooth_manage";
    public static final String COLUMN_SYNC_MANAGE = "sync_manage";
    public static final String COLUMN_WIFI_STATE = "wifi_state";
    public static final String COLUMN_DATA_STATE = "data_state";
    public static final String COLUMN_BLUETOOTH_STATE = "bluetooth_state";
    public static final String COLUMN_SYNC_STATE = "sync_state";
    public static final String COLUMN_WIFI_REOPEN = "wifi_reopen";
    public static final String COLUMN_DATA_REOPEN = "data_reopen";
    public static final String COLUMN_BLUETOOTH_REOPEN = "bluetooth_reopen";
    public static final String COLUMN_SYNC_REOPEN = "sync_reopen";
    public static final String COLUMN_AUTO_BRIGHTNESS = "auto_bright";
    public static final String COLUMN_BRIGHTNESS_LEVEL = "brightness";
    public static final String COLUMN_VOLUME = "volume";
    public static final String COLUMN_ENABLED = "enabled";
    public static final String COLUMN_AVAILABLE = "available";
  }
}
