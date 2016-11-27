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

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.dagger.wrapper.JobQueuerWrapper;
import javax.inject.Inject;
import javax.inject.Named;

public abstract class BaseLongTermService extends IntentService {

  @NonNull static final String EXTRA_JOB_TYPE = "extra_job_queue_type";
  @NonNull static final String EXTRA_IGNORE_CHARGING = "extra_ignore_charging";

  @Inject @Named("obs_charging_state") BooleanInterestObserver chargingObserver;
  @Inject JobQueuerWrapper jobQueuerWrapper;

  BaseLongTermService(String name) {
    super(name);
  }

  @Override protected final void onHandleIntent(Intent intent) {
    inject();

    if (intent == null) {
      getLogger().e("Intent is NULL. Skip");
      return;
    }

    if (!intent.hasExtra(EXTRA_JOB_TYPE)) {
      getLogger().e("Intent does not have QueueType. Skip");
      return;
    }

    final String type = intent.getStringExtra(EXTRA_JOB_TYPE);
    if (type == null) {
      getLogger().e("QueuerType extra is NULL. Skip");
      return;
    }

    final int ignoreCharging = intent.getIntExtra(EXTRA_IGNORE_CHARGING, -1);
    if (ignoreCharging < 0) {
      getLogger().e("Ignore Charging was not passed with Intent");
      return;
    }

    final QueuerType queuerType = QueuerType.valueOf(type);
    getLogger().d("Run long queue job: %s", getJobTag());
    QueueRunner.run(getJobTag(), queuerType, getStateObserver(), getStateModifier(),
        chargingObserver, getLogger(), ignoreCharging);
  }

  private void inject() {
    Injector.get().provideComponent().plusQueuerComponent().inject(this);
    injectDependencies();
  }

  @CheckResult @NonNull abstract String getJobTag();

  @CheckResult @NonNull abstract Logger getLogger();

  @CheckResult @Named abstract BooleanInterestObserver getStateObserver();

  @CheckResult @Named abstract BooleanInterestModifier getStateModifier();

  abstract void injectDependencies();
}
