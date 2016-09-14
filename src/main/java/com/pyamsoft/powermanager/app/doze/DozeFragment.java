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

package com.pyamsoft.powermanager.app.doze;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.base.BaseOverviewSingleItemFragment;
import com.pyamsoft.pydroid.app.fragment.CircularRevealFragmentUtil;

public class DozeFragment extends BaseOverviewSingleItemFragment {

  @NonNull public static final String TAG = "Doze";

  @CheckResult @NonNull
  public static DozeFragment newInstance(@NonNull View from, @NonNull View container) {
    final Bundle args = CircularRevealFragmentUtil.bundleArguments(from, container);
    final DozeFragment fragment = new DozeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override protected Fragment getPreferenceFragment() {
    return new DozeManagePreferenceFragment();
  }

  @NonNull @Override protected String getPreferenceTag() {
    return DozeManagePreferenceFragment.TAG;
  }

  @Override protected int provideAppBarColor() {
    return R.color.purple500;
  }

  @Override protected int provideStatusBarColor() {
    return R.color.purple700;
  }
}
