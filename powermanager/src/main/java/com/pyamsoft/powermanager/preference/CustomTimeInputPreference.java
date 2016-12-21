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

package com.pyamsoft.powermanager.preference;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.PreferenceCustomTimeInputBinding;
import com.pyamsoft.powermanagerpresenter.preference.CustomTimeInputPreferencePresenter;
import com.pyamsoft.pydroidui.app.BaseBoundPreference;
import java.util.Locale;
import timber.log.Timber;

public abstract class CustomTimeInputPreference extends BaseBoundPreference
    implements CustomTimeInputPreferencePresenter.View {

  @SuppressWarnings("WeakerAccess") @Nullable CustomTimeInputPreferencePresenter presenter;
  @Nullable private TextWatcher watcher;
  @Nullable private EditText editText;
  @Nullable private PreferenceCustomTimeInputBinding binding;
  private boolean isDetaching;

  protected CustomTimeInputPreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context);
  }

  protected CustomTimeInputPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  protected CustomTimeInputPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  protected CustomTimeInputPreference(Context context) {
    super(context);
    init(context);
  }

  private void init(@NonNull Context context) {
    setLayoutResource(R.layout.preference_custom_time_input);
    presenter = getPresenter(getPreferenceType());
  }

  @Override public void onDetached() {
    isDetaching = true;
    super.onDetached();
    Timber.d("onDetached");
    if (presenter != null) {
      presenter.unbindView();
      presenter.destroy();
    }
  }

  @Override public void onAttached() {
    super.onAttached();
    isDetaching = false;
    Timber.d("onAttached");
    if (presenter != null) {
      presenter.bindView(this);
    }
  }

  @Override public final void onBindViewHolder(PreferenceViewHolder holder) {
    super.onBindViewHolder(holder);
    Timber.d("onBindViewHolder");

    // Crashes if we try to use actual DB class
    binding = DataBindingUtil.bind(holder.itemView);
    editText = binding.preferenceCustomTimeInput.getEditText();

    holder.itemView.setClickable(false);
    if (editText != null) {
      watcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override public void afterTextChanged(Editable s) {
          Timber.d("After text changed");
          if (presenter != null) {
            final String text = s.toString();
            presenter.updateCustomTime(text);
          }
        }
      };

      Timber.d("Add text watcher");
      editText.addTextChangedListener(watcher);
    }

    if (presenter != null) {
      presenter.initializeCustomTime();
    }
  }

  @Override protected void onUnbindViewHolder() {
    super.onUnbindViewHolder();
    if (binding != null) {
      Timber.d("unbind");
      if (editText != null) {
        editText.removeTextChangedListener(watcher);
        editText.setOnFocusChangeListener(null);
        editText.setOnEditorActionListener(null);

        if (isDetaching) {
          if (presenter != null) {
            // Save the last entered value to preferences
            final String text = editText.getText().toString();
            presenter.updateCustomTime(text, 0, false);
          }
        }
      }

      binding.unbind();
    }
  }

  @Override public void onCustomTimeUpdate(long time) {
    if (binding != null) {
      Timber.d("Custom time updated to: %d", time);
      if (watcher != null) {
        if (editText != null) {
          Timber.d("Remove text watcher");
          editText.removeTextChangedListener(watcher);
        }
      }

      binding.preferenceCustomTimeInput.setErrorEnabled(false);
      if (editText != null) {
        editText.setText(String.valueOf(time));
        editText.setSelection(editText.getText().length());
      }
      binding.preferenceCustomTimeSummary.setText(formatSummaryStringForTime(time));

      if (watcher != null) {
        Timber.d("Add text watcher");
        editText.addTextChangedListener(watcher);
      }
    } else {
      Timber.e("Failed to update view, but presenter updated storage");
    }
  }

  public void updatePresetDelay(@NonNull String presetDelay) {
    if (presenter != null) {
      Timber.d("Update time with preset delay of: %s", presetDelay);
      presenter.updateCustomTime(presetDelay, 0);
    }
  }

  @CheckResult @NonNull private CharSequence formatSummaryStringForTime(long time) {
    final PreferenceType type = getPreferenceType();
    final String deviceType;
    switch (type) {
      case DELAY:
        deviceType = "delay";
        break;
      case PERIODIC_ENABLE:
        deviceType = "enable";
        break;
      case PERIODIC_DISABLE:
        deviceType = "disable";
        break;
      default:
        throw new IllegalStateException("Invalid enum PreferenceType: " + type);
    }
    return String.format(Locale.getDefault(), "Current %s %s time period: %d seconds", getName(),
        deviceType, time);
  }

  @Override public void onCustomTimeError() {
    // TODO can this ever happen
  }

  @CheckResult @NonNull
  private CustomTimeInputPreferencePresenter getPresenter(@NonNull PreferenceType type) {
    final CustomTimeInputPreferencePresenter preferencePresenter;
    switch (type) {
      case DELAY:
        preferencePresenter = getDelayPresenter();
        break;
      case PERIODIC_ENABLE:
        preferencePresenter = getPeriodicEnablePresenter();
        break;
      case PERIODIC_DISABLE:
        preferencePresenter = getPeriodicDisablePresenter();
        break;
      default:
        throw new IllegalStateException("Invalid enum PreferenceType: " + type);
    }
    return preferencePresenter;
  }

  @CheckResult @NonNull protected abstract String getName();

  @CheckResult @NonNull protected abstract CustomTimeInputPreferencePresenter getDelayPresenter();

  @CheckResult @NonNull
  protected abstract CustomTimeInputPreferencePresenter getPeriodicEnablePresenter();

  @CheckResult @NonNull
  protected abstract CustomTimeInputPreferencePresenter getPeriodicDisablePresenter();

  @CheckResult @NonNull private PreferenceType getPreferenceType() {
    final String key = getKey();
    if (key == null) {
      throw new IllegalStateException("CustomTimeInputPreference Key is NULL");
    }

    final PreferenceType type;
    if (key.endsWith("_time")) {
      type = PreferenceType.DELAY;
    } else if (key.startsWith("periodic_")) {
      if (key.endsWith("_enable")) {
        type = PreferenceType.PERIODIC_ENABLE;
      } else if (key.endsWith("_disable")) {
        type = PreferenceType.PERIODIC_DISABLE;
      } else {
        throw new IllegalStateException("Invalid CustomTimeInputPreference Periodic Type");
      }
    } else {
      throw new IllegalStateException("Invalid CustomTimeInputPreference type");
    }
    return type;
  }

  public enum PreferenceType {
    DELAY,
    PERIODIC_ENABLE,
    PERIODIC_DISABLE
  }
}
