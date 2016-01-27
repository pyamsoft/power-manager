package com.pyamsoft.powermanager.ui.grid;

import android.content.Context;
import android.os.Bundle;
import com.pyamsoft.powermanager.backend.notification.PersistentNotification;
import com.pyamsoft.powermanager.backend.service.MonitorService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.ui.detail.DetailBaseFragment;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class GridModel {

  private final WeakReference<Context> weakContext;

  public GridModel(final Context context) {
    weakContext = new WeakReference<>(context);
  }

  public DetailBaseFragment createFragment(final String name, final int image) {
    final Bundle detailArgs = new Bundle();
    detailArgs.putString(DetailBaseFragment.EXTRA_PARAM_ID, name);
    detailArgs.putInt(DetailBaseFragment.EXTRA_PARAM_IMAGE, image);

    final DetailBaseFragment detailFragment = new DetailBaseFragment();
    detailFragment.setArguments(detailArgs);

    return detailFragment;
  }

  public boolean moveItems(final List<String> items, final int from, final int to) {
    final Context context = weakContext.get();
    if (context != null && items != null && !items.isEmpty()) {
      Collections.swap(items, from, to);
      final GlobalPreferenceUtil preferenceUtil = GlobalPreferenceUtil.with(context);
      preferenceUtil.gridOrder().set(to, items.get(to));
      preferenceUtil.gridOrder().set(from, items.get(from));
      return true;
    } else {
      return false;
    }
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
