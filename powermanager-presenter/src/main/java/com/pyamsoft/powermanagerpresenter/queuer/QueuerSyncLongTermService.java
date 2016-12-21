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

package com.pyamsoft.powermanagerpresenter.queuer;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanagermodel.BooleanInterestModifier;
import com.pyamsoft.powermanagermodel.BooleanInterestObserver;
import com.pyamsoft.powermanagerpresenter.Injector;
import com.pyamsoft.powermanagerpresenter.logger.Logger;
import javax.inject.Inject;
import javax.inject.Named;

public abstract class QueuerSyncLongTermService extends BaseLongTermService {

  @Inject @Named("obs_sync_state") BooleanInterestObserver stateObserver;
  @Inject @Named("mod_sync_state") BooleanInterestModifier stateModifier;
  @Inject @Named("logger_sync") Logger logger;
  @Inject @Named("queuer_sync") Queuer queuer;

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
    return QueuerSyncEnableService.class;
  }

  @NonNull @Override Class<? extends BaseLongTermService> getScreenOffServiceClass() {
    return QueuerSyncDisableService.class;
  }

  @Override public BooleanInterestObserver getStateObserver() {
    return stateObserver;
  }

  @Override void injectDependencies() {
    Injector.get().provideComponent().plusQueuerComponent().inject(this);
  }
}
