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

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.util.AppUtil;
import timber.log.Timber;

public class PowerTriggerListAdapter
    extends RecyclerView.Adapter<PowerTriggerListAdapter.ViewHolder>
    implements TriggerListAdapterPresenter.TriggerListAdapterView {

  @NonNull private final TriggerListAdapterPresenter presenter;
  @NonNull private final Fragment fragment;

  public PowerTriggerListAdapter(@NonNull Fragment fragment,
      @NonNull TriggerListAdapterPresenter presenter) {
    this.fragment = fragment;
    this.presenter = presenter;
  }

  public void onCreate() {
    presenter.bindView(this);
  }

  public void onDestroy() {
    presenter.unbindView();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_item_trigger, parent, false);
    return new ViewHolder(view);
  }

  @Override public int getItemCount() {
    final int size = presenter.size();
    Timber.d("List size: %d", size);
    return size;
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final PowerTriggerEntry entry = presenter.get(position);
    holder.name.setText(entry.name());

    // Set up delete onLongClick
    holder.itemView.setOnLongClickListener(view -> {
      AppUtil.guaranteeSingleDialogFragment(fragment.getFragmentManager(),
          DeleteTriggerDialog.newInstance(entry), "delete_trigger");
      return true;
    });

    holder.percent.setText("Percent: " + entry.percent());

    holder.enabled.setOnCheckedChangeListener(null);
    holder.enabled.setChecked(entry.enabled());
    holder.enabled.setOnCheckedChangeListener((compoundButton, b) -> {
      Timber.d("Toggle enabled: %s", b);
      presenter.toggleEnabledState(holder.getAdapterPosition(),
          presenter.get(holder.getAdapterPosition()), b);
    });
  }

  @Override public void onViewRecycled(ViewHolder holder) {
    super.onViewRecycled(holder);
    holder.name.setText(null);
    holder.itemView.setOnLongClickListener(null);
  }

  @Override public void updateViewHolder(int position) {
    //Timber.d("Update view holder at %d", position);
    //notifyItemChanged(position);
  }

  public void onAddTriggerForPercent(int percent) {
    Timber.d("Insert new item for percent: %d", percent);
    final int position = presenter.getPositionForPercent(percent);
    Timber.d("Insert new item at: %d", position);
    notifyItemInserted(position);
  }

  public void onDeleteTriggerAtPosition(int position) {
    Timber.d("Delete item at position: %d", position);
    notifyItemRemoved(position);
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.trigger_name) TextView name;
    @BindView(R.id.trigger_percent) TextView percent;
    @BindView(R.id.trigger_enabled_switch) SwitchCompat enabled;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
