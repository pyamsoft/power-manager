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
import com.pyamsoft.powermanager.backend.trigger.PowerTriggerDB;
import com.pyamsoft.pydroid.util.LogUtil;
import java.lang.ref.WeakReference;
import java.util.Set;

final class PowerTriggerDialogModel {
  private static final String TAG = PowerTriggerDialogModel.class.getSimpleName();
  private final WeakReference<Context> weakContext;

  public PowerTriggerDialogModel(Context context) {
    this.weakContext = new WeakReference<>(context);
  }

  public PowerTrigger onCreateClicked(final String name, final int level) {
    // fetch all adapter values
    final Context context = weakContext.get();
    if (context == null) {
      return null;
    }
    return new PowerTrigger(PowerTriggerDB.with(context).getNextId(), name, level);
  }

  public boolean createNewPowerTrigger(final PowerTrigger newTrigger) {
    final Context context = weakContext.get();
    if (context == null) {
      return false;
    }
    PowerTrigger trigger = null;
    final Set<PowerTrigger> triggers = PowerTriggerDB.TriggerSet.with(context).asSet();
    for (final PowerTrigger t : triggers) {
      if (t.getId() == newTrigger.getId()) {
        LogUtil.d(TAG, "Found matching trigger by ID");
        trigger = t;
        break;
      }

      if (t.getName().equalsIgnoreCase(newTrigger.getName())) {
        LogUtil.d(TAG, "Found matching trigger by NAME");
        trigger = t;
        break;
      }

      if (t.getLevel() == newTrigger.getLevel()) {
        LogUtil.d(TAG, "Found matching trigger by LEVEL");
        trigger = t;
        break;
      }
    }

    if (trigger != null) {
      LogUtil.d(TAG, "Adopt trigger ", newTrigger.getName());
      trigger.adopt(newTrigger);
    } else {
      LogUtil.d(TAG, "Create trigger ", newTrigger.getName());
      trigger = newTrigger;
    }

    PowerTriggerDB.with(context).createTrigger(trigger);
    return true;
  }
}
