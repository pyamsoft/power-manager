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

import android.content.res.ColorStateList;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.bluetooth.BluetoothFragment;
import com.pyamsoft.powermanager.app.data.DataFragment;
import com.pyamsoft.powermanager.app.doze.DozeFragment;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.settings.SettingsFragment;
import com.pyamsoft.powermanager.app.sync.SyncFragment;
import com.pyamsoft.powermanager.app.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.app.wear.WearFragment;
import com.pyamsoft.powermanager.app.wifi.WifiFragment;
import com.pyamsoft.pydroid.tool.AsyncMap;
import com.pyamsoft.pydroid.util.AsyncDrawable;
import java.util.List;

class OverviewItem extends AbstractItem<OverviewItem, OverviewItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

  @NonNull private final AsyncDrawable.Mapper taskMap = new AsyncDrawable.Mapper();
  @NonNull private final View rootView;
  @NonNull private final String title;
  @DrawableRes private final int image;
  @ColorRes private final int background;
  @NonNull private final ItemClickListener itemClickListener;
  @Nullable private final BooleanInterestObserver observer;

  OverviewItem(@NonNull View rootView, @NonNull String title, @DrawableRes int image,
      @ColorRes int background, @Nullable BooleanInterestObserver observer,
      @NonNull ItemClickListener itemClickListener) {
    this.rootView = rootView;
    this.title = title;
    this.image = image;
    this.background = background;
    this.observer = observer;
    this.itemClickListener = itemClickListener;
  }

  @Override public int getType() {
    return R.id.adapter_overview_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_overview;
  }

  @Override public void bindView(ViewHolder holder, List payloads) {
    super.bindView(holder, payloads);
    recycleOld(holder);

    final Fragment fragment;
    switch (title) {
      case WifiFragment.TAG:
        fragment = WifiFragment.newInstance(holder.itemView, rootView);
        break;
      case DataFragment.TAG:
        fragment = DataFragment.newInstance(holder.itemView, rootView);
        break;
      case BluetoothFragment.TAG:
        fragment = BluetoothFragment.newInstance(holder.itemView, rootView);
        break;
      case SyncFragment.TAG:
        fragment = SyncFragment.newInstance(holder.itemView, rootView);
        break;
      case PowerTriggerFragment.TAG:
        fragment = PowerTriggerFragment.newInstance(holder.itemView, rootView);
        break;
      case DozeFragment.TAG:
        fragment = DozeFragment.newInstance(holder.itemView, rootView);
        break;
      case WearFragment.TAG:
        fragment = WearFragment.newInstance(holder.itemView, rootView);
        break;
      case SettingsFragment.TAG:
        fragment = SettingsFragment.newInstance(holder.itemView, rootView);
        break;
      default:
        throw new IllegalStateException("Invalid tag: " + title);
    }

    holder.itemView.setBackgroundColor(
        ContextCompat.getColor(holder.itemView.getContext(), background));

    holder.title.setText(title);
    holder.title.setCheckMarkTintList(ColorStateList.valueOf(
        ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white)));
    if (observer != null) {
      holder.title.setChecked(observer.is());
    } else {
      holder.title.setCheckMarkDrawable(null);
    }

    holder.root.setOnClickListener(
        view -> getItemClickListener().onItemClicked(getTitle(), fragment));

    final AsyncMap.Entry task = AsyncDrawable.with(holder.itemView.getContext())
        .load(image)
        .tint(android.R.color.white)
        .into(holder.image);
    taskMap.put(title, task);
  }

  private void recycleOld(ViewHolder holder) {
    taskMap.clear();
    holder.root.setOnClickListener(null);
    holder.image.setImageDrawable(null);
    holder.image.setOnClickListener(null);
    holder.title.setText(null);
    holder.title.setOnClickListener(null);
  }

  @SuppressWarnings("WeakerAccess") @NonNull @CheckResult ItemClickListener getItemClickListener() {
    return itemClickListener;
  }

  @NonNull @CheckResult public String getTitle() {
    return title;
  }

  @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

  interface ItemClickListener {

    void onItemClicked(@NonNull String title, @NonNull Fragment fragment);
  }

  @SuppressWarnings("WeakerAccess") protected static class ItemFactory
      implements ViewHolderFactory<ViewHolder> {
    @Override public ViewHolder create(View v) {
      return new ViewHolder(v);
    }
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final Unbinder unbinder;
    @BindView(R.id.adapter_item_overview_root) FrameLayout root;
    @BindView(R.id.adapter_item_overview_image) ImageView image;
    @BindView(R.id.adapter_item_overview_title) CheckedTextView title;

    public ViewHolder(View itemView) {
      super(itemView);
      unbinder = ButterKnife.bind(this, itemView);
    }
  }
}
