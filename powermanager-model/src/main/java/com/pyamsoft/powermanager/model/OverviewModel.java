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
import android.support.annotation.Nullable;
import android.view.View;
import com.google.auto.value.AutoValue;
import com.pyamsoft.powermanager.model.overlord.StateChangeObserver;

@AutoValue public abstract class OverviewModel {

  @CheckResult @NonNull public static Builder builder() {
    return new AutoValue_OverviewModel.Builder();
  }

  public abstract View rootView();

  public abstract String title();

  @DrawableRes public abstract int image();

  @ColorRes public abstract int background();

  @Nullable public abstract StateChangeObserver observer();

  @AutoValue.Builder public static abstract class Builder {

    public abstract Builder rootView(View v);

    public abstract Builder title(String s);

    public abstract Builder image(@DrawableRes int i);

    public abstract Builder background(@ColorRes int i);

    public abstract Builder observer(@Nullable StateChangeObserver observer);

    public abstract OverviewModel build();
  }
}
