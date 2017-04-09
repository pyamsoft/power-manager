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

package com.pyamsoft.powermanager.job;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;

@AutoValue public abstract class JobQueuerEntry {

  @CheckResult @NonNull public static Builder builder(@NonNull String tag) {
    return new AutoValue_JobQueuerEntry.Builder().tag(tag);
  }

  @CheckResult public abstract String tag();

  @CheckResult public abstract QueuerType type();

  @CheckResult public abstract long delay();

  @CheckResult public abstract boolean repeating();

  @CheckResult public abstract long repeatingOnWindow();

  @CheckResult public abstract long repeatingOffWindow();

  @CheckResult public abstract boolean ignoreIfCharging();

  @AutoValue.Builder public static abstract class Builder {

    @CheckResult abstract Builder tag(String tag);

    @CheckResult public abstract Builder type(QueuerType type);

    @CheckResult public abstract Builder delay(long delay);

    @CheckResult public abstract Builder repeating(boolean repeating);

    @CheckResult public abstract Builder repeatingOnWindow(long window);

    @CheckResult public abstract Builder repeatingOffWindow(long window);

    @CheckResult public abstract Builder ignoreIfCharging(boolean ignore);

    @CheckResult public abstract JobQueuerEntry build();
  }
}
