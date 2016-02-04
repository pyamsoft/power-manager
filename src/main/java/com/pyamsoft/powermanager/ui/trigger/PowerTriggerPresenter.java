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
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;
import com.pyamsoft.powermanager.ui.trigger.dialog.PowerTriggerDialogFragment;
import com.pyamsoft.pydroid.base.PresenterBase;
import java.lang.ref.WeakReference;

public class PowerTriggerPresenter extends PresenterBase<PowerTriggerInterface> {

  private PowerTriggerModel model;

  @Override public void bind(PowerTriggerInterface reference) {
    throw new IllegalBindException("Needs context");
  }

  public void bind(final Context context, final PowerTriggerInterface reference) {
    super.bind(reference);
    model = new PowerTriggerModel(context);
  }

  public void onLongClick(final PowerTrigger trigger) {
    final PowerTriggerInterface reference = getBoundReference();
    if (reference == null) {
      return;
    }

    final AlertDialog.Builder builder = model.createDeleteWarningDialog(trigger);
    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
      private WeakReference<PowerTrigger> weakTrigger = new WeakReference<>(trigger);
      private WeakReference<PowerTriggerPresenter> weakPresenter =
          new WeakReference<>(PowerTriggerPresenter.this);

      @Override public void onClick(DialogInterface dialog, int which) {
        final PowerTriggerPresenter p = weakPresenter.get();
        if (p != null) {
          final PowerTrigger t = weakTrigger.get();
          if (t != null) {
            p.onItemRemoveClicked(t);
          }
        }
      }
    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });

    reference.onDeleteWarningDialogCreated(builder.create());
  }

  public void onAddButtonClicked() {
    final PowerTriggerInterface reference = getBoundReference();
    if (reference == null) {
      return;
    }

    final PowerTriggerDialogFragment fragment = model.createFragment(reference);
    if (fragment != null) {
      reference.onDialogFragmentCreated(fragment);
    }
  }

  public void onItemRemoveClicked(PowerTrigger trigger) {
    final PowerTriggerInterface reference = getBoundReference();
    if (reference == null) {
      return;
    }

    final boolean removed = model.removeItem(trigger);
    if (removed) {
      reference.onItemRemoved(trigger);
    }
  }
}
