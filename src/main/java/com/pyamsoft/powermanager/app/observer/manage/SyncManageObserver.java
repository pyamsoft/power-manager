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

package com.pyamsoft.powermanager.app.observer.manage;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.R;

public class SyncManageObserver extends ManagePreferenceObserver {

  @NonNull private final View view;

  public SyncManageObserver(@NonNull Context context, @NonNull View view) {
    super(context, context.getString(R.string.manage_sync_key),
        context.getResources().getBoolean(R.bool.manage_sync_default));
    this.view = view;
  }

  @Override void onChange() {
    if (is()) {
      view.onSyncManageEnabled();
    } else {
      view.onSyncManageDisabled();
    }
  }

  public interface View {

    void onSyncManageEnabled();

    void onSyncManageDisabled();
  }
}
