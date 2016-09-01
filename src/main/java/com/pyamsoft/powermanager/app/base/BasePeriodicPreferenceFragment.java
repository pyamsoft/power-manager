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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.View;
import com.pyamsoft.powermanager.app.preference.CustomTimeInputPreference;
import com.pyamsoft.pydroid.base.app.PersistLoader;
import com.pyamsoft.pydroid.tool.PersistentCache;
import timber.log.Timber;

public abstract class BasePeriodicPreferenceFragment extends PreferenceFragmentCompat
    implements BasePeriodPreferencePresenter.PeriodPreferenceView {

  @NonNull private static final String KEY_PRESENTER = "key_period_pref_presenter";
  @SuppressWarnings("WeakerAccess") BasePeriodPreferencePresenter presenter;
  @SuppressWarnings("WeakerAccess") SwitchPreferenceCompat periodicPreference;
  @SuppressWarnings("WeakerAccess") ListPreference presetEnableTimePreference;
  @SuppressWarnings("WeakerAccess") CustomTimeInputPreference customEnableTimePreference;
  @SuppressWarnings("WeakerAccess") ListPreference presetDisableTimePreference;
  @SuppressWarnings("WeakerAccess") CustomTimeInputPreference customDisableTimePreference;
  private String periodicKey;
  private String presetEnableTimeKey;
  private String presetDisableTimeKey;
  private String enableTimeKey;
  private String disableTimeKey;
  private long loadedKey;

  @Override public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(getPreferencesResId());
    periodicKey = getString(getPeriodicKeyResId());
    presetEnableTimeKey = getString(getPresetEnableTimeKeyResId());
    presetDisableTimeKey = getString(getPresetDisableTimeKeyResId());
    enableTimeKey = getString(getEnableTimeKeyResId());
    disableTimeKey = getString(getDisableTimeKeyResId());
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedKey = PersistentCache.load(KEY_PRESENTER, savedInstanceState,
        new PersistLoader.Callback<BasePeriodPreferencePresenter>() {
          @NonNull @Override public PersistLoader<BasePeriodPreferencePresenter> createLoader() {
            return createPresenterLoader(getContext());
          }

          @Override public void onPersistentLoaded(@NonNull BasePeriodPreferencePresenter persist) {
            presenter = persist;
          }
        });
  }

  private void resolvePreferences() {
    periodicPreference = (SwitchPreferenceCompat) findPreference(periodicKey);
    presetEnableTimePreference = (ListPreference) findPreference(presetEnableTimeKey);
    presetDisableTimePreference = (ListPreference) findPreference(presetDisableTimeKey);
    customEnableTimePreference = (CustomTimeInputPreference) findPreference(enableTimeKey);
    customDisableTimePreference = (CustomTimeInputPreference) findPreference(disableTimeKey);

    if (periodicPreference == null) {
      throw new NullPointerException("Periodic Preference is NULL");
    }

    if (presetEnableTimePreference == null) {
      throw new NullPointerException("Preset Enable Time Preference is NULL");
    }

    if (presetDisableTimePreference == null) {
      throw new NullPointerException("Preset Disable Time Preference is NULL");
    }
  }

  @Override public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    resolvePreferences();

    periodicPreference.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof Boolean) {
        final boolean b = (boolean) newValue;
        Timber.d("onPreferenceChange for key: %s", preference.getKey());
        final boolean canChange = onPeriodicPreferenceChanged(b);
        if (canChange) {
          presenter.updatePeriodic(b);

          setCustomEnableTimePreferenceEnabled(b, presetEnableTimePreference.getValue());
        }
      }

      // We always return false so the preference is updated by the modifier/observer backend
      return false;
    });

    presetEnableTimePreference.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof String) {
        final String presetDelay = (String) newValue;
        Timber.d("onPreferenceChange for key: %s", preference.getKey());
        final boolean canChange =
            onPresetEnableTimePreferenceChanged(presetDelay, customEnableTimePreference);
        if (canChange) {
          final long delayTime = Long.parseLong(presetDelay);
          if (delayTime != -1) {
            // Update the delay time to a preset instantly
            customEnableTimePreference.updatePresetDelay(presetDelay);
          }

          // Defer updates to the custom view
          setCustomEnableTimePreferenceEnabled(periodicPreference.isChecked(), presetDelay);
          return true;
        }
      }

      return false;
    });

    presetDisableTimePreference.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof String) {
        final String presetDelay = (String) newValue;
        Timber.d("onPreferenceChange for key: %s", preference.getKey());
        final boolean canChange =
            onPresetDisableTimePreferenceChanged(presetDelay, customDisableTimePreference);
        if (canChange) {
          final long delayTime = Long.parseLong(presetDelay);
          if (delayTime != -1) {
            // Update the delay time to a preset instantly
            customDisableTimePreference.updatePresetDelay(presetDelay);
          }

          // Defer updates to the custom view
          setCustomDisableTimePreferenceEnabled(periodicPreference.isChecked(), presetDelay);
          return true;
        }
      }

      return false;
    });

    setCustomDisableTimePreferenceEnabled(periodicPreference.isChecked(),
        presetDisableTimePreference.getValue());
    setCustomEnableTimePreferenceEnabled(periodicPreference.isChecked(),
        presetEnableTimePreference.getValue());
  }

  /**
   * Override if you implement any custom conditions for changing preferences
   */
  @SuppressWarnings("WeakerAccess") @CheckResult boolean onPresetEnableTimePreferenceChanged(
      String presetDelay, @Nullable CustomTimeInputPreference customEnableTimePreference) {
    return true;
  }

  /**
   * Override if you implement any custom conditions for changing preferences
   */
  @SuppressWarnings("WeakerAccess") @CheckResult boolean onPresetDisableTimePreferenceChanged(
      @NonNull String presetDelay,
      @Nullable CustomTimeInputPreference customDisableTimePreference) {
    return true;
  }

  /**
   * Override if you implement any custom conditions for changing preferences
   */
  @SuppressWarnings("WeakerAccess") @CheckResult boolean onPeriodicPreferenceChanged(
      boolean periodic) {
    return true;
  }

  @SuppressWarnings("WeakerAccess") void setCustomEnableTimePreferenceEnabled(boolean periodic,
      @NonNull String presetDelay) {
    if (customEnableTimePreference != null) {
      // Disable delay custom when unchecked
      // Enable delay custom when checked and custom delay time
      final long delayTime = Long.parseLong(presetDelay);
      customEnableTimePreference.setEnabled(periodic && delayTime == -1);
    }
  }

  @SuppressWarnings("WeakerAccess") void setCustomDisableTimePreferenceEnabled(boolean periodic,
      @NonNull String presetDelay) {
    if (customDisableTimePreference != null) {
      // Disable delay custom when unchecked
      // Enable delay custom when checked and custom delay time
      final long delayTime = Long.parseLong(presetDelay);
      customDisableTimePreference.setEnabled(periodic && delayTime == -1);
    }
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.unload(loadedKey);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.saveKey(KEY_PRESENTER, outState, loadedKey);
    super.onSaveInstanceState(outState);
  }

  @Override public final void onDestroyView() {
    super.onDestroyView();
    customEnableTimePreference.unbind();
    customDisableTimePreference.unbind();
  }

  @Override public void onPeriodicSet() {
    periodicPreference.setChecked(true);
  }

  @Override public void onPeriodicUnset() {
    periodicPreference.setChecked(false);
  }

  @XmlRes @CheckResult protected abstract int getPreferencesResId();

  @CheckResult @StringRes protected abstract int getPeriodicKeyResId();

  @CheckResult @StringRes protected abstract int getPresetDisableTimeKeyResId();

  @CheckResult @StringRes protected abstract int getPresetEnableTimeKeyResId();

  @CheckResult @StringRes protected abstract int getEnableTimeKeyResId();

  @CheckResult @StringRes protected abstract int getDisableTimeKeyResId();

  @CheckResult @NonNull
  protected abstract PersistLoader<BasePeriodPreferencePresenter> createPresenterLoader(
      @NonNull Context context);
}
