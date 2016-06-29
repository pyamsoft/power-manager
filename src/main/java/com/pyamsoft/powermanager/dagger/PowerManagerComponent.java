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

package com.pyamsoft.powermanager.dagger;

import android.content.Context;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import com.pyamsoft.powermanager.app.manager.ManagerSettingsPagerAdapter;
import com.pyamsoft.powermanager.app.receiver.ScreenOnOffReceiver;
import com.pyamsoft.powermanager.app.service.ForegroundService;
import com.pyamsoft.powermanager.dagger.manager.backend.ManagerModule;
import com.pyamsoft.powermanager.dagger.service.ForegroundModule;
import dagger.Component;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Scheduler;

@Singleton
@Component(modules = { PowerManagerModule.class, ManagerModule.class, ForegroundModule.class })
public interface PowerManagerComponent {

  Context provideContext();

  PowerManagerPreferences providePreferences();

  @Named("main") Scheduler provideMainScheduler();

  @Named("io") Scheduler provideIoScheduler();

  void inject(ForegroundService service);

  void inject(ScreenOnOffReceiver receiver);

  void inject(ManagerSettingsPagerAdapter adapter);
}
