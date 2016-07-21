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

package com.pyamsoft.powermanager.dagger.modifier.state;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.modifier.InterestModifier;

abstract class StateModifier implements InterestModifier {

  @NonNull private final Context appContext;
  @NonNull private final Handler handler;

  StateModifier(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
    this.handler = new Handler(Looper.getMainLooper());
  }

  @NonNull @CheckResult final Context getAppContext() {
    return appContext;
  }

  @Override public final void set() {
    handler.removeCallbacksAndMessages(null);
    handler.post(this::mainThreadSet);
  }

  @Override public final void unset() {
    handler.removeCallbacksAndMessages(null);
    handler.post(this::mainThreadUnset);
  }

  abstract void mainThreadSet();

  abstract void mainThreadUnset();
}
