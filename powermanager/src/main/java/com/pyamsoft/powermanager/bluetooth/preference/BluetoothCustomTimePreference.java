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

package com.pyamsoft.powermanager.bluetooth.preference;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.uicore.preference.CustomTimeInputPreference;
import javax.inject.Inject;
import javax.inject.Named;

public class BluetoothCustomTimePreference extends CustomTimeInputPreference {

  @SuppressWarnings("WeakerAccess") @Named("bluetooth_custom_delay") @Inject
  CustomTimeInputPreferencePresenter delayPresenter;
  @SuppressWarnings("WeakerAccess") @Named("bluetooth_custom_enable") @Inject
  CustomTimeInputPreferencePresenter enablePresenter;
  @SuppressWarnings("WeakerAccess") @Named("bluetooth_custom_disable") @Inject
  CustomTimeInputPreferencePresenter disablePresenter;

  public BluetoothCustomTimePreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public BluetoothCustomTimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public BluetoothCustomTimePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BluetoothCustomTimePreference(Context context) {
    super(context);
  }

  @NonNull @Override protected String getName() {
    return "Bluetooth";
  }

  @Override protected void injectDependencies() {
    Injector.get().provideComponent().plusBluetoothPreferenceComponent().inject(this);
  }

  @NonNull @Override protected CustomTimeInputPreferencePresenter provideEnablePresenter() {
    return enablePresenter;
  }

  @NonNull @Override protected CustomTimeInputPreferencePresenter provideDisablePresenter() {
    return disablePresenter;
  }

  @NonNull @Override protected CustomTimeInputPreferencePresenter provideDelayPresenter() {
    return delayPresenter;
  }
}
