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

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.ui.detail.DetailBaseFragment;
import com.pyamsoft.pydroid.base.PresenterBase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GridContentAdapter extends RecyclerView.Adapter<GridContentAdapter.ViewHolder>
    implements GridItemTouchInterface, GridInterface {

  private static final int NUMBER_ITEMS = 10;
  private static final String TAG = GridContentAdapter.class.getSimpleName();

  private final List<String> items;
  //private final FragmentManager fm;
  private GridPresenter presenter;

  public GridContentAdapter(final Context context) {
    //this.fm = f.getFragmentManager();
    presenter = new GridPresenter();
    presenter.bind(context, this);

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
  }

  public void destroy() {
    if (presenter != null) {
      presenter.unbind();
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /* Use the activity context to keep app themes */
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View view = inflater.inflate(R.layout.adapter_item_gridentry, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, int position) {
    final String name = items.get(position);
    holder.name.setText(name);

    int image;
    switch (name) {
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_WIFI:
        image = R.drawable.hero_wifi;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_DATA:
        image = R.drawable.hero_cell;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BLUETOOTH:
        image = R.drawable.hero_bluetooth;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SYNC:
        image = R.drawable.hero_sync;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_PLAN:
        image = R.drawable.hero_plan;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_POWER_TRIGGER:
        image = R.drawable.hero_trigger;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_BATTERY_INFO:
        image = R.drawable.hero_batteryinfo;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_SETTINGS:
        image = R.drawable.hero_settings;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_HELP:
        image = R.drawable.hero_help;
        break;
      case GlobalPreferenceUtil.GridOrder.VIEW_POSITION_ABOUT:
        image = R.drawable.hero_about;
        break;
      default:
        image = 0;
    }

    if (image != 0) {
      Picasso.with(holder.image.getContext()).load(image).into(holder);

      // KLUDGE because java.
      final int javaPlease = image;
      holder.mainHolder.setOnClickListener(new View.OnClickListener() {

        @Override public void onClick(View v) {
          final Bundle detailArgs = new Bundle();
          detailArgs.putString(DetailBaseFragment.EXTRA_PARAM_ID, name);
          detailArgs.putInt(DetailBaseFragment.EXTRA_PARAM_IMAGE, javaPlease);

          final DetailBaseFragment detailFragment = new DetailBaseFragment();
          detailFragment.setArguments(detailArgs);
          // TODO
          //fm.beginTransaction()
          //    .replace(R.id.fragment_place, detailFragment)
          //    .addToBackStack(null)
          //    .commit();
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
    throw new PresenterBase.IllegalBindException("No Fab Here");
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder implements Target {

    private final Handler handler = new Handler();
    public final LinearLayout mainHolder;
    public final ImageView image;
    public final TextView name;

    public ViewHolder(final View itemView) {
      super(itemView);
      mainHolder = (LinearLayout) itemView.findViewById(R.id.card_main_holder);
      name = (TextView) itemView.findViewById(R.id.card_name);
      image = (ImageView) itemView.findViewById(R.id.card_image);
    }

    @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
      if (image != null) {
        final Drawable drawable = new BitmapDrawable(image.getResources(), bitmap);
        drawable.setAlpha(0);
        image.setImageDrawable(drawable);
        handler.post(new Runnable() {
          @Override public void run() {
            final ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "alpha", 255);
            animator.setTarget(drawable);
            animator.setDuration(600L);
            animator.start();
          }
        });
      }
    }

    @Override public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
  }
}

