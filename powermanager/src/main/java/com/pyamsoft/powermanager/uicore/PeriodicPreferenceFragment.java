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

package com.pyamsoft.powermanager.uicore;

import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.BoolRes;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.view.View;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.uicore.preference.CustomTimeInputPreference;
import timber.log.Timber;

public abstract class PeriodicPreferenceFragment extends FormatterPreferenceFragment
    implements PeriodPreferencePresenter.OnboardingCallback, PagerItem {

  @SuppressWarnings("WeakerAccess") PeriodPreferencePresenter presenter;
  @SuppressWarnings("WeakerAccess") SwitchPreference periodicPreference;
  @SuppressWarnings("WeakerAccess") ListPreference presetEnableTimePreference;
  @SuppressWarnings("WeakerAccess") CustomTimeInputPreference customEnableTimePreference;
  @SuppressWarnings("WeakerAccess") ListPreference presetDisableTimePreference;
  @SuppressWarnings("WeakerAccess") CustomTimeInputPreference customDisableTimePreference;
  @SuppressWarnings("WeakerAccess") String presetDisableTimeKey;
  @SuppressWarnings("WeakerAccess") String presetEnableTimeKey;
  private String periodicKey;
  private PreferenceCategory enableCategory;
  private PreferenceCategory disableCategory;
  private PreferenceCategory periodicCategory;

  @Override public void onSelected() {
  }

  @Override public void onUnselected() {
  }

  @Override public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
    initPreferenceKeys();

    addPreferenceCategories();
    addPeriodicPreferences();
    addEnablePreferences();
    addDisablePreferences();
  }

  private void addPreferenceCategories() {
    periodicCategory = new PreferenceCategory(getActivity());
    periodicCategory.setTitle("Recurring REPLACE_ME Settings");
    getPreferenceScreen().addPreference(periodicCategory);

    enableCategory = new PreferenceCategory(getActivity());
    enableCategory.setTitle("Recurring REPLACE_ME Enable");
    getPreferenceScreen().addPreference(enableCategory);

    disableCategory = new PreferenceCategory(getActivity());
    disableCategory.setTitle("Recurring REPLACE_ME Disable");
    getPreferenceScreen().addPreference(disableCategory);
  }

  private void addPeriodicPreferences() {
    periodicPreference = new SwitchPreference(getActivity());
    periodicPreference.setKey(periodicKey);
    periodicPreference.setSummaryOn(R.string.periodic_pref_summary_checked);
    periodicPreference.setSummaryOff(R.string.periodic_pref_summary_unchecked);
    periodicPreference.setTitle(R.string.periodic_pref_title);
    periodicPreference.setDefaultValue(getResources().getBoolean(providePeriodicDefaultResId()));
    periodicCategory.addPreference(periodicPreference);
  }

  private void addEnablePreferences() {
    presetEnableTimePreference = new ListPreference(getActivity());
    presetEnableTimePreference.setKey(presetEnableTimeKey);
    presetEnableTimePreference.setTitle(R.string.periodic_pref_enable_title);
    presetEnableTimePreference.setSummary(R.string.periodic_pref_enable_summary);
    presetEnableTimePreference.setEntries(providePresetNamesResId());
    presetEnableTimePreference.setEntryValues(providePresetValuesResId());
    presetEnableTimePreference.setDefaultValue(getString(provideEnableDefaultResId()));

    enableCategory.addPreference(presetEnableTimePreference);

    // Deps after add
    presetEnableTimePreference.setDependency(periodicKey);

    customEnableTimePreference = provideCustomEnableTimePreference();
    enableCategory.addPreference(customEnableTimePreference);
  }

  private void addDisablePreferences() {
    presetDisableTimePreference = new ListPreference(getActivity());
    presetDisableTimePreference.setKey(presetDisableTimeKey);
    presetDisableTimePreference.setTitle(R.string.periodic_pref_disable_title);
    presetDisableTimePreference.setSummary(R.string.periodic_pref_disable_summary);
    presetDisableTimePreference.setEntries(providePresetNamesResId());
    presetDisableTimePreference.setEntryValues(providePresetValuesResId());
    presetDisableTimePreference.setDefaultValue(getString(provideDisableDefaultResId()));

    disableCategory.addPreference(presetDisableTimePreference);

    // Deps after add
    presetDisableTimePreference.setDependency(periodicKey);

    customDisableTimePreference = provideCustomDisableTimePreference();
    disableCategory.addPreference(customDisableTimePreference);
  }

  private void initPreferenceKeys() {
    periodicKey = getString(providePeriodicKeyResId());
    presetEnableTimeKey = getString(providePresetEnableTimeKeyResId());
    presetDisableTimeKey = getString(providePresetDisableTimeKeyResId());
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectDependencies();
    presenter = providePresenter();
  }

  @Override void applyFormattedStrings(@NonNull String name) {
    applyFormattedStrings(periodicPreference, name);
    applyFormattedStrings(presetEnableTimePreference, name);
    applyFormattedStrings(presetDisableTimePreference, name);
    applyFormattedStrings(periodicCategory, name);
    applyFormattedStrings(enableCategory, name);
    applyFormattedStrings(disableCategory, name);
  }

  @Override public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    periodicPreference.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof Boolean) {
        final boolean b = (boolean) newValue;
        Timber.d("onPreferenceChange for key: %s", preference.getKey());
        setCustomEnableTimePreferenceEnabled(b, presetEnableTimePreference.getValue());
        setCustomDisableTimePreferenceEnabled(b, presetDisableTimePreference.getValue());
        return true;
      }

      return false;
    });

    presetEnableTimePreference.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof String) {
        final String presetDelay = (String) newValue;
        Timber.d("onPreferenceChange for key: %s", preference.getKey());
        final long delayTime = Long.parseLong(presetDelay);
        if (delayTime != -1) {
          // Update the delay time to a preset instantly
          customEnableTimePreference.updatePresetDelay(presetDelay);
        }

        // Defer updates to the custom view
        setCustomEnableTimePreferenceEnabled(periodicPreference.isChecked(), presetDelay);
        return true;
      }

      return false;
    });

    presetDisableTimePreference.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof String) {
        final String presetDelay = (String) newValue;
        Timber.d("onPreferenceChange for key: %s", preference.getKey());
        final long delayTime = Long.parseLong(presetDelay);
        if (delayTime != -1 && customDisableTimePreference != null) {
          // Update the delay time to a preset instantly
          customDisableTimePreference.updatePresetDelay(presetDelay);
        }

        // Defer updates to the custom view
        setCustomDisableTimePreferenceEnabled(periodicPreference.isChecked(), presetDelay);
        return true;
      }

      return false;
    });

    setCustomDisableTimePreferenceEnabled(periodicPreference.isChecked(),
        presetDisableTimePreference.getValue());
    setCustomEnableTimePreferenceEnabled(periodicPreference.isChecked(),
        presetEnableTimePreference.getValue());
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
    presenter.bindView(null);
    presenter.registerObserver(new PeriodPreferencePresenter.PeriodicCallback() {
      @Override public void onPeriodicSet() {
        periodicPreference.setChecked(true);
      }

      @Override public void onPeriodicUnset() {
        periodicPreference.setChecked(false);
      }
    });

    presenter.showOnboardingIfNeeded(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    PowerManager.getRefWatcher(this).watch(this);
  }

  @Override public final void onDestroyView() {
    super.onDestroyView();
    if (customEnableTimePreference != null) {
      customEnableTimePreference.setOnPreferenceChangeListener(null);
      customEnableTimePreference.setOnPreferenceClickListener(null);
    }

    if (customDisableTimePreference != null) {
      customDisableTimePreference.setOnPreferenceChangeListener(null);
      customDisableTimePreference.setOnPreferenceClickListener(null);
    }

    if (periodicPreference != null) {
      periodicPreference.setOnPreferenceChangeListener(null);
      periodicPreference.setOnPreferenceClickListener(null);
    }

    if (presetEnableTimePreference != null) {
      presetEnableTimePreference.setOnPreferenceChangeListener(null);
      presetEnableTimePreference.setOnPreferenceClickListener(null);
    }

    if (presetDisableTimePreference != null) {
      presetDisableTimePreference.setOnPreferenceChangeListener(null);
      presetDisableTimePreference.setOnPreferenceClickListener(null);
    }
  }

  @Override public void onShowOnboarding() {
    Timber.d("Show periodic onboarding");
  }

  @CheckResult @NonNull protected abstract PeriodPreferencePresenter providePresenter();

  protected abstract void injectDependencies();

  @CheckResult @StringRes protected abstract int providePeriodicKeyResId();

  @BoolRes @CheckResult protected abstract int providePeriodicDefaultResId();

  @CheckResult @StringRes protected abstract int providePresetEnableTimeKeyResId();

  @StringRes @CheckResult protected abstract int provideEnableDefaultResId();

  @CheckResult @NonNull
  protected abstract CustomTimeInputPreference provideCustomEnableTimePreference();

  @CheckResult @StringRes protected abstract int providePresetDisableTimeKeyResId();

  @StringRes @CheckResult protected abstract int provideDisableDefaultResId();

  @CheckResult @NonNull
  protected abstract CustomTimeInputPreference provideCustomDisableTimePreference();

  @ArrayRes @CheckResult protected abstract int providePresetNamesResId();

  @ArrayRes @CheckResult protected abstract int providePresetValuesResId();
}
