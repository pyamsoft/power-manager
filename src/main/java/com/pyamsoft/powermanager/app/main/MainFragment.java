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

package com.pyamsoft.powermanager.app.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.manager.ManagerFragment;

public class MainFragment extends Fragment {

  @Nullable @BindView(R.id.toggle_wifi_test) Button toggleWifi;
  @Nullable @BindView(R.id.toggle_data_test) Button toggleData;
  @Nullable @BindView(R.id.toggle_bluetooth_test) Button toggleBluetooth;
  @Nullable @BindView(R.id.toggle_sync_test) Button toggleSync;
  @Nullable @BindView(R.id.toggle_all_off) Button toggleOff;
  @Nullable @BindView(R.id.toggle_all_on) Button toggleOn;

  @Nullable private Unbinder unbinder;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_main, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    assert unbinder != null;
    unbinder.unbind();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupButtons();
  }

  private void setupButtons() {
    assert toggleWifi != null;
    toggleWifi.setOnClickListener(view -> {
      getFragmentManager().beginTransaction()
          .replace(R.id.main_container, ManagerFragment.newInstance(ManagerFragment.TYPE_WIFI))
          .addToBackStack(null)
          .commit();
      final FragmentActivity fragmentActivity = getActivity();
      if (fragmentActivity instanceof MainActivity) {
        final MainActivity mainActivity = (MainActivity) fragmentActivity;
        mainActivity.setActionBarUpEnabled(true);
      }
    });

    assert toggleData != null;
    toggleData.setOnClickListener(view -> {
      getFragmentManager().beginTransaction()
          .replace(R.id.main_container, ManagerFragment.newInstance(ManagerFragment.TYPE_DATA))
          .addToBackStack(null)
          .commit();
      final FragmentActivity fragmentActivity = getActivity();
      if (fragmentActivity instanceof MainActivity) {
        final MainActivity mainActivity = (MainActivity) fragmentActivity;
        mainActivity.setActionBarUpEnabled(true);
      }
    });

    assert toggleBluetooth != null;
    toggleBluetooth.setOnClickListener(view -> {
      getFragmentManager().beginTransaction()
          .replace(R.id.main_container, ManagerFragment.newInstance(ManagerFragment.TYPE_BLUETOOTH))
          .addToBackStack(null)
          .commit();
      final FragmentActivity fragmentActivity = getActivity();
      if (fragmentActivity instanceof MainActivity) {
        final MainActivity mainActivity = (MainActivity) fragmentActivity;
        mainActivity.setActionBarUpEnabled(true);
      }
    });

    assert toggleSync != null;
    toggleSync.setOnClickListener(view -> {
      getFragmentManager().beginTransaction()
          .replace(R.id.main_container, ManagerFragment.newInstance(ManagerFragment.TYPE_SYNC))
          .addToBackStack(null)
          .commit();
      final FragmentActivity fragmentActivity = getActivity();
      if (fragmentActivity instanceof MainActivity) {
        final MainActivity mainActivity = (MainActivity) fragmentActivity;
        mainActivity.setActionBarUpEnabled(true);
      }
    });
  }
}
