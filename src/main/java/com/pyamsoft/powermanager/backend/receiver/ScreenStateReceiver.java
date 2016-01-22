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
package com.pyamsoft.powermanager.backend.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.service.ActiveService;
import com.pyamsoft.pydroid.util.LogUtil;

public final class ScreenStateReceiver extends BroadcastReceiver {

  private static final String TAG = ScreenStateReceiver.class.getSimpleName();
  private static final boolean SCREEN_OFF = true;
  private static final boolean SCREEN_ON = false;
  private final IntentFilter filter;
  private boolean isRegistered;

  public ScreenStateReceiver() {
    filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
    filter.addAction(Intent.ACTION_SCREEN_ON);
    isRegistered = false;
  }

  @Override public final void onReceive(final Context context, final Intent intent) {
    if (null != intent) {
      final String action = intent.getAction();
      switch (action) {
        case Intent.ACTION_SCREEN_OFF:
          LogUtil.d(TAG, context.getString(R.string.screen_off));
          ActiveService.startService(context.getApplicationContext(), SCREEN_OFF);
          break;
        case Intent.ACTION_SCREEN_ON:
          LogUtil.d(TAG, context.getString(R.string.screen_on));
          ActiveService.startService(context.getApplicationContext(), SCREEN_ON);
          break;
        default:
      }
    }
  }

  public final boolean register(final Context c) {
    boolean ret = false;
    if (!isRegistered) {
      c.getApplicationContext().registerReceiver(this, filter);
      isRegistered = true;
      ret = true;
    }
    return ret;
  }

  public final boolean unregister(final Context c) {
    boolean ret = false;
    if (isRegistered) {
      c.getApplicationContext().unregisterReceiver(this);
      isRegistered = false;
      ret = true;
    }
    return ret;
  }
}
