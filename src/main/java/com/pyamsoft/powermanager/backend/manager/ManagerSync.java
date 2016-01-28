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
package com.pyamsoft.powermanager.backend.manager;

import android.content.ContentResolver;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.LogUtil;

public final class ManagerSync extends ManagerBase {

  private static final String SYNC_MANAGER = "SyncManager";
  private static ManagerBase instance = null;

  private ManagerSync() {
    super();
    LogUtil.d(SYNC_MANAGER, "Initialize ManagerSync");
  }

  public static ManagerBase get() {
    if (instance == null) {
      synchronized (ManagerSync.class) {
        if (instance == null) {
          instance = new ManagerSync();
        }
      }
    }
    return instance;
  }

  @Override synchronized void disable() {
    ContentResolver.setMasterSyncAutomatically(false);
    LogUtil.i(SYNC_MANAGER, "setMasterSyncAutomatically: false");
  }

  @Override synchronized void enable() {
    ContentResolver.setMasterSyncAutomatically(true);
    LogUtil.i(SYNC_MANAGER, "setMasterSyncAutomatically: true");
  }

  @Override public final synchronized boolean isEnabled() {
    return ContentResolver.getMasterSyncAutomatically();
  }

  @Override public String getTag() {
    return SYNC_MANAGER;
  }

  @Override public String getName() {
    return "Sync";
  }

  public static final class Interval extends ManagerBase.Interval {

    public Interval() {
      super(Interval.class.getName());
    }

    @Override protected long getTargetCloseTime(GlobalPreferenceUtil preferenceUtil) {
      return preferenceUtil.intervalDisableService().getSyncReopenTime();
    }

    @Override protected ManagerBase getTargetManager() {
      return get();
    }

    @Override protected long getTargetIntervalTime(GlobalPreferenceUtil preferenceUtil) {
      return preferenceUtil.powerManagerActive().getIntervalTimeSync();
    }

    @Override protected Class<? extends ManagerBase.Interval> getServiceClass() {
      return Interval.class;
    }
  }

  public static final class Toggle extends ManagerBase.Toggle {

    public Toggle() {
      super(Toggle.class.getName());
    }

    @Override protected void setManageState(GlobalPreferenceUtil preferenceUtil) {
      final boolean managed = preferenceUtil.powerManagerActive().isManagedSync();
      preferenceUtil.powerManagerActive().setManagedSync(!managed);
      PowerPlanUtil.with(getApplicationContext())
          .updateCustomPlan(PowerPlanUtil.FIELD_MANAGE_SYNC, !managed);
    }
  }
}

