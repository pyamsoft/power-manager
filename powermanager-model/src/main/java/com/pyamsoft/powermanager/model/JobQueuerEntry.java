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

package com.pyamsoft.powermanager.model;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;

@AutoValue public abstract class JobQueuerEntry {

  @CheckResult @NonNull public static Builder builder(@NonNull String tag) {
    return new AutoValue_JobQueuerEntry.Builder().tag(tag);
  }

  public abstract String tag();

  public abstract QueuerType type();

  public abstract long delay();

  public abstract boolean repeating();

  public abstract long repeatingOnWindow();

  public abstract long repeatingOffWindow();

  public abstract boolean ignoreIfCharging();

  @AutoValue.Builder public static abstract class Builder {

    abstract Builder tag(String tag);

    public abstract Builder type(QueuerType type);

    public abstract Builder delay(long delay);

    public abstract Builder repeating(boolean repeating);

    public abstract Builder repeatingOnWindow(long window);

    public abstract Builder repeatingOffWindow(long window);

    public abstract Builder ignoreIfCharging(boolean ignore);

    public abstract JobQueuerEntry build();
  }
}
