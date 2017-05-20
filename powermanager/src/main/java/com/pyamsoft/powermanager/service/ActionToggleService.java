/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.Injector;
import javax.inject.Inject;
import timber.log.Timber;

public class ActionToggleService extends IntentService {

  @Inject ActionTogglePresenter presenter;

  public ActionToggleService() {
    super(ActionToggleService.class.getSimpleName());
  }

  @Override public void onCreate() {
    super.onCreate();
    Injector.get().provideComponent().inject(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    presenter.stop();
    presenter.destroy();
  }

  @Override protected void onHandleIntent(@Nullable Intent intent) {
    presenter.toggleForegroundState(state -> {
      Timber.d("Foreground state toggled: %s", state);
      if (state) {
        ForegroundService.start(getApplicationContext());
      } else {
        ForegroundService.stop(getApplicationContext());
      }
    });
  }
}
