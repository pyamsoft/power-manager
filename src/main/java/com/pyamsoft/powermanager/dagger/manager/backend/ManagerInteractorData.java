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

package com.pyamsoft.powermanager.dagger.manager.backend;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import java.lang.reflect.Method;
import javax.inject.Inject;
import timber.log.Timber;

final class ManagerInteractorData extends ManagerInteractorBase {

  @NonNull private static final String TAG = "data_manager_job";
  @NonNull private static final String SETTINGS_MOBILE_DATA = "mobile_data";
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final Context appContext;

  @Inject ManagerInteractorData(@NonNull PowerManagerPreferences preferences,
      @NonNull Context context) {
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
  }

  @Override public void cancelJobs() {
    cancelJobs(TAG);
  }

  @Override public boolean isEnabled() {
    boolean enabled;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      enabled =
          Settings.Global.getInt(appContext.getContentResolver(), SETTINGS_MOBILE_DATA, 0) == 1;
    } else {
      enabled =
          Settings.Secure.getInt(appContext.getContentResolver(), SETTINGS_MOBILE_DATA, 0) == 1;
    }
    return enabled;
  }

  @Override public boolean isManaged() {
    return preferences.isDataManaged();
  }

  @Override public long getDelayTime() {
    return preferences.getDataDelay();
  }

  @NonNull @Override public DeviceJob createEnableJob(long delayTime, boolean periodic) {
    return new EnableJob(appContext, delayTime, isOriginalStateEnabled(), periodic);
  }

  @NonNull @Override public DeviceJob createDisableJob(long delayTime, boolean periodic) {
    return new DisableJob(appContext, delayTime, isOriginalStateEnabled(), periodic);
  }

  static final class EnableJob extends Job {

    protected EnableJob(@NonNull Context context, long delayTime, boolean originalState,
        boolean periodic) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_ENABLE, originalState,
          periodic);
    }
  }

  static final class DisableJob extends Job {

    protected DisableJob(@NonNull Context context, long delayTime, boolean originalState,
        boolean periodic) {
      super(context, new Params(PRIORITY).setDelayMs(delayTime), JOB_TYPE_DISABLE, originalState,
          periodic);
    }
  }

  static abstract class Job extends DeviceJob {

    @NonNull private static final String SET_METHOD_NAME = "setMobileDataEnabled";
    @NonNull private static final String GET_METHOD_NAME = "getMobileDataEnabled";
    @Nullable private static final Method SET_MOBILE_DATA_ENABLED_METHOD;
    @Nullable private static final Method GET_MOBILE_DATA_ENABLED_METHOD;

    static {
      SET_MOBILE_DATA_ENABLED_METHOD = reflectSetMethod();
      GET_MOBILE_DATA_ENABLED_METHOD = reflectGetMethod();
    }

    protected Job(@NonNull Context context, @NonNull Params params, int jobType,
        boolean originalState, boolean periodic) {
      super(context, params.addTags(ManagerInteractorData.TAG), jobType, originalState, periodic);
    }

    @CheckResult @Nullable private static String resolveSetMethodName() {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        return SET_METHOD_NAME;
      } else {
        return null;
      }
    }

    @CheckResult @Nullable private static String resolveGetMethodName() {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        return GET_METHOD_NAME;
      } else {
        return null;
      }
    }

    @CheckResult @Nullable private static Method reflectGetMethod() {
      final String getMethodName = resolveGetMethodName();
      if (getMethodName != null) {
        synchronized (Job.class) {
          try {
            final Method method = ConnectivityManager.class.getDeclaredMethod(getMethodName);
            method.setAccessible(true);
            return method;
          } catch (final Exception e) {
            Timber.e(e, "ManagerData reflectGetMethod ERROR");
          }
        }
      }

      Timber.e("Unable to resolve getMobileDataEnabled using reflection");
      return null;
    }

    @CheckResult @Nullable private static Method reflectSetMethod() {
      final String setMethodName = resolveSetMethodName();
      if (setMethodName != null) {
        synchronized (Job.class) {
          try {
            final Method method =
                ConnectivityManager.class.getDeclaredMethod(setMethodName, Boolean.TYPE);
            method.setAccessible(true);
            return method;
          } catch (final Exception e) {
            Timber.e(e, "ManagerData reflectSetMethod ERROR");
          }
        }
      }

      Timber.e("Unable to resolve setMobileDataEnabled using reflection");
      return null;
    }

    @CheckResult
    private static boolean isEnabled(final @NonNull ConnectivityManager connectivityManager) {
      if (GET_MOBILE_DATA_ENABLED_METHOD != null) {
        synchronized (Job.class) {
          try {
            return (boolean) GET_MOBILE_DATA_ENABLED_METHOD.invoke(connectivityManager);
          } catch (final Exception e) {
            Timber.e(e, "ManagerData isEnabled ERROR");
          }
        }
      }
      Timber.e("Unable to resolve isEnabled using reflection");
      return false;
    }

    private static void setMobileDataEnabled(@NonNull ConnectivityManager connectivityManager,
        boolean enabled) {
      if (SET_MOBILE_DATA_ENABLED_METHOD != null) {
        synchronized (Job.class) {
          try {
            SET_MOBILE_DATA_ENABLED_METHOD.invoke(connectivityManager, enabled);
          } catch (final Exception e) {
            Timber.e(e, "ManagerData setMobileDataEnabled ERROR");
          }
        }
      }
    }

    @Override protected void enable() {
      Timber.d("Data job enable");
      if (isOriginalState()) {
        final ConnectivityManager connectivityManager =
            (ConnectivityManager) getContext().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!isEnabled(connectivityManager)) {
          Timber.d("Turn on Data");
          setMobileDataEnabled(connectivityManager, true);
          if (isPeriodic()) {
            Timber.d("Data is periodic");
          }
        } else {
          Timber.e("Data is already on");
        }
      } else {
        Timber.e("Data was not originally on");
      }
    }

    @Override protected void disable() {
      Timber.d("Data job disable");

      if (isOriginalState()) {
        final ConnectivityManager connectivityManager =
            (ConnectivityManager) getContext().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (isEnabled(connectivityManager)) {
          Timber.d("Turn off Data");
          setMobileDataEnabled(connectivityManager, false);
          if (isPeriodic()) {
            Timber.d("Data is periodic");
          }
        } else {
          Timber.e("Data is already off");
        }
      } else {
        Timber.e("Data was not originally on");
      }
    }
  }
}
