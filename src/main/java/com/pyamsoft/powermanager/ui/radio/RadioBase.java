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

package com.pyamsoft.powermanager.ui.radio;

import android.support.design.widget.FloatingActionButton;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.ui.Coloring;
import com.pyamsoft.powermanager.ui.FABController;
import com.pyamsoft.powermanager.ui.FABMiniController;
import com.pyamsoft.pydroid.misc.FABVisibilityController;
import java.lang.ref.WeakReference;

abstract class RadioBase
    implements RadioContentInterface, Coloring, FABMiniController, FABController,
    FABVisibilityController {

  private final WeakReference<RadioPresenter> weakPresenter;

  RadioBase(final RadioPresenter presenter) {
    weakPresenter = new WeakReference<>(presenter);
  }

  public RadioPresenter getPresenter() {
    return weakPresenter.get();
  }

  @Override public boolean isFABShown(final FloatingActionButton fab) {
    return true;
  }

  @Override public int getFABMiniIconEnabled() {
    return R.drawable.ic_check_white_24dp;
  }

  @Override public int getFABMiniIconDisabled() {
    return R.drawable.ic_close_white_24dp;
  }
}
