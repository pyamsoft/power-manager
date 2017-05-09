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

package com.pyamsoft.powermanager.job;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;

@AutoValue public abstract class JobQueuerEntry {

  @CheckResult @NonNull public static Builder builder(@NonNull String tag) {
    return new AutoValue_JobQueuerEntry.Builder().tag(tag);
  }

  @CheckResult public abstract String tag();

  @CheckResult public abstract boolean firstRun();

  @CheckResult public abstract boolean oneshot();

  @CheckResult public abstract boolean screenOn();

  @CheckResult public abstract long delay();

  @CheckResult public abstract long repeatingOnWindow();

  @CheckResult public abstract long repeatingOffWindow();

  @AutoValue.Builder public static abstract class Builder {

    @CheckResult abstract Builder tag(String tag);

    @CheckResult public abstract Builder screenOn(boolean screen);

    @CheckResult public abstract Builder firstRun(boolean first);

    @CheckResult public abstract Builder oneshot(boolean oneshot);

    @CheckResult public abstract Builder delay(long delay);

    @CheckResult public abstract Builder repeatingOnWindow(long window);

    @CheckResult public abstract Builder repeatingOffWindow(long window);

    @CheckResult public abstract JobQueuerEntry build();
  }
}
