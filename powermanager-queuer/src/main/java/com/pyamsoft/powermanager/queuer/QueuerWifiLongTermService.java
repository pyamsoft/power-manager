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

package com.pyamsoft.powermanager.queuer;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.Injector;
import com.pyamsoft.powermanager.base.logger.Logger;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import javax.inject.Inject;
import javax.inject.Named;

abstract class QueuerWifiLongTermService extends BaseLongTermService {

  @Inject @Named("obs_wifi_state") BooleanInterestObserver stateObserver;
  @Inject @Named("mod_wifi_state") BooleanInterestModifier stateModifier;
  @Inject @Named("logger_wifi") Logger logger;
  @Inject @Named("queuer_wifi") Queuer queuer;

  @NonNull @Override Logger getLogger() {
    return logger;
  }

  @Override public BooleanInterestObserver getStateObserver() {
    return stateObserver;
  }

  @Override public BooleanInterestModifier getStateModifier() {
    return stateModifier;
  }

  @Override Queuer getQueuer() {
    return queuer;
  }

  @NonNull @Override Class<? extends BaseLongTermService> getScreenOnServiceClass() {
    return QueuerWifiEnableService.class;
  }

  @NonNull @Override Class<? extends BaseLongTermService> getScreenOffServiceClass() {
    return QueuerWifiDisableService.class;
  }

  @Override void injectDependencies() {
    DaggerQueuerComponent.builder()
        .powerManagerComponent(Injector.get().provideComponent())
        .build()
        .inject(this);
  }
}
