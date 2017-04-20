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

package com.pyamsoft.powermanager.overview;

import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;
import com.pyamsoft.powermanager.model.States;

@AutoValue abstract class OverviewModel {

  @CheckResult @NonNull static Builder builder() {
    return new AutoValue_OverviewModel.Builder();
  }

  @CheckResult abstract String title();

  @CheckResult abstract States state();

  @CheckResult @DrawableRes abstract int image();

  @CheckResult @ColorRes abstract int background();

  @AutoValue.Builder static abstract class Builder {

    @CheckResult abstract Builder title(String s);

    @CheckResult abstract Builder image(@DrawableRes int i);

    @CheckResult abstract Builder background(@ColorRes int i);

    @CheckResult abstract Builder state(States state);

    @CheckResult abstract OverviewModel build();
  }
}
