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
import com.pyamsoft.powermanager.logger.Logger;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import javax.inject.Inject;
import javax.inject.Named;

abstract class QueuerDataLongTermService extends BaseLongTermService {

  @Inject @Named("obs_data_state") BooleanInterestObserver stateObserver;
  @Inject @Named("mod_data_state") BooleanInterestModifier stateModifier;
  @Inject @Named("logger_data") Logger logger;
  @Inject @Named("queuer_data") Queuer queuer;

  @NonNull @Override Logger getLogger() {
    return logger;
  }

  @Override public BooleanInterestModifier getStateModifier() {
    return stateModifier;
  }

  @Override public Queuer getQueuer() {
    return queuer;
  }

  @NonNull @Override Class<? extends BaseLongTermService> getScreenOnServiceClass() {
    return QueuerDataEnableService.class;
  }

  @NonNull @Override Class<? extends BaseLongTermService> getScreenOffServiceClass() {
    return QueuerDataDisableService.class;
  }

  @Override public BooleanInterestObserver getStateObserver() {
    return stateObserver;
  }

  @Override void injectDependencies() {
    DaggerQueuerComponent.builder()
        .powerManagerComponent(Injector.get().provideComponent())
        .build()
        .inject(this);
  }
}
