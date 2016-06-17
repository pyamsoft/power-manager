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

package com.pyamsoft.powermanager.app.manager.backend;

import android.support.annotation.CheckResult;

public interface Manager {

  /**
   * Calls enable only if all checks pass
   */
  void enable();

  /**
   * Calls directly to enable
   */
  void enable(long time);

  /**
   * Calls disable only if all checks pass
   */
  void disable();

  /**
   * Calls directly to disable
   */
  void disable(long time);

  @CheckResult boolean isEnabled();

  @CheckResult boolean isManaged();
}
