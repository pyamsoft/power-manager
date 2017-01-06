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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.View;
import com.getkeepsafe.taptargetview.TapTarget;
import com.pyamsoft.powermanager.R;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.pyamsoft.powermanager.uicore.preference.CustomTimeInputPreference;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.util.PersistentCache;
import timber.log.Timber;

public abstract class PeriodicPreferenceFragment extends FormatterPreferenceFragment
    implements PeriodPreferencePresenter.PeriodPreferenceView, PagerItem {

  @NonNull private static final String KEY_PRESENTER = "key_base_period_presenter";
  @SuppressWarnings("WeakerAccess") PeriodPreferencePresenter presenter;
  @SuppressWarnings("WeakerAccess") SwitchPreferenceCompat periodicPreference;
  @SuppressWarnings("WeakerAccess") ListPreference presetEnableTimePreference;
  @SuppressWarnings("WeakerAccess") CustomTimeInputPreference customEnableTimePreference;
  @SuppressWarnings("WeakerAccess") ListPreference presetDisableTimePreference;
  @SuppressWarnings("WeakerAccess") CustomTimeInputPreference customDisableTimePreference;
  @SuppressWarnings("WeakerAccess") String presetDisableTimeKey;
  @SuppressWarnings("WeakerAccess") @Nullable TapTargetView periodicDisableTapTarget;
  @SuppressWarnings("WeakerAccess") @Nullable TapTargetView periodicEnableTapTarget;
  @SuppressWarnings("WeakerAccess") String presetEnableTimeKey;
  private String periodicKey;
  private String enableTimeKey;
  private String disableTimeKey;
  private long loadedKey;
  private boolean showOnboardingOnBind = false;
  @Nullable private TapTargetView periodicTapTarget;

  @Override public void onSelected() {
    Timber.d("Select PeriodicPreferenceFragment");
    showOnboardingOnBind = (presenter == null);
    if (presenter != null) {
      presenter.showOnboardingIfNeeded();
    }
  }

  @Override public void onUnselected() {
    Timber.d("Unselect PeriodicPreferenceFragment");
    showOnboardingOnBind = false;
    if (presenter != null) {
      presenter.dismissOnboarding();
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
              @NonNull @Override public PersistLoader<PeriodPreferencePresenter> createLoader() {
                return createPresenterLoader();
              }

              @Override public void onPersistentLoaded(@NonNull PeriodPreferencePresenter persist) {
                presenter = persist;
              }
            });
  }

  @Override void resolvePreferences() {
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

  @Override void applyFormattedStrings(@NonNull String name) {
    applyFormattedStrings(periodicPreference, name);
    applyFormattedStrings(presetEnableTimePreference, name);
    applyFormattedStrings(presetDisableTimePreference, name);
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
    presenter.bindView(this);

    if (showOnboardingOnBind) {
      presenter.showOnboardingIfNeeded();
    }
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get()
        .saveKey(outState, KEY_PRESENTER, loadedKey, PeriodPreferencePresenter.class);
    super.onSaveInstanceState(outState);
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

  @Override void dismissOnboarding() {
    dismissOnboarding(periodicTapTarget);
    periodicTapTarget = null;

    dismissOnboarding(periodicDisableTapTarget);
    periodicDisableTapTarget = null;

    dismissOnboarding(periodicEnableTapTarget);
    periodicEnableTapTarget = null;
  }

  @Override public void onPeriodicSet() {
    periodicPreference.setChecked(true);
  }

  @Override public void onPeriodicUnset() {
    periodicPreference.setChecked(false);
  }

  @Override public void showOnBoarding() {
    Timber.d("Show periodic onboarding");
    if (periodicTapTarget == null) {
      Timber.d("Create Periodic TapTarget");
      final PreferenceViewHolder manageViewHolder = findViewForPreference(periodicKey);
      if (manageViewHolder != null) {
        Timber.d("Find switch view in view holder");
        final View switchView =
            manageViewHolder.findViewById(com.pyamsoft.powermanager.base.R.id.switchWidget);
        if (switchView != null) {
          Timber.d("Create tap target on switch view");
          createPeriodicTarget(switchView);
        }
      }
    }
  }

  private void createPeriodicTarget(@NonNull View switchView) {
    final TapTarget periodicTarget =
        TapTarget.forView(switchView, getString(R.string.onboard_title_period_period),
            getString(R.string.onboard_desc_period_period)).tintTarget(false).cancelable(false);
    periodicTapTarget =
        TapTargetView.showFor(getActivity(), periodicTarget, new TapTargetView.Listener() {

          @Override public void onTargetClick(TapTargetView view) {
            super.onTargetClick(view);
            Timber.d("Periodic clicked");
            periodicDisableTapTarget = null;

            Timber.d("Attempt to find preset disable view holder");
            final PreferenceViewHolder listViewHolder = findViewForPreference(presetDisableTimeKey);
            if (listViewHolder != null) {
              createPresetDisableTapTarget(listViewHolder.itemView);
            } else {
              Timber.w("Cannot find Preset Disable TapTarget.");
              endOnboarding();
            }
          }
        });
  }

  @SuppressWarnings("WeakerAccess") void endOnboarding() {
    if (presenter != null) {
      Timber.d("End manage onboarding");
      presenter.setShownOnBoarding();
    }
  }

  @SuppressWarnings("WeakerAccess") void createPresetDisableTapTarget(@NonNull View itemView) {
    final TapTarget periodicDisable =
        TapTarget.forView(itemView, getString(R.string.onboard_title_period_disable),
            getString(R.string.onboard_desc_period_disable)).tintTarget(false).cancelable(false);
    periodicDisableTapTarget =
        TapTargetView.showFor(getActivity(), periodicDisable, new TapTargetView.Listener() {

          @Override public void onTargetClick(TapTargetView view) {
            super.onTargetClick(view);
            Timber.d("Periodic Disable clicked");
            periodicEnableTapTarget = null;

            Timber.d("Attempt to find preset enable view holder");
            final PreferenceViewHolder listViewHolder = findViewForPreference(presetEnableTimeKey);
            if (listViewHolder != null) {
              createPresetEnableTapTarget(listViewHolder.itemView);
            } else {
              Timber.w("Cannot find Preset Enable TapTarget.");
              endOnboarding();
            }
          }
        });
  }

  @SuppressWarnings("WeakerAccess") void createPresetEnableTapTarget(@NonNull View itemView) {
    final TapTarget periodicEnable =
        TapTarget.forView(itemView, getString(R.string.onboard_title_period_enable),
            getString(R.string.onboard_desc_period_enable)).tintTarget(false).cancelable(false);

    periodicEnableTapTarget =
        TapTargetView.showFor(getActivity(), periodicEnable, new TapTargetView.Listener() {
          @Override public void onTargetClick(TapTargetView view) {
            super.onTargetClick(view);
            Timber.d("Periodic Enable clicked");
            endOnboarding();
          }
        });
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
