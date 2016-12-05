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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemTriggerBinding;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import java.util.List;
import java.util.Locale;
import timber.log.Timber;

class PowerTriggerListItem
    extends AbstractItem<PowerTriggerListItem, PowerTriggerListItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

  @SuppressWarnings("WeakerAccess") @NonNull final PowerTriggerEntry trigger;

  PowerTriggerListItem(@NonNull PowerTriggerEntry trigger) {
    this.trigger = trigger;
  }

  @NonNull @CheckResult PowerTriggerEntry getTrigger() {
    return trigger;
  }

  @Override public int getType() {
    return R.id.adapter_trigger_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_trigger;
  }

  @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

  @Override public void bindView(ViewHolder holder, List payloads) {
    super.bindView(holder, payloads);
    holder.binding.triggerName.setText(trigger.name());
    holder.binding.triggerPercent.setText(
        String.format(Locale.getDefault(), "Percent: %s", trigger.percent()));
    holder.binding.triggerEnabledSwitch.setOnCheckedChangeListener(null);
    Timber.d("Entry enabled: %s", trigger.enabled());
    holder.binding.triggerEnabledSwitch.setChecked(trigger.enabled());
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.binding.triggerName.setText(null);
    holder.binding.triggerPercent.setText(null);
  }

  @SuppressWarnings("WeakerAccess") protected static class ItemFactory
      implements ViewHolderFactory<ViewHolder> {
    @Override public ViewHolder create(View v) {
      return new ViewHolder(v);
    }
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemTriggerBinding binding;

    public ViewHolder(View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
    }
  }
}
