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

package com.pyamsoft.powermanager.dagger.modifier.state;

import com.pyamsoft.powermanager.app.modifier.InterestModifier;
import com.pyamsoft.powermanager.app.service.FullNotificationActivity;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerModule;
import com.pyamsoft.powermanager.dagger.trigger.TriggerJob;
import dagger.Subcomponent;
import javax.inject.Named;

@ActivityScope @Subcomponent(modules = {
    StateModifierModule.class, ManagerModule.class
}) public interface StateModifierComponent {

  void inject(FullNotificationActivity.FullDialog activity);

  void inject(TriggerJob job);

  @Named("wifi") InterestModifier provideWifiStateModifier();

  @Named("data") InterestModifier provideDataStateModifier();

  @Named("bluetooth") InterestModifier provideBluetoothStateModifier();

  @Named("sync") InterestModifier provideSyncStateModifier();
}
