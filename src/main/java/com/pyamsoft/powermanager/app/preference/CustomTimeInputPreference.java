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

public class CustomTimeInputPreference extends Preference {

  @BindView(R.id.preference_custom_time_summary) TextView summary;
  @BindView(R.id.preference_custom_time_input) TextInputLayout textInputLayout;
  private TextWatcher watcher;
  private Unbinder unbinder;
  private String customSummary;

  public CustomTimeInputPreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setLayoutResource(R.layout.preference_custom_time_input);
  }

  public CustomTimeInputPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public CustomTimeInputPreference(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CustomTimeInputPreference(Context context) {
    this(context, null);
  }

  @Override public void onBindViewHolder(PreferenceViewHolder holder) {
    super.onBindViewHolder(holder);
    holder.itemView.setClickable(false);
    unbinder = ButterKnife.bind(this, holder.itemView);

    watcher = new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        final String text = s.toString();
        // TODO Save this entry after a slight delay (600?)
      }
    };

    final EditText editText = textInputLayout.getEditText();
    if (editText != null) {
      editText.addTextChangedListener(watcher);
    }
  }
}
