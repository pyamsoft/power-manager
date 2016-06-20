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

package com.pyamsoft.powermanager.app.manager.custom;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import java.util.Locale;
import timber.log.Timber;

public abstract class ManagerTimePreference extends Preference
    implements ManagerTimePresenter.TimeView {

  @NonNull private final Handler handler;

  @Nullable private ManagerTimePresenter presenter;
  @Nullable private TextView summary;
  @Nullable private TextInputLayout textInputLayout;
  @Nullable private TextWatcher watcher;

  public ManagerTimePreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    handler = new Handler();

    setLayoutResource(R.layout.layout_manage_delay_time);
  }

  public ManagerTimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public ManagerTimePreference(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ManagerTimePreference(Context context) {
    this(context, null);
  }

  @Override public void onBindViewHolder(PreferenceViewHolder holder) {
    super.onBindViewHolder(holder);
    Timber.d("onBindViewHolder");
    holder.itemView.setClickable(false);

    summary = (TextView) holder.findViewById(R.id.preference_manage_delay_summary);
    textInputLayout = (TextInputLayout) holder.findViewById(R.id.preference_manage_delay_times);
    final EditText editText = textInputLayout.getEditText();

    assert presenter != null;
    presenter.setDelayTimeFromPreference(getKey());

    watcher = new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        final String text = s.toString();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
          Timber.d("afterTextChanged");
          saveDelayTimeToPreference(text, text.isEmpty(), true);
        }, 600L);
      }
    };

    assert editText != null;
    editText.addTextChangedListener(watcher);
  }

  private void saveDelayTimeToPreference(@NonNull String text, boolean updateText,
      boolean updateSummary) {
    long value;
    if (text.isEmpty() || text.startsWith("-")) {
      value = 0;
    } else {
      value = Long.parseLong(text);
    }

    assert presenter != null;
    presenter.updateTime(getKey(), value, updateText, updateSummary);
  }

  public final void updateTime(long value) {
    assert presenter != null;
    presenter.updateTime(getKey(), value, true, true);
  }

  final void bindView(@NonNull ManagerTimePresenter presenter) {
    Timber.d("bindView");
    presenter.bindView(this);
    this.presenter = presenter;
  }

  public final void unbindView() {
    Timber.d("unbindView");

    handler.removeCallbacksAndMessages(null);

    assert textInputLayout != null;
    final EditText editText = textInputLayout.getEditText();
    assert editText != null;
    editText.removeTextChangedListener(watcher);

    editText.setOnFocusChangeListener(null);
    editText.setOnEditorActionListener(null);

    // Save the last entered value to preferences
    final String text = editText.getText().toString();
    saveDelayTimeToPreference(text, false, false);

    assert presenter != null;
    presenter.unbindView();
  }

  @Override public void setTimeSummary(long time) {
    assert summary != null;
    summary.setText(String.format(Locale.US, "Current delay: %d seconds", time));
  }

  @Override public void setTimeText(long time) {
    assert textInputLayout != null;
    final EditText editText = textInputLayout.getEditText();

    assert editText != null;
    if (watcher != null) {
      editText.removeTextChangedListener(watcher);
    }

    editText.setText(String.valueOf(time));
    editText.setSelection(editText.getText().length());

    if (watcher != null) {
      editText.addTextChangedListener(watcher);
    }
  }
}
