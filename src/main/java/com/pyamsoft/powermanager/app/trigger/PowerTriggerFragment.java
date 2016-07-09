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

package com.pyamsoft.powermanager.app.trigger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.main.FabColorBus;
import com.pyamsoft.powermanager.dagger.trigger.DaggerTriggerComponent;
import com.pyamsoft.powermanager.model.FabColorEvent;
import javax.inject.Inject;
import timber.log.Timber;

public class PowerTriggerFragment extends Fragment implements TriggerPresenter.TriggerView {

  @NonNull public static final String TAG = "power_triggers";

  @BindView(R.id.power_trigger_list) RecyclerView recyclerView;

  @Inject TriggerListAdapterPresenter listAdapterPresenter;
  @Inject TriggerPresenter presenter;

  private PowerTriggerListAdapter adapter;
  private Unbinder unbinder;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DaggerTriggerComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build()
        .inject(this);

    adapter = new PowerTriggerListAdapter(this, listAdapterPresenter);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_powertrigger, container, false);
    unbinder = ButterKnife.bind(this, view);
    adapter.onCreate();
    presenter.bindView(this);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    adapter.onDestroy();
    presenter.unbindView();
  }

  @Override public void onResume() {
    super.onResume();
    presenter.onResume();
  }

  @Override public void onPause() {
    super.onPause();
    presenter.onPause();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupFab();
    setupRecyclerView();
    presenter.loadTriggerView();
  }

  private void setupFab() {
    FabColorBus.get()
        .post(FabColorEvent.create(R.drawable.ic_settings_24dp,
            () -> presenter.createPowerTrigger()));
  }

  private void setupRecyclerView() {
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setHasFixedSize(true);
  }

  @Override public void loadEmptyView() {
    Timber.d("Load empty view");
    recyclerView.setVisibility(View.GONE);
    recyclerView.setAdapter(null);
  }

  @Override public void loadListView() {
    Timber.d("Load list view");
    recyclerView.setAdapter(adapter);
    recyclerView.setVisibility(View.VISIBLE);
  }

  @Override public void onNewTriggerAdded(int percent) {
    adapter.onAddTriggerForPercent(percent);
    if (recyclerView.getAdapter() == null) {
      Timber.d("First trigger, show list");
      loadListView();
    }
  }

  @Override public void onTriggerDeleted(int position) {
    adapter.onDeleteTriggerAtPosition(position);
    if (adapter.getItemCount() == 0) {
      Timber.d("Last trigger, hide list");
      loadEmptyView();
    }
  }
}
