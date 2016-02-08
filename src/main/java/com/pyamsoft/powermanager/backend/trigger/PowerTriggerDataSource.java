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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.pyamsoft.pydroid.util.LogUtil;
import java.util.HashSet;
import java.util.Set;

public final class PowerTriggerDataSource {

  private static final int ID = 0;
  private static final int NAME = 1;
  private static final int LEVEL = 2;
  private static final int WIFI_MANAGE = 3;
  private static final int DATA_MANAGE = 4;
  private static final int BLUETOOTH_MANAGE = 5;
  private static final int SYNC_MANAGE = 6;
  private static final int WIFI_STATE = 7;
  private static final int DATA_STATE = 8;
  private static final int BLUETOOTH_STATE = 9;
  private static final int SYNC_STATE = 10;
  private static final int WIFI_REOPEN = 11;
  private static final int DATA_REOPEN = 12;
  private static final int BLUETOOTH_REOPEN = 13;
  private static final int SYNC_REOPEN = 14;
  private static final int AUTO_BRIGHT = 15;
  private static final int BRIGHTNESS = 16;
  private static final int VOLUME = 14;
  private static final int ENABLED = 15;
  private static final int AVAILABLE = 16;
  private static final String TAG = PowerTriggerDataSource.class.getSimpleName();

  private static PowerTriggerDataSource instance = null;
  private PowerTriggerTable dbHelper = null;
  private SQLiteDatabase database = null;
  private Context context;
  private int nextId = 0;
  private int refCount = 0;

  private PowerTriggerDataSource(final Context context) {
    LogUtil.d(TAG, "Initialize PowerTriggerDataSource");
    this.context = context.getApplicationContext();
  }

  public static PowerTriggerDataSource with(final Context context) {
    if (instance == null) {
      instance = new PowerTriggerDataSource(context);
    }
    return instance;
  }

  public void open() {
    if (database == null) {
      LogUtil.d(TAG, "Opening db");
      dbHelper = new PowerTriggerTable(context.getApplicationContext());
      database = dbHelper.getWritableDatabase();
      nextId = getAllTriggers().size();
    }
    if (database != null) {
      ++refCount;
      LogUtil.d(TAG, "current refCount: ", refCount);
    }
  }

  public void close() {
    if (database != null) {
      --refCount;
      LogUtil.d(TAG, "current refCount: ", refCount);
      if (refCount == 0) {
        LogUtil.d(TAG, "Closing db");
        dbHelper.close();
        database.close();
        database = null;
        dbHelper = null;
      }
    }
  }

  public boolean isOpened() {
    return database != null && database.isOpen();
  }

  public final int getNextId() {
    return nextId;
  }

  public final void createTrigger(final PowerTrigger trigger) {
    final ContentValues values = new ContentValues();
    final int id = trigger.getId();
    final int level = trigger.getLevel();
    final String name = trigger.getName();
    values.put(PowerTriggerTable.Entry.COLUMN_ID, id);
    values.put(PowerTriggerTable.Entry.COLUMN_NAME, name);
    values.put(PowerTriggerTable.Entry.COLUMN_LEVEL, level);
    values.put(PowerTriggerTable.Entry.COLUMN_WIFI_MANAGE, trigger.getManageWifi());
    values.put(PowerTriggerTable.Entry.COLUMN_DATA_MANAGE, trigger.getManageData());
    values.put(PowerTriggerTable.Entry.COLUMN_BLUETOOTH_MANAGE, trigger.getManageBluetooth());
    values.put(PowerTriggerTable.Entry.COLUMN_SYNC_MANAGE, trigger.getManageSync());
    values.put(PowerTriggerTable.Entry.COLUMN_WIFI_STATE, trigger.getStateWifi());
    values.put(PowerTriggerTable.Entry.COLUMN_DATA_STATE, trigger.getStateData());
    values.put(PowerTriggerTable.Entry.COLUMN_BLUETOOTH_STATE, trigger.getStateBluetooth());
    values.put(PowerTriggerTable.Entry.COLUMN_SYNC_STATE, trigger.getStateSync());
    values.put(PowerTriggerTable.Entry.COLUMN_WIFI_REOPEN, trigger.getReopenWifi());
    values.put(PowerTriggerTable.Entry.COLUMN_DATA_REOPEN, trigger.getReopenData());
    values.put(PowerTriggerTable.Entry.COLUMN_BLUETOOTH_REOPEN, trigger.getReopenBluetooth());
    values.put(PowerTriggerTable.Entry.COLUMN_SYNC_REOPEN, trigger.getReopenSync());
    values.put(PowerTriggerTable.Entry.COLUMN_AUTO_BRIGHTNESS, trigger.getAutoBrightness());
    values.put(PowerTriggerTable.Entry.COLUMN_BRIGHTNESS_LEVEL, trigger.getBrightnessLevel());
    values.put(PowerTriggerTable.Entry.COLUMN_VOLUME, trigger.getVolume());
    values.put(PowerTriggerTable.Entry.COLUMN_ENABLED, trigger.getEnabled());
    values.put(PowerTriggerTable.Entry.COLUMN_AVAILABLE, trigger.getAvailable());
    if (getTriggerByUnique(id, name, level) == null) {
      LogUtil.d(TAG, "Insert id: ", id);
      database.insert(PowerTriggerTable.TABLE_NAME, null, values);
      ++nextId;
    } else {
      LogUtil.d(TAG, "Update id: ", id);
      database.update(PowerTriggerTable.TABLE_NAME, values,
          PowerTriggerTable.Entry.COLUMN_ID + " = " + id, null);
      final PowerTrigger removeMe = TriggerSet.with(context).contains(name);
      TriggerSet.with(context).remove(removeMe.getName());
    }
    TriggerSet.with(context).add(trigger);
  }

