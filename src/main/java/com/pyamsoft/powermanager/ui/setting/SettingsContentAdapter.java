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
package com.pyamsoft.powermanager.ui.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.ui.ValueRunnable;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.DrawableUtil;
import com.pyamsoft.pydroid.util.ElevationUtil;
import com.pyamsoft.pydroid.util.StringUtil;
import java.lang.ref.WeakReference;

public final class SettingsContentAdapter
    extends RecyclerView.Adapter<SettingsContentAdapter.ViewHolder> implements SettingsInterface {

  public static final int POSITION_BOOT = 0;
  public static final int POSITION_SUSPEND = 1;
  public static final int POSITION_NOTIFICATION = 2;
  public static final int POSITION_FOREGROUND = 3;
  public static final int POSITION_RESET = 4;
  public static final int NUMBER_ITEMS = 5;
  private static final int TYPE_NORMAL = 0;
  private static final int TYPE_RESET = 1;
  private SettingsPresenter presenter;

  public SettingsContentAdapter(final Context context) {
    presenter = new SettingsPresenter();
    presenter.bind(context, this);
  }

  public void destroy() {
    presenter.unbind();
  }

  @Override public int getItemViewType(int position) {
    int type;
    switch (position) {
      case POSITION_RESET:
        type = TYPE_RESET;
        break;
      default:
        type = TYPE_NORMAL;
    }
    return type;
  }

  @Override public SettingsContentAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
      final int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    int layout;
    switch (viewType) {
      case TYPE_RESET:
        layout = R.layout.adapter_item_resetbutton;
        break;
      default:
        layout = R.layout.adapter_item_simple_card;
    }
    final View view = inflater.inflate(layout, parent, false);
    return new ViewHolder(view, viewType);
  }

  @Override
  public void onBindViewHolder(final SettingsContentAdapter.ViewHolder holder, final int position) {
    final Context context = holder.itemView.getContext();

    final boolean isReset = holder.getItemViewType() == TYPE_RESET;
    String title;
    String explanation;
    switch (position) {
      case POSITION_BOOT:
        title = presenter.getBootTitle();
        explanation = presenter.getBootExplanation();
        break;
      case POSITION_SUSPEND:
        title = presenter.getSuspendTitle();
        explanation = presenter.getSuspendExplanation();
        break;
      case POSITION_NOTIFICATION:
        title = presenter.getNotificationTitle();
        explanation = presenter.getNotificationExplanation();
        break;
      case POSITION_FOREGROUND:
        title = presenter.getForegroundTitle();
        explanation = presenter.getForegroundExplanation();
        break;
      case POSITION_RESET:
        title = presenter.getResetTitle();
        explanation = presenter.getResetExplanation();
        break;
      default:
        title = null;
        explanation = null;
    }

    final Spannable span = StringUtil.createBuilder(title, explanation);
    if (title != null) {
      fillSpannable(context, span, title.length());
    }

    final int resId =
        isReset ? R.drawable.ic_warning_white_24dp : R.drawable.ic_settings_white_24dp;
    if (isReset) {
      holder.resetButton.setText(span);
      holder.image.setBackground(DrawableUtil.createOval(context, R.color.red500));
      holder.resetButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          presenter.onResetClicked();
        }
      });
    } else {
      setupViewHolder(presenter, holder, span, position);
    }

    holder.image.setImageResource(resId);
  }

  private void setupViewHolder(final SettingsPresenter presenter, final ViewHolder holder,
      final Spannable span, final int position) {
    boolean isClickable;
    boolean isChecked;
    ValueRunnable<Boolean> onClick;
    switch (position) {
      case POSITION_BOOT:
        isClickable = presenter.isBootClickable();
        isChecked = presenter.isBootEnabled();
        onClick = new ValueRunnable<Boolean>() {
          @Override public void run() {
            presenter.onBootClicked(getValue());
          }
        };
        break;
      case POSITION_SUSPEND:
        isClickable = presenter.isSuspendClickable();
        isChecked = presenter.isSuspendEnabled();
        onClick = new ValueRunnable<Boolean>() {
          @Override public void run() {
            presenter.onSuspendClicked(getValue());
          }
        };
        break;
      case POSITION_NOTIFICATION:
        isClickable = presenter.isNotificationClickable();
        isChecked = presenter.isNotificationEnabled();
        onClick = new ValueRunnable<Boolean>() {
          @Override public void run() {
            presenter.onNotificationClicked(getValue());
          }
        };
        break;
      case POSITION_FOREGROUND:
        isClickable = presenter.isForegroundClickable();
        isChecked = presenter.isForegroundEnabled();
        onClick = new ValueRunnable<Boolean>() {
          @Override public void run() {
            presenter.onForegroundClicked(getValue());
          }
        };
        break;
      default:
        isClickable = false;
        isChecked = false;
        onClick = null;
    }

    // Java you beautiful monster
    holder.image.setEnabled(isClickable && isChecked);
    holder.switchCompat.setOnCheckedChangeListener(null);
    holder.switchCompat.setText(span);
    holder.switchCompat.setChecked(isChecked);
    holder.switchCompat.setEnabled(isClickable);

    final ValueRunnable<Boolean> finalOnClick = onClick;
    holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (finalOnClick != null) {
          finalOnClick.run(isChecked);
        }
      }
    });
  }

  @Override public int getItemCount() {
    return NUMBER_ITEMS;
  }

  private void fillSpannable(final Context context, final Spannable span, final int titleLength) {
    final int smallColor =
        StringUtil.getTextColorFromAppearance(context, android.R.attr.textAppearanceSmall);
    final int smallSize =
        StringUtil.getTextSizeFromAppearance(context, android.R.attr.textAppearanceSmall);
    if (smallColor != -1) {
      StringUtil.colorSpan(span, titleLength, span.length(), smallColor);
    }

    if (smallSize != -1) {
      StringUtil.sizeSpan(span, titleLength, span.length(), smallSize);
    }
  }

  @Override public void onResetRequested(final SettingsPresenter presenter, final Context context) {
    new AlertDialog.Builder(context).setTitle(context.getString(R.string.reset_settings_title))
        .setCancelable(false)
        .setIcon(R.drawable.ic_warning_white_24dp)
        .setMessage(context.getString(R.string.reset_settings_msg))
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

          private final WeakReference<SettingsPresenter> weakPresenter =
              new WeakReference<>(presenter);

          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            final SettingsPresenter p = weakPresenter.get();
            if (p != null) {
              p.onResetConfirmed();
            }
          }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {

          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create()
        .show();
  }

  @Override public void onBootEnabled() {
    notifyItemChanged(POSITION_BOOT);
  }

  @Override public void onBootDisabled() {
    notifyItemChanged(POSITION_BOOT);
  }

  @Override public void onSuspendEnabled() {
    notifyItemChanged(POSITION_SUSPEND);
  }

  @Override public void onSuspendDisabled() {
    notifyItemChanged(POSITION_SUSPEND);
  }

  @Override public void onNotificationEnabled() {
    notifyItemChanged(POSITION_NOTIFICATION);
  }

  @Override public void onNotificationDisabled() {
    notifyItemChanged(POSITION_NOTIFICATION);
  }

  @Override public void onForegroundEnabled() {
    notifyItemChanged(POSITION_FOREGROUND);
  }

  @Override public void onForegroundDisabled() {
    notifyItemChanged(POSITION_FOREGROUND);
  }

  @Override public void onForegroundAffected() {
    notifyItemChanged(POSITION_FOREGROUND);
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    private final ImageView image;
    private final SwitchCompat switchCompat;
    private final Button resetButton;

    public ViewHolder(final View itemView, final int viewType) {
      super(itemView);
      float dp;
      if (viewType == TYPE_RESET) {
        image = (ImageView) itemView.findViewById(R.id.card_image);
        resetButton = (Button) itemView.findViewById(R.id.card_reset_button);
        switchCompat = null;
        dp = AppUtil.convertToDP(itemView.getContext(), ElevationUtil.ELEVATION_RAISED_BUTTON);
        ViewCompat.setElevation(resetButton, dp);
      } else {
        image = (ImageView) itemView.findViewById(R.id.card_image);
        switchCompat = (SwitchCompat) itemView.findViewById(R.id.card_switch_toggle);
        resetButton = null;
        dp = AppUtil.convertToDP(itemView.getContext(), ElevationUtil.ELEVATION_SWITCH);
        ViewCompat.setElevation(switchCompat, dp);
      }
    }
  }
}
