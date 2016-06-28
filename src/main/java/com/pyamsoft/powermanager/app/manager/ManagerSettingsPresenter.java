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

package com.pyamsoft.powermanager.app.manager;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.base.SchedulerPresenter;
import com.pyamsoft.powermanager.dagger.manager.ManagerSettingsInteractor;
import javax.inject.Named;
import rx.Scheduler;

abstract class ManagerSettingsPresenter<I extends ManagerSettingsPresenter.ManagerSettingsView>
    extends SchedulerPresenter<I> {

  @NonNull private final ManagerSettingsInteractor interactor;

  protected ManagerSettingsPresenter(@NonNull ManagerSettingsInteractor interactor,
      @Named("main") Scheduler mainScheduler, @Named("io") Scheduler ioScheduler) {
    super(mainScheduler, ioScheduler);
    this.interactor = interactor;
  }

  public final void registerSharedPreferenceChangeListener(
      @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener, @NonNull String key) {
    interactor.registerSharedPreferenceChangeListener(listener, key);
  }

  public final void unregisterSharedPreferenceChangeListener(
      @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
    interactor.unregisterSharedPreferenceChangeListener(listener);
  }

  public interface ManagerSettingsView {

  }
}
