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

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.manager.ManagerSettingsPagerAdapter;
import com.pyamsoft.powermanager.app.settings.SettingsFragment;
import com.pyamsoft.pydroid.model.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncVectorDrawableTask;

final class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {

  public static final int POSITION_WIFI = 0;
  public static final int POSITION_DATA = 1;
  public static final int POSITION_BLUETOOTH = 2;
  public static final int POSITION_SYNC = 3;
  public static final int POSITION_SETTINGS = 4;
  private static final int NUMBER_ITEMS = 5;

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_item_overview, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    String title;
    @DrawableRes int image;
    @ColorRes int background;
    String type;
    switch (position) {
      case POSITION_WIFI:
        title = "WiFi";
        image = R.drawable.ic_network_wifi_24dp;
        background = R.color.green500;
        type = ManagerSettingsPagerAdapter.TYPE_WIFI;
        break;
      case POSITION_DATA:
        title = "Data";
        image = R.drawable.ic_network_cell_24dp;
        background = R.color.orange500;
        type = ManagerSettingsPagerAdapter.TYPE_DATA;
        break;
      case POSITION_BLUETOOTH:
        title = "Bluetooth";
        image = R.drawable.ic_bluetooth_24dp;
        background = R.color.blue500;
        type = ManagerSettingsPagerAdapter.TYPE_BLUETOOTH;
        break;
      case POSITION_SYNC:
        title = "Sync";
        image = R.drawable.ic_sync_24dp;
        background = R.color.yellow500;
        type = ManagerSettingsPagerAdapter.TYPE_SYNC;
        break;
      case POSITION_SETTINGS:
        title = "Settings";
        image = R.drawable.ic_settings_24dp;
        background = R.color.pink500;
        type = SettingsFragment.TAG;
        break;
      default:
        throw new IllegalStateException("Position out of range");
    }

    holder.itemView.setBackgroundColor(
        ContextCompat.getColor(holder.itemView.getContext(), background));

    assert holder.root != null;
    holder.root.setOnClickListener(v -> OverviewSelectionBus.get()
        .post(new OverviewSelectionBus.OverviewSelectionEvent(type)));

    assert holder.title != null;
    holder.title.setText(title);

    cancelAsyncTask(holder);

    assert holder.image != null;
    holder.task = new AsyncVectorDrawableTask(holder.image, android.R.color.white);
    holder.task.execute(
        new AsyncDrawable(holder.itemView.getContext().getApplicationContext(), image));
  }

  @Override public void onViewRecycled(ViewHolder holder) {
    super.onViewRecycled(holder);
    assert holder.root != null;
    holder.root.setOnClickListener(null);

    assert holder.title != null;
    holder.title.setText(null);

    assert holder.image != null;
    holder.image.setImageResource(0);

    cancelAsyncTask(holder);
  }

  private void cancelAsyncTask(@NonNull ViewHolder holder) {
    if (holder.task != null) {
      holder.task.cancel(true);
      holder.task = null;
    }
  }

  @Override public int getItemCount() {
    return NUMBER_ITEMS;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @Nullable @BindView(R.id.adapter_item_overview_root) FrameLayout root;
    @Nullable @BindView(R.id.adapter_item_overview_image) ImageView image;
    @Nullable @BindView(R.id.adapter_item_overview_title) TextView title;
    @Nullable AsyncVectorDrawableTask task;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
