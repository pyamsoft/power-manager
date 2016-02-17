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

package com.pyamsoft.powermanager.ui.trigger;

import android.support.v7.app.AppCompatActivity;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;
import com.pyamsoft.powermanager.ui.trigger.dialog.PowerTriggerDialogFragment;
import com.pyamsoft.pydroid.base.PresenterBase;

final class PowerTriggerPresenter extends PresenterBase<PowerTriggerInterface> {

  private final PowerTriggerModel model;

  protected PowerTriggerPresenter(final AppCompatActivity activity, PowerTriggerInterface iface) {
    super(iface);
    model = new PowerTriggerModel(activity);
  }

  void onLongClick(final PowerTrigger trigger) {
    final PowerTriggerInterface reference = getInterface();
    if (reference == null) {
      return;
    }

    reference.onCreateDeleteWarningDialog(trigger);
  }

  void onAddButtonClicked() {
    final PowerTriggerInterface reference = getInterface();
    if (reference == null) {
      return;
    }

    reference.onCreateDialogFragment(new PowerTriggerDialogFragment());
  }

  void onItemRemoveClicked(PowerTrigger trigger) {
    final PowerTriggerInterface reference = getInterface();
    if (reference == null) {
      return;
    }

    final boolean removed = model.removeItem(trigger);
    if (removed) {
      reference.onItemRemoved(trigger);
    }
  }
}
