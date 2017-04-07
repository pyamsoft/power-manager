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

package com.pyamsoft.powermanager.trigger;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.FragmentPowertriggerBinding;
import com.pyamsoft.powermanager.trigger.create.CreateTriggerDialog;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry;
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.ui.loader.DrawableLoader;
import com.pyamsoft.pydroid.ui.loader.DrawableMap;
import com.pyamsoft.pydroid.util.DialogUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class PowerTriggerListFragment extends ActionBarFragment {

  @NonNull public static final String TAG = "PowerTriggerListFragment";
  @NonNull private final DrawableMap drawableMap = new DrawableMap();

  @SuppressWarnings("WeakerAccess") @Inject TriggerPresenter presenter;

  @SuppressWarnings("WeakerAccess") FastItemAdapter<PowerTriggerListItem> adapter;
  boolean listIsRefreshed;
  FragmentPowertriggerBinding binding;
  private RecyclerView.ItemDecoration dividerDecoration;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Injector.get().provideComponent().plusTriggerComponent().inject(this);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    listIsRefreshed = false;
    dividerDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
    binding = FragmentPowertriggerBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    setActionBarUpEnabled(false);

    drawableMap.clear();
    binding.powerTriggerList.removeItemDecoration(dividerDecoration);
    binding.unbind();
  }

  @Override public void onStart() {
    super.onStart();
    if (adapter == null) {
      adapter = new FastItemAdapter<>();
      adapter.withSelectable(true);
      adapter.withSelectOnLongClick(true);
      adapter.withOnLongClickListener((view, iAdapter, item, i) -> {
        DialogUtil.guaranteeSingleDialogFragment(getActivity(),
            DeleteTriggerDialog.newInstance(item.getModel()), "delete_trigger");
        return true;
      });

      adapter.getItemAdapter().withComparator((item1, item2) -> {
        final int item1Percent = item1.getModel().percent();
        final int item2PPercent = item2.getModel().percent();
        if (item1Percent == item2PPercent) {
          throw new IllegalStateException("Cannot have two triggers with same percent");
        } else if (item1Percent < item2PPercent) {
          return -1;
        } else {
          return 1;
        }
      }, true);
    }

    if (!listIsRefreshed) {
      // Because we may already have an Adapter with entries, we clear it first so that there are no doubles.
      adapter.clear();
      presenter.loadTriggerView(new TriggerPresenter.TriggerLoadCallback() {
        @Override public void onTriggerLoaded(@NonNull PowerTriggerEntry entry) {
          adapter.add(createNewPowerTriggerListItem(entry));
        }

        @Override public void onTriggerLoadFinished() {
          if (adapter.getItemCount() == 0) {
            loadEmptyView();
          } else {
            listIsRefreshed = true;
            loadListView();
          }
        }
      }, false);
    }

    presenter.registerOnBus(new TriggerPresenter.BusCallback() {
      @Override public void onNewTriggerAdded(@NonNull PowerTriggerEntry entry) {
        Timber.d("Added new trigger with percent: %d", entry.percent());

        adapter.add(createNewPowerTriggerListItem(entry));
        if (binding.powerTriggerList.getAdapter() == null) {
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

      @Override public void onTriggerDeleted(int position) {
        adapter.remove(position);
        if (adapter.getItemCount() == 0) {
          Timber.d("Last trigger, hide list");
          loadEmptyView();
        }
      }
    });
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(true);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.stop();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    presenter.destroy();
    PowerManager.getRefWatcher(this).watch(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupRecyclerView();
    setupFab();
  }

  private void setupFab() {
    final DrawableLoader.Loaded subscription = DrawableLoader.load(R.drawable.ic_add_24dp)
        .tint(android.R.color.white)
        .into(binding.powerTriggerFab);
    drawableMap.put("fab", subscription);

    binding.powerTriggerFab.setOnClickListener(v -> {
      Timber.d("Show new trigger dialog");
      DialogUtil.guaranteeSingleDialogFragment(getActivity(), new CreateTriggerDialog(),
          "create_trigger");
    });
  }

  private void setupRecyclerView() {
    binding.powerTriggerList.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.powerTriggerList.setHasFixedSize(true);
    binding.powerTriggerList.addItemDecoration(dividerDecoration);
  }

  void loadEmptyView() {
    Timber.d("Load empty view");
    binding.powerTriggerList.setVisibility(View.GONE);
    binding.powerTriggerList.setAdapter(null);
    binding.powerTriggerEmpty.setVisibility(View.VISIBLE);
  }

  void loadListView() {
    Timber.d("Load list view");
    binding.powerTriggerEmpty.setVisibility(View.GONE);
    binding.powerTriggerList.setAdapter(adapter);
    binding.powerTriggerList.setVisibility(View.VISIBLE);
  }

  @CheckResult @NonNull PowerTriggerListItem createNewPowerTriggerListItem(
      @NonNull PowerTriggerEntry entry) {
    return new PowerTriggerListItem(entry);
  }
}
