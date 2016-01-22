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
package com.pyamsoft.powermanager.backend.manager;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import com.pyamsoft.pydroid.util.StringUtil;

public abstract class ManagerBase {

  private final Handler handler;
  private final Runnable enableRun;
  private final Runnable disableRun;

  ManagerBase() {
    this.handler = new Handler(Looper.getMainLooper());
    enableRun = new Runnable() {

      @Override public void run() {
        enable();
      }
    };
    disableRun = new Runnable() {

      @Override public void run() {
        disable();
      }
    };
  }

  public final synchronized void enable(final long time) {
    handler.removeCallbacksAndMessages(null);
    if (time <= 0) {
      enable();
    } else {
      handler.postDelayed(enableRun, time);
    }
  }

  public final synchronized void disable(final long time) {
    handler.removeCallbacksAndMessages(null);
    if (time <= 0) {
      disable();
    } else {
      handler.postDelayed(disableRun, time);
    }
  }

  public abstract void init(final Context context);

  public abstract String getTag();

  abstract void disable();

  abstract void enable();

  public abstract boolean isEnabled();

  public abstract static class Interval extends IntentService {

    private static final String TAG = Interval.class.getSimpleName();

    public Interval(final String name) {
      super(name);
    }

    @Override protected final void onHandleIntent(final Intent intent) {
      final ManagerBase manager = getTargetManager();
      manager.enable();
      final long delay = getTargetCloseTime(GlobalPreferenceUtil.get());
      LogUtil.d(manager.getTag(),
          StringUtil.formatString(getString(R.string.disable_in_time), delay));
      manager.disable(delay);
      final AlarmManager alarmManager =
          (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
      final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.get();
      final long interval = preferenceUtil.powerManagerActive().getIntervalTime();
      LogUtil.d(TAG, getApplicationContext().getString(R.string.set_alarm_for_interval), interval);
      alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
          SystemClock.elapsedRealtime() + interval + delay,
          PendingIntent.getService(getApplicationContext(), 0,
              new Intent(getApplicationContext(), getServiceClass()),
              PendingIntent.FLAG_CANCEL_CURRENT));
    }

    protected abstract Class<? extends Interval> getServiceClass();

    /*
     * Access the GlobalPreferenceUtil for a specific reopen time
     */
    protected abstract long getTargetCloseTime(GlobalPreferenceUtil preferenceUtil);

    /*
     * Retrun a specific Manager
     */
    protected abstract ManagerBase getTargetManager();
  }

  public abstract static class Toggle extends IntentService {

    public Toggle(final String name) {
      super(name);
    }

    @Override protected void onHandleIntent(Intent intent) {
      setManageState(GlobalPreferenceUtil.get());
      PowerPlanUtil.get()
          .setPlan(PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_CUSTOM[PowerPlanUtil.FIELD_INDEX]));
      MonitorService.updateService(getApplicationContext());
    }

    protected abstract void setManageState(GlobalPreferenceUtil preferenceUtil);
  }
}
