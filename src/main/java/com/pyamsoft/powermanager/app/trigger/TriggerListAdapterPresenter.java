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

package com.pyamsoft.powermanager.app.trigger;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.base.presenter.Presenter;

public interface TriggerListAdapterPresenter
    extends Presenter<TriggerListAdapterPresenter.TriggerListAdapterView> {

  @CheckResult int size();

  @CheckResult @NonNull PowerTriggerEntry get(int position);

  @CheckResult int getPositionForPercent(int percent);

  void toggleEnabledState(int position, @NonNull PowerTriggerEntry entry, boolean enabled);

  interface TriggerListAdapterView {

    void updateViewHolder(int position);
  }
}
