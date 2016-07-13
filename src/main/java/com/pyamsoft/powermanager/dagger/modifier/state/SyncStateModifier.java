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

package com.pyamsoft.powermanager.dagger.modifier.state;

import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncStateModifier extends StateModifier {

  @Inject SyncStateModifier(@NonNull Context context) {
    super(context);
  }

  @Override void mainThreadSet() {
    Timber.d("Set sync: true");
    ContentResolver.setMasterSyncAutomatically(true);
  }

  @Override void mainThreadUnset() {
    Timber.d("Set sync: false");
    ContentResolver.setMasterSyncAutomatically(false);
  }
}