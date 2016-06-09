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

package com.pyamsoft.powermanager.dagger;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.pydroid.base.app.ApplicationPreferences;
import javax.inject.Inject;

final class PowerManagerPreferencesImpl extends ApplicationPreferences
    implements PowerManagerPreferences {

  @Inject protected PowerManagerPreferencesImpl(@NonNull Context context) {
    super(context);
  }

  @Override public long getWifiDelay() {
    // TODO
    return 1;
  }

  @Override public void setWifiDelay(long time) {
    // TODO
  }

  @Override public long getDataDelay() {
    return 1;
  }

  @Override public void setDataDelay(long time) {
    // TODO
  }

  @Override public long getBluetoothDelay() {
    return 1;
  }

  @Override public void setBluetoothDelay(long time) {
    // TODO
  }

  @Override public long getMasterSyncDelay() {
    return 1;
  }

  @Override public void setMasterSyncDelay(long time) {
    // TODO
  }
}
