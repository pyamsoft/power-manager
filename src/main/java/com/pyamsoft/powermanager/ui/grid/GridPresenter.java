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

package com.pyamsoft.powermanager.ui.grid;

import android.content.Context;
import com.pyamsoft.pydroid.base.PresenterBase;
import com.pyamsoft.pydroid.util.LogUtil;

final class GridPresenter extends PresenterBase<GridInterface> {

  private static final String TAG = GridPresenter.class.getSimpleName();
  private final GridModel model;

  GridPresenter(Context context, GridInterface iface) {
    super(iface);
    model = new GridModel(context);
  }

  void clickFAB() {
    final GridInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "Null reference");
      return;
    }

    LogUtil.d(TAG, "onFABClicked");
    model.launchPowerManagerService();
    reference.onFABClicked();
  }

  void clickGridItem(final String viewCode, final int image) {
    final GridInterface reference = getInterface();
    if (reference == null) {
      LogUtil.e(TAG, "Null reference");
      return;
    }
    LogUtil.d(TAG, "clickGridItem");
    reference.onGridItemClicked(viewCode, image);
  }
}
