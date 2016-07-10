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

package com.pyamsoft.powermanager.dagger.manager.manage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.powermanager.dagger.manager.ManagerSettingsInteractorImpl;
import javax.inject.Inject;
import rx.Observable;

final class ManagerManageInteractorImpl extends ManagerSettingsInteractorImpl
    implements ManagerManageInteractor {

  @Inject ManagerManageInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    super(context, preferences);
  }

  @Override @NonNull public Observable<Boolean> isCustomDelayTime(@NonNull String key) {
    return Observable.defer(() -> {
      boolean custom;
      if (key.equals(KEY_MANAGE_WIFI)) {
        custom = getPreferences().isCustomDelayTimeWifi();
      } else if (key.equals(KEY_MANAGE_DATA)) {
        custom = getPreferences().isCustomDelayTimeData();
      } else if (key.equals(KEY_MANAGE_BLUETOOTH)) {
        custom = getPreferences().isCustomDelayTimeBluetooth();
      } else if (key.equals(KEY_MANAGE_SYNC)) {
        custom = getPreferences().isCustomDelayTimeSync();
      } else {
        throw new IllegalStateException("Invalid key");
      }
      return Observable.just(custom);
    });
  }

  @Override public void updateNotificationOnManageStateChange() {
    final Intent serviceUpdateIntent = new Intent(getAppContext(), ForegroundService.class);
    getAppContext().startService(serviceUpdateIntent);
  }
}
