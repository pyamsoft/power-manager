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
import com.pyamsoft.powermanager.app.logger.Logger;
import com.pyamsoft.powermanager.app.modifier.BooleanInterestModifier;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;

final class QueueRunner {

  private QueueRunner() {
    throw new RuntimeException("No instances");
  }

  static void run(@NonNull String jobTag, @NonNull QueuerType type,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier,
      @NonNull BooleanInterestObserver chargingObserver, @NonNull Logger logger,
      int ignoreCharging) {
    if (type == QueuerType.ENABLE) {
      set(type, jobTag, stateObserver, stateModifier, logger);
    } else if (type == QueuerType.DISABLE) {
      if (ignoreCharging == 1) {
        if (chargingObserver.is()) {
          logger.w("Ignore disable job because we are charging: %s", jobTag);
          return;
        }
      }
      unset(type, jobTag, stateObserver, stateModifier, logger);
    } else if (type == QueuerType.TOGGLE_ENABLE) {
      unset(type, jobTag, stateObserver, stateModifier, logger);
    } else if (type == QueuerType.TOGGLE_DISABLE) {
      if (ignoreCharging == 1) {
        if (chargingObserver.is()) {
          logger.w("Ignore disable job because we are charging: %s", jobTag);
          return;
        }
      }
      set(type, jobTag, stateObserver, stateModifier, logger);
    } else {
      throw new IllegalStateException("QueueType is Invalid");
    }
  }

  private static void set(@NonNull QueuerType type, @NonNull String jobTag,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier, @NonNull Logger logger) {
    logger.i("Prereq %s job: %s", type, jobTag);
    if (!stateObserver.is()) {
      logger.w("RUN: %s job %s", type, jobTag);
      stateModifier.set();
    }
  }

  private static void unset(@NonNull QueuerType type, @NonNull String jobTag,
      @NonNull BooleanInterestObserver stateObserver,
      @NonNull BooleanInterestModifier stateModifier, @NonNull Logger logger) {
    logger.i("Prereq %s job: %s", type, jobTag);
    if (stateObserver.is()) {
      logger.w("RUN: %s job %s", type, jobTag);
      stateModifier.unset();
    }
  }
}
