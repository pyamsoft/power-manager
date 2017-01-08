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

package com.pyamsoft.powermanager.manager;

import com.google.auto.value.AutoValue;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;

@AutoValue abstract class JobQueuer {

  abstract long delay();

  abstract boolean repeating();

  abstract long repeatingOnWindow();

  abstract long repeatingOffWindow();

  abstract boolean ignoreIfCharging();

  abstract BooleanInterestObserver observer();

  abstract BooleanInterestModifier modifier();

  @AutoValue.Builder static abstract class Builder {

    abstract Builder delay(long delay);

    abstract Builder repeating(boolean repeating);

    abstract Builder repeatingOnWindow(long window);

    abstract Builder repeatingOffWindow(long window);

    abstract Builder ignoreIfCharging(boolean ignore);

    abstract Builder observer(BooleanInterestObserver observer);

    abstract Builder modifier(BooleanInterestModifier modifier);

    abstract JobQueuer build();
  }
}
