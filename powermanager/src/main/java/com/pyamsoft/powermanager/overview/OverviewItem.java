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

package com.pyamsoft.powermanager.overview;

import android.databinding.DataBindingUtil;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemOverviewBinding;
import com.pyamsoft.powermanager.model.OverviewModel;
import com.pyamsoft.powermanager.model.states.States;
import com.pyamsoft.pydroid.drawable.AsyncDrawable;
import com.pyamsoft.pydroid.drawable.AsyncMap;
import com.pyamsoft.pydroid.drawable.AsyncMapEntry;
import com.pyamsoft.pydroid.helper.AsyncMapHelper;
import java.util.List;
import javax.inject.Inject;

public class OverviewItem
    extends GenericAbstractItem<OverviewModel, OverviewItem, OverviewItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
  @SuppressWarnings("WeakerAccess") @Inject OverviewItemPresenter presenter;
  @SuppressWarnings("WeakerAccess") @NonNull AsyncMapEntry checkTask = AsyncMap.emptyEntry();
  @NonNull private AsyncMapEntry titleTask = AsyncMap.emptyEntry();

  OverviewItem(@NonNull String title, @DrawableRes int image,
      @ColorRes int background, @NonNull States states) {
    super(OverviewModel.builder()
        .background(background)
        .image(image)
        .state(states)
        .title(title)
        .build());
    Injector.get().provideComponent().plusOverviewComponent().inject(this);
  }

  @Override public int getType() {
    return R.id.adapter_overview_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_overview;
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    checkTask = AsyncMapHelper.unsubscribe(checkTask);
    titleTask = AsyncMapHelper.unsubscribe(titleTask);
    holder.binding.adapterItemOverviewImage.setImageDrawable(null);
    holder.binding.adapterItemOverviewTitle.setText(null);

    presenter.stop();
    presenter.destroy();
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.binding.adapterItemOverviewColor.setBackgroundColor(
        ContextCompat.getColor(holder.itemView.getContext(), getModel().background()));
    holder.binding.adapterItemOverviewTitle.setText(getModel().title());

    titleTask = AsyncMapHelper.unsubscribe(titleTask);
    titleTask = AsyncDrawable.load(getModel().image())
        .tint(android.R.color.white)
        .into(holder.binding.adapterItemOverviewImage);

    presenter.decideManageState(getModel().state(),
        new OverviewItemPresenter.ManageStateCallback() {
          @Override public void onManageStateDecided(@DrawableRes int icon) {
            checkTask = AsyncMapHelper.unsubscribe(checkTask);
            checkTask = AsyncDrawable.load(icon)
                .tint(android.R.color.white)
                .into(holder.binding.adapterItemOverviewCheck);
          }

          @Override public void onManageStateNone() {
            holder.binding.adapterItemOverviewCheck.setImageDrawable(null);
          }
        });
  }

  @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

  private static class ItemFactory implements ViewHolderFactory<ViewHolder> {
    ItemFactory() {
    }

    @Override public ViewHolder create(View v) {
      return new ViewHolder(v);
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemOverviewBinding binding;

    ViewHolder(View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
    }
  }
}
