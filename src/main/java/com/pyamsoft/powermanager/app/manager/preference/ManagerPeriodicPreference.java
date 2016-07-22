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

package com.pyamsoft.powermanager.app.manager.preference;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.dagger.manager.backend.DeviceJob;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public abstract class ManagerPeriodicPreference extends ManagerTimePreference {

  @NonNull private static final String ERROR_SUMMARY =
      "The specified time period is too low: %d seconds";
  @Inject @Named("periodic") ManagerTimePresenter presenter;
  private String specifiedCustomSummary;

  public ManagerPeriodicPreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);

    PowerManager.getInstance().getPowerManagerComponent().plusManagerTime().inject(this);
  }

  public ManagerPeriodicPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public ManagerPeriodicPreference(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ManagerPeriodicPreference(Context context) {
    this(context, null);
  }

  @Override public void setCustomSummary(@NonNull String formattable) {
    // Store the custom summary for later
    specifiedCustomSummary = formattable;
    super.setCustomSummary(formattable);
  }

  @Override public void setTimeSummary(long time) {
    // If the time is less than minimum time, set a custom error message
    if (time < DeviceJob.MINIMUM_ALLOWED_PERIOD) {
      Timber.e("Time too small, display error");
      super.setCustomSummary(ERROR_SUMMARY);
    } else {
      Timber.d("Time is allowed, set normal summary");
      super.setCustomSummary(specifiedCustomSummary);
    }

    super.setTimeSummary(time);
  }

  public final void bindView() {
    bindView(presenter);
  }
}
