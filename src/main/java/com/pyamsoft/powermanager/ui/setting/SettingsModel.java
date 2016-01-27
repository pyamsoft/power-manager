package com.pyamsoft.powermanager.ui.setting;

import android.content.Context;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.receiver.BootActionReceiver;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.trigger.PowerTriggerDataSource;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;
import com.pyamsoft.pydroid.util.NotificationUtil;
import java.lang.ref.WeakReference;

public final class SettingsModel {

  public static final int POSITION_BOOT = 0;
  public static final int POSITION_SUSPEND = 1;
  public static final int POSITION_NOTIFICATION = 2;
  public static final int POSITION_FOREGROUND = 3;
  public static final int POSITION_RESET = 4;
  public static final int NUMBER_ITEMS = 5;
  private static final String TAG = SettingsModel.class.getSimpleName();
  private WeakReference<Context> weakContext;

  public Context provideResetContext() {
    return weakContext.get();
  }

  public SettingsModel(final Context c) {
    weakContext = new WeakReference<>(c.getApplicationContext());
  }

  public String getTitle(final int position) {
    final Context c = weakContext.get();
    String title;
    if (c != null) {
      switch (position) {
        case POSITION_BOOT:
          title = c.getString(R.string.boot_enabled) + "\n";
          break;
        case POSITION_SUSPEND:
          title = c.getString(R.string.suspend_charging) + "\n";
          break;
        case POSITION_NOTIFICATION:
          title = c.getString(R.string.enable_notification) + "\n";
          break;
        case POSITION_FOREGROUND:
          title = c.getString(R.string.enable_foreground) + "\n";
          break;
        case POSITION_RESET:
          title = c.getString(R.string.reset_all_settings);
          break;
        default:
          title = null;
      }
      return title;
    } else {
      return null;
    }
  }

  public String getExplanation(final int position) {
    String explain;
    switch (position) {
      case POSITION_BOOT:
        explain = "Start Power Manager when device starts";
        break;
      case POSITION_SUSPEND:
        explain = "Suspend Power Manager functions while charging";
        break;
      case POSITION_NOTIFICATION:
        explain = "Show a persistent notification in the Notification Drawer";
        break;
      case POSITION_FOREGROUND:
        explain = "Increase the memory used by Power Manager in exchange for better performance";
        break;
      case POSITION_RESET:
        explain = null;
        break;
      default:
        explain = null;
    }
    return explain;
  }

  public boolean isBootEnabled() {
    final Context context = weakContext.get();
    if (context != null) {
      final boolean checked = BootActionReceiver.isBootEnabled(context);
      LogUtil.d(TAG, "Boot enabled: ", checked);
      return checked;
    } else {
      return false;
    }
  }

