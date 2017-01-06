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

package com.pyamsoft.powermanager.manager.queuer;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.base.logger.Logger;
import com.pyamsoft.powermanager.model.BooleanInterestModifier;
import com.pyamsoft.powermanager.model.BooleanInterestObserver;
import com.pyamsoft.powermanager.model.QueuerType;

class QueueRunner {

  @NonNull private final QueuerType type;
  @NonNull private final BooleanInterestObserver observer;
  @NonNull private final BooleanInterestModifier modifier;
  @NonNull private final BooleanInterestObserver charging;
  @NonNull private final Logger logger;
  private final int ignoreCharging;

  @SuppressWarnings("WeakerAccess") QueueRunner(@NonNull QueuerType type, int ignoreCharging,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier,
      @NonNull BooleanInterestObserver chargingObserver, @NonNull Logger logger) {
    this.type = type;
    this.ignoreCharging = ignoreCharging;
    this.observer = stateObserver;
    this.modifier = stateModifier;
    this.charging = chargingObserver;
    this.logger = logger;
  }

  @CheckResult @NonNull static Builder builder() {
    return new Builder();
  }

  void run() {
    if (type == QueuerType.SCREEN_OFF_DISABLE || type == QueuerType.SCREEN_OFF_ENABLE) {
      if (ignoreCharging == 1) {
        if (charging.is()) {
          logger.w("Ignore disable job because we are charging");
          return;
        }
      }
    }

    if (type == QueuerType.SCREEN_ON_ENABLE || type == QueuerType.SCREEN_OFF_ENABLE) {
      set();
    } else {
      unset();
    }
  }

  private void set() {
    if (!observer.is()) {
      modifier.set();
    }
  }

  private void unset() {
    if (observer.is()) {
      modifier.unset();
    }
  }

  static final class Builder {

    @Nullable private QueuerType type;
    @Nullable private BooleanInterestObserver observer;
    @Nullable private BooleanInterestModifier modifier;
    @Nullable private BooleanInterestObserver charging;
    @Nullable private Logger logger;
    private int ignoreCharging;

    Builder() {
      type = null;
      ignoreCharging = -1;
    }

    @CheckResult @NonNull Builder setType(@NonNull QueuerType type) {
      this.type = type;
      return this;
    }

    @CheckResult @NonNull Builder setIgnoreCharging(int ignore) {
      this.ignoreCharging = ignore;
      return this;
    }

    @CheckResult @NonNull Builder setObserver(@NonNull BooleanInterestObserver observer) {
      this.observer = observer;
      return this;
    }

    @CheckResult @NonNull Builder setModifier(@NonNull BooleanInterestModifier modifier) {
      this.modifier = modifier;
      return this;
    }

    @CheckResult @NonNull Builder setCharging(@NonNull BooleanInterestObserver charging) {
      this.charging = charging;
      return this;
    }

    @CheckResult @NonNull Builder setLogger(@NonNull Logger logger) {
      this.logger = logger;
      return this;
    }

    private void checkAll() {
      if (type == null) {
        throw new IllegalStateException("Type is unset");
      }

      if (ignoreCharging < 0) {
        throw new IllegalStateException("Ignore Charging is not set");
      }

      if (observer == null) {
        throw new IllegalStateException("Observer is NULL");
      }

      if (modifier == null) {
        throw new IllegalStateException("Modifier is NULL");
      }

      if (charging == null) {
        throw new IllegalStateException("Charging Observer is NULL");
      }

      if (logger == null) {
        throw new IllegalStateException("Logger is NULL");
      }
    }

    @CheckResult @NonNull QueueRunner build() {
      checkAll();

      // We checked for nulls and such before this
      //noinspection ConstantConditions
      return new QueueRunner(type, ignoreCharging, observer, modifier, charging, logger);
    }
  }
}
