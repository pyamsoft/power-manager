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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemTriggerBinding;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.ActionSingle;
import java.lang.ref.WeakReference;
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

  void click(@NonNull ActionSingle<PowerTriggerEntry> onClick) {
    onClick.call(trigger);
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

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.bind(trigger);
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.unbind();
  }

  interface OnTriggerEnabledChanged {

    void onChange(int position, @NonNull PowerTriggerEntry entry, boolean isChecked);
  }

  @SuppressWarnings("WeakerAccess") protected static class ItemFactory
      implements ViewHolderFactory<ViewHolder> {
    @Override public ViewHolder create(View v) {
      return new ViewHolder(v);
    }
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull private final AdapterItemTriggerBinding binding;
    @NonNull WeakReference<PowerTriggerEntry> weakTrigger;

    ViewHolder(View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
      weakTrigger = new WeakReference<>(null);
    }

    void bind(@NonNull PowerTriggerEntry trigger) {
      binding.triggerName.setText(trigger.name());
      binding.triggerPercent.setText(
          String.format(Locale.getDefault(), "Percent: %s", trigger.percent()));
      binding.triggerEnabledSwitch.setOnCheckedChangeListener(null);
      binding.triggerEnabledSwitch.setChecked(trigger.enabled());

      weakTrigger.clear();
      weakTrigger = new WeakReference<>(trigger);
    }

    void unbind() {
      binding.triggerName.setText(null);
      binding.triggerPercent.setText(null);
      binding.triggerEnabledSwitch.setOnCheckedChangeListener(null);
      weakTrigger.clear();
    }

    void bind(@NonNull OnTriggerEnabledChanged onTriggerEnabledChanged) {
      final CompoundButton.OnCheckedChangeListener listener =
          new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {
              compoundButton.setOnCheckedChangeListener(null);
              compoundButton.setChecked(!b);
              compoundButton.setOnCheckedChangeListener(this);

              Timber.d("Toggle enabled: %s", b);
              final PowerTriggerEntry entry = weakTrigger.get();
              if (entry != null) {
                onTriggerEnabledChanged.onChange(getAdapterPosition(), entry, b);
              }
            }
          };

      binding.triggerEnabledSwitch.setOnCheckedChangeListener(listener);
    }
  }
}
