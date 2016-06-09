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

package com.pyamsoft.powermanager.app.manager;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

public interface Manager {

  void enable(@NonNull Activity activity);

  void enable(@NonNull Fragment fragment);

  void enable(@NonNull Service service);

  void enable(@NonNull Application application);

  void enable(@NonNull Activity activity, long time);

  void enable(@NonNull Fragment fragment, long time);

  void enable(@NonNull Service service, long time);

  void enable(@NonNull Application application, long time);

  void disable(@NonNull Activity activity);

  void disable(@NonNull Fragment fragment);

  void disable(@NonNull Service service);

  void disable(@NonNull Application application);

  void disable(@NonNull Activity activity, long time);

  void disable(@NonNull Fragment fragment, long time);

  void disable(@NonNull Service service, long time);

  void disable(@NonNull Application application, long time);

  @CheckResult boolean isEnabled();
}
