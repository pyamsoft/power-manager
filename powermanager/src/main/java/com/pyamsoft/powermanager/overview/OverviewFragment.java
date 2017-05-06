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

package com.pyamsoft.powermanager.overview;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.airplane.AirplaneFragment;
import com.pyamsoft.powermanager.bluetooth.BluetoothFragment;
import com.pyamsoft.powermanager.data.DataFragment;
import com.pyamsoft.powermanager.databinding.FragmentOverviewBinding;
import com.pyamsoft.powermanager.doze.DozeFragment;
import com.pyamsoft.powermanager.model.States;
import com.pyamsoft.powermanager.settings.SettingsPreferenceFragment;
import com.pyamsoft.powermanager.sync.SyncFragment;
import com.pyamsoft.powermanager.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.wear.WearFragment;
import com.pyamsoft.powermanager.wifi.WifiFragment;
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarFragment;
import javax.inject.Inject;
import timber.log.Timber;

public class OverviewFragment extends ActionBarFragment {

  @NonNull public static final String TAG = "Overview";
  @SuppressWarnings("WeakerAccess") @Inject OverviewPresenter presenter;
  FragmentOverviewBinding binding;
  FastItemAdapter<OverviewItem> adapter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Injector.get().provideComponent().inject(this);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    adapter = new FastItemAdapter<>();
    binding = FragmentOverviewBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    adapter.withOnClickListener(null);
    binding.unbind();
  }

  @Override public void onStart() {
    super.onStart();
    presenter.showOnBoarding(() -> {
      Timber.d("Show onboarding");
      // Hold a ref to the sequence or Activity will recycle bitmaps and crash
    });

    if (adapter.getAdapterItems().isEmpty()) {
      populateAdapter();
    }
  }

  @Override public void onStop() {
    super.onStop();
    presenter.stop();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupRecyclerView();
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(false);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    presenter.destroy();
    PowerManager.getRefWatcher(this).watch(this);
  }

  private void populateAdapter() {
    presenter.getWifiObserver(observer -> adapter.add(
        new OverviewItem(WifiFragment.TAG, R.drawable.ic_network_wifi_24dp, R.color.green500,
            observer)));
    presenter.getDataObserver(observer -> adapter.add(
        new OverviewItem(DataFragment.TAG, R.drawable.ic_network_cell_24dp, R.color.orange500,
            observer)));
    presenter.getBluetoothObserver(observer -> adapter.add(
        new OverviewItem(BluetoothFragment.TAG, R.drawable.ic_bluetooth_24dp, R.color.blue500,
            observer)));
    presenter.getSyncObserver(observer -> adapter.add(
        new OverviewItem(SyncFragment.TAG, R.drawable.ic_sync_24dp, R.color.yellow500, observer)));

    adapter.add(
        new OverviewItem(PowerTriggerFragment.TAG, R.drawable.ic_battery_24dp, R.color.red500,
            States.UNKNOWN));

    presenter.getAirplaneObserver(observer -> adapter.add(
        new OverviewItem(AirplaneFragment.TAG, R.drawable.ic_airplanemode_24dp, R.color.cyan500,
            observer)));
    presenter.getDozeObserver(observer -> adapter.add(
        new OverviewItem(DozeFragment.TAG, R.drawable.ic_doze_24dp, R.color.purple500, observer)));

    adapter.add(new OverviewItem(WearFragment.TAG, R.drawable.ic_watch_24dp, R.color.lightgreen500,
        States.UNKNOWN));
    adapter.add(new OverviewItem(SettingsPreferenceFragment.TAG, R.drawable.ic_settings_24dp,
        R.color.pink500, States.UNKNOWN));
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
    final int currentOrientation = getActivity().getResources().getConfiguration().orientation;
    final int columnCount;
    switch (currentOrientation) {
      case Configuration.ORIENTATION_PORTRAIT:
        columnCount = 2;
        break;
      case Configuration.ORIENTATION_LANDSCAPE:
        columnCount = 3;
        break;
      default:
        columnCount = 2;
    }

    adapter.withSelectable(true);
    adapter.withOnClickListener((view1, iAdapter, item, i) -> {
      final Fragment fragment;
      final String title = item.getModel().title();
      switch (title) {
        case WifiFragment.TAG:
          fragment = new WifiFragment();
          break;
        case DataFragment.TAG:
          fragment = new DataFragment();
          break;
        case BluetoothFragment.TAG:
          fragment = new BluetoothFragment();
          break;
        case SyncFragment.TAG:
          fragment = new SyncFragment();
          break;
        case PowerTriggerFragment.TAG:
          fragment = new PowerTriggerFragment();
          break;
        case DozeFragment.TAG:
          fragment = new DozeFragment();
          break;
        case WearFragment.TAG:
          fragment = new WearFragment();
          break;
        case SettingsPreferenceFragment.TAG:
          fragment = new SettingsPreferenceFragment();
          break;
        case AirplaneFragment.TAG:
          fragment = new AirplaneFragment();
          break;
        default:
          throw new IllegalStateException("Invalid tag: " + title);
      }

      loadFragment(title, fragment);
      return true;
    });

    final RecyclerView.LayoutManager layoutManager =
        new GridLayoutManager(getActivity(), columnCount);
    binding.overviewRecycler.setLayoutManager(layoutManager);
    binding.overviewRecycler.setHasFixedSize(true);
    binding.overviewRecycler.setAdapter(adapter);
  }
}
