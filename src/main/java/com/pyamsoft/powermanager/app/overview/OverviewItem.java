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

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.app.airplane.AirplaneFragment;
import com.pyamsoft.powermanager.app.bluetooth.BluetoothFragment;
import com.pyamsoft.powermanager.app.data.DataFragment;
import com.pyamsoft.powermanager.app.doze.DozeFragment;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import com.pyamsoft.powermanager.app.settings.SettingsFragment;
import com.pyamsoft.powermanager.app.sync.SyncFragment;
import com.pyamsoft.powermanager.app.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.app.wear.WearFragment;
import com.pyamsoft.powermanager.app.wifi.WifiFragment;
import com.pyamsoft.powermanager.databinding.AdapterItemOverviewBinding;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncMap;
import com.pyamsoft.pydroid.tool.AsyncMapHelper;
import java.lang.ref.WeakReference;
import java.util.List;

class OverviewItem
    extends GenericAbstractItem<OverviewModel, OverviewItem, OverviewItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

  OverviewItem(@NonNull View rootView, @NonNull String title, @DrawableRes int image,
      @ColorRes int background, @Nullable BooleanInterestObserver observer) {
    super(OverviewModel.builder()
        .background(background)
        .image(image)
        .rootView(rootView)
        .title(title)
        .observer(observer)
        .build());
  }

  @Override public int getType() {
    return R.id.adapter_overview_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_overview;
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.unbind();
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.bind(getModel());
  }

  void click(@NonNull View view, @NonNull OnOverviewItemClicked itemClicked) {
    final Fragment fragment;
    final View rootView = getModel().rootView();
    final String title = getModel().title();
    switch (title) {
      case WifiFragment.TAG:
        fragment = WifiFragment.newInstance(view, rootView);
        break;
      case DataFragment.TAG:
        fragment = DataFragment.newInstance(view, rootView);
        break;
      case BluetoothFragment.TAG:
        fragment = BluetoothFragment.newInstance(view, rootView);
        break;
      case SyncFragment.TAG:
        fragment = SyncFragment.newInstance(view, rootView);
        break;
      case PowerTriggerFragment.TAG:
        fragment = PowerTriggerFragment.newInstance(view, rootView);
        break;
      case DozeFragment.TAG:
        fragment = DozeFragment.newInstance(view, rootView);
        break;
      case WearFragment.TAG:
        fragment = WearFragment.newInstance(view, rootView);
        break;
      case SettingsFragment.TAG:
        fragment = SettingsFragment.newInstance(view, rootView);
        break;
      case AirplaneFragment.TAG:
        fragment = AirplaneFragment.newInstance(view, rootView);
        break;
      default:
        throw new IllegalStateException("Invalid tag: " + title);
    }

    itemClicked.onItemClicked(title, fragment);
  }

  @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

  interface OnOverviewItemClicked {

    void onItemClicked(@NonNull String title, @NonNull Fragment fragment);
  }

  @SuppressWarnings("WeakerAccess") protected static class ItemFactory
      implements ViewHolderFactory<ViewHolder> {
    @Override public ViewHolder create(View v) {
      return new ViewHolder(v);
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull private final AdapterItemOverviewBinding binding;
    @Nullable private AsyncMap.Entry checkTask;
    @Nullable private AsyncMap.Entry titleTask;
    @NonNull private WeakReference<OverviewModel> weakModel;

    ViewHolder(View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
      weakModel = new WeakReference<>(null);
    }

    @NonNull @CheckResult AdapterItemOverviewBinding getBinding() {
      return binding;
    }

    void bind(@NonNull OverviewModel model) {
      // Tint check mark white
      // Avoids a NoMethod crash on API 19
      binding.adapterItemOverviewColor.setBackgroundColor(
          ContextCompat.getColor(itemView.getContext(), model.background()));
      binding.adapterItemOverviewTitle.setText(model.title());

      weakModel.clear();
      weakModel = new WeakReference<>(model);
    }

    void bind(@NonNull Activity activity) {
      final OverviewModel model = weakModel.get();
      final BooleanInterestObserver observer = model.observer();
      if (observer != null) {
        final int check;
        if (observer.is()) {
          check = R.drawable.ic_check_box_24dp;
        } else {
          check = R.drawable.ic_check_box_outline_24dp;
        }

        AsyncMapHelper.unsubscribe(checkTask);
        checkTask = AsyncDrawable.with(activity)
            .load(check)
            .tint(android.R.color.white)
            .into(binding.adapterItemOverviewCheck);
      } else {
        binding.adapterItemOverviewCheck.setImageDrawable(null);
      }

      AsyncMapHelper.unsubscribe(titleTask);
      titleTask = AsyncDrawable.with(activity)
          .load(model.image())
          .tint(android.R.color.white)
          .into(binding.adapterItemOverviewImage);
    }

    void unbind() {
      AsyncMapHelper.unsubscribe(checkTask, titleTask);
      binding.adapterItemOverviewImage.setImageDrawable(null);
      binding.adapterItemOverviewTitle.setText(null);
      weakModel.clear();
    }
  }
}
