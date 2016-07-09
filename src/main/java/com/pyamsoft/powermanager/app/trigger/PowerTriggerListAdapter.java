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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;

public class PowerTriggerListAdapter
    extends RecyclerView.Adapter<PowerTriggerListAdapter.ViewHolder>
    implements TriggerListAdapterPresenter.TriggerListAdapterView {

  @NonNull private final TriggerListAdapterPresenter presenter;

  public PowerTriggerListAdapter(@NonNull TriggerListAdapterPresenter presenter) {
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
    return presenter.size();
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final PowerTriggerEntry entry = presenter.get(position);
    holder.triggerName.setText(entry.name());
  }

  public void onAddTriggerForPercent(int percent) {
    notifyItemInserted(presenter.getPositionForPercent(percent));
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.trigger_name) TextView triggerName;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
