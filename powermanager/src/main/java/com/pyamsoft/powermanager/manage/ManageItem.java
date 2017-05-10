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
import android.view.View;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemManageBinding;
import java.util.List;

class ManageItem extends BaseItem<ManageItem, ManageItem.ViewHolder> {

  @NonNull static final String TAG = "ManageItem";

  ManageItem() {
    super(TAG);
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  @Override public int getType() {
    return R.id.adapter_manage_card_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_manage;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.binding.manageWifi.setText("Manage WiFi");
    holder.binding.manageData.setText("Manage Data");
    holder.binding.manageBluetooth.setText("Manage Bluetooth");
    holder.binding.manageSync.setText("Manage Sync");
    holder.binding.manageAirplane.setText("Manage Airplane");
    holder.binding.manageDoze.setText("Manage Doze");
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.binding.manageWifi.setText(null);
    holder.binding.manageData.setText(null);
    holder.binding.manageBluetooth.setText(null);
    holder.binding.manageSync.setText(null);
    holder.binding.manageAirplane.setText(null);
    holder.binding.manageDoze.setText(null);
    holder.binding.manageWifi.setOnCheckedChangeListener(null);
    holder.binding.manageData.setOnCheckedChangeListener(null);
    holder.binding.manageBluetooth.setOnCheckedChangeListener(null);
    holder.binding.manageSync.setOnCheckedChangeListener(null);
    holder.binding.manageAirplane.setOnCheckedChangeListener(null);
    holder.binding.manageDoze.setOnCheckedChangeListener(null);
    holder.binding.unbind();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemManageBinding binding;
    @NonNull final Context context;

    ViewHolder(View itemView) {
      super(itemView);
      binding = AdapterItemManageBinding.bind(itemView);
      context = itemView.getContext();
    }
  }
}
