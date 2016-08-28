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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import com.pyamsoft.powermanager.app.trigger.create.CreateTriggerDialog;
import com.pyamsoft.pydroid.base.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.base.fragment.CircularRevealFragmentUtil;
import com.pyamsoft.pydroid.tool.DividerItemDecoration;
import com.pyamsoft.pydroid.util.AppUtil;
import timber.log.Timber;

public class PowerTriggerFragment extends ActionBarFragment
    implements TriggerPresenter.TriggerView {

  @NonNull public static final String TAG = "Power Triggers";

  @BindView(R.id.power_trigger_list) RecyclerView recyclerView;
  @BindView(R.id.power_trigger_empty) FrameLayout emptyView;

  TriggerListAdapterPresenter listAdapterPresenter;
  TriggerPresenter presenter;

  PowerTriggerListAdapter adapter;
  Unbinder unbinder;
  RecyclerView.ItemDecoration dividerDecoration;

  @CheckResult @NonNull
  public static PowerTriggerFragment newInstance(@NonNull View view, @NonNull View container) {
    final Bundle args = CircularRevealFragmentUtil.bundleArguments(view, container);
    final PowerTriggerFragment fragment = new PowerTriggerFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    dividerDecoration =
        new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);

    getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<TriggerPresenter>() {
      @Override public Loader<TriggerPresenter> onCreateLoader(int id, Bundle args) {
        return new TriggerPresenterLoader(getContext());
      }

      @Override public void onLoadFinished(Loader<TriggerPresenter> loader, TriggerPresenter data) {
        presenter = data;
      }

      @Override public void onLoaderReset(Loader<TriggerPresenter> loader) {
        presenter = null;
      }
    });

    getLoaderManager().initLoader(1, null,
        new LoaderManager.LoaderCallbacks<TriggerListAdapterPresenter>() {
          @Override public Loader<TriggerListAdapterPresenter> onCreateLoader(int id, Bundle args) {
            return new TriggerListAdapterPresenterLoader(getContext());
          }

          @Override public void onLoadFinished(Loader<TriggerListAdapterPresenter> loader,
              TriggerListAdapterPresenter data) {
            listAdapterPresenter = data;
          }

          @Override public void onLoaderReset(Loader<TriggerListAdapterPresenter> loader) {
            listAdapterPresenter = null;
          }
        });

    final View view = inflater.inflate(R.layout.fragment_powertrigger, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    setActionBarUpEnabled(false);

    recyclerView.removeItemDecoration(dividerDecoration);
    unbinder.unbind();
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(true);
    if (adapter == null) {
      adapter = new PowerTriggerListAdapter(this, listAdapterPresenter);
    }

    adapter.onCreate();
    presenter.bindView(this);
    presenter.loadTriggerView();
  }

  @Override public void onPause() {
    super.onPause();
    adapter.onDestroy();
    presenter.unbindView();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    CircularRevealFragmentUtil.runCircularRevealOnViewCreated(view, getArguments());
    setupRecyclerView();
  }

  void setupRecyclerView() {
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
}
