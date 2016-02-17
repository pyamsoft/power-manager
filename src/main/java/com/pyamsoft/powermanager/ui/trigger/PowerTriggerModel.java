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

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;
import com.pyamsoft.powermanager.backend.trigger.PowerTriggerDataSource;
import com.pyamsoft.powermanager.ui.trigger.dialog.PowerTriggerDialogFragment;
import java.lang.ref.WeakReference;

final class PowerTriggerModel {

  private final WeakReference<AppCompatActivity> weakActivity;

  PowerTriggerModel(final AppCompatActivity activity) {
    this.weakActivity = new WeakReference<>(activity);
  }

  boolean removeItem(final PowerTrigger trigger) {
    final Context context = weakActivity.get();
    if (context != null) {
      final PowerTriggerDataSource source = PowerTriggerDataSource.with(context);
      source.open();
      boolean removed = false;
      if (source.isOpened()) {
        removed = source.deleteTrigger(trigger);
        source.close();
      }
      return removed;
    }

    return false;
  }
}
