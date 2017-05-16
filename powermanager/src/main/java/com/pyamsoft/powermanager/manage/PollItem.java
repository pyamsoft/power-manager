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
import android.view.View;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemSimpleBinding;
import java.util.List;

public class PollItem extends BaseItem<PollItem, PollItem.ViewHolder> {

  @NonNull static final String TAG = "PollItem";

  PollItem() {
    super(TAG);
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  @Override public int getType() {
    return R.id.adapter_poll_card_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_simple;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.binding.simpleExpander.setTitle("Smart Polling");
    holder.binding.simpleExpander.setDescription(
        "Power Manager will periodically poll the features it manages, to do things like automatically sync your latest emails and messages.");
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.binding.unbind();
  }

  @Override void unbindItem() {

  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull AdapterItemSimpleBinding binding;

    ViewHolder(View itemView) {
      super(itemView);
      binding = AdapterItemSimpleBinding.bind(itemView);
    }
  }
}