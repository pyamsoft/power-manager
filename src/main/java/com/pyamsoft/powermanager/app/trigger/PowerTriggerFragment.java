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

package com.pyamsoft.powermanager.app.trigger;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.OverviewSingleItemFragment;

public class PowerTriggerFragment extends OverviewSingleItemFragment {

  @NonNull public static final String TAG = "Power Triggers";

  @CheckResult @NonNull
  public static PowerTriggerFragment newInstance(@NonNull View from, @NonNull View container) {
    final PowerTriggerFragment fragment = new PowerTriggerFragment();
    fragment.setArguments(bundleArguments(from, container));
    return fragment;
  }

  @NonNull @Override protected Fragment getPreferenceFragment() {
    return new PowerTriggerListFragment();
  }

  @NonNull @Override protected String getPreferenceTag() {
    return PowerTriggerListFragment.TAG;
  }

  @Override protected int provideAppBarColor() {
    return R.color.red500;
  }

  @Override protected int provideStatusBarColor() {
    return R.color.red700;
  }
}
