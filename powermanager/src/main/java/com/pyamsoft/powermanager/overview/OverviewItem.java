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

package com.pyamsoft.powermanager.overview;

import android.databinding.DataBindingUtil;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.powermanager.Injector;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.airplane.AirplaneFragment;
import com.pyamsoft.powermanager.bluetooth.BluetoothFragment;
import com.pyamsoft.powermanager.data.DataFragment;
import com.pyamsoft.powermanager.databinding.AdapterItemOverviewBinding;
import com.pyamsoft.powermanager.doze.DozeFragment;
import com.pyamsoft.powermanager.model.OverviewModel;
import com.pyamsoft.powermanager.model.overlord.States;
import com.pyamsoft.powermanager.settings.SettingsPreferenceFragment;
import com.pyamsoft.powermanager.sync.SyncFragment;
import com.pyamsoft.powermanager.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.wear.WearFragment;
import com.pyamsoft.powermanager.wifi.WifiFragment;
import com.pyamsoft.pydroid.drawable.AsyncDrawable;
import com.pyamsoft.pydroid.drawable.AsyncMap;
import com.pyamsoft.pydroid.drawable.AsyncMapEntry;
import com.pyamsoft.pydroid.helper.AsyncMapHelper;
import java.util.List;
import javax.inject.Inject;

public class OverviewItem
    extends GenericAbstractItem<OverviewModel, OverviewItem, OverviewItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

  OverviewItem(@NonNull View rootView, @NonNull String title, @DrawableRes int image,
      @ColorRes int background, @NonNull States states) {
    super(OverviewModel.builder()
        .background(background)
        .image(image)
        .state(states)
        .rootView(rootView)
        .title(title)
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
      case SettingsPreferenceFragment.TAG:
        fragment = SettingsPreferenceFragment.newInstance(view, rootView);
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

    @NonNull final AdapterItemOverviewBinding binding;
    @Inject OverviewItemPresenter presenter;
    @NonNull AsyncMapEntry checkTask = AsyncMap.emptyEntry();
    @NonNull private AsyncMapEntry titleTask = AsyncMap.emptyEntry();

    ViewHolder(View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
      Injector.get().provideComponent().plusOverviewComponent().inject(this);
    }

    @NonNull @CheckResult AdapterItemOverviewBinding getBinding() {
      return binding;
    }

    void bind(@NonNull OverviewModel model) {
      presenter.bindView(null);

      binding.adapterItemOverviewColor.setBackgroundColor(
          ContextCompat.getColor(itemView.getContext(), model.background()));
      binding.adapterItemOverviewTitle.setText(model.title());

      titleTask = AsyncMapHelper.unsubscribe(titleTask);
      titleTask = AsyncDrawable.load(model.image())
          .tint(android.R.color.white)
          .into(binding.adapterItemOverviewImage);

      presenter.decideManageState(model.state(), new OverviewItemPresenter.ManageStateCallback() {
        @Override public void onManageStateDecided(@DrawableRes int icon) {
          checkTask = AsyncMapHelper.unsubscribe(checkTask);
          checkTask = AsyncDrawable.load(icon)
              .tint(android.R.color.white)
              .into(binding.adapterItemOverviewCheck);
        }

        @Override public void onManageStateNone() {
          binding.adapterItemOverviewCheck.setImageDrawable(null);
        }
      });
    }

    void unbind() {
      checkTask = AsyncMapHelper.unsubscribe(checkTask);
      titleTask = AsyncMapHelper.unsubscribe(titleTask);
      binding.adapterItemOverviewImage.setImageDrawable(null);
      binding.adapterItemOverviewTitle.setText(null);

      presenter.unbindView();
    }
  }
}
