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

package com.pyamsoft.powermanager.app.overview;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.bluetooth.BluetoothFragment;
import com.pyamsoft.powermanager.app.data.DataFragment;
import com.pyamsoft.powermanager.app.doze.DozeFragment;
import com.pyamsoft.powermanager.app.settings.SettingsFragment;
import com.pyamsoft.powermanager.app.sync.SyncFragment;
import com.pyamsoft.powermanager.app.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.app.wifi.WifiFragment;
import com.pyamsoft.pydroid.app.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.app.fragment.CircularRevealFragmentUtil;

public class OverviewFragment extends ActionBarFragment {

  @NonNull public static final String TAG = "Overview";
  @BindView(R.id.overview_recycler) RecyclerView recyclerView;
  private FastItemAdapter<OverviewItem> adapter;
  private Unbinder unbinder;

  @CheckResult @NonNull public static OverviewFragment newInstance(int cX, int cY) {
    final Bundle args = CircularRevealFragmentUtil.bundleArguments(cX, cY, 600L);
    final OverviewFragment fragment = new OverviewFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_overview, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    CircularRevealFragmentUtil.runCircularRevealOnViewCreated(view, getArguments());
    populateAdapter(view);
    setupRecyclerView();
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(false);
  }

  private void populateAdapter(@NonNull View view) {
    adapter = new FastItemAdapter<>();
    adapter.add(
        new OverviewItem(view, WifiFragment.TAG, R.drawable.ic_network_wifi_24dp, R.color.green500,
            this::loadFragment));
    adapter.add(
        new OverviewItem(view, DataFragment.TAG, R.drawable.ic_network_cell_24dp, R.color.orange500,
            this::loadFragment));
    adapter.add(
        new OverviewItem(view, BluetoothFragment.TAG, R.drawable.ic_bluetooth_24dp, R.color.blue500,
            this::loadFragment));
    adapter.add(new OverviewItem(view, SyncFragment.TAG, R.drawable.ic_sync_24dp, R.color.yellow500,
        this::loadFragment));
    adapter.add(
        new OverviewItem(view, PowerTriggerFragment.TAG, R.drawable.ic_battery_24dp, R.color.red500,
            this::loadFragment));
    adapter.add(new OverviewItem(view, DozeFragment.TAG, R.drawable.ic_doze_24dp, R.color.purple500,
        this::loadFragment));
    adapter.add(
        new OverviewItem(view, SettingsFragment.TAG, R.drawable.ic_settings_24dp, R.color.pink500,
            this::loadFragment));

    // Can't use the normal withOnClickListener on the adapter for some reason
  }

  @SuppressWarnings("WeakerAccess") void loadFragment(@NonNull String title,
      @NonNull Fragment fragment) {
    final FragmentManager fragmentManager = getFragmentManager();
    if (fragmentManager.findFragmentByTag(title) == null) {
      fragmentManager.beginTransaction()
          .replace(R.id.main_container, fragment, title)
          .addToBackStack(null)
          .commit();
    }
  }

  private void setupRecyclerView() {
    final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(adapter);
  }
}
