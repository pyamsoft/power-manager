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

package com.pyamsoft.powermanager.presenter.observer.permission;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.PermissionObserver;
import com.pyamsoft.powermanager.presenter.PowerManagerPreferences;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class PermissionObserverModule {

  @Singleton @Named("obs_root_permission") @Provides
  PermissionObserver provideRootPermissionObserver(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new RootPermissionObserver(context, preferences);
  }

  @Singleton @Named("obs_doze_permission") @Provides
  PermissionObserver provideDozePermissionObserver(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new DozePermissionObserver(context, preferences);
  }

  @Singleton @Named("obs_write_permission") @Provides
  PermissionObserver provideSystemWritePermissionObserver(@NonNull Context context) {
    return new SystemWritePermissionObserver(context);
  }
}
