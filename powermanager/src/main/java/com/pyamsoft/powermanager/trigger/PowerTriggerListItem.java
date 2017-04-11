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

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemTriggerBinding;
import com.pyamsoft.powermanager.trigger.db.PowerTriggerEntry;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;

public class PowerTriggerListItem extends
    GenericAbstractItem<PowerTriggerEntry, PowerTriggerListItem, PowerTriggerListItem.ViewHolder> {

  @SuppressWarnings("WeakerAccess") @Inject TriggerItemPresenter presenter;

  PowerTriggerListItem(@NonNull PowerTriggerEntry trigger) {
    super(trigger);
    Injector.get().provideComponent().plusTriggerComponent().inject(this);
  }

  @Override public int getType() {
    return R.id.adapter_trigger_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_trigger;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    bindModelToHolder(holder);

    holder.binding.triggerEnabledSwitch.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            buttonView.setOnCheckedChangeListener(null);
            buttonView.setChecked(!isChecked);

            final CompoundButton.OnCheckedChangeListener listener = this;
            presenter.toggleEnabledState(getModel(), isChecked, entry -> {
              withModel(entry);
              bindModelToHolder(holder);
              buttonView.setOnCheckedChangeListener(listener);
            });
          }
        });
  }

  @SuppressWarnings("WeakerAccess") void bindModelToHolder(@NonNull ViewHolder holder) {
    holder.binding.triggerName.setText(getModel().name());
    holder.binding.triggerPercent.setText(
        String.format(Locale.getDefault(), "Percent: %s", getModel().percent()));
    holder.binding.triggerEnabledSwitch.setOnCheckedChangeListener(null);
    holder.binding.triggerEnabledSwitch.setChecked(getModel().enabled());
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.binding.triggerName.setText(null);
    holder.binding.triggerPercent.setText(null);
    holder.binding.triggerEnabledSwitch.setOnCheckedChangeListener(null);

    presenter.stop();
    presenter.destroy();
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  static final class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemTriggerBinding binding;

    ViewHolder(View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
    }
  }
}