  private PowerTrigger getTriggerByUnique(final int id, final String name, final int level) {
    PowerTrigger trigger = null;
    if (database != null) {
      final Cursor cursor =
          database.query(PowerTriggerTable.TABLE_NAME, PowerTriggerTable.getAllColumns(),
              PowerTriggerTable.Entry.COLUMN_ID + " = " + id, null, null, null, null);
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        if (cursor.getInt(ID) == id || cursor.getString(NAME).equalsIgnoreCase(name) ||
            cursor.getInt(LEVEL) == level) {
          trigger = cursorToTrigger(cursor);
          LogUtil.d(TAG, "Found trigger by id: ", cursor.getInt(ID), ": ", cursor.getString(NAME));
          break;
        }
        cursor.moveToNext();
      }
      cursor.close();
    }
    return trigger;
  }

  public final boolean deleteTrigger(final PowerTrigger trigger) {
    boolean removed = false;
    if (database != null) {
      final int id = trigger.getId();
      int deleted = database.delete(PowerTriggerTable.TABLE_NAME,
          PowerTriggerTable.Entry.COLUMN_ID + " = " + id, null);
      LogUtil.d(TAG, "Trigger: ", id, "deleted");
      TriggerSet.with(context).remove(trigger.getName());
      removed = (deleted > 0);
    }
    return removed;
  }

  public final void deleteAllTriggers() {
    if (database != null) {
      final Set<PowerTrigger> allTriggers = getAllTriggers();
      if (allTriggers != null) {
        for (final PowerTrigger trigger : allTriggers) {
          deleteTrigger(trigger);
        }
        nextId = 0;
      }
    }
  }

  private PowerTrigger cursorToTrigger(final Cursor cursor) {
    LogUtil.d(TAG, "Populate trigger with value from cursor id: ", cursor.getInt(ID), ": ",
        cursor.getString(NAME));
    final PowerTrigger trigger =
        new PowerTrigger(cursor.getInt(ID), cursor.getString(NAME), cursor.getInt(LEVEL));
    trigger.setManageWifi(cursor.getInt(WIFI_MANAGE));
    trigger.setManageData(cursor.getInt(DATA_MANAGE));
    trigger.setManageBluetooth(cursor.getInt(BLUETOOTH_MANAGE));
    trigger.setManageSync(cursor.getInt(SYNC_MANAGE));
    trigger.setReopenWifi(cursor.getInt(WIFI_REOPEN));
    trigger.setReopenData(cursor.getInt(DATA_REOPEN));
    trigger.setReopenBluetooth(cursor.getInt(BLUETOOTH_REOPEN));
    trigger.setReopenSync(cursor.getInt(SYNC_REOPEN));
    trigger.setAutoBrightness(cursor.getInt(AUTO_BRIGHT));
    trigger.setBrightnessLevel(cursor.getInt(BRIGHTNESS));
    trigger.setVolume(cursor.getInt(VOLUME));
    trigger.setEnabled(cursor.getInt(ENABLED));
    trigger.setAvailable(cursor.getInt(AVAILABLE));

    int state = cursor.getInt(WIFI_STATE);
    switch (state) {
      case PowerTrigger.TOGGLE_STATE_ON:
        trigger.setStateOnWifi();
        break;
      case PowerTrigger.TOGGLE_STATE_OFF:
        trigger.setStateOffWifi();
        break;
      default:
        trigger.setStateNoneWifi();
    }

    state = cursor.getInt(DATA_STATE);
    switch (state) {
      case PowerTrigger.TOGGLE_STATE_ON:
        trigger.setStateOnData();
        break;
      case PowerTrigger.TOGGLE_STATE_OFF:
        trigger.setStateOffData();
        break;
      default:
        trigger.setStateNoneData();
    }

    state = cursor.getInt(BLUETOOTH_STATE);
    switch (state) {
      case PowerTrigger.TOGGLE_STATE_ON:
        trigger.setStateOnBluetooth();
        break;
      case PowerTrigger.TOGGLE_STATE_OFF:
        trigger.setStateOffBluetooth();
        break;
      default:
        trigger.setStateNoneBluetooth();
    }

    state = cursor.getInt(SYNC_STATE);
    switch (state) {
      case PowerTrigger.TOGGLE_STATE_ON:
        trigger.setStateOnSync();
        break;
      case PowerTrigger.TOGGLE_STATE_OFF:
        trigger.setStateOffSync();
        break;
      default:
        trigger.setStateNoneSync();
    }

    return trigger;
  }

  private PowerTrigger getTriggerById(final int id) {
    PowerTrigger trigger = null;
    if (database != null) {
      final Cursor cursor =
          database.query(PowerTriggerTable.TABLE_NAME, PowerTriggerTable.getAllColumns(),
              PowerTriggerTable.Entry.COLUMN_ID + " = " + id, null, null, null, null);
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        if (cursor.getInt(ID) == id) {
          trigger = cursorToTrigger(cursor);
          LogUtil.d(TAG, "Found trigger by id: ", cursor.getInt(ID), ": ", cursor.getString(NAME));
          break;
        }
        cursor.moveToNext();
      }
      cursor.close();
    }
    return trigger;
  }

  private Set<PowerTrigger> getAllTriggers() {
    Set<PowerTrigger> triggers = null;
    if (database != null) {
      triggers = new HashSet<>();
      final Cursor cursor =
          database.query(PowerTriggerTable.TABLE_NAME, PowerTriggerTable.getAllColumns(), null,
              null, null, null, null);
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        triggers.add(cursorToTrigger(cursor));
        LogUtil.d(TAG, "Found trigger id: ", cursor.getInt(ID), ": ", cursor.getString(NAME));
        cursor.moveToNext();
      }
      cursor.close();
    }
    return triggers;
  }

  public static final class TriggerSet {

    public static final int NO_INDEX = -1;
    public static final int PLACEHOLDER_ID = -1;
    public static final String PLACEHOLDER_NAME = "Add Item";
    public static final int PLACEHOLDER_LEVEL = -1;
    private static TriggerSet instance = null;
    private final Set<PowerTrigger> allTriggers = new HashSet<>();

    private TriggerSet(final Context context) {
      final PowerTrigger placeHolder =
          new PowerTrigger(PLACEHOLDER_ID, PLACEHOLDER_NAME, PLACEHOLDER_LEVEL);
      add(placeHolder);
      final PowerTriggerDataSource source = PowerTriggerDataSource.with(context);
      source.open();
      if (source.isOpened()) {
        final Set<PowerTrigger> triggers = source.getAllTriggers();
        for (final PowerTrigger trigger : triggers) {
          add(trigger);
        }
        source.close();
      }
    }

    public static TriggerSet with(final Context context) {
      if (instance == null) {
        synchronized (TriggerSet.class) {
          if (instance == null) {
            instance = new TriggerSet(context);
          }
        }
      }
      return instance;
    }

    public final Set<PowerTrigger> asSet() {
      return allTriggers;
    }

    public final PowerTrigger contains(final String name) {
      PowerTrigger trigger = null;
      for (final PowerTrigger test : allTriggers) {
        if (test.getName().equalsIgnoreCase(name)) {
          trigger = test;
          break;
        }
      }
      return trigger;
    }

    private void add(final PowerTrigger entry) {
      LogUtil.d(TAG, "Attempt to add trigger: " + entry.getName());
      if (contains(entry.getName()) == null) {
        LogUtil.d(TAG, "Add trigger: " + entry.getName());
        allTriggers.add(entry);
      } else {
        LogUtil.d(TAG, "Trigger already exists: " + entry.getName());
      }
    }

    private synchronized void remove(final String name) {
      LogUtil.d(TAG, "Attempt to remove trigger: " + name);
      final PowerTrigger entry = contains(name);
      if (entry != null) {
        final boolean b = allTriggers.remove(entry);
        if (b) {
          LogUtil.d(TAG, "Remove trigger: " + name);
        }
      }
    }

    public final int size() {
      return allTriggers.size();
    }
  }
}
