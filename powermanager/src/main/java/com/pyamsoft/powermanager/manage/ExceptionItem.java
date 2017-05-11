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
import com.pyamsoft.powermanager.databinding.AdapterItemExceptionsBinding;
import com.pyamsoft.pydroid.loader.LoaderMap;
import java.util.List;

public class ExceptionItem extends BaseItem<ExceptionItem, ExceptionItem.ViewHolder> {

  @NonNull public static final String TAG = "ExceptionItem";
  @SuppressWarnings("WeakerAccess") boolean expandedCharging;
  @SuppressWarnings("WeakerAccess") boolean expandedWear;
  @NonNull private LoaderMap loaderMap = new LoaderMap();

  ExceptionItem() {
    super(TAG);
    expandedCharging = false;
    expandedWear = false;
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
    loaderMap.put("charging", loadArrow(holder.binding.exceptionChargingArrow));
    setArrowRotation(holder.binding.exceptionChargingArrow, expandedCharging);
    holder.binding.exceptionChargingContainer.setVisibility(
        expandedCharging ? View.VISIBLE : View.GONE);
    holder.binding.exceptionChargingTitleContainer.setOnClickListener(v -> {
      expandedCharging = !expandedCharging;
      setArrowRotation(holder.binding.exceptionChargingArrow, expandedCharging);
      holder.binding.exceptionChargingContainer.setVisibility(
          expandedCharging ? View.VISIBLE : View.GONE);
    });

    loaderMap.put("wear", loadArrow(holder.binding.exceptionWearArrow));
    setArrowRotation(holder.binding.exceptionWearArrow, expandedWear);
    holder.binding.exceptionWearContainer.setVisibility(expandedWear ? View.VISIBLE : View.GONE);
    holder.binding.exceptionWearTitleContainer.setOnClickListener(v -> {
      expandedWear = !expandedWear;
      setArrowRotation(holder.binding.exceptionWearArrow, expandedWear);
      holder.binding.exceptionWearContainer.setVisibility(expandedWear ? View.VISIBLE : View.GONE);
    });
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    loaderMap.clear();
    holder.binding.exceptionChargingArrow.setImageDrawable(null);
    holder.binding.exceptionChargingTitleContainer.setOnClickListener(null);
    holder.binding.exceptionWearArrow.setImageDrawable(null);
    holder.binding.exceptionWearTitleContainer.setOnClickListener(null);
    holder.binding.unbind();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemExceptionsBinding binding;

    ViewHolder(View itemView) {
      super(itemView);
      binding = AdapterItemExceptionsBinding.bind(itemView);
    }
  }
}
