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
package com.pyamsoft.powermanager.ui.adapter;

import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import java.util.HashSet;
import java.util.Set;

public final class PowerPlanAdapter extends RecyclerView.Adapter<PowerPlanAdapter.ViewHolder>
    implements PowerPlanInterface {

  private static final float SCALE_Y = 1f;
  private static final int POWER_PLAN_NUMBER = 9;
  private static final String TAG = PowerPlanAdapter.class.getSimpleName();
  private final Set<Integer> expandedPositions = new HashSet<>(POWER_PLAN_NUMBER);

  @Override public final ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View v = inflater.inflate(R.layout.adapter_item_power_plan, parent, false);
    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    // Collapse the view on creation to avoid the visual bug on bind
    LogUtil.d(TAG, "Holder position: ", position, " isExpanded: ",
        expandedPositions.contains(position));
    if (!expandedPositions.contains(position)) {
      holder.layout.setVisibility(View.GONE);
    } else {
      holder.layout.setVisibility(View.VISIBLE);
    }

    holder.arrowHolder.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(final View v) {
        final boolean isCurrentlyExpanded = expandedPositions.contains(position);
        if (!isCurrentlyExpanded) {
          expandedPositions.add(position);
        } else {
          expandedPositions.remove(position);
        }
        // flip the arrow
        AnimUtil.flipVertical(holder.expandArrow).setListener(new ViewPropertyAnimatorListener() {

          @Override public void onAnimationStart(final View view) {

          }

          @Override public void onAnimationEnd(final View view) {
          }

          @Override public void onAnimationCancel(final View view) {
            if (expandedPositions.contains(position)) {
              LogUtil.d(TAG, "Is expanded, set to flipped");
              holder.expandArrow.setScaleY(-SCALE_Y);
            } else {
              LogUtil.d(TAG, "Is not expanded, set to normal");
              holder.expandArrow.setScaleY(SCALE_Y);
            }
          }
        }).start();
        if (!isCurrentlyExpanded) {
          // going to expand
          LogUtil.d(TAG, "Fade in layout");
          AnimUtil.expand(holder.layout);
        } else {
          // going to contract
          LogUtil.d(TAG, "Fade out layout");
          AnimUtil.collapse(holder.layout);
        }
      }
    });

    setupCurrentLayoutForPlan(holder, position);
  }

  private void setupCurrentLayoutForPlan(final ViewHolder holder, final int position) {
    final PowerPlanPresenter presenter = new PowerPlanPresenter();
    presenter.bind(holder.itemView.getContext(), this, position);
    holder.name.setText(presenter.getName());
    holder.image.setImageResource(R.drawable.ic_settings_white_24dp);

    final boolean wifiManaged = presenter.isWifiManaged();
    holder.imageWifi.setEnabled(wifiManaged);
    holder.imageWifi.setImageResource(wifiManaged ? R.drawable.ic_network_wifi_white_24dp
        : R.drawable.ic_signal_wifi_off_white_24dp);

    final boolean dataManaged = presenter.isDataManaged();
    holder.imageData.setEnabled(dataManaged);
    holder.imageData.setImageResource(dataManaged ? R.drawable.ic_network_cell_white_24dp
        : R.drawable.ic_signal_cellular_off_white_24dp);

    final boolean bluetoothManaged = presenter.isBluetoothManaged();
    holder.imageBluetooth.setEnabled(bluetoothManaged);
    holder.imageBluetooth.setImageResource(bluetoothManaged ? R.drawable.ic_bluetooth_white_24dp
        : R.drawable.ic_bluetooth_disabled_white_24dp);

    final boolean syncManaged = presenter.isSyncManaged();
    holder.imageSync.setEnabled(syncManaged);
    holder.imageSync.setImageResource(
        syncManaged ? R.drawable.ic_sync_white_24dp : R.drawable.ic_sync_disabled_white_24dp);

    holder.textWifi.setText(String.format("%d", presenter.getWifiDelay() / 1000));
    holder.textData.setText(String.format("%d", presenter.getDataDelay() / 1000));
    holder.textBluetooth.setText(String.format("%d", presenter.getBluetoothDelay() / 1000));
    holder.textSync.setText(String.format("%d", presenter.getSyncDelay() / 1000));

    holder.boot.setChecked(presenter.isBootEnabled());
    holder.suspend.setChecked(presenter.isSuspendEnabled());

    final boolean wifiReopen = presenter.isWifiReOpenEnabled();
    holder.imageReOpenWifi.setEnabled(wifiReopen);
    holder.imageReOpenWifi.setImageResource(wifiReopen ? R.drawable.ic_network_wifi_white_24dp
        : R.drawable.ic_signal_wifi_off_white_24dp);

    final boolean dataReopen = presenter.isDataReOpenEnabled();
    holder.imageReOpenData.setEnabled(dataReopen);
    holder.imageReOpenData.setImageResource(dataReopen ? R.drawable.ic_network_cell_white_24dp
        : R.drawable.ic_signal_cellular_off_white_24dp);

    final boolean bluetoothReopen = presenter.isBluetoothReOpenEnabled();
    holder.imageReOpenBluetooth.setEnabled(bluetoothReopen);
    holder.imageReOpenBluetooth.setImageResource(
        bluetoothReopen ? R.drawable.ic_bluetooth_white_24dp
            : R.drawable.ic_bluetooth_disabled_white_24dp);

    final boolean syncReopen = presenter.isSyncReOpenEnabled();
    holder.imageReOpenSync.setEnabled(syncReopen);
    holder.imageReOpenSync.setImageResource(
        syncReopen ? R.drawable.ic_sync_white_24dp : R.drawable.ic_sync_disabled_white_24dp);

    holder.textReOpenWifi.setText(String.format("%d", presenter.getWifiReOpenTime() / 1000));
    holder.textReOpenData.setText(String.format("%d", presenter.getDataReOpenTime() / 1000));
    holder.textReOpenBluetooth.setText(
        String.format("%d", presenter.getBluetoothReOpenTime() / 1000));
    holder.textReOpenSync.setText(String.format("%d", presenter.getSyncReOpenTime() / 1000));


        /* Need to do this or RecyclerView throws an error about updating while layouts are being
         made */
    holder.select.setOnCheckedChangeListener(null);
    holder.select.setChecked(presenter.isActivePlan());
    holder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

      @Override
      public final void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                /* Set the power plan */
        presenter.setActivePlan();
      }
    });
  }

  @Override public void onSetAsActivePlan() {
    LogUtil.d(TAG, "onSetAsActivePlan");
    notifyDataSetChanged();
  }

  @Override public int getItemCount() {
    return POWER_PLAN_NUMBER;
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    private final ImageView image;
    private final TextView name;
    private final RadioButton select;
    private final ImageView expandArrow;

    private final ImageView imageWifi;
    private final TextView textWifi;
    private final ImageView imageData;
    private final TextView textData;
    private final ImageView imageBluetooth;
    private final TextView textBluetooth;
    private final ImageView imageSync;
    private final TextView textSync;

    private final CheckedTextView boot;
    private final CheckedTextView suspend;

    private final ImageView imageReOpenWifi;
    private final TextView textReOpenWifi;
    private final ImageView imageReOpenData;
    private final TextView textReOpenData;
    private final ImageView imageReOpenBluetooth;
    private final TextView textReOpenBluetooth;
    private final ImageView imageReOpenSync;
    private final TextView textReOpenSync;

    private final LinearLayout layout;
    private final LinearLayout arrowHolder;

    public ViewHolder(final View itemView) {
      super(itemView);
      layout = (LinearLayout) itemView.findViewById(R.id.card_layout_power_plan);
      arrowHolder = (LinearLayout) itemView.findViewById(R.id.card_expand_arrow_holder);

      image = (ImageView) itemView.findViewById(R.id.card_image);
      name = (TextView) itemView.findViewById(R.id.card_name);
      select = (RadioButton) itemView.findViewById(R.id.card_select_plan);
      expandArrow = (ImageView) itemView.findViewById(R.id.card_expand_arrow);

      imageWifi = (ImageView) itemView.findViewById(R.id.power_plan_manage_wifi_image);
      textWifi = (TextView) itemView.findViewById(R.id.power_plan_manage_wifi_text);
      imageData = (ImageView) itemView.findViewById(R.id.power_plan_manage_data_image);
      textData = (TextView) itemView.findViewById(R.id.power_plan_manage_data_text);
      imageBluetooth = (ImageView) itemView.findViewById(R.id.power_plan_manage_bluetooth_image);
      textBluetooth = (TextView) itemView.findViewById(R.id.power_plan_manage_bluetooth_text);
      imageSync = (ImageView) itemView.findViewById(R.id.power_plan_manage_sync_image);
      textSync = (TextView) itemView.findViewById(R.id.power_plan_manage_sync_text);

      boot = (CheckedTextView) itemView.findViewById(R.id.power_plan_misc_boot);
      suspend = (CheckedTextView) itemView.findViewById(R.id.power_plan_misc_suspend);

      imageReOpenWifi = (ImageView) itemView.findViewById(R.id.power_plan_reopen_wifi_image);
      textReOpenWifi = (TextView) itemView.findViewById(R.id.power_plan_reopen_wifi_text);
      imageReOpenData = (ImageView) itemView.findViewById(R.id.power_plan_reopene_data_image);
      textReOpenData = (TextView) itemView.findViewById(R.id.power_plan_reopen_data_text);
      imageReOpenBluetooth =
          (ImageView) itemView.findViewById(R.id.power_plan_reopen_bluetooth_image);
      textReOpenBluetooth = (TextView) itemView.findViewById(R.id.power_plan_reopen_bluetooth_text);
      imageReOpenSync = (ImageView) itemView.findViewById(R.id.power_plan_reopen_sync_image);
      textReOpenSync = (TextView) itemView.findViewById(R.id.power_plan_reopen_sync_text);
    }
  }
}
