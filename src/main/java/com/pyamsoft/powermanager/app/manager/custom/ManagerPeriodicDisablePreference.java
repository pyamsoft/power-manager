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

package com.pyamsoft.powermanager.app.manager.custom;

import android.content.Context;
import android.util.AttributeSet;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.dagger.manager.custom.DaggerManagerTimeComponent;
import javax.inject.Inject;
import javax.inject.Named;

public final class ManagerPeriodicDisablePreference extends ManagerTimePreference {

  @Inject @Named("periodic_disable") ManagerTimePresenter presenter;

  public ManagerPeriodicDisablePreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);

    DaggerManagerTimeComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build()
        .inject(this);
  }

  public ManagerPeriodicDisablePreference(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public ManagerPeriodicDisablePreference(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ManagerPeriodicDisablePreference(Context context) {
    this(context, null);
  }

  public final void bindView() {
    bindView(presenter);
  }
}
