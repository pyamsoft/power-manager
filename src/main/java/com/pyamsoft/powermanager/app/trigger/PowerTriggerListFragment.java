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

import android.databinding.DataBindingUtil;
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
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.trigger.create.CreateTriggerDialog;
import com.pyamsoft.powermanager.databinding.FragmentPowertriggerBinding;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.app.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncMap;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import java.util.List;
import timber.log.Timber;

public class PowerTriggerListFragment extends ActionBarFragment
    implements TriggerPresenter.TriggerView {

  @NonNull public static final String TAG = "PowerTriggerListFragment";
  @NonNull private static final String KEY_PRESENTER = "key_trigger_presenter";
  @NonNull private final AsyncDrawable.Mapper drawableMap = new AsyncDrawable.Mapper();

  @SuppressWarnings("WeakerAccess") TriggerPresenter presenter;

  @SuppressWarnings("WeakerAccess") FastItemAdapter<PowerTriggerListItem> adapter;
  private RecyclerView.ItemDecoration dividerDecoration;
  private long loadedPresenterKey;
  private FragmentPowertriggerBinding binding;
  private boolean listIsRefreshed;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedPresenterKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState, new PersistLoader.Callback<TriggerPresenter>() {
          @NonNull @Override public PersistLoader<TriggerPresenter> createLoader() {
            return new TriggerPresenterLoader();
          }

          @Override public void onPersistentLoaded(@NonNull TriggerPresenter persist) {
            presenter = persist;
          }
        });
  }

  @CheckResult @NonNull public TriggerPresenter getPresenter() {
    if (presenter == null) {
      throw new NullPointerException("Presenter is NULL");
    }
    return presenter;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    listIsRefreshed = false;
    dividerDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_powertrigger, container, false);
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
        item.click(item1 -> AppUtil.guaranteeSingleDialogFragment(getFragmentManager(),
            DeleteTriggerDialog.newInstance(item1), "delete_trigger"));
        return true;
      });

      adapter.getItemAdapter().withComparator((item1, item2) -> {
        final int item1Percent = item1.getPercent();
        final int item2PPercent = item2.getPercent();
        if (item1Percent == item2PPercent) {
          throw new IllegalStateException("Cannot have two triggers with same percent");
        } else if (item1Percent < item2PPercent) {
          return -1;
        } else {
          return 1;
        }
      }, true);

      adapter.withOnBindViewHolderListener(new FastAdapter.OnBindViewHolderListener() {
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, List<Object> list) {
          final PowerTriggerListItem.ViewHolder holder = toPowerTriggerListItem(viewHolder);
          adapter.getAdapterItem(holder.getAdapterPosition()).bindView(holder, list);
          holder.bind((position, entry, isChecked) -> {
            presenter.toggleEnabledState(position, entry, isChecked);
          });
        }

        @CheckResult @NonNull private PowerTriggerListItem.ViewHolder toPowerTriggerListItem(
            RecyclerView.ViewHolder viewHolder) {
          if (viewHolder instanceof PowerTriggerListItem.ViewHolder) {
            return (PowerTriggerListItem.ViewHolder) viewHolder;
          } else {
            throw new IllegalStateException("ViewHolder is not PowerTriggerListItem.ViewHolder");
          }
        }

        @Override public void unBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
          final PowerTriggerListItem.ViewHolder holder = toPowerTriggerListItem(viewHolder);
          final PowerTriggerListItem item = (PowerTriggerListItem) holder.itemView.getTag();
          if (item != null) {
            item.unbindView(holder);
          }
        }
      });
    }

    presenter.bindView(this);

    if (!listIsRefreshed) {
      // Because we may already have an Adapter with entries, we clear it first so that there are no doubles.
      adapter.clear();
      presenter.loadTriggerView();
    }
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(true);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedPresenterKey);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_PRESENTER, loadedPresenterKey);
    super.onSaveInstanceState(outState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupRecyclerView();
    setupFab();
  }

  private void setupFab() {
    final AsyncMap.Entry subscription = AsyncDrawable.load(R.drawable.ic_add_24dp)
        .tint(android.R.color.white)
        .into(binding.powerTriggerFab);
    drawableMap.put("fab", subscription);

    binding.powerTriggerFab.setOnClickListener(v -> presenter.showNewTriggerDialog());
  }

  private void setupRecyclerView() {
    binding.powerTriggerList.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.powerTriggerList.setHasFixedSize(true);
    binding.powerTriggerList.addItemDecoration(dividerDecoration);
  }

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

  private void loadEmptyView() {
    Timber.d("Load empty view");
    binding.powerTriggerList.setVisibility(View.GONE);
    binding.powerTriggerList.setAdapter(null);
    binding.powerTriggerEmpty.setVisibility(View.VISIBLE);
  }

  private void loadListView() {
    Timber.d("Load list view");
    binding.powerTriggerEmpty.setVisibility(View.GONE);
    binding.powerTriggerList.setAdapter(adapter);
    binding.powerTriggerList.setVisibility(View.VISIBLE);
  }

  @Override public void onNewTriggerAdded(@NonNull PowerTriggerEntry entry) {
    Timber.d("Added new trigger with percent: %d", entry.percent());

    adapter.add(createNewPowerTriggerListItem(entry));
    if (binding.powerTriggerList.getAdapter() == null) {
      Timber.d("First trigger, show list");
      loadListView();
    }
  }

  @CheckResult @NonNull
  private PowerTriggerListItem createNewPowerTriggerListItem(@NonNull PowerTriggerEntry entry) {
    return new PowerTriggerListItem(entry);
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
    adapter.remove(position);
    if (adapter.getItemCount() == 0) {
      Timber.d("Last trigger, hide list");
      loadEmptyView();
    }
  }

  @Override public void updateViewHolder(int position, @NonNull PowerTriggerEntry entry) {
    Timber.d("update view holder at position: %d", position);
    adapter.set(position, createNewPowerTriggerListItem(entry));
  }
}
