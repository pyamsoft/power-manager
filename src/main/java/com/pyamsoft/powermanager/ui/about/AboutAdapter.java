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

package com.pyamsoft.powermanager.ui.about;

import android.content.Context;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.pyamsoft.powermanager.BuildConfig;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.ui.BindableRecyclerAdapter;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import com.pyamsoft.pydroid.util.StringUtil;
import java.util.HashSet;
import java.util.Set;

public final class AboutAdapter extends BindableRecyclerAdapter<AboutAdapter.ViewHolder>
    implements AboutInterface {

  private static final int NUMBER_ABOUT_ITEMS = 5;
  private static final int VIEW_TYPE_TEXT = 0;
  private static final int VIEW_TYPE_BUTTON = 1;
  private static final int POSITION_APP_INTENT = 0;
  private static final int POSITION_APP_VERSION = 1;
  private static final int POSITION_APP_BUILD = 2;
  private static final int POSITION_APP_DATE = 3;
  private static final int POSITION_LICENSES = 4;
  private static final float SCALE_Y = 1.0F;
  private static final String TAG = AboutAdapter.class.getSimpleName();
  private static final String VERSION_CODE = "Version Code: " + BuildConfig.VERSION_NAME;
  private static final String BUILD_CODE = "Build Code: " + BuildConfig.VERSION_CODE;
  private final Set<Integer> expandedPositions = new HashSet<>(NUMBER_ABOUT_ITEMS);
  private final Context context;
  private final String dateCode;
  private final AboutPresenter presenter;

  public AboutAdapter(final Context context) {
    dateCode = "Build Date: " + context.getString(R.string.app_date);
    this.context = context;
    presenter = new AboutPresenter();
  }

  @Override protected void onBind() {
    presenter.bind(context, this);
  }

  @Override protected void onUnbind() {
    presenter.unbind();
  }

  @Override public int getItemViewType(final int position) {
    int viewType;
    switch (position) {
      case POSITION_APP_INTENT:
        viewType = VIEW_TYPE_BUTTON;
        break;
      default:
        viewType = VIEW_TYPE_TEXT;
        break;
    }
    return viewType;
  }

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

        int animTime;
        if (position == POSITION_LICENSES) {
          animTime = 1000;
        } else {
          animTime = -1;
        }

        if (!isCurrentlyExpanded) {
          // going to expand
          LogUtil.d(TAG, "Fade in layout");
          AnimUtil.expand(holder.expandLayout, animTime);
        } else {
          // going to contract
          LogUtil.d(TAG, "Fade out layout");
          AnimUtil.collapse(holder.expandLayout, animTime);
        }
      }
    });

    inflateContentText(holder, position);

    String title;
    Spannable content;

    final Context context = holder.itemView.getContext();
    switch (position) {
      case POSITION_APP_INTENT:
        title = context.getString(R.string.about_app_info);
        content = StringUtil.createBuilder(context.getString(R.string.about_click_to_view));
        break;
      case POSITION_APP_VERSION:
        title = context.getString(R.string.about_app_version);
        content = StringUtil.createBuilder(VERSION_CODE);
        break;
      case POSITION_APP_BUILD:
        title = context.getString(R.string.about_app_build);
        content = StringUtil.createBuilder(BUILD_CODE);
        break;
      case POSITION_APP_DATE:
        title = context.getString(R.string.about_app_date);
        content = StringUtil.createBuilder(dateCode);
        break;
      case POSITION_LICENSES:
        title = context.getString(R.string.open_source_licenses);
        final String picasso = "Picasso" + "\n\n";
        final String picassoLicense = context.getString(R.string.picasso_license) + "\n\n";

        final String androidSupport = "Android Support Libraries" + "\n\n";
        final String androidSupportLicense =
            context.getString(R.string.android_support_license) + "\n\n";

        final String pydroid = "PYDroid" + "\n\n";
        final String pydroidLicense = context.getString(R.string.pydroid_license) + "\n\n";

        content =
            StringUtil.createBuilder(picasso, picassoLicense, androidSupport, androidSupportLicense,
                pydroid, pydroidLicense);

        final int mediumSize =
            StringUtil.getTextSizeFromAppearance(context, android.R.attr.textAppearanceMedium);
        final int mediumColor =
            StringUtil.getTextColorFromAppearance(context, android.R.attr.textAppearanceMedium);

        if (mediumSize != -1 && mediumColor != -1) {
          int start = 0;
          int end = picasso.length();
          StringUtil.sizeSpan(content, start, end, mediumSize);
          StringUtil.colorSpan(content, start, end, mediumColor);

          start = end + picassoLicense.length();
          end = start + androidSupport.length();
          StringUtil.sizeSpan(content, start, end, mediumSize);
          StringUtil.colorSpan(content, start, end, mediumColor);

          start = end + androidSupportLicense.length();
          end = start + pydroid.length();
          StringUtil.sizeSpan(content, start, end, mediumSize);
          StringUtil.colorSpan(content, start, end, mediumColor);
        }
        break;
      default:
        title = null;
        content = null;
    }

    if (title != null) {
      holder.title.setText(title);
    }
    if (content != null) {
      if (holder.textContent != null) {
        holder.textContent.setText(content);
      } else if (holder.buttonContent != null) {
        holder.buttonContent.setText(content);
        holder.buttonContent.setOnClickListener(new View.OnClickListener() {

          @Override public void onClick(final View v) {
            presenter.onClickDetailButton();
          }
        });
      }
    }
  }

  private void inflateContentText(final ViewHolder holder, final int position) {
    if (holder.itemView instanceof ViewGroup) {
      holder.expandLayout.removeAllViews();
      final LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
      final ViewGroup vg = (ViewGroup) holder.itemView;
      final int viewType = getItemViewType(position);
      switch (viewType) {
        case VIEW_TYPE_BUTTON:
          final Button button = (Button) inflater.inflate(R.layout.layout_button_single, vg, false);
          holder.expandLayout.addView(button);
          holder.buttonContent = button;
          holder.textContent = null;
          break;
        default:
          final TextView text =
              (TextView) inflater.inflate(R.layout.layout_textview_single, vg, false);
          holder.expandLayout.addView(text);
          holder.buttonContent = null;
          holder.textContent = text;
      }
    }
  }

  @Override public int getItemCount() {
    return NUMBER_ABOUT_ITEMS;
  }

  @Override public void onDetailActivityLaunched() {

  }

  @Override public int getStatusbarColor() {
    return R.color.orange700;
  }

  @Override public int getToolbarColor() {
    return R.color.orange500;
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    private final TextView title;
    private final ViewGroup expandLayout;
    private final ImageView arrow;
    private TextView textContent;
    private Button buttonContent;

    public ViewHolder(final View itemView) {
      super(itemView);
      title = (TextView) itemView.findViewById(R.id.item_title);
      expandLayout = (ViewGroup) itemView.findViewById(R.id.item_expand);
      arrow = (ImageView) itemView.findViewById(R.id.item_expand_arrow);
    }
  }
}
