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

package com.pyamsoft.powermanager.ui.plan;

import android.content.Context;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.service.ActiveService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import java.lang.ref.WeakReference;

final class PowerPlanModel {

  private static final String TAG = PowerPlanModel.class.getSimpleName();
  private final WeakReference<Context> weakContext;

  PowerPlanModel(Context context) {
    LogUtil.d(TAG, "Create new PowerPlanModel");
    weakContext = new WeakReference<>(context.getApplicationContext());
  }

  String getName(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toString(plan[PowerPlanUtil.FIELD_NAME]);
    } else {
      return null;
    }
  }

  int getIndex(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toInt(plan[PowerPlanUtil.FIELD_INDEX]);
    } else {
      return PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_STANDARD[PowerPlanUtil.FIELD_INDEX]);
    }
  }

  boolean isWifiManaged(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_WIFI]);
    } else {
      return false;
    }
  }

  boolean isDataManaged(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_DATA]);
    } else {
      return false;
    }
  }

  boolean isBluetoothManaged(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_BLUETOOTH]);
    } else {
      return false;
    }
  }

  boolean isSyncManaged(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MANAGE_SYNC]);
    } else {
      return false;
    }
  }

  void setActivePlan(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      final int index = PowerPlanUtil.toInt(plan[PowerPlanUtil.FIELD_INDEX]);
      LogUtil.d(TAG, "setActivePlan: ", index);
      PowerPlanUtil.with(context).setPlan(index);
      PersistentNotification.update(context);
    }
  }

  boolean isActivePlan(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      final int index = PowerPlanUtil.toInt(plan[PowerPlanUtil.FIELD_INDEX]);
      final int activeIndex = GlobalPreferenceUtil.with(context).powerPlans().getActivePlan();
      return activeIndex == index;
    } else {
      return false;
    }
  }

  long getWifiDelay(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_WIFI]);
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  long getDataDelay(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_DATA]);
    } else {
      return ActiveService.Constants.DELAY_RADIO_SIXTY;
    }
  }

  long getBluetoothDelay(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_BLUETOOTH]);
    } else {
      return ActiveService.Constants.DELAY_RADIO_SIXTY;
    }
  }

  long getSyncDelay(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_DELAY_SYNC]);
    } else {
      return ActiveService.Constants.DELAY_RADIO_FIFTEEN;
    }
  }

  boolean isBootEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MISC_BOOT]);
    } else {
      return false;
    }
  }

  boolean isSuspendEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_MISC_SUSPEND]);
    } else {
      return false;
    }
  }

  boolean isWifiReOpenEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_WIFI]);
    } else {
      return false;
    }
  }

  boolean isDataReOpenEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_DATA]);
    } else {
      return false;
    }
  }

  boolean isBluetoothReOpenEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_BLUETOOTH]);
    } else {
      return false;
    }
  }

  boolean isSyncReOpenEnabled(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toBoolean(plan[PowerPlanUtil.FIELD_REOPEN_SYNC]);
    } else {
      return false;
    }
  }

  long getWifiReOpenTime(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_WIFI]);
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_SIXTY;
    }
  }

  long getDataReOpenTime(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_DATA]);
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN;
    }
  }

  long getBluetoothReOpenTime(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_BLUETOOTH]);
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }
  }

  long getSyncReOpenTime(final int position) {
    final Context context = weakContext.get();
    if (context != null) {
      final Object[] plan = PowerPlanUtil.with(context).getPowerPlan(position);
      return PowerPlanUtil.toLong(plan[PowerPlanUtil.FIELD_REOPEN_TIME_SYNC]);
    } else {
      return ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY;
    }
  }
}
