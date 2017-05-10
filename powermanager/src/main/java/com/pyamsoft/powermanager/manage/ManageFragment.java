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

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.GenericItemAdapter;
import com.pyamsoft.powermanager.databinding.FragmentManageBinding;
import com.pyamsoft.powermanager.uicore.WatchedFragment;

public class ManageFragment extends WatchedFragment {

  @NonNull public static final String TAG = "ManageFragment";
  private FragmentManageBinding binding;

  @CheckResult @NonNull public static ManageFragment newInstance() {
    Bundle args = new Bundle();
    ManageFragment fragment = new ManageFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentManageBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    binding.unbind();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    GenericItemAdapter<String, BaseItem<?, ?>> adapter = new GenericItemAdapter<>(s -> {
      if (ManageItem.TAG.equals(s)) {
        return new ManageItem();
      } else {
        return null;
      }
    });

    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
    manager.setItemPrefetchEnabled(true);
    manager.setInitialPrefetchItemCount(3);
    binding.recycler.setLayoutManager(manager);
    binding.recycler.setClipToPadding(false);
    binding.recycler.setHasFixedSize(true);
    binding.recycler.setAdapter(adapter.wrap(new FastAdapter().withSelectable(true)));

    adapter.add(new ManageItem());
    adapter.add(new ManageItem());
    adapter.add(new ManageItem());
  }
}