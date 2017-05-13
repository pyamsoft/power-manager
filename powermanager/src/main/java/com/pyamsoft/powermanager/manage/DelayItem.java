/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.manage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemSimpleBinding;
import com.pyamsoft.powermanager.databinding.LayoutContainerDelayBinding;
import java.util.List;
import javax.inject.Inject;

public class DelayItem extends BaseItem<DelayItem, DelayItem.ViewHolder> {

  @NonNull static final String TAG = "DelayItem";
  @SuppressWarnings("WeakerAccess") @Inject DelayPresenter presenter;

  DelayItem() {
    super(TAG);
    Injector.get().provideComponent().inject(this);
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  @Override public int getType() {
    return R.id.adapter_delay_card_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_simple;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.binding.simpleExpander.setTitle("Active Delay");
    holder.binding.simpleExpander.setDescription(
        "Power Manager will wait for the specified amount of time before automatically managing certain device functions");
    holder.binding.simpleExpander.setExpandingContent(holder.delayBinding.getRoot());

    final Context context = holder.itemView.getContext();
    presenter.getDelayTime(new DelayPresenter.DelayCallback() {
      @Override public void onCustomDelay(long time) {
        holder.delayBinding.delayRadioGroup.clearCheck();
        holder.delayBinding.delayRadioCustom.setChecked(true);
        holder.delayBinding.delayInputCustom.setEnabled(true);
      }

      @Override public void onPresetDelay(int index) {
        holder.delayBinding.delayRadioCustom.setChecked(false);
        holder.delayBinding.delayInputCustom.setEnabled(false);
        holder.delayBinding.delayRadioGroup.check(
            holder.delayBinding.delayRadioGroup.getChildAt(index).getId());
      }

      @Override public void onError(@NonNull Throwable throwable) {
        Toast.makeText(context, "Error getting delay time", Toast.LENGTH_SHORT).show();
      }

      @Override public void onComplete() {

      }
    });
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.delayBinding.unbind();
    holder.binding.unbind();
  }

  @Override void unbindItem() {
    presenter.stop();
    presenter.destroy();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull AdapterItemSimpleBinding binding;
    @NonNull LayoutContainerDelayBinding delayBinding;

    ViewHolder(View itemView) {
      super(itemView);
      binding = AdapterItemSimpleBinding.bind(itemView);

      View containerDelay = LayoutInflater.from(itemView.getContext())
          .inflate(R.layout.layout_container_delay, (ViewGroup) itemView, false);
      delayBinding = LayoutContainerDelayBinding.bind(containerDelay);
    }
  }
}
