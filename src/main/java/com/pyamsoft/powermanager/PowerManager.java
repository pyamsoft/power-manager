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
package com.pyamsoft.powermanager;

import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.pydroid.base.ApplicationBase;

public final class PowerManager extends ApplicationBase {

  public static final String RATE = "market://details?id=com.pyamsoft.powermanager";
  private static final String TAG = PowerManager.class.getName();

  @Override public final boolean isBuildConfigDebug() {
    return BuildConfig.DEBUG;
  }

  @Override public final void onCreate() {
    super.onCreate();
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(this);
    if (p.powerManagerMonitor().isEnabled()) {
      // Start service
      MonitorService.start(getApplicationContext());
    } else if (p.powerManagerMonitor().isNotificationEnabled()) {
      // Just update notification from service
      PersistentNotification.update(getApplicationContext());
    }
  }

  @Override protected String setRateString() {
    return RATE;
  }
}
