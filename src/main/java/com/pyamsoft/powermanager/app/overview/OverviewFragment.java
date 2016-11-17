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

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
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
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.airplane.AirplaneFragment;
import com.pyamsoft.powermanager.app.bluetooth.BluetoothFragment;
import com.pyamsoft.powermanager.app.data.DataFragment;
import com.pyamsoft.powermanager.app.doze.DozeFragment;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.settings.SettingsFragment;
import com.pyamsoft.powermanager.app.sync.SyncFragment;
import com.pyamsoft.powermanager.app.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.app.wear.WearFragment;
import com.pyamsoft.powermanager.app.wifi.WifiFragment;
import com.pyamsoft.powermanager.databinding.FragmentOverviewBinding;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.app.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.util.PersistentCache;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class OverviewFragment extends ActionBarFragment implements OverviewPresenter.View {

  @NonNull public static final String TAG = "Overview";
  @NonNull private static final String KEY_PRESENTER = "key_overview_presenter";
  @Inject @Named("obs_wifi_manage") BooleanInterestObserver wifiManageObserver;
  @Inject @Named("obs_data_manage") BooleanInterestObserver dataManageObserver;
  @Inject @Named("obs_bluetooth_manage") BooleanInterestObserver bluetoothManageObserver;
  @Inject @Named("obs_sync_manage") BooleanInterestObserver syncManageObserver;
  @Inject @Named("obs_airplane_manage") BooleanInterestObserver airplaneManageObserver;
  @Inject @Named("obs_wear_manage") BooleanInterestObserver wearManageObserver;
  @Inject @Named("obs_doze_manage") BooleanInterestObserver dozeManageObserver;
  OverviewPresenter presenter;
  private FastItemAdapter<OverviewItem> adapter;
  private FragmentOverviewBinding binding;
  private long loadedKey;
  @Nullable private TapTargetSequence sequence;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState, new PersistLoader.Callback<OverviewPresenter>() {
          @NonNull @Override public PersistLoader<OverviewPresenter> createLoader() {
            return new OverviewPresenterLoader();
          }

          @Override public void onPersistentLoaded(@NonNull OverviewPresenter persist) {
            presenter = persist;
          }
        });
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    Injector.get().provideComponent().plusOverviewComponent().inject(this);
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false);
    return binding.getRoot();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    binding.unbind();
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    populateAdapter(view);
    setupRecyclerView();
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(false);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_PRESENTER, loadedKey);
    super.onSaveInstanceState(outState);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  private void populateAdapter(@NonNull View view) {
    adapter = new FastItemAdapter<>();
    adapter.add(
        new OverviewItem(view, WifiFragment.TAG, R.drawable.ic_network_wifi_24dp, R.color.green500,
            wifiManageObserver, this::loadFragment));
    adapter.add(
        new OverviewItem(view, DataFragment.TAG, R.drawable.ic_network_cell_24dp, R.color.orange500,
            dataManageObserver, this::loadFragment));
    adapter.add(
        new OverviewItem(view, BluetoothFragment.TAG, R.drawable.ic_bluetooth_24dp, R.color.blue500,
            bluetoothManageObserver, this::loadFragment));
    adapter.add(new OverviewItem(view, SyncFragment.TAG, R.drawable.ic_sync_24dp, R.color.yellow500,
        syncManageObserver, this::loadFragment));
    adapter.add(
        new OverviewItem(view, PowerTriggerFragment.TAG, R.drawable.ic_battery_24dp, R.color.red500,
            null, this::loadFragment));
    adapter.add(new OverviewItem(view, AirplaneFragment.TAG, R.drawable.ic_airplanemode_24dp,
        R.color.cyan500, airplaneManageObserver, this::loadFragment));
    adapter.add(new OverviewItem(view, DozeFragment.TAG, R.drawable.ic_doze_24dp, R.color.purple500,
        dozeManageObserver, this::loadFragment));
    adapter.add(
        new OverviewItem(view, WearFragment.TAG, R.drawable.ic_watch_24dp, R.color.lightgreen500,
            wearManageObserver, this::loadFragment));
    adapter.add(
        new OverviewItem(view, SettingsFragment.TAG, R.drawable.ic_settings_24dp, R.color.pink500,
            null, this::loadFragment));

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
    final RecyclerView.LayoutManager layoutManager =
        new GridLayoutManager(getActivity(), columnCount);
    binding.overviewRecycler.setLayoutManager(layoutManager);
    binding.overviewRecycler.setHasFixedSize(true);
    binding.overviewRecycler.setAdapter(adapter);
  }

  @Override public void showOnBoarding() {
    Timber.d("Show onboarding");
    // Hold a ref to the sequence or Activity will recycle bitmaps and crash
    if (sequence == null) {

      // If we use the first item we get a weird location, try a different item
      final OverviewItem.ViewHolder tapTargetView =
          (OverviewItem.ViewHolder) binding.overviewRecycler.findViewHolderForAdapterPosition(1);
      final TapTarget overview = TapTarget.forView(tapTargetView.binding.adapterItemOverviewImage,
          getString(R.string.onboard_title_module), getString(R.string.onboard_desc_module))
          .cancelable(false);

      final TapTarget manageTarget =
          TapTarget.forView(tapTargetView.binding.adapterItemOverviewCheck,
              getString(R.string.onboard_title_module_manage),
              getString(R.string.onboard_desc_module_manage)).cancelable(false);

      sequence = new TapTargetSequence(getActivity()).targets(overview, manageTarget)
          .listener(new TapTargetSequence.Listener() {
            @Override public void onSequenceFinish() {
              if (presenter != null) {
                presenter.setShownOnBoarding();
              }
            }

            @Override public void onSequenceCanceled(TapTarget lastTarget) {

            }
          });
    }

    sequence.start();
  }
}
