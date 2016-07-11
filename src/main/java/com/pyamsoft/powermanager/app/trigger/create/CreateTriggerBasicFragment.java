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

package com.pyamsoft.powermanager.app.trigger.create;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import timber.log.Timber;

public class CreateTriggerBasicFragment extends Fragment {

  @BindView(R.id.create_trigger_basic_name_layout) TextInputLayout nameLayout;
  private Unbinder unbinder;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_trigger_basic, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();

    unbinder.unbind();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @CheckResult @NonNull public final String getTriggerName() {
    String name;
    if (nameLayout == null) {
      Timber.e("Name layout is empty!");
      name = PowerTriggerEntry.EMPTY_NAME;
    } else {
      final EditText editText = nameLayout.getEditText();
      if (editText == null) {
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
    return PowerTriggerEntry.EMPTY_PERCENT;
  }
}
