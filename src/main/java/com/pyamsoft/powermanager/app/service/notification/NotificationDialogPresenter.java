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

package com.pyamsoft.powermanager.app.service.notification;

import com.pyamsoft.pydroid.base.Presenter;

public interface NotificationDialogPresenter extends Presenter<NotificationDialogPresenter.View> {

  void wifiToggleClicked();

  void dataToggleClicked();

  void bluetoothToggleClicked();

  void syncToggleClicked();

  void wifiManageClicked();

  void dataManageClicked();

  void bluetoothManageClicked();

  void syncManageClicked();

  interface View {

    void setWifiToggleState(boolean value);

    void setDataToggleState(boolean value);

    void setBluetoothToggleState(boolean value);

    void setSyncToggleState(boolean value);

    void setWifiManageState(boolean value);

    void setDataManageState(boolean value);

    void setBluetoothManageState(boolean value);

    void setSyncManageState(boolean value);
  }
}
