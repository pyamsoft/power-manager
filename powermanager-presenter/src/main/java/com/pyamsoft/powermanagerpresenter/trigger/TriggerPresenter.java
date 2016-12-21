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

package com.pyamsoft.powermanagerpresenter.trigger;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanagermodel.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.presenter.Presenter;

public interface TriggerPresenter extends Presenter<TriggerPresenter.TriggerView> {

  void toggleEnabledState(int position, @NonNull PowerTriggerEntry entry, boolean enabled);

  void loadTriggerView();

  void showNewTriggerDialog();

  void deleteTrigger(int percent);

  void createPowerTrigger(@NonNull PowerTriggerEntry entry);

  interface TriggerView {

    void onShowNewTriggerDialog();

    void onTriggerDeleted(int position);

    void onNewTriggerAdded(@NonNull PowerTriggerEntry entry);

    void onNewTriggerCreateError();

    void onNewTriggerInsertError();

    void onTriggerLoaded(@NonNull PowerTriggerEntry entry);

    void onTriggerLoadFinished();

    void updateViewHolder(int position, @NonNull PowerTriggerEntry entry);
  }
}