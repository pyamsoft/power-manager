/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.trigger.create;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.FragmentTriggerBasicBinding;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry;
import timber.log.Timber;

public class CreateTriggerBasicFragment extends Fragment {

  private FragmentTriggerBasicBinding binding;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trigger_basic, container, false);
    return binding.getRoot();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    binding.unbind();
  }

  @CheckResult @NonNull public final String getTriggerName() {
    String name;
    if (binding.createTriggerBasicNameLayout == null) {
      Timber.e("Name layout is empty!");
      name = PowerTriggerEntry.EMPTY_NAME;
    } else {
      final EditText editText = binding.createTriggerBasicNameLayout.getEditText();
      if (editText == null || editText.getText().toString().isEmpty()) {
        Timber.e("Name edit is empty!");
        name = PowerTriggerEntry.EMPTY_NAME;
      } else {
        Timber.d("Get name");
        name = editText.getText().toString();
      }
    }
    return name;
  }

  @CheckResult public final int getTriggerPercent() {
    int percent;
    if (binding.createTriggerBasicPercentLayout == null) {
      Timber.e("Percent layout is empty!");
      percent = PowerTriggerEntry.EMPTY_PERCENT;
    } else {
      final EditText editText = binding.createTriggerBasicPercentLayout.getEditText();
      if (editText == null) {
        Timber.e("Percent edit is empty!");
        percent = PowerTriggerEntry.EMPTY_PERCENT;
      } else {
        Timber.d("Get percent");
        try {
          percent = Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException e) {
          Timber.e("Percent is not a Number");
          percent = PowerTriggerEntry.EMPTY_PERCENT;
        }
      }
    }
    return percent;
  }
}
