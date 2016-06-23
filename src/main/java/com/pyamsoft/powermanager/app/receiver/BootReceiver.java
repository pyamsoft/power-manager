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

package com.pyamsoft.powermanager.app.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;
import timber.log.Timber;

public final class BootReceiver extends BroadcastReceiver {

  public static void setBootEnabled(final Context context, final boolean bootEnabled) {
    Timber.d("set boot enabled state: %s", bootEnabled);
    final ComponentName cmp =
        new ComponentName(context.getApplicationContext(), BootReceiver.class);
    final int componentState = bootEnabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    context.getApplicationContext()
        .getPackageManager()
        .setComponentEnabledSetting(cmp, componentState, PackageManager.DONT_KILL_APP);
  }

  public static boolean isBootEnabled(final Context context) {
    final ComponentName cmp =
        new ComponentName(context.getApplicationContext(), BootReceiver.class);
    final int componentState =
        context.getApplicationContext().getPackageManager().getComponentEnabledSetting(cmp);
    return componentState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
  }

  @Override public void onReceive(Context context, Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
        Timber.d("Boot completed");
        Toast.makeText(context.getApplicationContext(), "Power Manager started", Toast.LENGTH_LONG)
            .show();
      }
    }
  }
}
