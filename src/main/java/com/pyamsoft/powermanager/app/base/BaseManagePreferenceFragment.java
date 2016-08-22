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

package com.pyamsoft.powermanager.app.base;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.app.preference.CustomTimeInputPreference;
import com.pyamsoft.powermanager.dagger.managepreference.BaseManagePreferencePresenter;
import timber.log.Timber;

public abstract class BaseManagePreferenceFragment extends PreferenceFragmentCompat
    implements BaseManagePreferencePresenter.ManagePreferenceView {

  private String manageKey;
  private String presetTimeKey;
  private String timeKey;

  private SwitchPreferenceCompat managePreference;
  private BaseManagePreferencePresenter presenter;
  private ListPreference presetTimePreference;
  private CustomTimeInputPreference customTimePreference;

  @Override public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(getPreferencesResId());
    manageKey = getString(getManageKeyResId());
    presetTimeKey = getString(getPresetTimeKeyResId());
    timeKey = getString(getTimeKeyResId());
  }

  @Override public final View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    injectPresenter();
    presenter = providePresenter();
    presenter.bindView(this);
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  private void resolvePreferences() {
    managePreference = (SwitchPreferenceCompat) findPreference(manageKey);
    presetTimePreference = (ListPreference) findPreference(presetTimeKey);
    customTimePreference = (CustomTimeInputPreference) findPreference(timeKey);

    if (managePreference == null) {
      throw new NullPointerException("Manage Preference is NULL");
    }

    if (presetTimePreference == null) {
      throw new NullPointerException("Preset Time Preference is NULL");
    }
  }

  @Override public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    resolvePreferences();

    managePreference.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof Boolean) {
        final boolean b = (boolean) newValue;
        Timber.d("onPreferenceChange for key: %s", preference.getKey());
        final boolean canChange = onManagePreferenceChanged(b);
        if (canChange) {
          presenter.updateManage(b);

          setCustomTimePreferenceEnabled(b, presetTimePreference.getValue());
        }
      }

      // We always return false so the preference is updated by the modifier/observer backend
      return false;
    });

    presetTimePreference.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof String) {
        final String presetDelay = (String) newValue;
        Timber.d("onPreferenceChange for key: %s", preference.getKey());
        final boolean canChange = onPresetTimePreferenceChanged(presetDelay, customTimePreference);
        if (canChange) {
          setCustomTimePreferenceEnabled(managePreference.isChecked(), presetDelay);
          return true;
        }
      }
      return false;
    });

    setCustomTimePreferenceEnabled(managePreference.isChecked(), presetTimePreference.getValue());
  }

  @Override public final void onStart() {
    super.onStart();
    presenter.start();
  }

  @Override public final void onStop() {
    super.onStop();
    presenter.stop();
  }

  @Override public final void onDestroyView() {
    super.onDestroyView();
    presenter.unbindView();
  }

  private void setCustomTimePreferenceEnabled(boolean managed, @NonNull String presetDelay) {
    if (customTimePreference != null) {
      // Disable delay custom when unchecked
      // Enable delay custom when checked and custom delay time
      final long delayTime = Long.parseLong(presetDelay);
      customTimePreference.setEnabled(managed && delayTime == -1);
    }
  }

  /**
   * Override if you implement any custom conditions for changing preferences
   */
  @CheckResult protected boolean onManagePreferenceChanged(boolean b) {
    return true;
  }

  /**
   * Override if you implement any custom conditions for changing preferences
   */
  @CheckResult protected boolean onPresetTimePreferenceChanged(@NonNull String presetDelay,
      @Nullable CustomTimeInputPreference customTimePreference) {
    return true;
  }

  @Override public void onManageSet() {
    managePreference.setChecked(true);
  }

  @Override public void onManageUnset() {
    managePreference.setChecked(false);
  }

  protected abstract void injectPresenter();

  @CheckResult @NonNull protected abstract BaseManagePreferencePresenter providePresenter();

  @StringRes @CheckResult protected abstract int getManageKeyResId();

  @StringRes @CheckResult protected abstract int getPresetTimeKeyResId();

  @StringRes @CheckResult protected abstract int getTimeKeyResId();

  @XmlRes @CheckResult protected abstract int getPreferencesResId();
}
