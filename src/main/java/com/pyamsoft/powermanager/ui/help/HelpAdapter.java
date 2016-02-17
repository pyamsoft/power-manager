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

package com.pyamsoft.powermanager.ui.help;

import android.content.Context;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.ui.BindableRecyclerAdapter;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import java.util.HashSet;
import java.util.Set;

public final class HelpAdapter extends BindableRecyclerAdapter<HelpAdapter.ViewHolder> {

  private static final int POSITION_WHAT_IS = 0;
  private static final int POSITION_HOW_TO = 1;
  private static final int POSITION_POWER_PLAN = 2;
  private static final int POSITION_POWER_TRIGGER = 3;
  private static final int POSITION_PERMISSIONS = 4;
  private static final int POSITION_HELP_OUT = 5;
  private static final int NUMBER_HELP_ITEMS = 6;
  private static final float SCALE_Y = 1.0F;
  private static final String TAG = HelpAdapter.class.getSimpleName();
  private final Set<Integer> expandedPositions = new HashSet<>(NUMBER_HELP_ITEMS);

  @Override public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View v = inflater.inflate(R.layout.adapter_item_help_about, parent, false);
    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    if (!expandedPositions.contains(position)) {
      holder.expandLayout.setVisibility(View.GONE);
    } else {
      holder.expandLayout.setVisibility(View.VISIBLE);
    }

    holder.arrow.setOnClickListener(null);
    holder.arrow.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(final View v) {
        final boolean isCurrentlyExpanded = expandedPositions.contains(position);
        if (!isCurrentlyExpanded) {
          expandedPositions.add(position);
        } else {
          expandedPositions.remove(position);
        }
        // flip the arrow
        AnimUtil.flipVertical(holder.arrow).setListener(new ViewPropertyAnimatorListener() {

          @Override public void onAnimationStart(final View view) {

          }

          @Override public void onAnimationEnd(final View view) {
          }

          @Override public void onAnimationCancel(final View view) {
            if (expandedPositions.contains(position)) {
              LogUtil.d(TAG, "Is expanded, set to flipped");
              holder.arrow.setScaleY(-SCALE_Y);
            } else {
              LogUtil.d(TAG, "Is not expanded, set to normal");
              holder.arrow.setScaleY(SCALE_Y);
            }
          }
        }).start();
        if (!isCurrentlyExpanded) {
          // going to expand
          LogUtil.d(TAG, "Fade in layout");
          AnimUtil.expand(holder.expandLayout);
        } else {
          // going to contract
          LogUtil.d(TAG, "Fade out layout");
          AnimUtil.collapse(holder.expandLayout);
        }
      }
    });

    String title;
    String content;

    final Context context = holder.itemView.getContext();
    switch (position) {
      case POSITION_WHAT_IS:
        title = context.getString(R.string.title_what_is);
        content = context.getString(R.string.content_what_is);
        break;
      case POSITION_HOW_TO:
        title = context.getString(R.string.title_how_use);
        content = context.getString(R.string.content_how_use);
        break;
      case POSITION_POWER_PLAN:
        title = context.getString(R.string.title_what_are_plans);
        content = context.getString(R.string.content_what_are_plans);
        break;
      case POSITION_POWER_TRIGGER:
        title = context.getString(R.string.title_what_are_triggers);
        content = context.getString(R.string.content_what_are_triggers);
        break;
      case POSITION_PERMISSIONS:
        title = context.getString(R.string.title_why_permissions);
        content = context.getString(R.string.content_why_permissions);
        break;
      case POSITION_HELP_OUT:
        title = context.getString(R.string.title_help_out);
        content = context.getString(R.string.content_help_out);

        break;
      default:
        title = null;
        content = null;
    }

    if (title != null) {
      holder.title.setText(title);
    }
    if (holder.content != null && content != null) {
      if (position == POSITION_HELP_OUT) {
        holder.content.setText(Html.fromHtml(content));
        holder.content.setMovementMethod(LinkMovementMethod.getInstance());
      } else {
        holder.content.setText(content);
        holder.content.setMovementMethod(null);
      }
    }
  }

  @Override public int getItemCount() {
    return NUMBER_HELP_ITEMS;
  }

  @Override public int getStatusbarColor() {
    return R.color.cyan700;
  }

  @Override public int getToolbarColor() {
    return R.color.cyan500;
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    private final TextView title;
    private final ViewGroup expandLayout;
    private final ImageView arrow;
    private final TextView content;

    public ViewHolder(final View itemView) {
      super(itemView);
      title = (TextView) itemView.findViewById(R.id.item_title);
      expandLayout = (ViewGroup) itemView.findViewById(R.id.item_expand);
      arrow = (ImageView) itemView.findViewById(R.id.item_expand_arrow);
      expandLayout.removeAllViews();
      final LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
      final TextView text =
          (TextView) inflater.inflate(R.layout.layout_textview_single, expandLayout, false);
      expandLayout.addView(text);
      content = text;
    }
  }
}
