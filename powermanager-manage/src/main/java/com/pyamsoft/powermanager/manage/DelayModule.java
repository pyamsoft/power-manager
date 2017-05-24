/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.manage;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.base.preference.ManagePreferences;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class DelayModule {

  @Singleton @Provides @Named("manage_delay_interactor")
  DelayInteractor provideManageDelayInteractor(@NonNull ManagePreferences preferences) {
    return new DelayInteractor(new TimePrefrenceWrapper() {
      @Override public boolean isCustom() {
        return preferences.getCustomManageDelay();
      }

      @Override public long getTime() {
        return preferences.getManageDelay();
      }

      @Override public void setTime(long time, boolean custom) {
        preferences.setManageDelay(time);
        preferences.setCustomManageDelay(custom);
      }

      @NonNull @Override
      public SharedPreferences.OnSharedPreferenceChangeListener registerTimeChanges(
          @NonNull ManagePreferences.TimeChangeListener listener) {
        return preferences.registerDelayChanges(listener);
      }

      @Override public void unregisterTimeChanges(
          @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.unregisterDelayChanges(listener);
      }
    });
  }

  @Singleton @Provides @Named("manage_disable_interactor")
  DelayInteractor provideManageDisableInteractor(@NonNull ManagePreferences preferences) {
    return new DelayInteractor(new TimePrefrenceWrapper() {
      @Override public boolean isCustom() {
        return preferences.getCustomDisableTime();
      }

      @Override public long getTime() {
        return preferences.getPeriodicDisableTime();
      }

      @Override public void setTime(long time, boolean custom) {
        preferences.setPeriodicDisableTime(time);
        preferences.setCustomDisableTime(custom);
      }

      @NonNull @Override
      public SharedPreferences.OnSharedPreferenceChangeListener registerTimeChanges(
          @NonNull ManagePreferences.TimeChangeListener listener) {
        return preferences.registerDisableChanges(listener);
      }

      @Override public void unregisterTimeChanges(
          @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.unregisterDisableChanges(listener);
      }
    });
  }

  @Provides @Named("manage_delay") DelayPresenter provideManageDelayPresenter(
      @Named("obs") Scheduler observeScheduler, @NonNull @Named("sub") Scheduler subscribeScheduler,
      @NonNull @Named("manage_delay_interactor") DelayInteractor interactor) {
    return new DelayPresenter(observeScheduler, subscribeScheduler, interactor);
  }

  @Provides @Named("manage_disable") DelayPresenter provideManageDisablePresenter(
      @Named("obs") Scheduler observeScheduler, @NonNull @Named("sub") Scheduler subscribeScheduler,
      @NonNull @Named("manage_disable_interactor") DelayInteractor interactor) {
    return new DelayPresenter(observeScheduler, subscribeScheduler, interactor);
  }
}
