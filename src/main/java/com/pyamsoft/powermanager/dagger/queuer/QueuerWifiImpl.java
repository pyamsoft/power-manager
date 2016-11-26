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

package com.pyamsoft.powermanager.dagger.queuer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.wrapper.JobQueuerWrapper;
import javax.inject.Inject;
import rx.Scheduler;

class QueuerWifiImpl extends QueuerImpl {

  @Inject QueuerWifiImpl(@NonNull Context context, @NonNull JobQueuerWrapper jobQueuerWrapper,
      @NonNull Scheduler handlerScheduler, @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier,
      @NonNull BooleanInterestObserver chargingObserver, @NonNull Logger logger) {
    super("WIFI", context, jobQueuerWrapper, handlerScheduler, stateObserver, stateModifier,
        chargingObserver, logger);
  }

  @NonNull @Override Intent getLongTermIntent(@NonNull Context context) {
    return new Intent(context.getApplicationContext(), QueuerWifiLongTermService.class);
  }
}
