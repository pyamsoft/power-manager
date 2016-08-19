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

package com.pyamsoft.powermanager.dagger.modifier.state;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerData;
import java.lang.reflect.Method;
import javax.inject.Inject;
import timber.log.Timber;

public class DataStateModifier extends StateModifier {

  @NonNull private static final String SETTINGS_MOBILE_DATA = "mobile_data";
  @NonNull private static final String SET_METHOD_NAME = "setMobileDataEnabled";
  @Nullable private static final Method SET_MOBILE_DATA_ENABLED_METHOD;

  static {
    SET_MOBILE_DATA_ENABLED_METHOD = reflectSetMethod();
  }

  @NonNull private final ConnectivityManager connectivityManager;

  @Inject DataStateModifier(@NonNull Context context) {
    super(context);
    connectivityManager = (ConnectivityManager) context.getApplicationContext()
        .getSystemService(Context.CONNECTIVITY_SERVICE);
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
      return null;
    }
  }

  private void setMobileDataEnabledReflection(boolean enabled) {
    if (SET_MOBILE_DATA_ENABLED_METHOD != null) {
      try {
        Timber.d("setMobileDataEnabledReflection: %s", enabled);
        SET_MOBILE_DATA_ENABLED_METHOD.invoke(connectivityManager, enabled);
      } catch (final Exception e) {
        Timber.e(e, "ManagerData setMobileDataEnabled ERROR");
      }
    }
  }

  private void setMobileDataEnabled(boolean enabled) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      setMobileDataEnabledReflection(enabled);
    } else {
      setMobileDataEnabledSettings(enabled);
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void setMobileDataEnabledSettings(boolean enabled) {
    if (ManagerData.checkWriteSettingsPermission(getAppContext())) {
      Timber.d("setMobileDataEnabledSettings: %s", enabled);
      Settings.Global.putInt(getAppContext().getContentResolver(), SETTINGS_MOBILE_DATA,
          enabled ? 1 : 0);
    } else {
      Timber.e("Missing WRITE_SECURE_SETTINGS permission");
    }
  }

  @Override void mainThreadSet() {
    setMobileDataEnabled(true);
  }

  @Override void mainThreadUnset() {
    setMobileDataEnabled(false);
  }
}
