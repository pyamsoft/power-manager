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

package com.pyamsoft.powermanager.dagger.service.jobs;

import com.pyamsoft.powermanager.app.service.job.PowerManagerFrameworkJobSchedulerService;
import com.pyamsoft.powermanager.app.service.job.PowerManagerGCMJobSchedulerService;
import com.pyamsoft.pydroid.ActivityScope;
import dagger.Subcomponent;

@ActivityScope @Subcomponent public interface JobServiceComponent {

  void inject(PowerManagerFrameworkJobSchedulerService service);

  void inject(PowerManagerGCMJobSchedulerService service);
}
