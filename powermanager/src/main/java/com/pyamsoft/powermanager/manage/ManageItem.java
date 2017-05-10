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
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemManageBinding;
import com.pyamsoft.powermanager.model.States;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class ManageItem extends BaseItem<ManageItem, ManageItem.ViewHolder> {

  @NonNull static final String TAG = "ManageItem";
  @Inject @Named("manage_wifi") ManagePresenter presenterWifi;
  @Inject @Named("manage_data") ManagePresenter presenterData;
  @Inject @Named("manage_bluetooth") ManagePresenter presenterBluetooth;
  @Inject @Named("manage_sync") ManagePresenter presenterSync;
  @Inject @Named("manage_airplane") ManagePresenter presenterAirplane;
  @Inject @Named("manage_doze") ManagePresenter presenterDoze;

  ManageItem() {
    super(TAG);
    Injector.get().provideComponent().plusManageComponent().inject(this);
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  @Override public int getType() {
    return R.id.adapter_manage_card_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_manage;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    bindSwitch(holder.binding.manageWifi, "WiFi", presenterWifi);
    bindSwitch(holder.binding.manageData, "Cellular Data", presenterData);
    bindSwitch(holder.binding.manageBluetooth, "Bluetooth", presenterBluetooth);
    bindSwitch(holder.binding.manageSync, "Auto Sync", presenterSync);
    bindSwitch(holder.binding.manageAirplane, "Airplane Mode", presenterAirplane);
    bindSwitch(holder.binding.manageDoze, "Doze Mode", presenterDoze);
  }

  private void bindSwitch(@NonNull SwitchCompat switchCompat, @NonNull String name,
      @NonNull ManagePresenter presenter) {
    // Set enabled in case it failed last time
    switchCompat.setEnabled(true);

    // Set title
    switchCompat.setText(name);

    // Get current state
    presenter.getState(new ManagePresenter.RetrieveCallback() {
      @Override public void onRetrieved(@NonNull States states) {
        // Make sure we don't trigger anything
        switchCompat.setOnCheckedChangeListener(null);

        if (states == States.UNKNOWN) {
          // We do not know, disable this UI
          switchCompat.setEnabled(false);
        } else {
          switchCompat.setChecked(states == States.ENABLED);
        }
      }

      @Override public void onError(@NonNull Throwable throwable) {
        Toast.makeText(switchCompat.getContext(), "Failed to retrieve state: " + name,
            Toast.LENGTH_SHORT).show();

        // Mark switch as disabled
        switchCompat.setEnabled(false);
      }

      @Override public void onComplete() {
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // Make sure we don't trigger anything
            buttonView.setOnCheckedChangeListener(null);

            // Save this listener for later
            final CompoundButton.OnCheckedChangeListener listener = this;
            ManagePresenter.ActionCallback callback = new ManagePresenter.ActionCallback() {
              @Override public void onError(@NonNull Throwable throwable) {
                Toast.makeText(switchCompat.getContext(), "Failed to set state: " + name,
                    Toast.LENGTH_SHORT).show();

                // Roll back
                buttonView.setChecked(!isChecked);
              }

              @Override public void onComplete() {
                // Re-apply listener
                buttonView.setOnCheckedChangeListener(listener);
              }
            };

            // Update backing
            presenter.setManaged(isChecked, callback);
          }
        });
      }
    });
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    unbindSwitch(holder.binding.manageWifi, presenterWifi);
    unbindSwitch(holder.binding.manageData, presenterData);
    unbindSwitch(holder.binding.manageBluetooth, presenterBluetooth);
    unbindSwitch(holder.binding.manageSync, presenterSync);
    unbindSwitch(holder.binding.manageAirplane, presenterAirplane);
    unbindSwitch(holder.binding.manageDoze, presenterDoze);
    holder.binding.unbind();
  }

  private void unbindSwitch(@NonNull SwitchCompat switchCompat,
      @NonNull ManagePresenter presenter) {
    switchCompat.setText(null);
    switchCompat.setOnCheckedChangeListener(null);
    presenter.stop();
    presenter.destroy();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemManageBinding binding;

    ViewHolder(View itemView) {
      super(itemView);
      binding = AdapterItemManageBinding.bind(itemView);
    }
  }
}
