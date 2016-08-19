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

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.app.main.FabColorBus;
import com.pyamsoft.powermanager.app.trigger.create.CreateTriggerDialog;
import com.pyamsoft.powermanager.model.FabColorEvent;
import com.pyamsoft.pydroid.base.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.tool.DividerItemDecoration;
import com.pyamsoft.pydroid.tool.RxBus;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class PowerTriggerFragment extends ActionBarFragment
    implements TriggerPresenter.TriggerView {

  @NonNull public static final String TAG = "power_triggers";

  @BindView(R.id.power_trigger_list) RecyclerView recyclerView;
  @BindView(R.id.power_trigger_empty) FrameLayout emptyView;

  @Inject TriggerListAdapterPresenter listAdapterPresenter;
  @Inject TriggerPresenter presenter;

  private PowerTriggerListAdapter adapter;
  private Unbinder unbinder;
  private RecyclerView.ItemDecoration dividerDecoration;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    setActionBarUpEnabled(true);
    Singleton.Dagger.with(getContext()).plusTrigger().inject(this);

    adapter = new PowerTriggerListAdapter(this, listAdapterPresenter);
    dividerDecoration =
        new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);

    final View view = inflater.inflate(R.layout.fragment_powertrigger, container, false);
    unbinder = ButterKnife.bind(this, view);
    adapter.onCreate();
    presenter.bindView(this);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    setActionBarUpEnabled(false);

    recyclerView.removeItemDecoration(dividerDecoration);

    adapter.onDestroy();
    presenter.unbindView();
    unbinder.unbind();
  }

  @Override public void onResume() {
    super.onResume();
    presenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    presenter.pause();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupFab();
    setupRecyclerView();
    presenter.loadTriggerView();
  }

  private void setupFab() {
    FabColorBus.get()
        .post(FabColorEvent.create(R.drawable.ic_add_24dp, () -> presenter.showNewTriggerDialog()));
  }

  private void setupRecyclerView() {
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setHasFixedSize(true);
    recyclerView.addItemDecoration(dividerDecoration);
  }

  @Override public void loadEmptyView() {
    Timber.d("Load empty view");
    recyclerView.setVisibility(View.GONE);
    recyclerView.setAdapter(null);
    emptyView.setVisibility(View.VISIBLE);
  }

  @Override public void loadListView() {
    Timber.d("Load list view");
    emptyView.setVisibility(View.GONE);
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

  @Override public void onNewTriggerCreateError() {
    Toast.makeText(getContext(), "ERROR: Trigger must have a name and unique percent",
        Toast.LENGTH_LONG).show();
  }

  @Override public void onNewTriggerInsertError() {
    Toast.makeText(getContext(), "ERROR: Two triggers cannot have the same percent",
        Toast.LENGTH_LONG).show();
  }

  @Override public void onShowNewTriggerDialog() {
    Timber.d("Show new trigger dialog");
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new CreateTriggerDialog(),
        "create_trigger");
  }

  @Override public void onTriggerDeleted(int position) {
    adapter.onDeleteTriggerAtPosition(position);
    if (adapter.getItemCount() == 0) {
      Timber.d("Last trigger, hide list");
      loadEmptyView();
    }
  }

  public static final class Bus extends RxBus<ContentValues> {

    @NonNull private static final Bus instance = new Bus();

    @CheckResult @NonNull public static Bus get() {
      return instance;
    }
  }
}
