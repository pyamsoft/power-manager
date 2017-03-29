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
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.view.View;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.uicore.preference.CustomTimeInputPreference;
import com.pyamsoft.pydroid.ui.helper.ProgressOverlay;
import timber.log.Timber;

public abstract class ManagePreferenceFragment extends FormatterPreferenceFragment
    implements PagerItem, OnboardingPresenter.OnboardingCallback {

  @SuppressWarnings("WeakerAccess") ManagePreferencePresenter presenter;
  @SuppressWarnings("WeakerAccess") SwitchPreference managePreference;
  @SuppressWarnings("WeakerAccess") ListPreference presetTimePreference;
  @Nullable @SuppressWarnings("WeakerAccess") CustomTimeInputPreference customTimePreference;
  @SuppressWarnings("WeakerAccess") String presetTimeKey;
  @NonNull ProgressOverlay overlay = ProgressOverlay.empty();
  @Nullable private CheckBoxPreference ignoreChargingPreference;
  private String manageKey;
  @Nullable private String ignoreChargingKey;
  private PreferenceCategory manageCategory;
  private PreferenceCategory delayCategory;

  @Override public void onSelected() {
  }

  @Override public void onUnselected() {
  }

  @Override public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
    initPreferenceKeys();

    addPreferenceCategories();
    addManagePreferences();
    addDelayPreferences();
  }

  private void initPreferenceKeys() {
    manageKey = getString(provideManageKeyResId());
    presetTimeKey = getString(providePresetTimeKeyResId());

    @StringRes final int ignoreKeyRes = provideIgnoreChargingKey();
    if (ignoreKeyRes != 0) {
      ignoreChargingKey = getString(ignoreKeyRes);
    } else {
      ignoreChargingKey = null;
    }
  }

  private void addPreferenceCategories() {
    manageCategory = new PreferenceCategory(getActivity());
    manageCategory.setTitle("REPLACE_ME Settings");
    getPreferenceScreen().addPreference(manageCategory);

    delayCategory = new PreferenceCategory(getActivity());
    delayCategory.setTitle("REPLACE_ME Delay");
    getPreferenceScreen().addPreference(delayCategory);
  }

  private void addManagePreferences() {
    managePreference = new SwitchPreference(getActivity());
    managePreference.setKey(manageKey);
    managePreference.setDefaultValue(getResources().getBoolean(provideManageDefaultValueResId()));
    managePreference.setTitle(R.string.manage_pref_title);
    managePreference.setSummaryOn(R.string.manage_pref_summary_checked);
    managePreference.setSummaryOff(R.string.manage_pref_summary_unchecked);
    manageCategory.addPreference(managePreference);

    if (ignoreChargingKey != null) {
      ignoreChargingPreference = new CheckBoxPreference(getActivity());
      ignoreChargingPreference.setKey(ignoreChargingKey);
      ignoreChargingPreference.setDefaultValue(
          getResources().getBoolean(provideIgnoreChargingDefaultResId()));
      ignoreChargingPreference.setTitle(R.string.ignore_charging_pref_title);
      ignoreChargingPreference.setSummaryOn(R.string.ignore_charging_pref_summary_checked);
      ignoreChargingPreference.setSummaryOff(R.string.ignore_charging_pref_summary_unchecked);
      manageCategory.addPreference(ignoreChargingPreference);

      // Can only add deps after adding to View
      ignoreChargingPreference.setDependency(manageKey);
    }
  }

  private void addDelayPreferences() {
    presetTimePreference = new ListPreference(getActivity());
    presetTimePreference.setKey(presetTimeKey);
    presetTimePreference.setDefaultValue(getString(providePresetTimeDefaultResId()));
    presetTimePreference.setEntries(providePresetEntriesResId());
    presetTimePreference.setEntryValues(providePresetValuesResId());
    presetTimePreference.setTitle(R.string.pref_time_title);
    presetTimePreference.setSummary(R.string.pref_time_summary);
    delayCategory.addPreference(presetTimePreference);

    // Can only add deps after adding to View
    presetTimePreference.setDependency(manageKey);

    customTimePreference = provideCustomTimePreference();
    if (customTimePreference != null) {
      delayCategory.addPreference(customTimePreference);
    }
  }

  @CallSuper @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectDependencies();
    presenter = providePresenter();
  }

  @Override void applyFormattedStrings(@NonNull String name) {
    applyFormattedStrings(manageCategory, name);
    applyFormattedStrings(delayCategory, name);
    applyFormattedStrings(managePreference, name);
    applyFormattedStrings(presetTimePreference, name);
    if (ignoreChargingPreference != null) {
      applyFormattedStrings(ignoreChargingPreference, name);
    }
  }

  @CallSuper @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    managePreference.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof Boolean) {
        final boolean b = (boolean) newValue;
        if (b) {
          if (shouldCheckManagePermission()) {
            Timber.d("We need to check manage permission, do not toggle preference just yet");
            checkManagePermission(true);
            return false;
          }
        }

        Timber.d("onPreferenceChange for key: %s", preference.getKey());
        setCustomTimePreferenceEnabled(b, presetTimePreference.getValue());
        return true;
      }

      return false;
    });

    presetTimePreference.setOnPreferenceChangeListener((preference, newValue) -> {
      if (newValue instanceof String) {
        final String presetDelay = (String) newValue;
        Timber.d("onPreferenceChange for key: %s", preference.getKey());
        final long delayTime = Long.parseLong(presetDelay);
        if (delayTime != -1 && customTimePreference != null) {
          // Update the delay time to a preset instantly
          customTimePreference.updatePresetDelay(presetDelay);
        }

        // Defer updates to the custom view
        setCustomTimePreferenceEnabled(managePreference.isChecked(), presetDelay);
        return true;
      }

      return false;
    });

    setCustomTimePreferenceEnabled(managePreference.isChecked(), presetTimePreference.getValue());
  }

  /**
   * Override if you need to implement a permission based check
   */
  @CheckResult protected boolean shouldCheckManagePermission() {
    return false;
  }

  @CallSuper @Override public void onStart() {
    super.onStart();
    presenter.bindView(null);
    presenter.registerObserver(new ManagePreferencePresenter.ManageCallback() {
      @Override public void onManageSet() {
        managePreference.setChecked(true);
      }

      @Override public void onManageUnset() {
        managePreference.setChecked(false);
      }
    });

    if (shouldCheckManagePermission() && managePreference.isChecked()) {
      checkManagePermission(false);
    }

    presenter.showOnboardingIfNeeded(this);
  }

  final void checkManagePermission(boolean showMessage) {
    presenter.checkManagePermission(new ManagePreferencePresenter.ManagePermissionCallback() {
      @Override public void onBegin() {
        overlay = ProgressOverlay.Helper.dispose(overlay);
        overlay = new ProgressOverlay.Builder().build(getActivity());
      }

      @Override public void onManagePermissionCallback(boolean hasPermission) {
        Timber.d("Has manage permission: %s", hasPermission);
        // Set based on permission state
        managePreference.setChecked(hasPermission);

        if (!hasPermission && showMessage) {
          onShowManagePermissionNeededMessage();
        }
      }

      @Override public void onComplete() {
        overlay = ProgressOverlay.Helper.dispose(overlay);
      }
    });
  }

  @CallSuper @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @CallSuper @Override public void onDestroy() {
    super.onDestroy();
    PowerManager.getRefWatcher(this).watch(this);
  }

  @CallSuper @Override public void onDestroyView() {
    super.onDestroyView();
    overlay = ProgressOverlay.Helper.dispose(overlay);
    if (customTimePreference != null) {
      customTimePreference.setOnPreferenceChangeListener(null);
      customTimePreference.setOnPreferenceClickListener(null);
    }

    if (managePreference != null) {
      managePreference.setOnPreferenceChangeListener(null);
      managePreference.setOnPreferenceClickListener(null);
    }

    if (presetTimePreference != null) {
      presetTimePreference.setOnPreferenceChangeListener(null);
      presetTimePreference.setOnPreferenceClickListener(null);
    }
  }

  @SuppressWarnings("WeakerAccess") void setCustomTimePreferenceEnabled(boolean managed,
      @NonNull String presetDelay) {
    if (customTimePreference != null) {
      // Disable delay custom when unchecked
      // Enable delay custom when checked and custom delay time
      final long delayTime = Long.parseLong(presetDelay);
      customTimePreference.setEnabled(managed && delayTime == -1);
    }
  }

  @Override public void onShowOnboarding() {
    Timber.d("Show manage onboarding");
  }

  /**
   * Override to show a permission needed prompt
   */
  protected void onShowManagePermissionNeededMessage() {

  }

  @CheckResult @NonNull protected abstract ManagePreferencePresenter providePresenter();

  protected abstract void injectDependencies();

  @StringRes @CheckResult protected abstract int provideManageKeyResId();

  @BoolRes @CheckResult protected abstract int provideManageDefaultValueResId();

  @StringRes @CheckResult protected abstract int provideIgnoreChargingKey();

  @BoolRes @CheckResult protected abstract int provideIgnoreChargingDefaultResId();

  @StringRes @CheckResult protected abstract int providePresetTimeKeyResId();

  @StringRes @CheckResult protected abstract int providePresetTimeDefaultResId();

  @ArrayRes @CheckResult protected abstract int providePresetEntriesResId();

  @ArrayRes @CheckResult protected abstract int providePresetValuesResId();

  @CheckResult @Nullable protected abstract CustomTimeInputPreference provideCustomTimePreference();
}
