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

package com.pyamsoft.powermanager.ui.grid;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.pyamsoft.powermanager.ui.RecyclerItemTouchInterface;

public final class GridItemTouchCallback extends ItemTouchHelper.SimpleCallback {

  private final RecyclerItemTouchInterface touchInterface;

  public GridItemTouchCallback(final RecyclerItemTouchInterface touchInterface) {
    super(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT |
        ItemTouchHelper.RIGHT, 0);
    this.touchInterface = touchInterface;
  }

  @Override
  public boolean onMove(final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
      final RecyclerView.ViewHolder target) {
    return touchInterface.onMoveItem(recyclerView.getContext(), viewHolder.getAdapterPosition(),
        target.getAdapterPosition());
  }

  @Override public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int direction) {
  }
}
