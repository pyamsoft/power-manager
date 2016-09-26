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

package com.pyamsoft.powermanager.dagger.receiver;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.observer.BooleanInterestObserver;
import javax.inject.Inject;
import timber.log.Timber;

class SensorFixReceiverImpl implements SensorFixReceiver {

  @NonNull private final BrightnessFixReceiver brightnessFixReceiver;
  @NonNull private final RotateFixReceiver rotateFixReceiver;

  @Inject SensorFixReceiverImpl(@NonNull Context context,
      @NonNull BooleanInterestObserver writePermissionObserver) {
    this.brightnessFixReceiver = new BrightnessFixReceiver(context, writePermissionObserver);
    this.rotateFixReceiver = new RotateFixReceiver(context, writePermissionObserver);
  }

  @Override public void register() {
    brightnessFixReceiver.register();
    rotateFixReceiver.register();

    Timber.d("Send initial events");
    brightnessFixReceiver.setAutoBrightnessEnabled(
        !brightnessFixReceiver.isAutoBrightnessEnabled());
    rotateFixReceiver.setAutoRotateEnabled(!rotateFixReceiver.isAutoRotateEnabled());
  }

  @Override public void unregister() {
    brightnessFixReceiver.unregister();
    rotateFixReceiver.unregister();
  }

  @SuppressWarnings("WeakerAccess") static final class BrightnessFixReceiver
      extends ContentObserver {

    @NonNull private final ContentResolver contentResolver;
    @NonNull private final BooleanInterestObserver writePermissionObserver;
    boolean originalAutoBright;
    boolean registered = false;

    BrightnessFixReceiver(@NonNull Context context,
        @NonNull BooleanInterestObserver writePermissionObserver) {
      super(new Handler(Looper.getMainLooper()));
      this.contentResolver = context.getApplicationContext().getContentResolver();
      this.writePermissionObserver = writePermissionObserver;
    }

    @CheckResult boolean isAutoBrightnessEnabled() {
      try {
        final boolean autobright =
            Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE)
                == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        Timber.d("is auto bright: %s", autobright);
        return autobright;
      } catch (Settings.SettingNotFoundException e) {
        Timber.e(e, "error getting autobrightness");
        return false;
      }
    }

    void setAutoBrightnessEnabled(boolean enabled) {
      if (writePermissionObserver.is()) {
        Timber.d("Set auto brightness: %s", enabled);
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
            enabled ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
      } else {
        Timber.e("Missing WRITE_SETTINGS permission");
      }
    }

    final void register() {
      if (!registered) {
        Timber.d("Register BrightnessFixReceiver");
        originalAutoBright = isAutoBrightnessEnabled();
        contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), false, this);
        registered = true;
      }
    }

    final void unregister() {
      if (registered) {
        Timber.d("Unregister BrightnessFixReceiver");
        contentResolver.unregisterContentObserver(this);
        registered = false;
      }
    }

    @Override public boolean deliverSelfNotifications() {
      return false;
    }

    @Override public void onChange(boolean selfChange) {
      onChange(selfChange, null);
    }

    @Override public void onChange(boolean selfChange, Uri uri) {
      Timber.d("AutoBrightness change event!");
      final boolean isAutoBright = isAutoBrightnessEnabled();
      if (isAutoBright == originalAutoBright) {
        Timber.d("Auto brightness has returned to original state");
        unregister();
      } else {
        Timber.d("Auto brightness has been toggled, set back to original and wait");
        setAutoBrightnessEnabled(originalAutoBright);
      }
    }
  }

  @SuppressWarnings("WeakerAccess") static final class RotateFixReceiver extends ContentObserver {

    @NonNull private final ContentResolver contentResolver;
    @NonNull private final BooleanInterestObserver writePermissionObserver;
    boolean originalAutoRotate;
    boolean registered = false;

    RotateFixReceiver(@NonNull Context context,
        @NonNull BooleanInterestObserver writePermissionObserver) {
      super(new Handler(Looper.getMainLooper()));
      this.contentResolver = context.getApplicationContext().getContentResolver();
      this.writePermissionObserver = writePermissionObserver;
    }

    @CheckResult boolean isAutoRotateEnabled() {
      final boolean autorotate =
          Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
      Timber.d("is auto rotate: %s", autorotate);
      return autorotate;
    }

    void setAutoRotateEnabled(boolean enabled) {
      if (writePermissionObserver.is()) {
        Timber.d("Set auto rotate: %s", enabled);
        Settings.System.putInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION,
            enabled ? 1 : 0);
      } else {
        Timber.e("Missing WRITE_SETTINGS permission");
      }
    }

    final void register() {
      if (!registered) {
        Timber.d("Register RotateFixReceiver");
        originalAutoRotate = isAutoRotateEnabled();
        contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, this);
        registered = true;
      }
    }

    final void unregister() {
      if (registered) {
        Timber.d("Unregister RotateFixReceiver");
        contentResolver.unregisterContentObserver(this);
        registered = false;
      }
    }

    @Override public boolean deliverSelfNotifications() {
      return false;
    }

    @Override public void onChange(boolean selfChange) {
      onChange(selfChange, null);
    }

    @Override public void onChange(boolean selfChange, Uri uri) {
      Timber.d("AutoRotate change event!");
      final boolean isAutoRotate = isAutoRotateEnabled();
      if (isAutoRotate == originalAutoRotate) {
        Timber.d("Auto rotate has returned to original state");
        unregister();
      } else {
        Timber.d("Auto rotate has been toggled, set back to original and wait");
        setAutoRotateEnabled(originalAutoRotate);
      }
    }
  }
}
