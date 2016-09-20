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

import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.bluetooth.BluetoothFragment;
import com.pyamsoft.powermanager.app.data.DataFragment;
import com.pyamsoft.powermanager.app.doze.DozeFragment;
import com.pyamsoft.powermanager.app.settings.SettingsFragment;
import com.pyamsoft.powermanager.app.sync.SyncFragment;
import com.pyamsoft.powermanager.app.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.app.wifi.WifiFragment;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncDrawableMap;
import java.util.List;
import rx.Subscription;

class OverviewItem extends AbstractItem<OverviewItem, OverviewItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

  private static final int POSITION_WIFI = 0;
  private static final int POSITION_DATA = 1;
  private static final int POSITION_BLUETOOTH = 2;
  private static final int POSITION_SYNC = 3;
  private static final int POSITION_TRIGGERS = 4;
  private static final int POSITION_DOZE = 5;
  private static final int POSITION_SETTINGS = 6;

  @NonNull private final AsyncDrawableMap taskMap = new AsyncDrawableMap();
  @NonNull private final View rootView;
  @NonNull private final String title;
  @DrawableRes private final int image;
  @ColorRes private final int background;
  @NonNull private final ItemClickListener itemClickListener;
  private Fragment fragment;

  OverviewItem(@NonNull View rootView, @NonNull String title, @DrawableRes int image,
      @ColorRes int background, @NonNull ItemClickListener itemClickListener) {
    this.rootView = rootView;
    this.title = title;
    this.image = image;
    this.background = background;
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

    final int position = holder.getAdapterPosition();
    switch (position) {
      case POSITION_WIFI:
        fragment = WifiFragment.newInstance(holder.itemView, rootView);
        break;
      case POSITION_DATA:
        fragment = DataFragment.newInstance(holder.itemView, rootView);
        break;
      case POSITION_BLUETOOTH:
        fragment = BluetoothFragment.newInstance(holder.itemView, rootView);
        break;
      case POSITION_SYNC:
        fragment = SyncFragment.newInstance(holder.itemView, rootView);
        break;
      case POSITION_TRIGGERS:
        fragment = PowerTriggerFragment.newInstance(holder.itemView, rootView);
        break;
      case POSITION_DOZE:
        fragment = DozeFragment.newInstance(holder.itemView, rootView);
        break;
      case POSITION_SETTINGS:
        fragment = SettingsFragment.newInstance(holder.itemView, rootView);
        break;
      default:
        throw new IllegalStateException("Position out of range: " + position);
    }

    holder.itemView.setBackgroundColor(
        ContextCompat.getColor(holder.itemView.getContext(), background));

    holder.title.setText(title);

    holder.root.setOnClickListener(
        view -> getItemClickListener().onItemClicked(getTitle(), getFragment()));

    final Subscription task = AsyncDrawable.with(holder.itemView.getContext())
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
    fragment = null;
  }

  @SuppressWarnings("WeakerAccess") @NonNull @CheckResult ItemClickListener getItemClickListener() {
    return itemClickListener;
  }

  @NonNull @CheckResult public String getTitle() {
    return title;
  }

  @CheckResult @NonNull public Fragment getFragment() {
    if (fragment == null) {
      throw new RuntimeException("Fragment type has not been bound yet");
    }

    return fragment;
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
    @BindView(R.id.adapter_item_overview_title) TextView title;

    public ViewHolder(View itemView) {
      super(itemView);
      unbinder = ButterKnife.bind(this, itemView);
    }
  }
}
