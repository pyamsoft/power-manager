package com.pyamsoft.powermanager.ui.grid;

import android.content.Context;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import java.lang.ref.WeakReference;

public final class GridModel {

  private final WeakReference<Context> weakContext;

  public GridModel(final Context context) {
    weakContext = new WeakReference<>(context);
  }

  public void clickFAB() {
    final Context context = weakContext.get();
    if (context != null) {
      MonitorService.powerManagerService(context);
      final GlobalPreferenceUtil p = GlobalPreferenceUtil.with(context);
      if (p.powerManagerMonitor().isNotificationEnabled()) {
        if (p.powerManagerMonitor().isForeground()) {
          MonitorService.startForeground(context);
        } else {
          PersistentNotification.update(context);
        }
      }
    }
  }
}
