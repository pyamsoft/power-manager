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

import android.support.annotation.NonNull;
import javax.inject.Inject;

class QueuerImpl implements Queuer {

  @Inject QueuerImpl() {

  }

  @NonNull @Override public Queuer cancel(@NonNull String tag) {
    return this;
  }

  @NonNull @Override public Queuer setType(@NonNull QueuerType queuerType) {
    return this;
  }

  @NonNull @Override public Queuer setDelayTime(long time) {
    return this;
  }

  @NonNull @Override public Queuer setPeriodic(boolean periodic) {
    return this;
  }

  @NonNull @Override public Queuer setIgnoreCharging(boolean ignore) {
    return this;
  }

  @NonNull @Override public Queuer setPeriodicEnableTime(long time) {
    return this;
  }

  @NonNull @Override public Queuer setPeriodicDisableTime(long time) {
    return this;
  }

  @Override public void queue() {

  }
}
