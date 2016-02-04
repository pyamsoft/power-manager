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

package com.pyamsoft.powermanager.ui.trigger.dialog;

import android.content.Context;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;
import com.pyamsoft.pydroid.base.PresenterBase;

public class PowerTriggerDialogPresenter extends PresenterBase<PowerTriggerDialogInterface> {

  private PowerTriggerDialogModel model;

  public void onCreateClicked(final String name, final int level) {
    final PowerTriggerDialogInterface reference = getBoundReference();
    if (reference == null) {
      return;
    }

    if (name == null || level == -1) {
      return;
    }

    final PowerTrigger trigger = model.onCreateClicked(name, level);
    reference.fillInNewTrigger(trigger);
  }

  public void onNewTriggerFilled(final PowerTrigger trigger) {
    final PowerTriggerDialogInterface reference = getBoundReference();
    if (reference == null) {
      return;
    }

    if (model.createNewPowerTrigger(trigger)) {
      reference.onTriggerCreateSuccess();
    } else {
      reference.onTriggerCreateFailed();
    }
  }

  public void bind(Context context, PowerTriggerDialogInterface reference) {
    super.bind(reference);
    model = new PowerTriggerDialogModel(context);
  }

  @Override public void unbind() {
    super.unbind();
    model = null;
  }
}
