/*
 * Copyright 2013 - 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.ui.battery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.util.BatteryUtil;
import com.pyamsoft.powermanager.ui.BindableRecyclerAdapter;
import com.pyamsoft.pydroid.util.DrawableUtil;
import com.pyamsoft.pydroid.util.StringUtil;

public final class BatteryInfoAdapter
    extends BindableRecyclerAdapter<BatteryInfoAdapter.ViewHolder> {

  private static final int POSITION_PERCENT = 0;
  private static final int POSITION_CHARGE = 1;
  private static final int POSITION_TEMPERATURE = 2;
  private static final int NUMBER_ITEMS = 3;

  @Override public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View view = inflater.inflate(R.layout.adapter_item_simple, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    final BatteryUtil bs = BatteryUtil.with(holder.itemView.getContext());
    bs.updateBatteryInformation();
    switch (position) {
      case POSITION_PERCENT:
        setPercent(holder, bs.getPercent());
        break;
      case POSITION_CHARGE:
        setCharging(holder, bs.isCharging());
        break;
      case POSITION_TEMPERATURE:
        setTemperature(holder, bs.getTemperature());
        break;
      default:
    }
  }

  private void setPercent(ViewHolder holder, double percent) {
    holder.image.setImageDrawable(DrawableUtil.tintDrawableFromRes(holder.itemView.getContext(),
        R.drawable.ic_settings_white_24dp, R.color.grey500));
    final String s = holder.title.getContext().getString(R.string.bat_percent);
    holder.title.setText(StringUtil.formatString(s, (int) percent));
  }

  private void setCharging(ViewHolder holder, boolean charging) {
    holder.image.setImageDrawable(DrawableUtil.tintDrawableFromRes(holder.itemView.getContext(),
        R.drawable.ic_settings_white_24dp, R.color.grey500));
    final String s = holder.title.getContext().getString(R.string.bat_charging);
    holder.title.setText(StringUtil.formatString(s, charging ? "Yes" : "No"));
  }

  private void setTemperature(ViewHolder holder, float temperature) {
    holder.image.setImageDrawable(DrawableUtil.tintDrawableFromRes(holder.itemView.getContext(),
        R.drawable.ic_settings_white_24dp, R.color.grey500));
    final String s = holder.title.getContext().getString(R.string.bat_temperature);
    holder.title.setText(StringUtil.formatString(s, String.format("%.1f", temperature)));
  }

  @Override public int getItemCount() {
    return NUMBER_ITEMS;
  }

  @Override public int getStatusbarColor() {
    return R.color.pink700;
  }

  @Override public int getToolbarColor() {
    return R.color.pink500;
  }

  @Override protected void onCreate() {

  }

  @Override protected void onDestroy() {

  }

  @Override protected void onStart() {

  }

  @Override protected void onStop() {

  }

  @Override public int getSmallFABIcon() {
    return 0;
  }

  @Override public int getLargeFABIcon() {
    return 0;
  }

  @Override public boolean isSmallFABShown() {
    return false;
  }

  @Override public boolean isLargeFABShown() {
    return false;
  }

  @Override public View.OnClickListener getSmallFABOnClick() {
    return null;
  }

  @Override public View.OnClickListener getLargeFABOnClick() {
    return null;
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    private final ImageView image;
    private final TextView title;

    public ViewHolder(final View itemView) {
      super(itemView);
      image = (ImageView) itemView.findViewById(R.id.item_image);
      title = (TextView) itemView.findViewById(R.id.item_title);
    }
  }
}
