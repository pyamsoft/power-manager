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

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.ui.BindableRecyclerAdapter;
import com.pyamsoft.powermanager.ui.ContainerInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GridContentAdapter extends BindableRecyclerAdapter<GridContentAdapter.ViewHolder>
    implements GridItemTouchInterface, GridInterface {

  private static final int NUMBER_ITEMS = 10;
  private static final String TAG = GridContentAdapter.class.getSimpleName();

  private final List<String> items;
  private final GridPresenter presenter;
  private final ContainerInterface container;

  public GridContentAdapter(final Context context, final ContainerInterface container) {
    this.container = container;
    presenter = new GridPresenter(context, this);

    items = new ArrayList<>(NUMBER_ITEMS);
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.with(context);
    items.add(preferenceUtil.gridOrder().getOne());
    items.add(preferenceUtil.gridOrder().getTwo());
    items.add(preferenceUtil.gridOrder().getThree());
    items.add(preferenceUtil.gridOrder().getFour());
    items.add(preferenceUtil.gridOrder().getFive());
    items.add(preferenceUtil.gridOrder().getSix());
    items.add(preferenceUtil.gridOrder().getSeven());
    items.add(preferenceUtil.gridOrder().getEight());
    items.add(preferenceUtil.gridOrder().getNine());
    items.add(preferenceUtil.gridOrder().getTen());

    create();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /* Use the activity context to keep app themes */
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View view = inflater.inflate(R.layout.adapter_item_gridentry, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    final String name = items.get(position);
    holder.name.setText(name);

    int image;
    int background;
    switch (name) {
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
        image = R.drawable.ic_help_outline_compat;
        background = R.color.green500;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
        image = R.drawable.ic_help_outline_compat;
        background = R.color.teal500;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
        image = R.drawable.ic_bluetooth_compat;
        background = R.color.blue500;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
        image = R.drawable.ic_help_outline_compat;
        background = R.color.purple500;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_PLAN:
        image = R.drawable.ic_help_outline_compat;
        background = R.color.red500;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_TRIGGER:
        image = R.drawable.ic_help_outline_compat;
        background = R.color.yellow500;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BATTERY_INFO:
        image = R.drawable.ic_help_outline_compat;
        background = R.color.pink500;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SETTINGS:
        image = R.drawable.ic_help_outline_compat;
        background = R.color.lightgreen500;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_HELP:
        image = R.drawable.ic_help_outline_compat;
        background = R.color.cyan500;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_ABOUT:
        image = R.drawable.ic_help_outline_compat;
        background = R.color.orange500;
        break;
      default:
        image = 0;
        background = 0;
    }

    if (image != 0) {
      holder.image.setImageResource(image);
      holder.background.setBackgroundColor(
          ContextCompat.getColor(holder.background.getContext(), background));

      final int javaPlease = image;
      final int whyJavaWhy = background;
      holder.mainHolder.setOnClickListener(new View.OnClickListener() {

        @Override public void onClick(View v) {
          presenter.clickGridItem(name, javaPlease, whyJavaWhy);
        }
      });
    }
  }

  @Override public int getItemCount() {
    return items.size();
  }

  @Override
  public boolean onMoveItem(final Context context, final int fromPosition, final int toPosition) {
    Collections.swap(items, fromPosition, toPosition);
    final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.with(context);
    preferenceUtil.gridOrder().set(toPosition, items.get(toPosition));
    preferenceUtil.gridOrder().set(fromPosition, items.get(fromPosition));

    // KLUDGE
    // This is buggy and creates duplicated views as a result.
    //        notifyItemMoved(fromPosition, toPosition);
    // KLUDGE
    // This halts the move after it exchanges one place
    // Maybe this is what we want?
    notifyDataSetChanged();

    return true;
  }

  @Override public void onFABClicked() {
    // No FAB here
    throw new RuntimeException("No Fab Here");
  }

  @Override public void onGridItemClicked(String viewCode, int image, int background) {
    container.setCurrentView(viewCode, image, background);
  }

  @Override public int getStatusbarColor() {
    return R.color.amber700;
  }

  @Override public int getToolbarColor() {
    return R.color.amber500;
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    public final LinearLayout mainHolder;
    public final ImageView image;
    public final View background;
    public final TextView name;

    public ViewHolder(final View itemView) {
      super(itemView);
      mainHolder = (LinearLayout) itemView.findViewById(R.id.card_main_holder);
      name = (TextView) itemView.findViewById(R.id.card_name);
      image = (ImageView) itemView.findViewById(R.id.card_image);
      background = itemView.findViewById(R.id.card_image_background);
    }
  }
}

