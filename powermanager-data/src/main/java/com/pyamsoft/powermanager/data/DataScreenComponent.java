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

package com.pyamsoft.powermanager.data;

import com.pyamsoft.powermanager.base.PowerManagerComponent;
import com.pyamsoft.powermanager.modifier.state.StateModifierModule;
import com.pyamsoft.powermanager.observer.permission.PermissionObserverModule;
import com.pyamsoft.powermanager.observer.preference.manage.ManageObserverModule;
import com.pyamsoft.powermanager.observer.preference.periodic.PeriodicObserverModule;
import com.pyamsoft.powermanager.observer.state.StateObserverModule;
import com.pyamsoft.pydroid.rx.scopes.FragmentScope;
import dagger.Component;

@FragmentScope @Component(dependencies = PowerManagerComponent.class, modules = {
    DataOverviewModule.class, DataManagePreferenceModule.class, DataPeriodPreferenceModule.class,
    StateObserverModule.class, StateModifierModule.class, ManageObserverModule.class,
    PeriodicObserverModule.class, PermissionObserverModule.class
}) interface DataScreenComponent {

  void inject(DataLoader loader);

  void inject(DataOverviewPresenterLoader loader);

  void inject(DataManagePresenterLoader loader);

  void inject(DataPeriodPresenterLoader loader);
}
