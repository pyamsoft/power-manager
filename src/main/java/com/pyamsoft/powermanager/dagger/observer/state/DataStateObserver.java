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

package com.pyamsoft.powermanager.dagger.observer.state;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.reflect.Method;
import javax.inject.Inject;
import timber.log.Timber;

class DataStateObserver extends StateObserver {

  @NonNull private static final String GET_METHOD_NAME = "setMobileDataEnabled";
  @Nullable private static final Method GET_MOBILE_DATA_ENABLED_METHOD;
  @NonNull private static final String SETTINGS_MOBILE_DATA = "mobile_data";

  static {
    GET_MOBILE_DATA_ENABLED_METHOD = reflectGetMethod();
  }

  @NonNull private final ConnectivityManager connectivityManager;

  @Inject DataStateObserver(@NonNull Context context) {
    super(context);

    Uri uri;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      uri = Settings.Global.getUriFor(SETTINGS_MOBILE_DATA);
    } else {
      uri = Settings.Secure.getUriFor(SETTINGS_MOBILE_DATA);
    }
    setUri(uri);

    connectivityManager = (ConnectivityManager) context.getApplicationContext()
        .getSystemService(Context.CONNECTIVITY_SERVICE);
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

    return false;
  }

  @CheckResult private boolean getMobileDataEnabledSettings() {
    boolean enabled;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      enabled =
          Settings.Global.getInt(getAppContext().getContentResolver(), SETTINGS_MOBILE_DATA, 0)
              == 1;
    } else {
      enabled =
          Settings.Secure.getInt(getAppContext().getContentResolver(), SETTINGS_MOBILE_DATA, 0)
              == 1;
    }

    return enabled;
  }

  @CheckResult private boolean getMobileDataEnabled() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return getMobileDataEnabledReflection();
    } else {
      Timber.e("Cannot get mobile data using reflection");
      return getMobileDataEnabledSettings();
    }
  }

  @Override public boolean is() {
    final boolean enabled = getMobileDataEnabled();
    Timber.d("Is mobile data enabled?: %s", enabled);
    return enabled;
  }
}
