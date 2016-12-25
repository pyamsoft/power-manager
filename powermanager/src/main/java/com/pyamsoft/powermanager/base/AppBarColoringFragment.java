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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.main.MainActivity;
import com.pyamsoft.pydroid.util.CircularRevealFragmentUtil;
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarFragment;

abstract class AppBarColoringFragment extends ActionBarFragment {

  private static final long ANIMATION_TIME = 400L;

  @CheckResult @NonNull
  protected static Bundle bundleArguments(@NonNull View from, @NonNull View container) {
    return CircularRevealFragmentUtil.bundleArguments(from, container, ANIMATION_TIME);
  }

  @CallSuper @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getMainActivity().colorAppBar(provideAppBarColor(), ANIMATION_TIME);
    getMainActivity().colorStatusBar(provideStatusBarColor(), ANIMATION_TIME);
  }

  @CallSuper @Override public void onDestroyView() {
    super.onDestroyView();
    getMainActivity().colorAppBar(R.color.amber500, ANIMATION_TIME);
    getMainActivity().colorStatusBar(R.color.amber700, ANIMATION_TIME);
  }

  @CheckResult @NonNull private MainActivity getMainActivity() {
    final Activity activity = getActivity();
    if (activity instanceof MainActivity) {
      return (MainActivity) activity;
    } else {
      throw new ClassCastException("Activity is not MainActivity");
    }
  }

  @CallSuper @Override public void onDestroy() {
    super.onDestroy();
    PowerManager.getRefWatcher(this).watch(this);
  }

  @CheckResult @ColorRes protected abstract int provideAppBarColor();

  @CheckResult @ColorRes protected abstract int provideStatusBarColor();
}
