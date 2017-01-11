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

package com.pyamsoft.powermanager.observer.permission;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.PowerManagerPreferences;
import com.pyamsoft.powermanager.base.shell.ShellCommandHelper;
import com.pyamsoft.powermanager.model.PermissionObserver;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class PermissionObserverModule {

  @Named("obs_root_permission") @Provides PermissionObserver provideRootPermissionObserver(
      @NonNull ShellCommandHelper shellCommandHelper, @NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new RootPermissionObserver(context, preferences, shellCommandHelper);
  }

  @Named("obs_doze_permission") @Provides PermissionObserver provideDozePermissionObserver(
      @NonNull ShellCommandHelper shellCommandHelper, @NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    return new DozePermissionObserver(context, preferences, shellCommandHelper);
  }

  @Named("obs_write_permission") @Provides PermissionObserver provideSystemWritePermissionObserver(
      @NonNull Context context) {
    return new SystemWritePermissionObserver(context);
  }
}
