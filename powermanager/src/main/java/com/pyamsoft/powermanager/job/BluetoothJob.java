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

package com.pyamsoft.powermanager.job;

import android.support.annotation.NonNull;
import com.evernote.android.job.Job;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.StateInterestObserver;
import com.pyamsoft.powermanager.model.Logger;
import javax.inject.Inject;
import javax.inject.Named;

public class BluetoothJob extends BaseJob {

  @SuppressWarnings("WeakerAccess") @Inject @Named("logger_bluetooth") Logger logger;
  @SuppressWarnings("WeakerAccess") @Inject @Named("obs_bluetooth_state") StateInterestObserver
      stateObserver;
  @SuppressWarnings("WeakerAccess") @Inject @Named("mod_bluetooth_state") BooleanInterestModifier
      stateModifier;

  @Override void inject() {
    Injector.get().provideComponent().plusJobComponent().inject(this);
  }

  @NonNull @Override Logger getLogger() {
    return logger;
  }

  @NonNull @Override StateInterestObserver getObserver() {
    return stateObserver;
  }

  @NonNull @Override BooleanInterestModifier getModifier() {
    return stateModifier;
  }

  public static class ManagedJob extends Job {

    @NonNull @Override protected Result onRunJob(Params params) {
      new BluetoothJob() {
        @Override boolean isStopped() {
          return isCanceled();
        }
      }.run(params.getTag(), params.getExtras());
      return Result.SUCCESS;
    }
  }
}
