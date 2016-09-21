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

package com.pyamsoft.powermanager.dagger.job;

import com.pyamsoft.pydroid.ActivityScope;
import dagger.Subcomponent;

@ActivityScope @Subcomponent public interface JobComponent {

  void inject(WifiManageJob.EnableJob job);

  void inject(WifiManageJob.DisableJob job);

  void inject(DataManageJob.EnableJob job);

  void inject(DataManageJob.DisableJob job);

  void inject(BluetoothManageJob.EnableJob job);

  void inject(BluetoothManageJob.DisableJob job);

  void inject(SyncManageJob.EnableJob job);

  void inject(SyncManageJob.DisableJob job);

  void inject(DozeManageJob.EnableJob job);

  void inject(DozeManageJob.DisableJob job);
}
