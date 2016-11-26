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

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Scheduler;

class QueuerDozeImpl extends QueuerImpl {

  @Inject QueuerDozeImpl(@NonNull Context context, @NonNull AlarmManager alarmManager,
      @NonNull Scheduler handlerScheduler, @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier, @NonNull Logger logger) {
    super("DOZE", context, alarmManager, handlerScheduler, stateObserver, stateModifier, logger);
  }

  @NonNull @Override Intent getLongTermIntent(@NonNull Context context) {
    return new Intent(context.getApplicationContext(), LongTermService.class);
  }

  public static class LongTermService extends BaseLongTermService {

    @Inject @Named("obs_doze_state") BooleanInterestObserver stateObserver;
    @Inject @Named("mod_doze_state") BooleanInterestModifier stateModifier;

    public LongTermService() {
      super(LongTermService.class.getName());
    }

    @Override void set() {
      if (!stateObserver.is()) {
        stateModifier.set();
      }
    }

    @Override void unset() {
      if (stateObserver.is()) {
        stateModifier.unset();
      }
    }

    @Override void injectDependencies() {
      if (stateObserver == null || stateModifier == null) {
        Injector.get().provideComponent().plusQueuerComponent().inject(this);
      }
    }
  }
}
