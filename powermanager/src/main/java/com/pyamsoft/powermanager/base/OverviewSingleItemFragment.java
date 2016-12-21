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

package com.pyamsoft.powermanager.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.util.CircularRevealFragmentUtil;

public abstract class OverviewSingleItemFragment extends AppBarColoringFragment {

  @Nullable @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_preference_container_single, container, false);
  }

  @CallSuper @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    CircularRevealFragmentUtil.runCircularRevealOnViewCreated(view, getArguments());
    addPreferenceFragment();
  }

  @Override public final void onResume() {
    super.onResume();
    setActionBarUpEnabled(true);
  }

  private void addPreferenceFragment() {
    final FragmentManager fragmentManager = getChildFragmentManager();
    if (fragmentManager.findFragmentByTag(getPreferenceTag()) == null) {
      fragmentManager.beginTransaction()
          .replace(R.id.preference_container_single, getPreferenceFragment(), getPreferenceTag())
          .commit();
    }
  }

  @CheckResult @NonNull protected abstract Fragment getPreferenceFragment();

  @CheckResult @NonNull protected abstract String getPreferenceTag();
}
