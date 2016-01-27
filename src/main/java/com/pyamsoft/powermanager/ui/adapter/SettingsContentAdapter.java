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
package com.pyamsoft.powermanager.ui.adapter;

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
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.receiver.BootActionReceiver;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.trigger.PowerTriggerDataSource;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.DrawableUtil;
import com.pyamsoft.pydroid.util.ElevationUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import com.pyamsoft.pydroid.util.NotificationUtil;
import com.pyamsoft.pydroid.util.StringUtil;

public final class SettingsContentAdapter
    extends RecyclerView.Adapter<SettingsContentAdapter.ViewHolder> {

  private static final int POSITION_BOOT = 0;
  private static final int POSITION_SUSPEND = 1;
  private static final int POSITION_NOTIFICATION = 2;
  private static final int POSITION_FOREGROUND = 3;
  private static final int POSITION_RESET = 4;
  private static final int TYPE_NORMAL = 0;
  private static final int TYPE_RESET = 1;
  private static final int NUMBER_ITEMS = 5;
  private static final String TAG = SettingsContentAdapter.class.getSimpleName();

  private static void showResetDialog(final Context c) {
    new AlertDialog.Builder(c).setTitle(c.getString(R.string.reset_settings_title))
        .setCancelable(false)
        .setIcon(R.drawable.ic_warning_white_24dp)
        .setMessage(c.getString(R.string.reset_settings_msg))
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            factoryReset(c);
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

  private static void factoryReset(final Context c) {
    MonitorService.stopService(c);
    NotificationUtil.stop(c, PersistentNotification.ID);
    MonitorService.killService(c);
    LogUtil.i(TAG, c.getString(R.string.close_app_msg));
    // Boot is handled outside of preferences, explicitly set it back to false, the default
    BootActionReceiver.setBootEnabled(c, false);
    final PowerTriggerDataSource source = PowerTriggerDataSource.with(c);
    source.open();
    if (source.isOpened()) {
      source.deleteAllTriggers();
      source.close();
    }
    GlobalPreferenceUtil.with(c).clear();
    android.os.Process.killProcess(android.os.Process.myPid());
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
    switch (position) {
      case POSITION_RESET:
        setResetButtonIcon(holder);
        setResetButtonOnClick(holder);
        break;
      default:
        setIcon(holder, position);
        setIconEnabledState(holder, position);
        setOnClick(holder, position);
        break;
    }
  }

  private void setResetButtonIcon(final ViewHolder holder) {
    holder.image.setBackground(DrawableUtil.createOval(holder.image.getContext(), R.color.red500));
    holder.image.setImageResource(R.drawable.ic_warning_white_24dp);
  }

  private void setResetButtonOnClick(final ViewHolder holder) {
    final String text = holder.resetButton.getContext().getString(R.string.reset_all_settings);
    holder.resetButton.setText(text);
    holder.resetButton.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View v) {
        SettingsContentAdapter.showResetDialog(v.getContext());
      }
    });
  }

  @Override public int getItemCount() {
    return NUMBER_ITEMS;
  }

  private void setIconEnabledState(final ViewHolder holder, final int position) {
    final Context c = holder.itemView.getContext();
    boolean enabled;
    Spannable span;
    String title;
    String explain;
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(holder.itemView.getContext());
    switch (position) {
      case POSITION_BOOT:
        enabled = BootActionReceiver.isBootEnabled(c);
        title = c.getString(R.string.boot_enabled) + "\n";
        explain = "Start Power Manager when device starts";
        break;
      case POSITION_SUSPEND:
        enabled = p.powerManagerActive().isSuspendPlugged();
        title = c.getString(R.string.suspend_charging) + "\n";
        explain = "Suspend Power Manager functions while charging";
        break;
      case POSITION_NOTIFICATION:
        enabled = p.powerManagerMonitor().isNotificationEnabled();
        title = c.getString(R.string.enable_notification) + "\n";
        explain = "Show a persistent notification in the Notification Drawer";
        break;
      case POSITION_FOREGROUND:
        enabled = p.powerManagerMonitor().isForeground();
        title = c.getString(R.string.enable_foreground) + "\n";
        explain = "Increase the memory used by Power Manager in exchange for better performance";
        break;
      default:
        enabled = false;
        title = null;
        explain = null;
    }
    if (title != null) {
      span = StringUtil.createBuilder(title, explain);
      final int smallColor =
          StringUtil.getTextColorFromAppearance(c, android.R.attr.textAppearanceSmall);
      final int smallSize =
          StringUtil.getTextSizeFromAppearance(c, android.R.attr.textAppearanceSmall);
      if (smallColor != -1) {
        StringUtil.colorSpan(span, title.length(), span.length(), smallColor);
      }

      if (smallSize != -1) {
        StringUtil.sizeSpan(span, title.length(), span.length(), smallSize);
      }

      holder.switchCompat.setText(span);
      holder.switchCompat.setChecked(enabled);
    }
  }

  private void setIcon(final ViewHolder holder, final int position) {
    final Context c = holder.itemView.getContext();
    boolean enabled;
    int resId;
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(holder.itemView.getContext());
    switch (position) {
      case POSITION_BOOT:
        enabled = BootActionReceiver.isBootEnabled(c);
        resId = R.drawable.ic_settings_white_24dp;
        break;
      case POSITION_SUSPEND:
        enabled = p.powerManagerActive().isSuspendPlugged();
        resId = R.drawable.ic_settings_white_24dp;
        break;
      case POSITION_NOTIFICATION:
        enabled = p.powerManagerMonitor().isNotificationEnabled();
        resId = R.drawable.ic_settings_white_24dp;
        break;
      case POSITION_FOREGROUND:
        enabled = p.powerManagerMonitor().isForeground();
        resId = R.drawable.ic_settings_white_24dp;
        break;
      default:
        enabled = false;
        resId = 0;
    }

    if (resId != 0) {
      holder.image.setEnabled(enabled);
      holder.image.setImageResource(resId);
    }
  }

  private void setOnClick(final ViewHolder holder, final int position) {
    holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(holder.itemView.getContext());
        final boolean oldNotificationState = p.powerManagerMonitor().isNotificationEnabled();
        final boolean oldForegroundState = p.powerManagerMonitor().isForeground();

        boolean changeNotification;
        boolean updateNotification;
        switch (position) {
          case POSITION_BOOT:
            BootActionReceiver.setBootEnabled(buttonView.getContext(), isChecked);
            updateNotification = true;
            changeNotification = false;
            break;
          case POSITION_SUSPEND:
            p.powerManagerActive().setSuspendPlugged(isChecked);
            updateNotification = true;
            changeNotification = false;
            break;
          case POSITION_NOTIFICATION:
            p.powerManagerMonitor().setNotificationEnabled(isChecked);
            updateNotification = false;
            changeNotification = true;
            break;
          case POSITION_FOREGROUND:
            p.powerManagerMonitor().setForeground(isChecked);
            updateNotification = false;
            changeNotification = true;
            break;
          default:
            updateNotification = false;
            changeNotification = false;
        }
        if (updateNotification) {
          LogUtil.d(TAG, "Update state of running notification");
          final PowerPlanUtil powerPlan = PowerPlanUtil.with(holder.itemView.getContext());
          powerPlan.updateCustomPlan(PowerPlanUtil.FIELD_MISC_BOOT, isChecked);
          powerPlan.updateCustomPlan(PowerPlanUtil.FIELD_MISC_SUSPEND, isChecked);
          powerPlan.setPlan(
              PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_CUSTOM[PowerPlanUtil.FIELD_INDEX]));
        } else {
          // If either notification or foreground were selected
          if (changeNotification) {
            LogUtil.d(TAG, "Change state of running notification");
            // Notification may need to be stopped if it was running before and is not now
            // Some delay may make these values unreliable if we read them here after they were applied.
            // Check the current index to see which is safe to read and which we should just flip from above
            final boolean isEnabled = (position == POSITION_NOTIFICATION) ? isChecked
                : p.powerManagerMonitor().isNotificationEnabled();
            final boolean isForeground = (position == POSITION_FOREGROUND) ? isChecked
                : p.powerManagerMonitor().isForeground();
            if (oldNotificationState && !isEnabled) {
              LogUtil.d(TAG, "Notification was enabled but is no longer");
              stopNotification(buttonView.getContext(), false, oldForegroundState);
            } else if (oldForegroundState && !isForeground) {
              LogUtil.d(TAG, "Notification was foreground but is no longer");
              stopNotification(buttonView.getContext(), isEnabled, true);
            } else {
              LogUtil.d(TAG, "Notification was either enabled or pushed to foreground");
              MonitorService.updateNotification(buttonView.getContext());
            }
          }
        }
        setIcon(holder, position);
      }
    });
  }

  private static void stopNotification(final Context context, final boolean isEnabled,
      final boolean wasForeground) {
    // If it was in the foreground, we need to call stop persistent first
    if (wasForeground) {
      LogUtil.d(TAG, "Notification was in foreground");
      MonitorService.stopPersistentNotification(context);
    }

    if (isEnabled) {
      LogUtil.d(TAG, "Foreground was stopped but notification is still active");
      MonitorService.updateNotification(context);
    } else {
      LogUtil.d(TAG, "Stop normal notification");
      NotificationUtil.stop(context, PersistentNotification.ID);
    }
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
