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

package com.pyamsoft.powermanager.dagger.observer.manage;

import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.app.service.FullNotificationActivity;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import com.pyamsoft.powermanager.dagger.PowerManagerComponent;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerModule;
import dagger.Component;
import javax.inject.Named;

@ActivityScope @Component(modules = {
    ManageObserverModule.class, ManagerModule.class
}, dependencies = PowerManagerComponent.class) public interface ManageObserverComponent {

  void inject(FullNotificationActivity.FullDialog activity);

  @Named("wifi") InterestObserver provideWifiManagerObserver();

  @Named("data") InterestObserver provideDataManagerObserver();

  @Named("bluetooth") InterestObserver provideBluetoothManagerObserver();

  @Named("sync") InterestObserver provideSyncManagerObserver();
}