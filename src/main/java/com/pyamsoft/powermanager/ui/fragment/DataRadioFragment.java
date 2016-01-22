/*
 * Copyright 2013 - 2016 Peter Kenji Yamanaka
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
package com.pyamsoft.powermanager.ui.fragment;

import android.os.Build;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.powermanager.ui.adapter.RadioContentAdapter;
import com.pyamsoft.pydroid.util.AppUtil;

public final class DataRadioFragment extends BaseRadioFragment
    implements RadioContentAdapter.RadioInterface {

  @Override public String getRadioNameString() {
    return getContext().getString(R.string.data);
  }

  @Override public long getRadioDelay() {
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.get();
    return p.powerManagerActive().getDelayData();
  }

  @Override public void setRadioDelay(final long delay) {
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.get();
    p.powerManagerActive().setDelayData(delay);
    PowerPlanUtil.get().updateCustomPlan(PowerPlanUtil.FIELD_DELAY_DATA, delay);
  }

  @Override public long getReOpenTime() {
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.get();
    return p.intervalDisableService().getDataReopenTime();
  }

  @Override public void setRadioReopen(final long reopen) {
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.get();
    p.intervalDisableService().setDataReopenTime(reopen);
    PowerPlanUtil.get().updateCustomPlan(PowerPlanUtil.FIELD_REOPEN_TIME_DATA, reopen);
  }

  @Override protected RadioContentAdapter.RadioInterface getRadio() {
    return this;
  }

  @Override int getBackgroundColor() {
    return AppUtil.androidVersionLessThan(Build.VERSION_CODES.LOLLIPOP) ? R.color.teal500
        : R.color.scrim45_teal500;
  }
}
