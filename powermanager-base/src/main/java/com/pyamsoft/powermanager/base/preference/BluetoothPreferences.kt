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

package com.pyamsoft.powermanager.base.preference

import android.support.annotation.CheckResult

interface BluetoothPreferences {

  var originalBluetooth: Boolean
    @CheckResult get

  var ignoreChargingBluetooth: Boolean
    @CheckResult get

  var bluetoothManaged: Boolean
    @CheckResult get

  var periodicBluetooth: Boolean
    @CheckResult get

  var ignoreWearBluetooth: Boolean
    @CheckResult get
}
