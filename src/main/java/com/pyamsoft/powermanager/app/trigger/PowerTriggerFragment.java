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
import android.support.design.widget.FloatingActionButton;
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
import com.pyamsoft.pydroid.base.PersistLoader;
import com.pyamsoft.pydroid.app.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.app.fragment.CircularRevealFragmentUtil;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncDrawableMap;
import com.pyamsoft.pydroid.app.widget.DividerItemDecoration;
import com.pyamsoft.pydroid.util.PersistentCache;
import com.pyamsoft.pydroid.util.AppUtil;
import rx.Subscription;
import timber.log.Timber;

public class PowerTriggerFragment extends ActionBarFragment
    implements TriggerPresenter.TriggerView {

  @NonNull public static final String TAG = "Power Triggers";
  @NonNull private static final String KEY_PRESENTER = "key_trigger_presenter";
  @NonNull private static final String KEY_PRESENTER_ADAPTER = "key_trigger_adapter_presenter";
  @NonNull private final AsyncDrawableMap drawableMap = new AsyncDrawableMap();

  @BindView(R.id.power_trigger_list) RecyclerView recyclerView;
  @BindView(R.id.power_trigger_empty) FrameLayout emptyView;
  @BindView(R.id.power_trigger_fab) FloatingActionButton floatingActionButton;

  TriggerListAdapterPresenter listAdapterPresenter;
  TriggerPresenter presenter;

  PowerTriggerListAdapter adapter;
  Unbinder unbinder;
  RecyclerView.ItemDecoration dividerDecoration;
  private long loadedPresenterKey;
  private long loadedPresenterAdapterKey;

  @CheckResult @NonNull
  public static PowerTriggerFragment newInstance(@NonNull View view, @NonNull View container) {
    final Bundle args = CircularRevealFragmentUtil.bundleArguments(view, container);
    final PowerTriggerFragment fragment = new PowerTriggerFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedPresenterKey = PersistentCache.load(KEY_PRESENTER, savedInstanceState,
        new PersistLoader.Callback<TriggerPresenter>() {
          @NonNull @Override public PersistLoader<TriggerPresenter> createLoader() {
            return new TriggerPresenterLoader(getContext());
          }

          @Override public void onPersistentLoaded(@NonNull TriggerPresenter persist) {
            presenter = persist;
          }
        });

    loadedPresenterAdapterKey = PersistentCache.load(KEY_PRESENTER_ADAPTER, savedInstanceState,
        new PersistLoader.Callback<TriggerListAdapterPresenter>() {
          @NonNull @Override public PersistLoader<TriggerListAdapterPresenter> createLoader() {
            return new TriggerListAdapterPresenterLoader(getContext());
          }

          @Override public void onPersistentLoaded(@NonNull TriggerListAdapterPresenter persist) {
            listAdapterPresenter = persist;
          }
        });
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    dividerDecoration =
        new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);

    final View view = inflater.inflate(R.layout.fragment_powertrigger, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    setActionBarUpEnabled(false);

    drawableMap.clear();
    recyclerView.removeItemDecoration(dividerDecoration);
    unbinder.unbind();
  }

  @Override public void onStart() {
    super.onStart();
    if (adapter == null) {
      adapter = new PowerTriggerListAdapter(this, listAdapterPresenter);
    }

    adapter.onCreate();
    presenter.bindView(this);
    presenter.loadTriggerView();
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(true);
  }

  @Override public void onStop() {
    super.onStop();
    adapter.onDestroy();
    presenter.unbindView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.unload(loadedPresenterKey);
      PersistentCache.unload(loadedPresenterAdapterKey);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.saveKey(KEY_PRESENTER, outState, loadedPresenterKey);
    PersistentCache.saveKey(KEY_PRESENTER_ADAPTER, outState, loadedPresenterAdapterKey);
    super.onSaveInstanceState(outState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    CircularRevealFragmentUtil.runCircularRevealOnViewCreated(view, getArguments());
    setupRecyclerView();
    setupFab();
  }

  private void setupFab() {
    final Subscription subscription = AsyncDrawable.with(getContext())
        .load(R.drawable.ic_add_24dp)
        .tint(android.R.color.white)
        .into(floatingActionButton);
    drawableMap.put("fab", subscription);

    floatingActionButton.setOnClickListener(
        view -> AppUtil.guaranteeSingleDialogFragment(getFragmentManager(),
            new CreateTriggerDialog(), "create_trigger"));
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
