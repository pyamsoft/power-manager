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

package com.pyamsoft.powermanager.trigger;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.presenter.Presenter;

interface TriggerPresenter extends Presenter<Presenter.Empty> {

  void toggleEnabledState(int position, @NonNull PowerTriggerEntry entry, boolean enabled,
      @NonNull TriggerToggleCallback callback);

  void loadTriggerView(@NonNull TriggerLoadCallback callback);

  void showNewTriggerDialog(@NonNull ShowTriggerDialogCallback callback);

  void deleteTrigger(int percent, @NonNull TriggerDeleteCallback callback);

  void createPowerTrigger(@NonNull PowerTriggerEntry entry,
      @NonNull TriggerCreateCallback callback);

  interface TriggerLoadCallback {

    void onTriggerLoaded(@NonNull PowerTriggerEntry entry);

    void onTriggerLoadFinished();
  }

  interface ShowTriggerDialogCallback {
    void onShowNewTriggerDialog();
  }

  interface TriggerDeleteCallback {

    void onTriggerDeleted(int position);
  }

  interface TriggerCreateCallback {

    void onNewTriggerAdded(@NonNull PowerTriggerEntry entry);

    void onNewTriggerCreateError();

    void onNewTriggerInsertError();
  }

  interface TriggerToggleCallback {

    void updateViewHolder(int position, @NonNull PowerTriggerEntry entry);
  }
}
