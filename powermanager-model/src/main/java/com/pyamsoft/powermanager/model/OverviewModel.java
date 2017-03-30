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
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import com.google.auto.value.AutoValue;
import com.pyamsoft.powermanager.model.states.States;

@AutoValue public abstract class OverviewModel {

  @CheckResult @NonNull public static Builder builder() {
    return new AutoValue_OverviewModel.Builder();
  }

  @CheckResult public abstract View rootView();

  @CheckResult public abstract String title();

  @CheckResult public abstract States state();

  @DrawableRes public abstract int image();

  @ColorRes public abstract int background();

  @AutoValue.Builder public static abstract class Builder {

    @CheckResult public abstract Builder rootView(View v);

    @CheckResult public abstract Builder title(String s);

    @CheckResult public abstract Builder image(@DrawableRes int i);

    @CheckResult public abstract Builder background(@ColorRes int i);

    @CheckResult public abstract Builder state(States state);

    @CheckResult public abstract OverviewModel build();
  }
}
