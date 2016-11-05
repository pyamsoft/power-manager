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
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v4.app.Fragment;
import android.support.v7.preference.CheckBoxPreference;
import android.view.View;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.preference.CustomTimeInputPreference;
import com.pyamsoft.powermanager.app.preference.ViewListPreference;
import com.pyamsoft.powermanager.app.preference.ViewSwitchPreferenceCompat;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.util.PersistentCache;
import timber.log.Timber;

public abstract class ManagePreferenceFragment extends FormatterPreferenceFragment
    implements ManagePreferencePresenter.ManagePreferenceView, PagerItem {

  @NonNull private static final String KEY_PRESENTER = "key_base_manage_presenter";
  @SuppressWarnings("WeakerAccess") ManagePreferencePresenter presenter;
  @SuppressWarnings("WeakerAccess") ViewSwitchPreferenceCompat managePreference;
  @SuppressWarnings("WeakerAccess") ViewListPreference presetTimePreference;
  @Nullable @SuppressWarnings("WeakerAccess") CustomTimeInputPreference customTimePreference;
  @Nullable private CheckBoxPreference ignoreChargingPreference;
  private String manageKey;
  private String presetTimeKey;
  @Nullable private String timeKey;
  @Nullable private String ignoreChargingKey;
  private long loadedKey;
  @Nullable private TapTargetSequence sequence;
  private boolean showOnboardingOnBind = false;

  @Override public void onSelected() {
    Timber.d("Select ManagePreferenceFragment");
    showOnboardingOnBind = (presenter == null);
    if (presenter != null) {
      presenter.showOnboardingIfNeeded();
    }
  }

  @Override public void onUnselected() {
    Timber.d("Unselect ManagePreferenceFragment");
    showOnboardingOnBind = false;
    if (presenter != null) {
      presenter.dismissOnboarding();
    }
  }

  @Override public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(getPreferencesResId());
    manageKey = getString(getManageKeyResId());
    presetTimeKey = getString(getPresetTimeKeyResId());

    @StringRes final int ignoreKeyRes = getIgnoreChargingKey();
    if (ignoreKeyRes != 0) {
      ignoreChargingKey = getString(ignoreKeyRes);
    } else {
      ignoreChargingKey = null;
    }
    @StringRes final int timeResId = getTimeKeyResId();
    if (timeResId != 0) {
      timeKey = getString(timeResId);
    } else {
      timeKey = null;
    }
    injectDependencies();
  }

  /**
   * Inject anything here is individual fragment instances
   */
  protected abstract void injectDependencies();

  @CallSuper @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<ManagePreferencePresenter>() {
              @NonNull @Override public PersistLoader<ManagePreferencePresenter> createLoader() {
                return createPresenterLoader();
              }

              @Override public void onPersistentLoaded(@NonNull ManagePreferencePresenter persist) {
                presenter = persist;
              }
            });
  }

  @Override void resolvePreferences() {
    managePreference = (ViewSwitchPreferenceCompat) findPreference(manageKey);
    presetTimePreference = (ViewListPreference) findPreference(presetTimeKey);

    if (timeKey != null) {
      customTimePreference = (CustomTimeInputPreference) findPreference(timeKey);
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
            presenter.checkManagePermission();
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
    presenter.bindView(this);

    if (checkManagePermission() && managePreference.isChecked()) {
      presenter.checkManagePermission();
    }

    if (showOnboardingOnBind) {
      presenter.showOnboardingIfNeeded();
    }
  }

  @CallSuper @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }


  @CallSuper @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_PRESENTER, loadedKey);
    super.onSaveInstanceState(outState);
  }

  @CallSuper @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  @CallSuper @Override public void onDestroyView() {
    super.onDestroyView();
    if (customTimePreference != null) {
      customTimePreference.setOnPreferenceChangeListener(null);
      customTimePreference.setOnPreferenceClickListener(null);
      customTimePreference.unbind();
      customTimePreference.destroy();
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

  @CallSuper @Override public void onManageSet() {
    managePreference.setChecked(true);
  }

  @CallSuper @Override public void onManageUnset() {
    managePreference.setChecked(false);
  }

  @CallSuper @Override public void showOnBoarding() {
    Timber.d("Show manage onboarding");
    if (sequence == null) {
      sequence = new TapTargetSequence(getActivity());

      TapTarget manageTarget = null;
      final View manageView = managePreference.getRootView();
      if (manageView != null) {
        final View switchView = manageView.findViewById(R.id.switchWidget);
        if (switchView != null) {
          manageTarget =
              TapTarget.forView(switchView, getString(R.string.onboard_title_manage_manage),
                  getString(R.string.onboard_desc_manage_manage))
                  .tintTarget(false)
                  .cancelable(false);
        }
      }

      TapTarget listTarget = null;
      final View listView = presetTimePreference.getRootView();
      if (listView != null) {
        listTarget = TapTarget.forView(listView, getString(R.string.onboard_title_manage_preset),
            getString(R.string.onboard_desc_manage_preset)).tintTarget(false).cancelable(false);
      }

      TapTarget customTarget = null;
      if (customTimePreference != null) {
        final View customView = customTimePreference.getRootView();
        if (customView != null) {
          customTarget =
              TapTarget.forView(customView, getString(R.string.onboard_title_manage_custom),
                  getString(R.string.onboard_desc_manage_custom))
                  .tintTarget(false)
                  .cancelable(false);
        }
      }

      TapTarget fabTarget = null;
      final Fragment parentFragment = getParentFragment();
      if (parentFragment instanceof OverviewPagerFragment) {
        final OverviewPagerFragment overviewPagerFragment = (OverviewPagerFragment) parentFragment;
        final View fab = overviewPagerFragment.getFabTarget();
        if (fab != null) {
          fabTarget = TapTarget.forView(fab, getString(R.string.onboard_title_overview_fab),
              getString(R.string.onboard_desc_overview_fab)).tintTarget(false).cancelable(false);
        }
      }

      if (manageTarget != null) {
        sequence.target(manageTarget);
      }
      if (listTarget != null) {
        sequence.target(listTarget);
      }
      if (customTarget != null) {
        sequence.target(customTarget);
      }
      if (fabTarget != null) {
        sequence.target(fabTarget);
      }

      sequence.listener(new TapTargetSequence.Listener() {
        @Override public void onSequenceFinish() {
          if (presenter != null) {
            presenter.setShownOnBoarding();
          }
        }

        @Override public void onSequenceCanceled(TapTarget lastTarget) {

        }
      });
    }

    //sequence.start();
  }

  @Override public void onManagePermissionCallback(boolean hasPermission) {
    Timber.d("Has manage permission: %s", hasPermission);
    // Set based on permission state
    managePreference.setChecked(hasPermission);

    if (!hasPermission) {
      onShowManagePermissionNeededMessage();
    }
  }

  /**
   * Override to show a permission needed prompt
   */
  protected void onShowManagePermissionNeededMessage() {

  }

  @CheckResult @NonNull
  protected abstract PersistLoader<ManagePreferencePresenter> createPresenterLoader();

  @StringRes @CheckResult protected abstract int getManageKeyResId();

  @StringRes @CheckResult protected abstract int getPresetTimeKeyResId();

  @StringRes @CheckResult protected abstract int getTimeKeyResId();

  @StringRes @CheckResult protected abstract int getIgnoreChargingKey();

  @XmlRes @CheckResult protected abstract int getPreferencesResId();
}