  public void setBootEnabled(final boolean enabled) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set boot enabled: ", enabled);
      BootActionReceiver.setBootEnabled(context, enabled);
      propagatePowerPlanChanges(context, enabled);
    }
  }

  public boolean isSuspendEnabled() {
    final Context context = weakContext.get();
    if (context != null) {
      final boolean checked =
          GlobalPreferenceUtil.with(context).powerManagerActive().isSuspendPlugged();
      LogUtil.d(TAG, "Suspend enabled: ", checked);
      return checked;
    } else {
      return false;
    }
  }

  public void setSuspendEnabled(final boolean enabled) {
    final Context context = weakContext.get();
    if (context != null) {
      LogUtil.d(TAG, "Set suspend enabled: ", enabled);
      GlobalPreferenceUtil.with(context).powerManagerActive().setSuspendPlugged(enabled);
      propagatePowerPlanChanges(context, enabled);
    }
  }

  public boolean isNotificationEnabled() {
    final Context context = weakContext.get();
    if (context != null) {
      final boolean checked =
          GlobalPreferenceUtil.with(context).powerManagerMonitor().isNotificationEnabled();
      LogUtil.d(TAG, "Notification enabled: ", checked);
      return checked;
    } else {
      return false;
    }
  }

  public void setNotificationEnabled(final boolean enabled) {
    final Context context = weakContext.get();
    if (context != null) {
      final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(context);
      final boolean oldForeground = p.powerManagerMonitor().isForeground();
      final boolean oldNotification = p.powerManagerMonitor().isNotificationEnabled();
      LogUtil.d(TAG, "Set notification enabled: ", enabled);
      GlobalPreferenceUtil.with(context).powerManagerMonitor().setNotificationEnabled(enabled);
      propagateNotificationChanges(context, oldNotification, oldForeground, true, enabled);
    }
  }

  public boolean isForegroundEnabled() {
    final Context context = weakContext.get();
    if (context != null) {
      final boolean checked =
          GlobalPreferenceUtil.with(context).powerManagerMonitor().isForeground();
      LogUtil.d(TAG, "Foreground enabled: ", checked);
      return checked;
    } else {
      return false;
    }
  }

  public boolean isForegroundClickable() {
    // Foreground relies on a present service and notification
    final Context context = weakContext.get();
    if (context != null) {
      final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(context);
      final boolean checked =
          p.powerManagerMonitor().isEnabled() && p.powerManagerMonitor().isNotificationEnabled();
      LogUtil.d(TAG, "Foreground clickable: ", checked);
      return checked;
    } else {
      return false;
    }
  }

  public void setForegroundEnabled(final boolean enabled) {
    final Context context = weakContext.get();
    if (context != null) {
      final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(context);
      final boolean oldForeground = p.powerManagerMonitor().isForeground();
      final boolean oldNotification = p.powerManagerMonitor().isNotificationEnabled();
      LogUtil.d(TAG, "Set notification enabled: ", enabled);
      GlobalPreferenceUtil.with(context).powerManagerMonitor().setForeground(enabled);
      propagateNotificationChanges(context, oldNotification, oldForeground, false, enabled);
    }
  }

  private static void propagateNotificationChanges(final Context context,
      final boolean oldNotification, final boolean oldForeground, final boolean fromNotification,
      final boolean isChecked) {
    LogUtil.d(TAG, "Change state of running notification");
    // Notification may need to be stopped if it was running before and is not now
    // Some delay may make these values unreliable if we read them here after they were applied.
    // Check the current index to see which is safe to read and which we should just flip from above
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(context);
    final boolean isEnabled =
        fromNotification ? isChecked : p.powerManagerMonitor().isNotificationEnabled();
    final boolean isForeground =
        !fromNotification ? isChecked : p.powerManagerMonitor().isForeground();
    if (oldNotification && !isEnabled) {
      LogUtil.d(TAG, "Notification was enabled but is no longer");
      stopNotification(context, false, oldForeground);
    } else if (oldForeground && !isForeground) {
      LogUtil.d(TAG, "Notification was foreground but is no longer");
      stopNotification(context, isEnabled, true);
    } else {
      LogUtil.d(TAG, "Notification was either enabled or pushed to foreground");
      if (isEnabled) {
        if (isForeground) {
          LogUtil.d(TAG, "Update foreground notification");
          MonitorService.startForeground(context);
        } else {
          LogUtil.d(TAG, "Update normal notification");
          PersistentNotification.update(context);
        }
      }
    }
  }

  private static void propagatePowerPlanChanges(final Context context, final boolean isChecked) {
    LogUtil.d(TAG, "Update state of running notification");
    final PowerPlanUtil powerPlan = PowerPlanUtil.with(context);
    powerPlan.updateCustomPlan(PowerPlanUtil.FIELD_MISC_BOOT, isChecked);
    powerPlan.updateCustomPlan(PowerPlanUtil.FIELD_MISC_SUSPEND, isChecked);
    powerPlan.setPlan(
        PowerPlanUtil.toInt(PowerPlanUtil.POWER_PLAN_CUSTOM[PowerPlanUtil.FIELD_INDEX]));
  }

  private static void stopNotification(final Context context, final boolean isEnabled,
      final boolean wasForeground) {
    // If it was in the foreground, we need to call stop persistent first
    if (wasForeground) {
      LogUtil.d(TAG, "Notification was in foreground");
      MonitorService.stopForeground(context);
    }

    if (isEnabled) {
      LogUtil.d(TAG, "Foreground was stopped but notification is still active");
      PersistentNotification.update(context);
    } else {
      LogUtil.d(TAG, "Stop normal notification");
      NotificationUtil.stop(context, PersistentNotification.ID);
    }
  }

  public void doReset() {
    final Context context = weakContext.get();
    if (context != null) {
      // Stop the notification
      MonitorService.stopForeground(context);
      NotificationUtil.stop(context, PersistentNotification.ID);

      // Clean stop the service
      MonitorService.stop(context);

      // Boot is handled outside of preferences, explicitly set it back to false, the default
      BootActionReceiver.setBootEnabled(context, false);

      // Remove all Power Triggers
      final PowerTriggerDataSource source = PowerTriggerDataSource.with(context);
      source.open();
      if (source.isOpened()) {
        source.deleteAllTriggers();
        source.close();
      }

      // Now clear everything
      GlobalPreferenceUtil.with(context).clear();

      // Log the reset
      LogUtil.i(TAG, context.getString(R.string.close_app_msg));

      // Kill ourselves
      android.os.Process.killProcess(android.os.Process.myPid());
    }
  }
}
