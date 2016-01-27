package com.pyamsoft.powermanager.ui.fragment;

import android.content.Context;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.receiver.BootActionReceiver;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.trigger.PowerTriggerDataSource;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.powermanager.ui.BooleanRunnable;
import com.pyamsoft.pydroid.util.LogUtil;
import com.pyamsoft.pydroid.util.NotificationUtil;
import java.util.concurrent.Callable;

public final class SettingsModel {

  public static final int POSITION_BOOT = 0;
  public static final int POSITION_SUSPEND = 1;
  public static final int POSITION_NOTIFICATION = 2;
  public static final int POSITION_FOREGROUND = 3;
  public static final int POSITION_RESET = 4;
  public static final int NUMBER_ITEMS = 5;
  private static final String TAG = SettingsModel.class.getSimpleName();
  private String title;
  private String explain;
  private Callable<Boolean> getProp;
  private Context context;
  private BooleanRunnable setProp;

  public SettingsModel(final Context c, int position) {
    context = c.getApplicationContext();
    switch (position) {
      case POSITION_BOOT:
        title = context.getString(R.string.boot_enabled) + "\n";
        explain = "Start Power Manager when device starts";
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return BootActionReceiver.isBootEnabled(context);
          }
        };
        setProp = new BooleanRunnable() {
          @Override public void run() {
            BootActionReceiver.setBootEnabled(context, isState());
          }
        };
        break;
      case POSITION_SUSPEND:
        title = context.getString(R.string.suspend_charging) + "\n";
        explain = "Suspend Power Manager functions while charging";
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return GlobalPreferenceUtil.with(context).powerManagerActive().isSuspendPlugged();
          }
        };
        setProp = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.with(context).powerManagerActive().setSuspendPlugged(isState());
          }
        };
        break;
      case POSITION_NOTIFICATION:
        title = context.getString(R.string.enable_notification) + "\n";
        explain = "Show a persistent notification in the Notification Drawer";
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return GlobalPreferenceUtil.with(context).powerManagerMonitor().isNotificationEnabled();
          }
        };
        setProp = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.with(context)
                .powerManagerMonitor()
                .setNotificationEnabled(isState());
          }
        };
        break;
      case POSITION_FOREGROUND:
        title = context.getString(R.string.enable_foreground) + "\n";
        explain = "Increase the memory used by Power Manager in exchange for better performance";
        getProp = new Callable<Boolean>() {
          @Override public Boolean call() throws Exception {
            return GlobalPreferenceUtil.with(context).powerManagerMonitor().isForeground();
          }
        };
        setProp = new BooleanRunnable() {
          @Override public void run() {
            GlobalPreferenceUtil.with(context).powerManagerMonitor().setForeground(isState());
          }
        };
        break;
      case POSITION_RESET:
        title = context.getString(R.string.reset_all_settings);
        explain = null;
        setProp = null;
        getProp = null;
        break;
      default:
        // ignored
    }
  }

  public String getTitle() {
    return title;
  }

  public String getExplanation() {
    return explain;
  }

  private boolean getProp() {
    if (getProp != null) {
      try {
        return getProp.call();
      } catch (Exception ignored) {
      }
    }
    return false;
  }

  private void setProp(final boolean checked) {
    if (setProp != null) {
      setProp.run(checked);
    }
  }

  public boolean isBootEnabled() {
    final boolean checked = getProp();
    LogUtil.d(TAG, "Boot enabled: ", checked);
    return checked;
  }

  public void setBootEnabled(final boolean enabled) {
    LogUtil.d(TAG, "Set boot enabled: ", enabled);
    setProp(enabled);
    propagatePowerPlanChanges(enabled);
  }

  public boolean isSuspendEnabled() {
    final boolean checked = getProp();
    LogUtil.d(TAG, "Suspend enabled: ", checked);
    return checked;
  }

  public void setSuspendEnabled(final boolean enabled) {
    LogUtil.d(TAG, "Set suspend enabled: ", enabled);
    setProp(enabled);
    propagatePowerPlanChanges(enabled);
  }

  public boolean isNotificationEnabled() {
    final boolean checked = getProp();
    LogUtil.d(TAG, "Notification enabled: ", checked);
    return checked;
  }

  public void setNotificationEnabled(final boolean enabled) {
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(context);
    final boolean oldForeground = p.powerManagerMonitor().isForeground();
    final boolean oldNotification = p.powerManagerMonitor().isNotificationEnabled();
    LogUtil.d(TAG, "Set notification enabled: ", enabled);
    setProp(enabled);
    propagateNotificationChanges(oldNotification, oldForeground, true, enabled);
  }

  public boolean isForegroundEnabled() {
    final boolean checked = getProp();
    LogUtil.d(TAG, "Foreground enabled: ", checked);
    return checked;
  }

  public boolean isForegroundClickable() {
    // Foreground relies on a present service and notification
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(context);
    final boolean checked =
        p.powerManagerMonitor().isEnabled() && p.powerManagerMonitor().isNotificationEnabled();
    LogUtil.d(TAG, "Foreground clickable: ", checked);
    return checked;
  }

  public void setForegroundEnabled(final boolean enabled) {
    final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(context);
    final boolean oldForeground = p.powerManagerMonitor().isForeground();
    final boolean oldNotification = p.powerManagerMonitor().isNotificationEnabled();
    LogUtil.d(TAG, "Set notification enabled: ", enabled);
    setProp(enabled);
    propagateNotificationChanges(oldNotification, oldForeground, false, enabled);
  }

  private void propagateNotificationChanges(final boolean oldNotification,
      final boolean oldForeground, final boolean fromNotification, final boolean isChecked) {
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

  private void propagatePowerPlanChanges(final boolean isChecked) {
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
