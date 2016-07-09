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

package com.pyamsoft.powermanager.model.sql;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;

@AutoValue public abstract class PowerTriggerEntry implements PowerTriggerModel {

  @NonNull public static final String EMPTY_NAME = "EMPTY";
  public static final int EMPTY_PERCENT = -1;

  @NonNull public static final Factory<PowerTriggerEntry> FACTORY =
      new Factory<>(AutoValue_PowerTriggerEntry::new);

  @CheckResult @NonNull public static PowerTriggerEntry empty() {
    return new AutoValue_PowerTriggerEntry(EMPTY_PERCENT, EMPTY_NAME, false, false, false, false,
        false, false, false, false, false);
  }

  @CheckResult public static boolean isEmpty(@NonNull PowerTriggerEntry entry) {
    return entry.percent() == EMPTY_PERCENT && entry.name().equals(EMPTY_NAME);
  }

  // SQLDelight does not yet support delete strings
  @NonNull public static final String DELETE_WITH_PERCENT = "percent = ?";
  @NonNull public static final String DELETE_ALL = "1=1";

  // SQLDelight does not yet support update strings
  @NonNull public static final String UPDATE_WITH_PERCENT = "percent = ?";
}
