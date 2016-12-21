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

package com.pyamsoft.powermanagerpresenter.preference;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanagermodel.PreferenceType;

public abstract class PreferenceLoader {

  public CustomTimeInputPreferencePresenter loadPresenter(@NonNull PreferenceType type) {
    final CustomTimeInputPreferencePresenter presenter;
    switch (type) {
      case DELAY:
        presenter = provideDelayPresenter();
        break;
      case PERIODIC_DISABLE:
        presenter = provideDisablePresenter();
        break;
      case PERIODIC_ENABLE:
        presenter = provideEnablePresenter();
        break;
      default:
        throw new IllegalStateException("Invalid PreferenceType: " + type);
    }
    return presenter;
  }

  @CheckResult @NonNull
  protected abstract CustomTimeInputPreferencePresenter provideDelayPresenter();

  @CheckResult @NonNull
  protected abstract CustomTimeInputPreferencePresenter provideDisablePresenter();

  @CheckResult @NonNull
  protected abstract CustomTimeInputPreferencePresenter provideEnablePresenter();
}
