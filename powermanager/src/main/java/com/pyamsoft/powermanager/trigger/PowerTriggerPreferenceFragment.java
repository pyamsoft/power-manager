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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.service.ForegroundService;
import com.pyamsoft.pydroid.cache.PersistentCache;

public class PowerTriggerPreferenceFragment extends PreferenceFragmentCompat
    implements TriggerPreferencePresenter.Provider {

  @NonNull public static final String TAG = "PowerTriggerPreferenceFragment";
  @NonNull private static final String KEY_PRESENTER = TAG + "key_trigger_pref_presenter";
  @SuppressWarnings("WeakerAccess") TriggerPreferencePresenter presenter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter =
        PersistentCache.load(getActivity(), KEY_PRESENTER, new TriggerPreferencePresenterLoader());
  }

  @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.power_trigger_options);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    PowerManager.getRefWatcher(this).watch(this);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final Preference triggerInterval = findPreference(getString(R.string.trigger_period_key));
    triggerInterval.setOnPreferenceChangeListener((preference, newValue) -> {
      presenter.restartService();
      return true;
    });
  }

  @Override public void onServiceRestartRequested() {
    ForegroundService.restartTriggers(getContext());
  }
}
