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

package com.pyamsoft.powermanager.dagger.observer.state;

import android.content.ContentResolver;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncStateObserver implements InterestObserver<SyncStateObserver.View> {

  @NonNull private final Handler handler;
  @Nullable private View view;
  private Object listener;
  private boolean registered;
  private boolean enabled;
  private boolean disabled;

  @Inject SyncStateObserver() {
    handler = new Handler(Looper.getMainLooper());
    registered = false;
    enabled = false;
    disabled = false;
  }

  public final void setView(@NonNull View view) {
    this.view = view;
  }

  @Override public boolean is() {
    final boolean b = ContentResolver.getMasterSyncAutomatically();
    Timber.d("Sync is %s", b);
    return b;
  }

  @Override public void register() {
    handler.removeCallbacksAndMessages(null);
    handler.post(() -> {
      if (!registered) {
        Timber.d("Register new state observer");
        listener =
            ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS,
                i -> {
                  if (view != null) {
                    if (is()) {
                      // Reset status of other flag here
                      disabled = false;

                      // Only call hook once
                      if (!enabled) {
                        enabled = true;
                        Timber.d("Enabled");
                        view.onSyncStateEnabled();
                      } else {
                        // KLUDGE on nexus 6, every 3rd or so time
                        // KLUDGE Master Sync is toggle, the enable hook runs
                        // KLUDGE like 5 times.
                        Timber.e("Sync has already run the enabled event hook");
                      }
                    } else {
                      // Reset status of other flag here
                      enabled = false;

                      // Only call hook once
                      if (!disabled) {
                        disabled = true;
                        Timber.d("Disabled");
                        view.onSyncStateDisabled();
                      }
                    }
                  }
                });
        registered = true;
      } else {
        Timber.e("Already registered");
      }
    });
  }

  @Override public void unregister() {
    handler.removeCallbacksAndMessages(null);
    handler.post(() -> {
      if (registered) {
        Timber.d("Unregister new state observer");
        ContentResolver.removeStatusChangeListener(listener);
        registered = false;
      } else {
        Timber.e("Already unregistered");
      }
    });
  }

  public interface View {

    void onSyncStateEnabled();

    void onSyncStateDisabled();
  }
}
