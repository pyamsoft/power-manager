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

package com.pyamsoft.powermanager.overview;

import android.databinding.DataBindingUtil;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemOverviewBinding;
import com.pyamsoft.powermanager.model.States;
import com.pyamsoft.pydroid.loader.ImageLoader;
import com.pyamsoft.pydroid.loader.LoaderMap;
import com.pyamsoft.pydroid.loader.loaded.Loaded;
import java.util.List;

public class OverviewItem
    extends GenericAbstractItem<OverviewModel, OverviewItem, OverviewItem.ViewHolder> {

  @NonNull private LoaderMap drawableMap = new LoaderMap();

  OverviewItem(@NonNull String title, @DrawableRes int image, @ColorRes int background,
      @NonNull States states) {
    super(OverviewModel.builder()
        .background(background)
        .image(image)
        .state(states)
        .title(title)
        .build());
    Injector.get().provideComponent().inject(this);
  }

  @Override public int getType() {
    return R.id.adapter_overview_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_overview;
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    drawableMap.clear();
    holder.binding.adapterItemOverviewImage.setImageDrawable(null);
    holder.binding.adapterItemOverviewTitle.setText(null);
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.binding.adapterItemOverviewColor.setBackgroundColor(
        ContextCompat.getColor(holder.itemView.getContext(), getModel().background()));
    holder.binding.adapterItemOverviewTitle.setText(getModel().title());

    Loaded titleTask = ImageLoader.fromResource(holder.itemView.getContext(), getModel().image())
        .tint(android.R.color.white)
        .into(holder.binding.adapterItemOverviewImage);
    drawableMap.put("title", titleTask);

    @DrawableRes final int icon;
    switch (getModel().state()) {
      case ENABLED:
        icon = R.drawable.ic_check_box_24dp;
        break;
      case DISABLED:
        icon = R.drawable.ic_check_box_outline_24dp;
        break;
      default:
        icon = 0;
    }
    if (icon == 0) {
      holder.binding.adapterItemOverviewCheck.setImageDrawable(null);
    } else {
      Loaded checkTask = ImageLoader.fromResource(holder.itemView.getContext(), icon)
          .tint(android.R.color.white)
          .into(holder.binding.adapterItemOverviewCheck);
      drawableMap.put("check", checkTask);
    }
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemOverviewBinding binding;

    ViewHolder(View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
    }
  }
}
