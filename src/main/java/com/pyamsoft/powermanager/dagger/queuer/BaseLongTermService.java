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
import timber.log.Timber;

abstract class BaseLongTermService extends IntentService {

  BaseLongTermService(String name) {
    super(name);
  }

  @Override protected final void onHandleIntent(Intent intent) {
    if (intent == null) {
      Timber.e("Intent is NULL. Skip");
      return;
    }

    if (!intent.hasExtra(QueuerImpl.EXTRA_JOB_TYPE)) {
      Timber.e("Intent does not have QueueType. Skip");
      return;
    }

    final String type = intent.getStringExtra(QueuerImpl.EXTRA_JOB_TYPE);
    if (type == null) {
      Timber.e("QueuerType extra is NULL. Skip");
      return;
    }

    final QueuerType queuerType = QueuerType.valueOf(type);
    if (queuerType == QueuerType.ENABLE) {
      injectDependencies();
      set();
    } else if (queuerType == QueuerType.DISABLE) {
      injectDependencies();
      unset();
    } else if (queuerType == QueuerType.TOGGLE_ENABLE) {
      injectDependencies();
      unset();
    } else if (queuerType == QueuerType.TOGGLE_DISABLE) {
      injectDependencies();
      set();
    } else {
      Timber.e("QueuerType %s invalid. Skip", queuerType.name());
    }
  }

  abstract void set();

  abstract void unset();

  abstract void injectDependencies();
}
