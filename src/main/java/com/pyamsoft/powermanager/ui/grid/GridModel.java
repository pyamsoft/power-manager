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

package com.pyamsoft.powermanager.ui.grid;

import android.content.Context;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import java.lang.ref.WeakReference;

final class GridModel {

  private final WeakReference<Context> weakContext;

  GridModel(final Context context) {
    weakContext = new WeakReference<>(context);
  }

  void launchPowerManagerService() {
    final Context context = weakContext.get();
    if (context != null) {
      MonitorService.powerManagerService(context);
      final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(context);
      if (p.powerManagerMonitor().isNotificationEnabled()) {
        if (p.powerManagerMonitor().isForeground()) {
          MonitorService.startForeground(context);
        } else {
          PersistentNotification.update(context);
        }
      }
    }
  }
}
