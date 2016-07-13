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

package com.pyamsoft.powermanager.dagger.observer.manage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.R;
import javax.inject.Inject;

public class BluetoothManageObserver
    extends ManagePreferenceObserver<BluetoothManageObserver.View> {

  @Nullable private View view;

  @Inject BluetoothManageObserver(@NonNull Context context) {
    super(context, context.getString(R.string.manage_bluetooth_key),
        context.getResources().getBoolean(R.bool.manage_bluetooth_default));
  }

  @Override void onChange() {
    if (view != null) {
      if (is()) {
        view.onBluetoothManageEnabled();
      } else {
        view.onBluetoothManageDisabled();
      }
    }
  }

  @Override public void setView(@NonNull View view) {
    this.view = view;
  }

  public interface View {

    void onBluetoothManageEnabled();

    void onBluetoothManageDisabled();
  }
}
