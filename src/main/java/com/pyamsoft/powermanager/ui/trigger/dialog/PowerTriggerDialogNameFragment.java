/*
 * Copyright 2013 - 2016 Peter Kenji Yamanaka
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
package com.pyamsoft.powermanager.ui.trigger.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;

public final class PowerTriggerDialogNameFragment extends Fragment {

  private static final String ERROR_MSG_LEVEL = "Invalid percentage";
  private TextInputLayout name;
  private TextInputLayout level;
  private final Runnable levelErrorRunnable = new Runnable() {

    @Override public void run() {
      if (level != null) {
        if (isValidLevel()) {
          level.setErrorEnabled(false);
        } else {
          level.setErrorEnabled(true);
          level.setError(ERROR_MSG_LEVEL);
        }
      }
    }
  };
  private final TextWatcher textWatcher = new TextWatcher() {

    @Override public void beforeTextChanged(final CharSequence s, final int start, final int count,
        final int after) {

    }

    @Override public void onTextChanged(final CharSequence s, final int start, final int before,
        final int count) {

    }

    @Override public void afterTextChanged(final Editable s) {
      levelErrorRunnable.run();
    }
  };

  @Nullable @Override
  public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
      @Nullable final Bundle savedInstanceState) {
    return inflater.inflate(R.layout.layout_new_trigger_dialog_name, container, false);
  }

  private boolean isValidLevel() {
    if (level != null) {
      final String str = level.getEditText().getText().toString();
      if (!str.isEmpty()) {
        final int percent = Integer.parseInt(str);
        if (percent >= 0 && percent <= 100) {
          return true;
        }
      }
    }
    return false;
  }

  @Override public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    name = (TextInputLayout) view.findViewById(R.id.new_trigger_dialog_name_input);
    level = (TextInputLayout) view.findViewById(R.id.new_trigger_dialog_level_input);
    name.setErrorEnabled(false);
    level.setErrorEnabled(false);

    final View.OnClickListener onClick = new View.OnClickListener() {

      @Override public void onClick(final View v) {
        levelErrorRunnable.run();
      }
    };

    name.setOnClickListener(onClick);
    name.getEditText().setOnClickListener(onClick);
    name.getEditText().addTextChangedListener(textWatcher);

    level.setOnClickListener(onClick);
    level.getEditText().setOnClickListener(onClick);
    level.getEditText().addTextChangedListener(textWatcher);

    view.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(final View v) {
        PowerTriggerDialogFragment.hideKeyboard(getActivity());
      }
    });
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (name != null) {
      name.setOnClickListener(null);
      name.getEditText().setOnClickListener(null);
      name.getEditText().removeTextChangedListener(textWatcher);
    }

    if (level != null) {
      level.setOnClickListener(null);
      level.getEditText().setOnClickListener(null);
      level.getEditText().removeTextChangedListener(textWatcher);
    }
  }

  public final String getName() {
    String txt;
    if (name != null) {
      txt = name.getEditText().getText().toString();
    } else {
      txt = null;
    }
    return txt;
  }

  public final int getLevel() {
    int lvl = -1;
    if (level != null) {
      if (isValidLevel()) {
        lvl = Integer.parseInt(level.getEditText().getText().toString());
      }
    }
    return lvl;
  }
}
