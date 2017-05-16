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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.AdapterItemSimpleBinding;
import com.pyamsoft.powermanager.databinding.LayoutContainerDelayBinding;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class DelayItem extends BaseItem<DelayItem, DelayItem.ViewHolder> {

  @NonNull static final String TAG = "DelayItem";
  @SuppressWarnings("WeakerAccess") @Inject DelayPresenter presenter;

  DelayItem() {
    super(TAG);
    Injector.get().provideComponent().inject(this);
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  @Override public int getType() {
    return R.id.adapter_delay_card_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_simple;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);

    final Context context = holder.itemView.getContext();
    presenter.getDelayTime(new DelayPresenter.DelayCallback() {
      @Override public void onCustomDelay(long time) {
        holder.delayBinding.delayRadioGroup.clearCheck();
        selectCustomDelay(holder);
        holder.delayBinding.delayInputCustom.setText(String.valueOf(time));
      }

      @Override public void onPresetDelay(int index, long time) {
        selectPresetDelay(holder);
        holder.delayBinding.delayRadioGroup.check(
            holder.delayBinding.delayRadioGroup.getChildAt(index).getId());
        holder.delayBinding.delayInputCustom.setText(String.valueOf(time));
      }

      @Override public void onError(@NonNull Throwable throwable) {
        Toast.makeText(context, "Error getting delay time", Toast.LENGTH_SHORT).show();
      }

      @Override public void onComplete() {

      }
    });

    holder.delayBinding.delayRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
      if (checkedId == -1) {
        Timber.d("Custom is checked");
        selectCustomDelay(holder);
      } else {
        Timber.d("Preset is checked");
        selectPresetDelay(holder);

        final long time;
        switch (checkedId) {
          case R.id.delay_radio_one:
            time = 5;
            break;
          case R.id.delay_radio_two:
            time = 10;
            break;
          case R.id.delay_radio_three:
            time = 15;
            break;
          case R.id.delay_radio_four:
            time = 30;
            break;
          case R.id.delay_radio_five:
            time = 45;
            break;
          case R.id.delay_radio_six:
            time = 60;
            break;
          case R.id.delay_radio_seven:
            time = 90;
            break;
          case R.id.delay_radio_eight:
            time = 120;
            break;
          default:
            throw new IllegalArgumentException("Could not find RadioButton with id: " + checkedId);
        }
        presenter.setDelayTime(time, throwable -> {
          Toast.makeText(context, "Failed to set delay time", Toast.LENGTH_SHORT).show();
          group.setEnabled(false);
        });
      }
    });

    holder.delayBinding.delayRadioCustom.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        holder.delayBinding.delayRadioGroup.clearCheck();
        selectCustomDelay(holder);
      }
    });

    presenter.listenForDelayTimeChanges(new DelayPresenter.OnDelayChangedCallback() {
      @Override public void onDelayTimeChanged(long time) {
        holder.delayBinding.delayInputCustom.setText(String.valueOf(time));
      }

      @Override public void onError(@NonNull Throwable throwable) {
        Toast.makeText(context, "Error while listening for delay changes", Toast.LENGTH_SHORT)
            .show();
        holder.delayBinding.delayRadioCustom.setEnabled(false);
        holder.delayBinding.delayInputCustom.setEnabled(false);
      }
    });
  }

  @SuppressWarnings("WeakerAccess") void selectPresetDelay(@NonNull ViewHolder holder) {
    holder.delayBinding.delayRadioCustom.setChecked(false);
    holder.delayBinding.delayInputCustom.setEnabled(false);
  }

  @SuppressWarnings("WeakerAccess") void selectCustomDelay(@NonNull ViewHolder holder) {
    holder.delayBinding.delayRadioCustom.setChecked(true);
    holder.delayBinding.delayInputCustom.setEnabled(true);
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.delayBinding.unbind();
    holder.binding.unbind();
  }

  @Override void unbindItem() {
    presenter.stop();
    presenter.destroy();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemSimpleBinding binding;
    @NonNull final LayoutContainerDelayBinding delayBinding;

    ViewHolder(View itemView) {
      super(itemView);
      binding = AdapterItemSimpleBinding.bind(itemView);

      View containerDelay = LayoutInflater.from(itemView.getContext())
          .inflate(R.layout.layout_container_delay, (ViewGroup) itemView, false);
      delayBinding = LayoutContainerDelayBinding.bind(containerDelay);

      binding.simpleExpander.setTitle("Active Delay");
      binding.simpleExpander.setDescription(
          "Power Manager will wait for the specified amount of time before automatically managing certain device functions");
      binding.simpleExpander.setExpandingContent(delayBinding.getRoot());
      delayBinding.delayRadioOne.setText("5 Seconds");
      delayBinding.delayRadioTwo.setText("10 Seconds");
      delayBinding.delayRadioThree.setText("15 Seconds");
      delayBinding.delayRadioFour.setText("30 Seconds");
      delayBinding.delayRadioFive.setText("45 Seconds");
      delayBinding.delayRadioSix.setText("1 Minute");
      delayBinding.delayRadioSeven.setText("1 Minute 30 Seconds");
      delayBinding.delayRadioEight.setText("2 Minutes");
    }
  }
}
