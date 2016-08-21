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
import butterknife.Unbinder;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncDrawableMap;
import rx.Subscription;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {

  private static final int POSITION_WIFI = 0;
  private static final int POSITION_DATA = 1;
  private static final int POSITION_BLUETOOTH = 2;
  private static final int POSITION_SYNC = 3;
  private static final int POSITION_TRIGGERS = 4;
  private static final int POSITION_DOZE = 5;
  private static final int POSITION_SETTINGS = 6;
  private static final int NUMBER_ITEMS = 7;

  @NonNull private final AsyncDrawableMap taskMap = new AsyncDrawableMap();

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_item_overview, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final String title;
    final String tag;
    @DrawableRes final int image;
    @ColorRes final int background;
    switch (position) {
      case POSITION_WIFI:
        title = "WiFi";
        image = R.drawable.ic_network_wifi_24dp;
        background = R.color.green500;
        break;
      case POSITION_DATA:
        title = "Data";
        image = R.drawable.ic_network_cell_24dp;
        background = R.color.orange500;
        break;
      case POSITION_BLUETOOTH:
        title = "Bluetooth";
        image = R.drawable.ic_bluetooth_24dp;
        background = R.color.blue500;
        break;
      case POSITION_SYNC:
        title = "Sync";
        image = R.drawable.ic_sync_24dp;
        background = R.color.yellow500;
        break;
      case POSITION_TRIGGERS:
        title = "Power Triggers";
        image = R.drawable.ic_battery_24dp;
        background = R.color.red500;
        break;
      case POSITION_DOZE:
        title = "Doze";
        image = R.drawable.ic_doze_24dp;
        background = R.color.orange500;
        break;
      case POSITION_SETTINGS:
        title = "Settings";
        image = R.drawable.ic_settings_24dp;
        background = R.color.pink500;
        break;
      default:
        throw new IllegalStateException("Position out of range");
    }

    holder.itemView.setBackgroundColor(
        ContextCompat.getColor(holder.itemView.getContext(), background));

    holder.root.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        // TODO load a new fragment
      }
    });

    holder.title.setText(title);

    final Subscription task = AsyncDrawable.with(holder.itemView.getContext())
        .load(image)
        .tint(android.R.color.white)
        .into(holder.image);
    taskMap.put(title, task);
  }

  @Override public void onViewRecycled(ViewHolder holder) {
    super.onViewRecycled(holder);
    holder.root.setOnClickListener(null);
    holder.title.setText(null);
    holder.image.setImageResource(0);
  }

  public void cleanup() {
    taskMap.clear();
  }

  @Override public int getItemCount() {
    return NUMBER_ITEMS;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull private final Unbinder unbinder;
    @BindView(R.id.adapter_item_overview_root) FrameLayout root;
    @BindView(R.id.adapter_item_overview_image) ImageView image;
    @BindView(R.id.adapter_item_overview_title) TextView title;

    public ViewHolder(View itemView) {
      super(itemView);
      unbinder = ButterKnife.bind(this, itemView);
    }
  }
}
