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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import timber.log.Timber;

public class CreateTriggerManageFragment extends Fragment {
  @NonNull private static final String FRAGMENT_TYPE = "fragment_type";
  public static final int TYPE_WIFI = 0;
  public static final int TYPE_DATA = 1;
  public static final int TYPE_BLUETOOTH = 2;
  public static final int TYPE_SYNC = 3;

  @BindView(R.id.create_trigger_manage_toggle) SwitchCompat switchToggle;
  @BindView(R.id.create_trigger_manage_manage) SwitchCompat switchManage;
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

  @Override public void onDestroyView() {
    super.onDestroyView();

    unbinder.unbind();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
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

  @CheckResult public final boolean getTriggerManage() {
    boolean manage;
    if (switchManage == null) {
      Timber.e("Manage is NULL");
      manage = false;
    } else {
      Timber.d("Get manage");
      manage = switchManage.isChecked();
    }
    return manage;
  }
}
