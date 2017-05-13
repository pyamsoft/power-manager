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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemExceptionsBinding;
import com.pyamsoft.powermanager.databinding.LayoutContainerExceptionChargingBinding;
import com.pyamsoft.powermanager.databinding.LayoutContainerExceptionWearBinding;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class ExceptionItem extends BaseItem<ExceptionItem, ExceptionItem.ViewHolder> {

  @NonNull static final String TAG = "ExceptionItem";
  @Inject @Named("exception_wifi") ExceptionPresenter presenterWifi;
  @Inject @Named("exception_data") ExceptionPresenter presenterData;
  @Inject @Named("exception_bluetooth") ExceptionPresenter presenterBluetooth;
  @Inject @Named("exception_sync") ExceptionPresenter presenterSync;
  @Inject @Named("exception_airplane") ExceptionPresenter presenterAirplane;
  @Inject @Named("exception_doze") ExceptionPresenter presenterDoze;

  ExceptionItem() {
    super(TAG);
    Injector.get().provideComponent().plusManageComponent().inject(this);
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  @Override public int getType() {
    return R.id.adapter_exception_card_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_exceptions;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.binding.exceptionChargingContainer.setTitleTextSize(18);
    holder.binding.exceptionChargingContainer.setTitle(R.string.charging);
    holder.binding.exceptionChargingContainer.setDescription(null);
    holder.binding.exceptionChargingContainer.setExpandingContent(
        holder.chargingContainerBinding.getRoot());

    holder.binding.exceptionWearContainer.setTitleTextSize(18);
    holder.binding.exceptionWearContainer.setTitle(R.string.connected_to_android_wear);
    holder.binding.exceptionWearContainer.setDescription(null);
    holder.binding.exceptionWearContainer.setExpandingContent(
        holder.wearContainerBinding.getRoot());

    bind(holder.chargingContainerBinding.exceptionChargingWifi,
        holder.wearContainerBinding.exceptionWearWifi, "Wifi", presenterWifi);
    bind(holder.chargingContainerBinding.exceptionChargingData,
        holder.wearContainerBinding.exceptionWearData, "Data", presenterData);
    bind(holder.chargingContainerBinding.exceptionChargingBluetooth,
        holder.wearContainerBinding.exceptionWearBluetooth, "Bluetooth", presenterBluetooth);
    bind(holder.chargingContainerBinding.exceptionChargingSync,
        holder.wearContainerBinding.exceptionWearSync, "Sync", presenterSync);
    bind(holder.chargingContainerBinding.exceptionChargingAirplane,
        holder.wearContainerBinding.exceptionWearAirplane, "Airplane", presenterAirplane);
    bind(holder.chargingContainerBinding.exceptionChargingDoze,
        holder.wearContainerBinding.exceptionWearDoze, "Doze", presenterDoze);
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    unbind(holder.chargingContainerBinding.exceptionChargingAirplane,
        holder.wearContainerBinding.exceptionWearAirplane, presenterAirplane);
    unbind(holder.chargingContainerBinding.exceptionChargingWifi,
        holder.wearContainerBinding.exceptionWearWifi, presenterWifi);
    unbind(holder.chargingContainerBinding.exceptionChargingData,
        holder.wearContainerBinding.exceptionWearData, presenterData);
    unbind(holder.chargingContainerBinding.exceptionChargingBluetooth,
        holder.wearContainerBinding.exceptionWearBluetooth, presenterBluetooth);
    unbind(holder.chargingContainerBinding.exceptionChargingSync,
        holder.wearContainerBinding.exceptionWearSync, presenterSync);
    unbind(holder.chargingContainerBinding.exceptionChargingDoze,
        holder.wearContainerBinding.exceptionWearDoze, presenterDoze);
    holder.wearContainerBinding.unbind();
    holder.chargingContainerBinding.unbind();
    holder.binding.unbind();
  }

  private void bind(@NonNull CheckBox charging, @NonNull CheckBox wear, @NonNull String name,
      @NonNull ExceptionPresenter presenter) {
    bindChargingCheck(charging, name, presenter);
    bindWearCheck(wear, name, presenter);

    presenter.registerOnBus(() -> {
      getIgnoreCharging(presenter, charging, name);
      getIgnoreWear(presenter, wear, name);
    });
  }

  private void bindChargingCheck(@NonNull CheckBox checkBox, @NonNull String name,
      @NonNull ExceptionPresenter presenter) {
    // Set enabled in case it failed last time
    checkBox.setEnabled(true);

    // Set title
    checkBox.setText("Do not manage " + name);

    // Get current state
    getIgnoreCharging(presenter, checkBox, name);
  }

  @SuppressWarnings("WeakerAccess") void getIgnoreCharging(@NonNull ExceptionPresenter presenter,
      @NonNull CheckBox checkBox, @NonNull String name) {
    presenter.getIgnoreCharging(new ExceptionPresenter.RetrieveCallback() {

      @Override public void onEnableRetrieved(boolean enabled) {
        checkBox.setEnabled(enabled);
      }

      @Override public void onStateRetrieved(boolean enabled) {
        // Make sure we don't trigger anything
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(enabled);
      }

      @Override public void onError(@NonNull Throwable throwable) {
        Toast.makeText(checkBox.getContext(), "Failed to retrieve state: " + name,
            Toast.LENGTH_SHORT).show();

        // Mark switch as disabled
        checkBox.setEnabled(false);
      }

      @Override public void onComplete() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // Make sure we don't trigger anything
            buttonView.setOnCheckedChangeListener(null);

            // Update backing
            final CompoundButton.OnCheckedChangeListener listener = this;
            presenter.setIgnoreCharging(isChecked, new ExceptionPresenter.ActionCallback() {
              @Override public void onError(@NonNull Throwable throwable) {
                Toast.makeText(checkBox.getContext(), "Failed to set state: " + name,
                    Toast.LENGTH_SHORT).show();

                // Roll back
                buttonView.setChecked(!isChecked);
              }

              @Override public void onComplete() {
                // Re-apply listener
                buttonView.setOnCheckedChangeListener(listener);
              }
            });
          }
        });
      }
    });
  }

  private void bindWearCheck(@NonNull CheckBox checkBox, @NonNull String name,
      @NonNull ExceptionPresenter presenter) {
    // Set enabled in case it failed last time
    checkBox.setEnabled(true);

    // Set title
    checkBox.setText("Do not manage " + name);

    // Get current state
    getIgnoreWear(presenter, checkBox, name);
  }

  @SuppressWarnings("WeakerAccess") void getIgnoreWear(@NonNull ExceptionPresenter presenter,
      @NonNull CheckBox checkBox, @NonNull String name) {
    presenter.getIgnoreWear(new ExceptionPresenter.RetrieveCallback() {

      @Override public void onEnableRetrieved(boolean enabled) {
        checkBox.setEnabled(enabled);
      }

      @Override public void onStateRetrieved(boolean enabled) {
        // Make sure we don't trigger anything
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(enabled);
      }

      @Override public void onError(@NonNull Throwable throwable) {
        Toast.makeText(checkBox.getContext(), "Failed to retrieve state: " + name,
            Toast.LENGTH_SHORT).show();

        // Mark switch as disabled
        checkBox.setEnabled(false);
      }

      @Override public void onComplete() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // Make sure we don't trigger anything
            buttonView.setOnCheckedChangeListener(null);

            // Update backing
            final CompoundButton.OnCheckedChangeListener listener = this;
            presenter.setIgnoreWear(isChecked, new ExceptionPresenter.ActionCallback() {
              @Override public void onError(@NonNull Throwable throwable) {
                Toast.makeText(checkBox.getContext(), "Failed to set state: " + name,
                    Toast.LENGTH_SHORT).show();

                // Roll back
                buttonView.setChecked(!isChecked);
              }

              @Override public void onComplete() {
                // Re-apply listener
                buttonView.setOnCheckedChangeListener(listener);
              }
            });
          }
        });
      }
    });
  }

  private void unbind(@NonNull CheckBox chargeCheckbox, @NonNull CheckBox wearCheckBox,
      @NonNull ExceptionPresenter presenter) {
    chargeCheckbox.setText(null);
    chargeCheckbox.setOnCheckedChangeListener(null);
    wearCheckBox.setText(null);
    wearCheckBox.setOnCheckedChangeListener(null);
    presenter.stop();
    presenter.destroy();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemExceptionsBinding binding;
    @NonNull final LayoutContainerExceptionChargingBinding chargingContainerBinding;
    @NonNull final LayoutContainerExceptionWearBinding wearContainerBinding;

    ViewHolder(View itemView) {
      super(itemView);
      binding = AdapterItemExceptionsBinding.bind(itemView);
      LayoutInflater layoutInflater = LayoutInflater.from(itemView.getContext());
      View chargingContainer =
          layoutInflater.inflate(R.layout.layout_container_exception_charging, (ViewGroup) itemView,
              false);
      chargingContainerBinding = LayoutContainerExceptionChargingBinding.bind(chargingContainer);

      View wearContainer =
          layoutInflater.inflate(R.layout.layout_container_exception_wear, (ViewGroup) itemView,
              false);
      wearContainerBinding = LayoutContainerExceptionWearBinding.bind(wearContainer);
    }
  }
}
