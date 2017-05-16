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
import com.pyamsoft.powermanager.databinding.AdapterItemSimpleBinding;
import com.pyamsoft.powermanager.databinding.LayoutContainerExceptionBinding;
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
    return R.layout.adapter_item_simple;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    bind(holder.containerBinding.exceptionChargingWifi, holder.containerBinding.exceptionWearWifi,
        "Wifi", presenterWifi);
    bind(holder.containerBinding.exceptionChargingData, holder.containerBinding.exceptionWearData,
        "Data", presenterData);
    bind(holder.containerBinding.exceptionChargingBluetooth,
        holder.containerBinding.exceptionWearBluetooth, "Bluetooth", presenterBluetooth);
    bind(holder.containerBinding.exceptionChargingSync, holder.containerBinding.exceptionWearSync,
        "Sync", presenterSync);
    bind(holder.containerBinding.exceptionChargingAirplane,
        holder.containerBinding.exceptionWearAirplane, "Airplane", presenterAirplane);
    bind(holder.containerBinding.exceptionChargingDoze, holder.containerBinding.exceptionWearDoze,
        "Doze", presenterDoze);
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    unbind(holder.containerBinding.exceptionChargingAirplane,
        holder.containerBinding.exceptionWearAirplane);
    unbind(holder.containerBinding.exceptionChargingWifi,
        holder.containerBinding.exceptionWearWifi);
    unbind(holder.containerBinding.exceptionChargingData,
        holder.containerBinding.exceptionWearData);
    unbind(holder.containerBinding.exceptionChargingBluetooth,
        holder.containerBinding.exceptionWearBluetooth);
    unbind(holder.containerBinding.exceptionChargingSync,
        holder.containerBinding.exceptionWearSync);
    unbind(holder.containerBinding.exceptionChargingDoze,
        holder.containerBinding.exceptionWearDoze);
    holder.containerBinding.unbind();
    holder.binding.unbind();
  }

  @Override void unbindItem() {
    presenterAirplane.stop();
    presenterAirplane.destroy();
    presenterWifi.stop();
    presenterWifi.destroy();
    presenterData.stop();
    presenterData.destroy();
    presenterBluetooth.stop();
    presenterBluetooth.destroy();
    presenterSync.stop();
    presenterSync.destroy();
    presenterDoze.stop();
    presenterDoze.destroy();
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

  private void unbind(@NonNull CheckBox chargeCheckbox, @NonNull CheckBox wearCheckBox) {
    chargeCheckbox.setText(null);
    chargeCheckbox.setOnCheckedChangeListener(null);
    wearCheckBox.setText(null);
    wearCheckBox.setOnCheckedChangeListener(null);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemSimpleBinding binding;
    @NonNull final LayoutContainerExceptionBinding containerBinding;

    ViewHolder(View itemView) {
      super(itemView);
      binding = AdapterItemSimpleBinding.bind(itemView);
      LayoutInflater layoutInflater = LayoutInflater.from(itemView.getContext());
      View chargingContainer =
          layoutInflater.inflate(R.layout.layout_container_exception, (ViewGroup) itemView, false);
      containerBinding = LayoutContainerExceptionBinding.bind(chargingContainer);
      binding.simpleExpander.setTitle(R.string.exceptions_title);
      binding.simpleExpander.setDescription(R.string.exceptions_desc);
      binding.simpleExpander.setExpandingContent(containerBinding.getRoot());
    }
  }
}
