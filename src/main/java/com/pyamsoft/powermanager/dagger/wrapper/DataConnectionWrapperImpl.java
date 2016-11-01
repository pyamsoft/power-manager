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

package com.pyamsoft.powermanager.dagger.wrapper;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.dagger.ShellCommandHelper;
import java.lang.reflect.Method;
import javax.inject.Inject;
import timber.log.Timber;

class DataConnectionWrapperImpl extends AirplaneRespectingDeviceWrapper {

  @NonNull private static final String GET_METHOD_NAME = "getMobileDataEnabled";
  @NonNull private static final String SET_METHOD_NAME = "setMobileDataEnabled";
  @Nullable private static final Method GET_MOBILE_DATA_ENABLED_METHOD;
  @Nullable private static final Method SET_MOBILE_DATA_ENABLED_METHOD;

  static {
    GET_MOBILE_DATA_ENABLED_METHOD = reflectGetMethod();
    SET_MOBILE_DATA_ENABLED_METHOD = reflectSetMethod();
  }

  @NonNull private final ConnectivityManager connectivityManager;
  @NonNull private final ContentResolver contentResolver;
  @NonNull private final PowerManagerPreferences preferences;

  @Inject DataConnectionWrapperImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    super(context, "Data");
    this.preferences = preferences;
    connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    contentResolver = context.getApplicationContext().getContentResolver();
  }

  @CheckResult @Nullable private static Method reflectGetMethod() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Timber.e("Reflection method %s does not exist on Lollipop+", GET_METHOD_NAME);
      return null;
    }

    try {
      final Method method = ConnectivityManager.class.getDeclaredMethod(GET_METHOD_NAME);
      method.setAccessible(true);
      return method;
    } catch (final Exception e) {
      Timber.e(e, "ManagerData reflectGetMethod ERROR");
    }

    return null;
  }

  @CheckResult @Nullable private static Method reflectSetMethod() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Timber.e("Reflection method %s does not exist on Lollipop+", SET_METHOD_NAME);
      return null;
    }

    try {
      final Method method =
          ConnectivityManager.class.getDeclaredMethod(SET_METHOD_NAME, Boolean.TYPE);
      method.setAccessible(true);
      return method;
    } catch (final Exception e) {
      Timber.e(e, "ManagerData reflectSetMethod ERROR");
    }

    return null;
  }

  @CheckResult private boolean getMobileDataEnabledReflection() {
    if (GET_MOBILE_DATA_ENABLED_METHOD != null) {
      try {
        return (boolean) GET_MOBILE_DATA_ENABLED_METHOD.invoke(connectivityManager);
      } catch (final Exception e) {
        Timber.e(e, "ManagerData getMobileDataEnabled ERROR");
      }
    }

    return getMobileDataEnabledSettings();
  }

  private void setMobileDataEnabledReflection(boolean enabled) {
    if (SET_MOBILE_DATA_ENABLED_METHOD != null) {
      try {
        SET_MOBILE_DATA_ENABLED_METHOD.invoke(connectivityManager, enabled);
      } catch (final Exception e) {
        Timber.e(e, "ManagerData setMobileDataEnabled ERROR");
      }
    }
  }

  /**
   * Requires ROOT to work properly
   *
   * Will exit with a failed 137 code or otherwise if ROOT is not allowed
   */
  private void setMobileDataEnabledRoot(boolean enabled) {
    if (preferences.isRootEnabled()) {
      final String command = "svc data " + (enabled ? "enable" : "disable");
      final boolean result = ShellCommandHelper.runRootShellCommand(command);
      Timber.d("Result: %s", result);
    } else {
      Timber.w("Root not enabled, cannot toggle Data");
    }
  }

  @CheckResult private boolean getMobileDataEnabledSettings() {
    return Settings.Global.getInt(contentResolver, SETTINGS_URI_MOBILE_DATA, 0) == 1;
  }

  private void setMobileDataEnabled(boolean enabled) {
    Timber.i("Data: %s", enabled ? "enable" : "disable");
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      setMobileDataEnabledReflection(enabled);
    } else {
      setMobileDataEnabledRoot(enabled);
    }
  }

  @Override void internalEnable() {
    setMobileDataEnabled(true);
  }

  @Override void internalDisable() {
    setMobileDataEnabled(false);
  }

  @Override public boolean isEnabled() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return getMobileDataEnabledReflection();
    } else {
      return getMobileDataEnabledSettings();
    }
  }
}
