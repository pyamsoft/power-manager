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

package com.pyamsoft.powermanager.app.manager.custom;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.base.SchedulerPresenter;
import javax.inject.Named;
import rx.Scheduler;
import timber.log.Timber;

public abstract class ManagerTimePresenter
    extends SchedulerPresenter<ManagerTimePresenter.TimeView> {

  protected ManagerTimePresenter(@NonNull @Named("main") Scheduler observeScheduler,
      @NonNull @Named("io") Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
  }

  protected abstract void updateTime(@NonNull String key, long time, boolean updateVisual,
      boolean updateSummary);

  protected abstract void setTimeFromPreference(@NonNull String key);

  protected void updateTime(long time, boolean updateVisual, boolean updateSummary) {
    if (updateVisual) {
      Timber.d("Visual update %d", time);
      getView().setTimeText(time);
    }
    if (updateSummary) {
      Timber.d("Summary update %d", time);
      getView().setTimeSummary(time);
    }
  }

  protected void setTimeText(long time) {
    Timber.d("Update time and summary to %d", time);
    getView().setTimeText(time);
    getView().setTimeSummary(time);
  }

  public interface TimeView {

    void setTimeText(long time);

    void setTimeSummary(long time);
  }
}
