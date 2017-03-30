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

package com.pyamsoft.powermanager.base.wrapper;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.shell.ShellCommandHelper;
import com.pyamsoft.powermanager.model.Logger;
import com.pyamsoft.powermanager.model.overlord.States;
import com.pyamsoft.pydroid.helper.Checker;
import java.lang.reflect.Method;
import javax.inject.Inject;
import timber.log.Timber;

class DataConnectionWrapperImpl implements DeviceFunctionWrapper {

  @NonNull private static final String GET_METHOD_NAME = "getMobileDataEnabled";
  @NonNull private static final String SET_METHOD_NAME = "setMobileDataEnabled";
  @Nullable private static final Method GET_MOBILE_DATA_ENABLED_METHOD;
  @Nullable private static final Method SET_MOBILE_DATA_ENABLED_METHOD;

  static {
    GET_MOBILE_DATA_ENABLED_METHOD = reflectGetMethod();
    SET_MOBILE_DATA_ENABLED_METHOD = reflectSetMethod();
  }

  @NonNull private final ShellCommandHelper shellCommandHelper;
  @NonNull private final Logger logger;
  @NonNull private final ConnectivityManager connectivityManager;
  @NonNull private final ContentResolver contentResolver;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final String dataUri;

  @Inject DataConnectionWrapperImpl(@NonNull Context context,
      @NonNull ShellCommandHelper shellCommandHelper, @NonNull Logger logger,
      @NonNull PowerManagerPreferences preferences, @NonNull String dataUri) {
    this.shellCommandHelper = Checker.checkNonNull(shellCommandHelper);
    this.logger = Checker.checkNonNull(logger);
    this.preferences = Checker.checkNonNull(preferences);
    this.dataUri = Checker.checkNonNull(dataUri);

    context = Checker.checkNonNull(context).getApplicationContext();
    connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    contentResolver = context.getContentResolver();
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

  @NonNull @CheckResult private States getMobileDataEnabledReflection() {
    if (GET_MOBILE_DATA_ENABLED_METHOD != null) {
      try {
        return (Boolean) GET_MOBILE_DATA_ENABLED_METHOD.invoke(connectivityManager) ? States.ENABLED
            : States.DISABLED;
      } catch (final Exception e) {
        logger.e("ManagerData getMobileDataEnabled ERROR");
      }
    }

    return getMobileDataEnabledSettings();
  }

  private void setMobileDataEnabledReflection(boolean enabled) {
    if (SET_MOBILE_DATA_ENABLED_METHOD != null) {
      try {
        SET_MOBILE_DATA_ENABLED_METHOD.invoke(connectivityManager, enabled);
      } catch (final Exception e) {
        logger.e("ManagerData setMobileDataEnabled ERROR");
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
      shellCommandHelper.runSUCommand(command);
    } else {
      logger.w("Root not enabled, cannot toggle Data");
    }
  }

  @NonNull @CheckResult private States getMobileDataEnabledSettings() {
    return (Settings.Global.getInt(contentResolver, dataUri, 0) == 1) ? States.ENABLED
        : States.DISABLED;
  }

  private void setMobileDataEnabled(boolean enabled) {
    logger.i("Data: %s", enabled ? "enable" : "disable");
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      setMobileDataEnabledReflection(enabled);
    } else {
      setMobileDataEnabledRoot(enabled);
    }
  }

  @Override public void enable() {
    setMobileDataEnabled(true);
  }

  @Override public void disable() {
    setMobileDataEnabled(false);
  }

  @NonNull @Override public States getState() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return getMobileDataEnabledReflection();
    } else {
      return getMobileDataEnabledSettings();
    }
  }
}
