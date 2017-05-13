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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemExceptionsBinding;
import com.pyamsoft.powermanager.databinding.LayoutContainerExceptionChargingBinding;
import com.pyamsoft.powermanager.databinding.LayoutContainerExceptionWearBinding;
import java.util.List;

public class ExceptionItem extends BaseItem<ExceptionItem, ExceptionItem.ViewHolder> {

  @NonNull static final String TAG = "ExceptionItem";

  ExceptionItem() {
    super(TAG);
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  @Override public int getType() {
    return R.id.adapter_exception_card_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_exceptions;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.binding.exceptionChargingContainer.setTitleTextSize(18);
    holder.binding.exceptionChargingContainer.setTitle(R.string.charging);
    holder.binding.exceptionChargingContainer.setDescription(null);
    holder.binding.exceptionChargingContainer.setExpandingContent(
        holder.chargingContainerBinding.getRoot());

    holder.binding.exceptionWearContainer.setTitleTextSize(18);
    holder.binding.exceptionWearContainer.setTitle(R.string.connected_to_android_wear);
    holder.binding.exceptionWearContainer.setDescription(null);
    holder.binding.exceptionWearContainer.setExpandingContent(
        holder.wearContainerBinding.getRoot());
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.wearContainerBinding.unbind();
    holder.chargingContainerBinding.unbind();
    holder.binding.unbind();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemExceptionsBinding binding;
    @NonNull final LayoutContainerExceptionChargingBinding chargingContainerBinding;
    @NonNull final LayoutContainerExceptionWearBinding wearContainerBinding;

    ViewHolder(View itemView) {
      super(itemView);
      binding = AdapterItemExceptionsBinding.bind(itemView);
      LayoutInflater layoutInflater = LayoutInflater.from(itemView.getContext());
      View chargingContainer =
          layoutInflater.inflate(R.layout.layout_container_exception_charging, (ViewGroup) itemView,
              false);
      chargingContainerBinding = LayoutContainerExceptionChargingBinding.bind(chargingContainer);

      View wearContainer =
          layoutInflater.inflate(R.layout.layout_container_exception_wear, (ViewGroup) itemView,
              false);
      wearContainerBinding = LayoutContainerExceptionWearBinding.bind(wearContainer);
    }
  }
}
