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

package com.pyamsoft.powermanager.dagger.observer.state;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class WearStateObserver extends StateObserver {
  WearStateObserver(@NonNull Context context) {
    super(context);
  }

  @Override public boolean is() {
    return true;
  }

  @Override public void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    throw new RuntimeException("Cannot monitor for state changes on Wearables");
  }

  @Override public void unregister(@NonNull String tag) {
    throw new RuntimeException("Cannot monitor for state changes on Wearables");
  }
}
