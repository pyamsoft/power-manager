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
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.RxBus;

public class OverviewSelectionBus extends RxBus<OverviewSelectionBus.OverviewSelectionEvent> {

  @NonNull private static final OverviewSelectionBus instance = new OverviewSelectionBus();

  @CheckResult @NonNull public static OverviewSelectionBus get() {
    return instance;
  }

  public static final class OverviewSelectionEvent {

    @NonNull private final String type;

    public OverviewSelectionEvent(@NonNull String type) {
      this.type = type;
    }

    @NonNull @CheckResult public final String getType() {
      return type;
    }
  }
}
