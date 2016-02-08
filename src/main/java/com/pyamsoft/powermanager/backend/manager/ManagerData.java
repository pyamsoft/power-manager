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

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import java.lang.reflect.Method;

public final class ManagerData extends ManagerBase {

  private static final String DATA_MANAGER = "DataManager";
  private static final String set = "setMobileDataEnabled";
  private static final String get = "getMobileDataEnabled";
  private static final Method setMobileDataEnabled = reflectSetMethod();
  private static final Method getMobileDataEnabled = reflectGetMethod();
  private static ManagerBase instance = null;
  private Context context;
  private ConnectivityManager data;

  private ManagerData(final Context context) {
    super();
    LogUtil.d(DATA_MANAGER, "Initialize ManagerData");
    this.context = context.getApplicationContext();
    this.data = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  public static ManagerBase with(final Context context) {
    if (instance == null) {
      synchronized (ManagerData.class) {
        if (instance == null) {
          instance = new ManagerData(context);
        }
      }
    }
    return instance;
  }

  private static String getSetMethodName() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return set;
    } else {
      return null;
    }
  }

  private static String getGetMethodName() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return get;
    } else {
      return null;
    }
  }

  private static synchronized Method reflectGetMethod() {
    final String get = getGetMethodName();
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && get != null) {
      try {
        final Method method = ConnectivityManager.class.getDeclaredMethod(get);
        method.setAccessible(true);
        return method;
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private static synchronized Method reflectSetMethod() {
    final String set = getSetMethodName();
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && set != null) {
      try {
        final Method method = ConnectivityManager.class.getDeclaredMethod(set, Boolean.TYPE);
        method.setAccessible(true);
        return method;
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private synchronized boolean isNull() {
    return (getMobileDataEnabled == null || setMobileDataEnabled == null);
  }

  private synchronized boolean getMobileData() {
    if (data != null) {
      if (getMobileDataEnabled != null) {
        try {
          return (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
              ? (Boolean) getMobileDataEnabled.invoke(data) : false;
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  private synchronized void setMobileData(final boolean state) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      if (data != null) {
        if (setMobileDataEnabled != null) {
          try {
            setMobileDataEnabled.invoke(data, state);
          } catch (final Exception e) {
            e.printStackTrace();
          }
        } else {
          LogUtil.e(DATA_MANAGER, context.getString(R.string.data_set_method_null));
        }
      }
    } else {
      LogUtil.e(DATA_MANAGER, context.getString(R.string.data_lollipop_error));
    }
  }

  @Override synchronized void disable() {
    if (!isNull()) {
      setMobileData(false);
      LogUtil.i(DATA_MANAGER, context.getString(R.string.set_data_false));
    }
  }

  @Override synchronized void enable() {
    if (!isNull() && !isAirplaneMode()) {
      setMobileData(true);
      LogUtil.i(DATA_MANAGER, context.getString(R.string.set_data_true));
    }
  }

  @Override public final synchronized boolean isEnabled() {
    return (!isNull() && getMobileData());
  }

  private boolean isAirplaneMode() {
    if (!isNull()) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
        return OldAndroid.isAirplaneModeOn(context);
      } else {
        return JellyBeanMR1.isAirplaneModeOn(context);
      }
    }
    return true;
  }

  @Override public String getTag() {
    return DATA_MANAGER;
  }

  @Override public String getName() {
    return "Data";
  }

  @SuppressWarnings("deprecation") private static class OldAndroid {

    private static boolean isAirplaneModeOn(final Context c) {
      return Settings.System.getInt(c.getApplicationContext().getContentResolver(),
          Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) private static class JellyBeanMR1 {

    private static boolean isAirplaneModeOn(final Context c) {
      return Settings.Global.getInt(c.getApplicationContext().getContentResolver(),
          Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
  }

  public static final class Interval extends ManagerBase.Interval {

    public Interval() {
      super(Interval.class.getName());
    }

    @Override protected long getTargetCloseTime(GlobalPreferenceUtil preferenceUtil) {
      return preferenceUtil.intervalDisableService().getDataReopenTime();
    }

    @Override protected ManagerBase getTargetManager() {
      return with(getApplicationContext());
    }

    @Override protected long getTargetIntervalTime(GlobalPreferenceUtil preferenceUtil) {
      return preferenceUtil.powerManagerActive().getIntervalTimeData();
    }

    @Override protected Class<? extends ManagerBase.Interval> getServiceClass() {
      return Interval.class;
    }
  }

  public static final class Toggle extends ManagerBase.Toggle {

    public Toggle() {
      super(Toggle.class.getName());
    }

    @Override protected void setManageState(GlobalPreferenceUtil preferenceUtil) {
      final boolean managed = preferenceUtil.powerManagerActive().isManagedData();
      preferenceUtil.powerManagerActive().setManagedData(!managed);
      PowerPlanUtil.with(getApplicationContext())
          .updateCustomPlan(PowerPlanUtil.FIELD_MANAGE_DATA, !managed);
    }
  }
}
