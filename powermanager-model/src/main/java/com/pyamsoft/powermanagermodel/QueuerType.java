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

package com.pyamsoft.powermanagermodel;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

public enum QueuerType {

  SCREEN_OFF_ENABLE,
  SCREEN_OFF_DISABLE,
  SCREEN_ON_ENABLE,
  SCREEN_ON_DISABLE;

  @CheckResult @NonNull public QueuerType flip() {
    final QueuerType newType;
    if (this == QueuerType.SCREEN_OFF_DISABLE) {
      newType = QueuerType.SCREEN_ON_ENABLE;
    } else if (this == QueuerType.SCREEN_OFF_ENABLE) {
      newType = QueuerType.SCREEN_ON_DISABLE;
    } else if (this == QueuerType.SCREEN_ON_ENABLE) {
      newType = QueuerType.SCREEN_OFF_DISABLE;
    } else if (this == QueuerType.SCREEN_ON_DISABLE) {
      newType = QueuerType.SCREEN_OFF_ENABLE;
    } else {
      throw new IllegalStateException("Invalid QueuerType " + this);
    }
    return newType;
  }

}
