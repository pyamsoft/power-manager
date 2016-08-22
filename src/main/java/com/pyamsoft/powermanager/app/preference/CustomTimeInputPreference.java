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

package com.pyamsoft.powermanager.app.preference;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.dagger.preference.CustomTimeInputPreferencePresenter;
import timber.log.Timber;

public abstract class CustomTimeInputPreference extends Preference
    implements CustomTimeInputPreferencePresenter.View {

  @BindView(R.id.preference_custom_time_summary) TextView summary;
  @BindView(R.id.preference_custom_time_input) TextInputLayout textInputLayout;
  private TextWatcher watcher;
  private Unbinder unbinder;
  private String customSummary;
  private CustomTimeInputPreferencePresenter presenter;
  private EditText editText;

  CustomTimeInputPreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setLayoutResource(R.layout.preference_custom_time_input);
    injectPresenter(context);
  }

  CustomTimeInputPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  CustomTimeInputPreference(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  CustomTimeInputPreference(Context context) {
    this(context, null);
  }

  @Override public final void onBindViewHolder(PreferenceViewHolder holder) {
    super.onBindViewHolder(holder);

    // We call unbind because when a preference is changed it can be re-bound without being properly recycled
    unbind(false);

    Timber.d("onBindViewHolder");
    presenter = getPresenter();
    presenter.bindView(this);

    holder.itemView.setClickable(false);
    unbinder = ButterKnife.bind(this, holder.itemView);

    watcher = new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        Timber.d("After text changed");
        final String text = s.toString();
        presenter.updateCustomTime(text);
      }
    };

    editText = textInputLayout.getEditText();
    if (editText != null) {
      Timber.d("Add text watcher");
      editText.addTextChangedListener(watcher);
    }

    presenter.initializeCustomTime();
  }

  public final void unbind() {
    unbind(true);
  }

  final void unbind(boolean finalSave) {
    if (unbinder == null) {
      Timber.w(
          "onBindViewHolder was never called for this preference. Maybe it never came into view?");
    } else {
      if (editText != null) {
        editText.removeTextChangedListener(watcher);
        editText.setOnFocusChangeListener(null);
        editText.setOnEditorActionListener(null);

        if (finalSave) {
          // Save the last entered value to preferences
          final String text = editText.getText().toString();
          presenter.updateCustomTime(text, 0, false);
        }
      }

      presenter.unbindView();
      unbinder.unbind();
    }
  }

  @Override public void onCustomTimeUpdate(long time) {
    Timber.d("Custom time updated to: %d", time);
    if (watcher != null) {
      Timber.d("Remove text watcher");
      editText.removeTextChangedListener(watcher);
    }

    editText.setText(String.valueOf(time));
    editText.setSelection(editText.getText().length());
    summary.setText(formatSummaryStringForTime(time));

    if (watcher != null) {
      Timber.d("Add text watcher");
      editText.addTextChangedListener(watcher);
    }
  }

  public void updatePresetDelay(@NonNull String presetDelay) {
    Timber.d("Update time with preset delay of: %s", presetDelay);
    presenter.updateCustomTime(presetDelay, 0);
  }

  @CheckResult @NonNull protected abstract CharSequence formatSummaryStringForTime(long time);

  @Override public void onCustomTimeError() {
    // TODO can this ever happen
  }

  @CheckResult @NonNull protected abstract CustomTimeInputPreferencePresenter getPresenter();

  protected abstract void injectPresenter(@NonNull Context context);
}
