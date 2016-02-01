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
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.StringUtil;

public abstract class PowerTriggerDialogRadioFragment extends Fragment {

  private static final int TOGGLE_NONE = 0;
  private static final int TOGGLE_ON = 1;
  private static final int TOGGLE_OFF = 2;

  private ImageView manageImage;
  private SwitchCompat manageSwitch;

  private ImageView reopenImage;
  private SwitchCompat reopenSwitch;

  private ViewGroup toggleGroup;
  private CheckedTextView toggleNone;
  private CheckedTextView toggleOn;
  private CheckedTextView toggleOff;

  private static void formatWithRadioName(final TextView view, final String radioName) {
    final String text = StringUtil.formatString(view.getText().toString(), radioName);
    view.setText(text);
  }

  @Nullable @Override
  public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
      @Nullable final Bundle savedInstanceState) {
    return inflater.inflate(R.layout.layout_new_trigger_dialog_radio, container, false);
  }

  @Override public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    final String radioName = getRadioName();
    final int radioDrawable = getRadioDrawable();
    manageImage = (ImageView) view.findViewById(R.id.new_trigger_dialog_radio_image);
    manageSwitch = (SwitchCompat) view.findViewById(R.id.new_trigger_dialog_radio_switch);
    reopenImage = (ImageView) view.findViewById(R.id.new_trigger_dialog_reopen_image);
    reopenSwitch = (SwitchCompat) view.findViewById(R.id.new_trigger_dialog_reopen_switch);
    toggleGroup = (ViewGroup) view.findViewById(R.id.new_trigger_dialog_radio_toggle);
    toggleNone = (CheckedTextView) view.findViewById(R.id.new_trigger_dialog_radio_toggle_none);
    toggleOn = (CheckedTextView) view.findViewById(R.id.new_trigger_dialog_radio_toggle_on);
    toggleOff = (CheckedTextView) view.findViewById(R.id.new_trigger_dialog_radio_toggle_off);

    manageImage.setImageResource(radioDrawable);
    formatWithRadioName(manageSwitch, radioName);

    reopenImage.setImageResource(radioDrawable);
    formatWithRadioName(reopenSwitch, radioName);

    formatWithRadioName(toggleNone, radioName);
    formatWithRadioName(toggleOn, radioName);
    formatWithRadioName(toggleOff, radioName);

    manageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        if (isChecked) {
          AnimUtil.expand(toggleGroup);
        } else {
          AnimUtil.collapse(toggleGroup);
        }
      }
    });

    final View.OnClickListener toggleOnClick = new View.OnClickListener() {

      @Override public void onClick(final View v) {
        if (v == toggleNone) {
          toggleOn.setChecked(false);
          toggleOff.setChecked(false);
          toggleNone.setChecked(true);
        } else if (v == toggleOn) {
          toggleNone.setChecked(false);
          toggleOff.setChecked(false);
          toggleOn.setChecked(true);
        } else if (v == toggleOff) {
          toggleNone.setChecked(false);
          toggleOn.setChecked(false);
          toggleOff.setChecked(true);
        }
      }
    };

    // Default to the noop toggle
    toggleNone.setChecked(true);

    toggleNone.setOnClickListener(toggleOnClick);
    toggleOn.setOnClickListener(toggleOnClick);
    toggleOff.setOnClickListener(toggleOnClick);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (manageImage != null) {
      AppUtil.nullifyCallback(manageImage);
    }

    if (manageSwitch != null) {
      manageSwitch.setOnCheckedChangeListener(null);
    }

    if (reopenImage != null) {
      AppUtil.nullifyCallback(reopenImage);
    }

    if (reopenSwitch != null) {
      reopenSwitch.setOnCheckedChangeListener(null);
    }

    if (toggleNone != null) {
      toggleNone.setOnClickListener(null);
    }

    if (toggleOn != null) {
      toggleOn.setOnClickListener(null);
    }

    if (toggleOff != null) {
      toggleOff.setOnClickListener(null);
    }
  }

  protected abstract String getRadioName();

  protected abstract int getRadioDrawable();

  public final boolean getManageEnabled() {
    return manageSwitch != null && manageSwitch.isChecked();
  }

  public final boolean getReOpenEnabled() {
    return reopenSwitch != null && reopenSwitch.isChecked();
  }

  public final int getToggleState() {
    int t = TOGGLE_NONE;
    if (toggleOn != null && toggleOff != null) {
      if (toggleOn.isChecked()) {
        t = TOGGLE_ON;
      } else if (toggleOff.isChecked()) {
        t = TOGGLE_OFF;
      }
    }

    return t;
  }
}
