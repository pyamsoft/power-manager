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
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.View;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.uicore.preference.CustomTimeInputPreference;
import timber.log.Timber;

public abstract class ManagePreferenceFragment extends FormatterPreferenceFragment
    implements PagerItem, OnboardingPresenter.OnboardingCallback,
    ManagePreferencePresenter.ManagePermissionCallback {

  @SuppressWarnings("WeakerAccess") ManagePreferencePresenter presenter;
  @SuppressWarnings("WeakerAccess") SwitchPreferenceCompat managePreference;
  @SuppressWarnings("WeakerAccess") ListPreference presetTimePreference;
  @Nullable @SuppressWarnings("WeakerAccess") CustomTimeInputPreference customTimePreference;
  @SuppressWarnings("WeakerAccess") String presetTimeKey;
  @SuppressWarnings("WeakerAccess") @Nullable String customTimeKey;
  @Nullable private CheckBoxPreference ignoreChargingPreference;
  private String manageKey;
  @Nullable private String ignoreChargingKey;
  private boolean showOnboardingOnBind = false;

  @Override public void onSelected() {
    Timber.d("Select ManagePreferenceFragment");
    showOnboardingOnBind = (presenter == null);
    if (presenter != null) {
      presenter.showOnboardingIfNeeded(this);
    }
  }

  @Override public void onUnselected() {
    Timber.d("Unselect ManagePreferenceFragment");
    showOnboardingOnBind = false;
    if (presenter != null) {
      presenter.dismissOnboarding(this::dismissOnboarding);
    }
  }

  @Override public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(getPreferencesResId());
    manageKey = getString(provideManageKeyResId());
    presetTimeKey = getString(providePresetTimeKeyResId());

    @StringRes final int ignoreKeyRes = provideIgnoreChargingKey();
    if (ignoreKeyRes != 0) {
      ignoreChargingKey = getString(ignoreKeyRes);
    } else {
      ignoreChargingKey = null;
    }
    @StringRes final int timeResId = provideTimeKeyResId();
    if (timeResId != 0) {
      customTimeKey = getString(timeResId);
    } else {
      customTimeKey = null;
    }
  }

  @CallSuper @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectDependencies();
    presenter = providePresenter();
  }

  @Override void resolvePreferences() {
    managePreference = (SwitchPreferenceCompat) findPreference(manageKey);
    presetTimePreference = (ListPreference) findPreference(presetTimeKey);

    if (customTimeKey != null) {
      customTimePreference = (CustomTimeInputPreference) findPreference(customTimeKey);
    }

    if (ignoreChargingKey != null) {
      ignoreChargingPreference = (CheckBoxPreference) findPreference(ignoreChargingKey);
    }

    if (managePreference == null) {
      throw new NullPointerException("Manage Preference is NULL");
    }

    if (presetTimePreference == null) {
      throw new NullPointerException("Preset Time Preference is NULL");
    }
  }

  @Override void applyFormattedStrings(@NonNull String name) {
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
          final boolean checkPermission = checkManagePermission();
          if (checkPermission) {
            Timber.d("We need to check manage permission, do not toggle preference just yet");
            presenter.checkManagePermission(hasPermission -> {
              Timber.d("Has manage permission: %s", hasPermission);
              // Set based on permission state
              managePreference.setChecked(hasPermission);

              if (!hasPermission) {
                onShowManagePermissionNeededMessage();
              }
            });
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
  @CheckResult protected boolean checkManagePermission() {
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

    if (checkManagePermission() && managePreference.isChecked()) {
      presenter.checkManagePermission(this);
    }

    if (showOnboardingOnBind) {
      presenter.showOnboardingIfNeeded(this);
    }
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

  @Override void dismissOnboarding() {
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

  @Override public void onManagePermissionCallback(boolean hasPermission) {
  }

  /**
   * Override to show a permission needed prompt
   */
  protected void onShowManagePermissionNeededMessage() {

  }

  @CheckResult @NonNull protected abstract ManagePreferencePresenter providePresenter();

  protected abstract void injectDependencies();

  @StringRes @CheckResult protected abstract int provideManageKeyResId();

  @StringRes @CheckResult protected abstract int providePresetTimeKeyResId();

  @StringRes @CheckResult protected abstract int provideTimeKeyResId();

  @StringRes @CheckResult protected abstract int provideIgnoreChargingKey();

  @XmlRes @CheckResult protected abstract int getPreferencesResId();

  @CheckResult @NonNull protected abstract String getPresenterKey();
}
