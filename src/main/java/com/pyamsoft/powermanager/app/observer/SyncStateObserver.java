/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.app.observer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import timber.log.Timber;

public class SyncStateObserver extends StateObserver {

  @NonNull private final SyncStateObserverView view;
  @NonNull private final SyncStatusObserver observer;
  private Object listener;

  public SyncStateObserver(@NonNull Context context, @NonNull SyncStateObserverView view) {
    super(context);
    this.view = view;
    observer = i -> {
      Timber.d("onStatusChanged: %d", i);
      if (isEnabled()) {
        Timber.d("Enabled");
        view.onSyncStateEnabled();
      } else {
        Timber.d("Disabled");
        view.onSyncStateDisabled();
      }
    };
  }

  @Override public boolean deliverSelfNotifications() {
    return false;
  }

  @Override public void onChange(boolean selfChange) {
    throw new RuntimeException("Not used");
  }

  @Override public void onChange(boolean selfChange, Uri uri) {
    throw new RuntimeException("Not used");
  }

  @Override boolean isEnabled() {
    return ContentResolver.getMasterSyncAutomatically();
  }

  @Override void internalRegister(@Nullable Uri uri) {
    listener = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS,
        observer);
  }

  @Override public void register() {
    register(null);
  }

  @Override void internalUnregister() {
    ContentResolver.removeStatusChangeListener(listener);
    listener = null;
  }

  public interface SyncStateObserverView {

    void onSyncStateEnabled();

    void onSyncStateDisabled();
  }
}
