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
import com.pyamsoft.powermanager.app.wrapper.DeviceFunctionWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
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

  @NonNull private final ConnectivityManager connectivityManager;
  @NonNull private final ContentResolver contentResolver;

  @Inject DataConnectionWrapperImpl(@NonNull Context context) {
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
    final Process process;
    try {
      final String command = "svc data " + (enabled ? "enable" : "disable");
      process = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
      try (final BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
        Timber.d("Read results of exec: '%s'", command);
        String line = bufferedReader.readLine();
        while (line != null && !line.isEmpty()) {
          Timber.d("%s", line);
          line = bufferedReader.readLine();
        }
      }

      try {
        process.waitFor();
        Timber.i("Command %s exited with value: %d", command, process.exitValue());
      } catch (InterruptedException e) {
        Timber.e(e, "Interrupted while waiting for exit");
      }
      // Will always be 0
    } catch (IOException e) {
      Timber.e(e, "Error running shell command");
    }
  }

  @CheckResult private boolean getMobileDataEnabledSettings() {
    return Settings.Global.getInt(contentResolver, SETTINGS_URI_MOBILE_DATA, 0) == 1;
  }

  @CheckResult private boolean isAirplaneMode() {
    return Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
  }

  private void setMobileDataEnabled(boolean enabled) {
    Timber.i("Data: %s", enabled ? "enable" : "disable");
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      setMobileDataEnabledReflection(enabled);
    } else {
      setMobileDataEnabledRoot(enabled);
    }
  }

  @Override public void enable() {
    if (isAirplaneMode()) {
      Timber.e("Cannot enable Data radio in airplane mode");
    } else {
      setMobileDataEnabled(true);
    }
  }

  @Override public void disable() {
    if (isAirplaneMode()) {
      Timber.e("Cannot disable Data radio in airplane mode");
    } else {
      setMobileDataEnabled(false);
    }
  }

  @Override public boolean isEnabled() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return getMobileDataEnabledReflection();
    } else {
      return getMobileDataEnabledSettings();
    }
  }
}
