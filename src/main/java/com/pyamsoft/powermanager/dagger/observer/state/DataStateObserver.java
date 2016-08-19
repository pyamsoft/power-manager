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
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import javax.inject.Inject;
import timber.log.Timber;

class DataStateObserver extends StateContentObserver {

  @NonNull private static final String SETTINGS_MOBILE_DATA = "mobile_data";

  @Inject DataStateObserver(@NonNull Context context) {
    super(context);

    Uri uri;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      uri = Settings.Global.getUriFor(SETTINGS_MOBILE_DATA);
    } else {
      uri = Settings.Secure.getUriFor(SETTINGS_MOBILE_DATA);
    }
    setUri(uri);
  }

  @Override public boolean is() {
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

    Timber.d("Is %s", enabled);
    return enabled;
  }
}
