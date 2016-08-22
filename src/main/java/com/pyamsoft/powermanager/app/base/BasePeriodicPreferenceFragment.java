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

package com.pyamsoft.powermanager.app.base;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.dagger.periodpreference.BasePeriodPreferencePresenter;

public abstract class BasePeriodicPreferenceFragment extends PreferenceFragmentCompat {

  private String periodicKey;

  private SwitchPreferenceCompat managePreference;
  private BasePeriodPreferencePresenter presenter;

  @Override public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(getPreferencesResId());
  }

  @Override public final View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override public final void onStart() {
    super.onStart();
  }

  @Override public final void onStop() {
    super.onStop();
  }

  @Override public final void onDestroyView() {
    super.onDestroyView();
  }

  @XmlRes @CheckResult protected abstract int getPreferencesResId();
}
