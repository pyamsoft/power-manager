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

package com.pyamsoft.powermanager.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.pydroid.base.ActivityRecyclerAdapterBase;
import com.pyamsoft.pydroid.FABVisibilityController;

public class BindableRecyclerAdapter<T extends RecyclerView.ViewHolder>
    extends ActivityRecyclerAdapterBase<T>
    implements Coloring, FABMiniController, FABController, FABVisibilityController {

  @Override protected void onCreate() {

  }

  @Override protected void onDestroy() {

  }

  @Override protected void onStart() {

  }

  @Override protected void onStop() {

  }

  @Override public T onCreateViewHolder(ViewGroup parent, int viewType) {
    return null;
  }

  @Override public void onBindViewHolder(T holder, int position) {

  }

  @Override public int getItemCount() {
    return 0;
  }

  @Override public int getStatusbarColor() {
    return 0;
  }

  @Override public int getToolbarColor() {
    return 0;
  }

  @Override public int getFABIconEnabled() {
    return 0;
  }

  @Override public int getFABIconDisabled() {
    return 0;
  }

  @Override public boolean isFABEnabled() {
    return false;
  }

  @Override public View.OnClickListener getFABOnClickListener() {
    return null;
  }

  @Override public int getFABMiniIconEnabled() {
    return 0;
  }

  @Override public int getFABMiniIconDisabled() {
    return 0;
  }

  @Override public boolean isFABMiniEnabled() {
    return false;
  }

  @Override public View.OnClickListener getFABMiniOnClickListener() {
    return null;
  }

  @Override public boolean isFABShown(final FloatingActionButton fab) {
    return false;
  }
}
