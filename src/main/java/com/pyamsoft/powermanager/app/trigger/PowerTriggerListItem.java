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
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemTriggerBinding;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.ActionSingle;
import java.util.List;
import java.util.Locale;
import timber.log.Timber;

class PowerTriggerListItem extends
    GenericAbstractItem<PowerTriggerEntry, PowerTriggerListItem, PowerTriggerListItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

  PowerTriggerListItem(@NonNull PowerTriggerEntry trigger) {
    super(trigger);
  }

  void click(@NonNull ActionSingle<PowerTriggerEntry> onClick) {
    onClick.call(getModel());
  }

  @CheckResult int getPercent() {
    return getModel().percent();
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
    holder.bind(getModel());
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
    @Nullable PowerTriggerEntry trigger;

    ViewHolder(View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
    }

    void bind(@NonNull PowerTriggerEntry trigger) {
      binding.triggerName.setText(trigger.name());
      binding.triggerPercent.setText(
          String.format(Locale.getDefault(), "Percent: %s", trigger.percent()));
      binding.triggerEnabledSwitch.setOnCheckedChangeListener(null);
      binding.triggerEnabledSwitch.setChecked(trigger.enabled());

      this.trigger = trigger;
    }

    void unbind() {
      binding.triggerName.setText(null);
      binding.triggerPercent.setText(null);
      binding.triggerEnabledSwitch.setOnCheckedChangeListener(null);
      trigger = null;
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
              if (trigger != null) {
                onTriggerEnabledChanged.onChange(getAdapterPosition(), trigger, b);
              } else {
                Timber.e("Trigger is NULL");
              }
            }
          };

      binding.triggerEnabledSwitch.setOnCheckedChangeListener(listener);
    }
  }
}
