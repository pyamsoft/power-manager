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

package com.pyamsoft.powermanager.ui;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import java.lang.ref.WeakReference;

public class ScrollingFABBehavior extends FloatingActionButton.Behavior {

  private WeakReference<FABVisibilityController> weakController;

  public ScrollingFABBehavior(final FABVisibilityController controller) {
    weakController = new WeakReference<>(controller);
  }

  @Override
  public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child,
      View dependency) {
    final FABVisibilityController controller = weakController.get();
    if (controller != null) {
      if (dependency instanceof AppBarLayout) {
        // If the adapter does not want to show the fab button, return false
        if (!controller.isLargeFABShown() || !controller.isSmallFABShown()) {
          return false;
        }
      }
    }
    return super.onDependentViewChanged(parent, child, dependency);
  }
}
