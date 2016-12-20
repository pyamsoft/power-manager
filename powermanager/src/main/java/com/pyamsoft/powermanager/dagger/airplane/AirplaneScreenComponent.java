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

package com.pyamsoft.powermanager.dagger.airplane;

import com.pyamsoft.powermanager.app.airplane.AirplaneFragment;
import com.pyamsoft.powermanager.app.airplane.AirplaneManagePresenterLoader;
import com.pyamsoft.powermanager.app.airplane.AirplaneOverviewPresenterLoader;
import com.pyamsoft.powermanager.app.airplane.AirplanePeriodPresenterLoader;
import dagger.Subcomponent;

@Subcomponent(modules = {
    AirplaneOverviewModule.class, AirplaneManagePreferenceModule.class,
    AirplanePeriodPreferenceModule.class
}) public interface AirplaneScreenComponent {

  void inject(AirplaneFragment fragment);

  void inject(AirplaneOverviewPresenterLoader loader);

  void inject(AirplaneManagePresenterLoader loader);

  void inject(AirplanePeriodPresenterLoader loader);
}
