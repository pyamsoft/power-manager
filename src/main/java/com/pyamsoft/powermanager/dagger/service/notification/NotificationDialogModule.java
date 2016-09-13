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

package com.pyamsoft.powermanager.dagger.service.notification;

import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.service.notification.NotificationDialogPresenter;
import com.pyamsoft.pydroid.dagger.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import rx.Scheduler;

@Module public class NotificationDialogModule {

  @ActivityScope @Provides NotificationDialogPresenter provideNotificationDialogPresenter(
      @Named("main") Scheduler mainScheduler, @Named("io") Scheduler ioScheduler,

      @Named("obs_wifi_state") BooleanInterestObserver wifiStateObserver,
      @Named("obs_data_state") BooleanInterestObserver dataStateObserver,
      @Named("obs_bluetooth_state") BooleanInterestObserver bluetoothStateObserver,
      @Named("obs_sync_state") BooleanInterestObserver syncStateObserver,

      @Named("obs_wifi_manage") BooleanInterestObserver wifiManageObserver,
      @Named("obs_data_manage") BooleanInterestObserver dataManageObserver,
      @Named("obs_bluetooth_manage") BooleanInterestObserver bluetoothManageObserver,
      @Named("obs_sync_manage") BooleanInterestObserver syncManageObserver,

      @Named("mod_wifi_state") BooleanInterestModifier wifiStateModifier,
      @Named("mod_data_state") BooleanInterestModifier dataStateModifier,
      @Named("mod_bluetooth_state") BooleanInterestModifier bluetoothStateModifier,
      @Named("mod_sync_state") BooleanInterestModifier syncStateModifier,

      @Named("mod_wifi_manage") BooleanInterestModifier wifiManageModifier,
      @Named("mod_data_manage") BooleanInterestModifier dataManageModifier,
      @Named("mod_bluetooth_manage") BooleanInterestModifier bluetoothManageModifier,
      @Named("mod_sync_manage") BooleanInterestModifier syncManageModifier) {
    return new NotificationDialogPresenterImpl(mainScheduler, ioScheduler, wifiStateObserver,
        dataStateObserver, bluetoothStateObserver, syncStateObserver, wifiManageObserver,
        dataManageObserver, bluetoothManageObserver, syncManageObserver, wifiStateModifier,
        dataStateModifier, bluetoothStateModifier, syncStateModifier, wifiManageModifier,
        dataManageModifier, bluetoothManageModifier, syncManageModifier);
  }
}
