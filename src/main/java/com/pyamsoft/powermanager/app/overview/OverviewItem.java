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

package com.pyamsoft.powermanager.app.overview;

import android.databinding.DataBindingUtil;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.databinding.AdapterItemOverviewBinding;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncMap;
import com.pyamsoft.pydroid.tool.AsyncMapHelper;
import java.util.List;

class OverviewItem extends AbstractItem<OverviewItem, OverviewItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

  @NonNull private final View rootView;
  @NonNull private final String title;
  @DrawableRes private final int image;
  @ColorRes private final int background;
  @Nullable private final BooleanInterestObserver observer;

  OverviewItem(@NonNull View rootView, @NonNull String title, @DrawableRes int image,
      @ColorRes int background, @Nullable BooleanInterestObserver observer) {
    this.rootView = rootView;
    this.title = title;
    this.image = image;
    this.background = background;
    this.observer = observer;
  }

  @NonNull @CheckResult View getRootView() {
    return rootView;
  }

  @Override public int getType() {
    return R.id.adapter_overview_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_overview;
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.unbind();
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.bind(background, title, image, observer);
  }

  @NonNull @CheckResult public String getTitle() {
    return title;
  }

  @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

  @SuppressWarnings("WeakerAccess") protected static class ItemFactory
      implements ViewHolderFactory<ViewHolder> {
    @Override public ViewHolder create(View v) {
      return new ViewHolder(v);
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull private final AdapterItemOverviewBinding binding;
    @Nullable private AsyncMap.Entry checkTask;
    @Nullable private AsyncMap.Entry titleTask;

    ViewHolder(View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
    }

    @NonNull @CheckResult AdapterItemOverviewBinding getBinding() {
      return binding;
    }

    void bind(@ColorRes int background, @NonNull String title, @DrawableRes int image,
        @Nullable BooleanInterestObserver observer) {
      // Tint check mark white
      // Avoids a NoMethod crash on API 19
      binding.adapterItemOverviewColor.setBackgroundColor(
          ContextCompat.getColor(itemView.getContext(), background));

      binding.adapterItemOverviewTitle.setText(title);
      if (observer != null) {
        final int check;
        if (observer.is()) {
          check = R.drawable.ic_check_box_24dp;
        } else {
          check = R.drawable.ic_check_box_outline_24dp;
        }

        AsyncMapHelper.unsubscribe(checkTask);
        checkTask = AsyncDrawable.with(itemView.getContext())
            .load(check)
            .tint(android.R.color.white)
            .into(binding.adapterItemOverviewCheck);
      } else {
        binding.adapterItemOverviewCheck.setImageDrawable(null);
      }

      AsyncMapHelper.unsubscribe(titleTask);
      titleTask = AsyncDrawable.with(itemView.getContext())
          .load(image)
          .tint(android.R.color.white)
          .into(binding.adapterItemOverviewImage);
    }

    void unbind() {
      AsyncMapHelper.unsubscribe(checkTask, titleTask);
      binding.adapterItemOverviewImage.setImageDrawable(null);
      binding.adapterItemOverviewTitle.setText(null);
    }
  }
}
