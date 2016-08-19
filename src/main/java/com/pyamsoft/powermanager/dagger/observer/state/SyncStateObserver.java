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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import java.lang.ref.WeakReference;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncStateObserver implements InterestObserver<SyncStateObserver.View> {

  @NonNull private final Handler handler;
  @NonNull private WeakReference<View> weakView = new WeakReference<>(null);
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
    weakView.clear();
    weakView = new WeakReference<>(view);
  }

  @Nullable @CheckResult public final View getView() {
    return weakView.get();
  }

  @Override public boolean is() {
    final boolean b = ContentResolver.getMasterSyncAutomatically();
    Timber.d("Is %s", b);
    return b;
  }

  @Override public void register() {
    handler.removeCallbacksAndMessages(null);
    if (!registered) {
      Timber.d("Register new state observer");
      listener =
          ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS,
              i -> handler.post(() -> {
                final View view = getView();
                if (view != null) {
                  if (is()) {
                    // Reset status of other flag here
                    disabled = false;

                    // Only call hook once
                    if (!enabled) {
                      enabled = true;
                      Timber.d("Enabled");
                      view.onSyncStateEnabled();
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
              }));
      registered = true;
    } else {
      Timber.e("Already registered");
    }
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
