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
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import timber.log.Timber;

public class CreateTriggerManageFragment extends Fragment {
  public static final int TYPE_WIFI = 0;
  public static final int TYPE_DATA = 1;
  public static final int TYPE_BLUETOOTH = 2;
  public static final int TYPE_SYNC = 3;
  @NonNull private static final String FRAGMENT_TYPE = "fragment_type";
  @BindView(R.id.create_trigger_manage_toggle) SwitchCompat switchToggle;
  @BindView(R.id.create_trigger_manage_enable) SwitchCompat switchEnable;
  @BindView(R.id.create_trigger_manage_toggle_explanation) TextView toggleExplain;
  @BindView(R.id.create_trigger_manage_enable_explanation) TextView enableExplain;
  private int type;
  private Unbinder unbinder;

  @CheckResult @NonNull public static CreateTriggerManageFragment newInstance(int type) {
    final Bundle args = new Bundle();
    final CreateTriggerManageFragment fragment = new CreateTriggerManageFragment();
    args.putInt(FRAGMENT_TYPE, type);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    type = getArguments().getInt(FRAGMENT_TYPE, -1);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_trigger_manage, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setExplanation();
  }

  private void setExplanation() {
    String radio;
    switch (type) {
      case TYPE_WIFI:
        radio = "Wifi";
        break;
      case TYPE_DATA:
        radio = "Data";
        break;
      case TYPE_BLUETOOTH:
        radio = "Bluetooth";
        break;
      case TYPE_SYNC:
        radio = "Sync";
        break;
      default:
        throw new IllegalStateException("Invalid type: " + type);
    }

    String toggle = "Toggle " + radio;
    String toggleExplainChecked = "Change state of " + radio + " as specified";
    String toggleExplainUnchecked = "Do not change state of " + radio;
    String enable = "Enable " + radio;
    String enableExplainChecked = radio + " will be turned on";
    String enableExplainUnchecked = radio + " will be turned off";

    toggleExplain.setText(toggleExplainUnchecked);
    switchToggle.setText(toggle);
    switchToggle.setOnCheckedChangeListener((compoundButton, b) -> toggleExplain.setText(
        b ? toggleExplainChecked : toggleExplainUnchecked));

    enableExplain.setText(enableExplainUnchecked);
    switchEnable.setText(enable);
    switchEnable.setOnCheckedChangeListener((compoundButton, b) -> enableExplain.setText(
        b ? enableExplainChecked : enableExplainUnchecked));
  }

  @Override public void onDestroyView() {
    super.onDestroyView();

    unbinder.unbind();
  }

  @CheckResult public final boolean getTriggerToggle() {
    boolean toggle;
    if (switchToggle == null) {
      Timber.e("Toggle is NULL");
      toggle = false;
    } else {
      Timber.d("Get toggle");
      toggle = switchToggle.isChecked();
    }
    return toggle;
  }

  @CheckResult public final boolean getTriggerEnable() {
    boolean enable;
    if (switchEnable == null) {
      Timber.e("Enable is NULL");
      enable = false;
    } else {
      Timber.d("Get enable");
      enable = switchEnable.isChecked();
    }
    return enable;
  }
}
