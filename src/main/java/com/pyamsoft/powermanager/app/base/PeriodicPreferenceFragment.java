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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.MainActivity;
import com.pyamsoft.powermanager.app.preference.CustomTimeInputPreference;
import com.pyamsoft.powermanager.app.preference.ViewListPreference;
import com.pyamsoft.powermanager.app.preference.ViewSwitchPreferenceCompat;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.util.PersistentCache;
import timber.log.Timber;

public abstract class PeriodicPreferenceFragment extends PreferenceFragmentCompat
    implements PeriodPreferencePresenter.PeriodPreferenceView, ModulePagerAdapter.Page {

  @NonNull private static final String KEY_PRESENTER = "key_base_period_presenter";
  @SuppressWarnings("WeakerAccess") PeriodPreferencePresenter presenter;
  @SuppressWarnings("WeakerAccess") ViewSwitchPreferenceCompat periodicPreference;
  @SuppressWarnings("WeakerAccess") ViewListPreference presetEnableTimePreference;
  @SuppressWarnings("WeakerAccess") CustomTimeInputPreference customEnableTimePreference;
  @SuppressWarnings("WeakerAccess") ViewListPreference presetDisableTimePreference;
  @SuppressWarnings("WeakerAccess") CustomTimeInputPreference customDisableTimePreference;
  private String periodicKey;
  private String presetEnableTimeKey;
  private String presetDisableTimeKey;
  private String enableTimeKey;
  private String disableTimeKey;
  private long loadedKey;
  @Nullable private TapTargetSequence sequence;
  private boolean showOnboardingWhenAvailable;

  void setBackButtonEnabled(boolean enabled) {
    final Activity activity = getActivity();
    if (activity instanceof MainActivity) {
      ((MainActivity) activity).setBackButtonEnabled(enabled);
    } else {
      throw new ClassCastException("Activity is not MainActivity");
    }
  }

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
    loadedKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<PeriodPreferencePresenter>() {
              @NonNull @Override
              public PersistLoader<PeriodPreferencePresenter> createLoader() {
                return createPresenterLoader();
              }

              @Override
              public void onPersistentLoaded(@NonNull PeriodPreferencePresenter persist) {
                presenter = persist;
              }
            });
  }

  @Override public void onSelected() {
    if (presenter == null || !presenter.isBound()) {
      showOnboardingWhenAvailable = true;
    } else {
      showOnboardingWhenAvailable = false;
      presenter.showOnboardingIfNeeded();
    }
  }

  @Override public void onUnselected() {
    showOnboardingWhenAvailable = false;
    if (presenter != null && presenter.isBound()) {
      presenter.dismissOnboarding();
    }
  }

  private void resolvePreferences() {
    periodicPreference = (ViewSwitchPreferenceCompat) findPreference(periodicKey);
    presetEnableTimePreference = (ViewListPreference) findPreference(presetEnableTimeKey);
    presetDisableTimePreference = (ViewListPreference) findPreference(presetDisableTimeKey);
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
        setCustomEnableTimePreferenceEnabled(b, presetEnableTimePreference.getValue());
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
    presenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onResume() {
    super.onResume();
    if (showOnboardingWhenAvailable) {
      presenter.showOnboardingIfNeeded();
    }
  }

  @Override public void onPause() {
    super.onPause();
    presenter.dismissOnboarding();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_PRESENTER, loadedKey);
    super.onSaveInstanceState(outState);
  }

  @Override public final void onDestroyView() {
    super.onDestroyView();
    if (customEnableTimePreference != null) {
      customEnableTimePreference.setOnPreferenceChangeListener(null);
      customEnableTimePreference.setOnPreferenceClickListener(null);
      customEnableTimePreference.unbind();
      customEnableTimePreference.destroy();
    }

    if (customDisableTimePreference != null) {
      customDisableTimePreference.setOnPreferenceChangeListener(null);
      customDisableTimePreference.setOnPreferenceClickListener(null);
      customDisableTimePreference.unbind();
      customDisableTimePreference.destroy();
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

  @Override public void onPeriodicSet() {
    periodicPreference.setChecked(true);
  }

  @Override public void onPeriodicUnset() {
    periodicPreference.setChecked(false);
  }

  @Override public void showOnBoarding() {
    Timber.d("Show manage onboarding");
    if (sequence == null) {
      sequence = new TapTargetSequence(getActivity());

      TapTarget periodicTarget = null;
      final View periodicView = periodicPreference.getRootView();
      if (periodicView != null) {
        final View switchView = periodicView.findViewById(R.id.switchWidget);
        if (switchView != null) {
          periodicTarget =
              TapTarget.forView(switchView, getString(R.string.onboard_title_period_period),
                  getString(R.string.onboard_desc_period_period))
                  .tintTarget(false)
                  .cancelable(false);
        }
      }

      TapTarget periodicEnable = null;
      final View enableView = presetEnableTimePreference.getRootView();
      if (enableView != null) {
        periodicEnable =
            TapTarget.forView(enableView, getString(R.string.onboard_title_period_enable),
                getString(R.string.onboard_desc_period_enable)).tintTarget(false).cancelable(false);
      }

      TapTarget periodicDisable = null;
      final View disableView = presetDisableTimePreference.getRootView();
      if (disableView != null) {
        periodicDisable =
            TapTarget.forView(disableView, getString(R.string.onboard_title_period_disable),
                getString(R.string.onboard_desc_period_disable))
                .tintTarget(false)
                .cancelable(false);
      }

      if (periodicTarget != null) {
        sequence.target(periodicTarget);
      }
      if (periodicEnable != null) {
        sequence.target(periodicEnable);
      }
      if (periodicDisable != null) {
        sequence.target(periodicDisable);
      }

      sequence.listener(new TapTargetSequence.Listener() {
        @Override public void onSequenceFinish() {
          if (presenter != null) {
            presenter.setShownOnBoarding();
          }
          setBackButtonEnabled(true);
        }

        @Override public void onSequenceCanceled() {
          setBackButtonEnabled(true);
        }
      });
    }

    setBackButtonEnabled(false);
    sequence.start();
  }

  @XmlRes @CheckResult protected abstract int getPreferencesResId();

  @CheckResult @StringRes protected abstract int getPeriodicKeyResId();

  @CheckResult @StringRes protected abstract int getPresetDisableTimeKeyResId();

  @CheckResult @StringRes protected abstract int getPresetEnableTimeKeyResId();

  @CheckResult @StringRes protected abstract int getEnableTimeKeyResId();

  @CheckResult @StringRes protected abstract int getDisableTimeKeyResId();

  @CheckResult @NonNull
  protected abstract PersistLoader<PeriodPreferencePresenter> createPresenterLoader();
}