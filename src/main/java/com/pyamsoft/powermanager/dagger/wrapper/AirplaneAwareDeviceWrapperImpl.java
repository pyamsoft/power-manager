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
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.wrapper.DeviceFunctionWrapper;

abstract class AirplaneAwareDeviceWrapperImpl implements DeviceFunctionWrapper {

  @NonNull private final ContentResolver contentResolver;

  AirplaneAwareDeviceWrapperImpl(@NonNull Context context) {
    this.contentResolver = context.getApplicationContext().getContentResolver();
  }

  @CheckResult boolean isAirplaneMode() {
    return Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
  }
}