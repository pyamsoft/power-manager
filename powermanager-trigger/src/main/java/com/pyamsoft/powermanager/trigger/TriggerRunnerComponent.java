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

package com.pyamsoft.powermanager.trigger;

import com.pyamsoft.powermanager.base.PowerManagerComponent;
import com.pyamsoft.powermanager.logger.LoggerModule;
import com.pyamsoft.powermanager.modifier.state.StateModifierModule;
import com.pyamsoft.powermanager.observer.state.StateObserverModule;
import com.pyamsoft.pydroid.rx.scopes.ServiceScope;
import dagger.Component;

@ServiceScope @Component(dependencies = PowerManagerComponent.class, modules = {
    LoggerModule.class, StateObserverModule.class, StateModifierModule.class
}) interface TriggerRunnerComponent {

  void inject(TriggerRunnerService runner);
}
