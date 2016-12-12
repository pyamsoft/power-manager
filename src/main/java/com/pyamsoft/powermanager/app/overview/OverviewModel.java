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

package com.pyamsoft.powermanager.app.overview;

import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.google.auto.value.AutoValue;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;

@AutoValue abstract class OverviewModel {

  @CheckResult @NonNull static Builder builder() {
    return new AutoValue_OverviewModel.Builder();
  }

  abstract View rootView();

  abstract String title();

  @DrawableRes abstract int image();

  @ColorRes abstract int background();

  @Nullable abstract BooleanInterestObserver observer();

  @AutoValue.Builder static abstract class Builder {

    abstract Builder rootView(View v);

    abstract Builder title(String s);

    abstract Builder image(@DrawableRes int i);

    abstract Builder background(@ColorRes int i);

    abstract Builder observer(@Nullable BooleanInterestObserver observer);

    abstract OverviewModel build();
  }
}