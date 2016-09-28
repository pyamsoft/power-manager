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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemTriggerBinding;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.util.AppUtil;
import timber.log.Timber;

class PowerTriggerListAdapter extends RecyclerView.Adapter<PowerTriggerListAdapter.ViewHolder>
    implements TriggerListAdapterPresenter.TriggerListAdapterView {

  @NonNull final TriggerListAdapterPresenter presenter;
  @NonNull final Fragment fragment;

  PowerTriggerListAdapter(@NonNull Fragment fragment,
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
    holder.binding.triggerName.setText(entry.name());

    // Set up delete onLongClick
    holder.itemView.setOnLongClickListener(view -> {
      AppUtil.guaranteeSingleDialogFragment(fragment.getFragmentManager(),
          DeleteTriggerDialog.newInstance(entry), "delete_trigger");
      return true;
    });

    holder.binding.triggerPercent.setText("Percent: " + entry.percent());

    holder.binding.triggerEnabledSwitch.setOnCheckedChangeListener(null);
    holder.binding.triggerEnabledSwitch.setChecked(entry.enabled());
    final CompoundButton.OnCheckedChangeListener listener =
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {
            compoundButton.setOnCheckedChangeListener(null);
            compoundButton.setChecked(!b);
            compoundButton.setOnCheckedChangeListener(this);

            Timber.d("Toggle enabled: %s", b);
            presenter.toggleEnabledState(holder.getAdapterPosition(),
                presenter.get(holder.getAdapterPosition()), b);
          }
        };

    holder.binding.triggerEnabledSwitch.setOnCheckedChangeListener(listener);
  }

  @Override public void onViewRecycled(ViewHolder holder) {
    super.onViewRecycled(holder);
    holder.binding.triggerName.setText(null);
    holder.itemView.setOnLongClickListener(null);
  }

  @Override public void updateViewHolder(int position) {
    Timber.d("Update view holder at %d", position);
    notifyItemChanged(position);
  }

  void onAddTriggerForPercent(int percent) {
    Timber.d("Insert new item for percent: %d", percent);
    final int position = presenter.getPositionForPercent(percent);
    Timber.d("Insert new item at: %d", position);
    notifyItemInserted(position);
  }

  void onDeleteTriggerAtPosition(int position) {
    Timber.d("Delete item at position: %d", position);
    notifyItemRemoved(position);
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemTriggerBinding binding;

    public ViewHolder(View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
    }
  }
}
