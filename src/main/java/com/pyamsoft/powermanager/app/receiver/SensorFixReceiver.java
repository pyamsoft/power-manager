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

package com.pyamsoft.powermanager.app.receiver;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import timber.log.Timber;

public class SensorFixReceiver {

  @NonNull private final Context appContext;
  @NonNull private final BrightnessFixReceiver brightnessFixReceiver;
  @NonNull private final RotateFixReceiver rotateFixReceiver;

  public SensorFixReceiver(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
    this.brightnessFixReceiver = new BrightnessFixReceiver(appContext);
    this.rotateFixReceiver = new RotateFixReceiver(appContext);
  }

  @CheckResult static boolean hasWritePermission(@NonNull Context context) {
    boolean hasRuntimePermission;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      Timber.d("Runtime permissions before M are auto granted");
      hasRuntimePermission = context.getApplicationContext()
          .checkCallingOrSelfPermission(Manifest.permission.WRITE_SETTINGS)
          == PackageManager.PERMISSION_GRANTED;
    } else {
      Timber.d("Check if system setting is granted");
      hasRuntimePermission = Settings.System.canWrite(context);
    }

    return hasRuntimePermission;
  }

  public void register() {
    brightnessFixReceiver.register();
    rotateFixReceiver.register();

    Timber.d("Send initial events");
    brightnessFixReceiver.setAutoBrightnessEnabled(
        !brightnessFixReceiver.isAutoBrightnessEnabled());
    rotateFixReceiver.setAutoRotateEnabled(!rotateFixReceiver.isAutoRotateEnabled());
  }

  public void unregister() {
    brightnessFixReceiver.unregister();
    rotateFixReceiver.unregister();
  }

  static final class BrightnessFixReceiver extends ContentObserver {

    @NonNull private final Context appContext;
    private boolean originalAutoBright;
    private boolean registered = false;

    public BrightnessFixReceiver(@NonNull Context context) {
      super(new Handler(Looper.getMainLooper()));
      this.appContext = context.getApplicationContext();
    }

    @CheckResult private boolean isAutoBrightnessEnabled() {
      try {
        final boolean autobright = Settings.System.getInt(appContext.getContentResolver(),
            Settings.System.SCREEN_BRIGHTNESS_MODE)
            == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        Timber.d("is auto bright: %s", autobright);
        return autobright;
      } catch (Settings.SettingNotFoundException e) {
        Timber.e(e, "error getting autobrightness");
        return false;
      }
    }

    private void setAutoBrightnessEnabled(boolean enabled) {
      if (hasWritePermission(appContext)) {
        Timber.d("Set auto brightness: %s", enabled);
        Settings.System.putInt(appContext.getContentResolver(),
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            enabled ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
      } else {
        Timber.e("Missing WRITE_SETTINGS permission");
      }
    }

    public final void register() {
      if (!registered) {
        originalAutoBright = isAutoBrightnessEnabled();
        appContext.getContentResolver()
            .registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), false, this);
        registered = true;
      }
    }

    public final void unregister() {
      if (registered) {
        Timber.d("Unregister BrightnessFixReceiver");
        appContext.getContentResolver().unregisterContentObserver(this);
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

  static final class RotateFixReceiver extends ContentObserver {

    @NonNull private final Context appContext;
    private boolean originalAutoRotate;
    private boolean registered = false;

    public RotateFixReceiver(@NonNull Context context) {
      super(new Handler(Looper.getMainLooper()));
      this.appContext = context.getApplicationContext();
    }

    @CheckResult private boolean isAutoRotateEnabled() {
      final boolean autorotate = Settings.System.getInt(appContext.getContentResolver(),
          Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
      Timber.d("is auto rotate: %s", autorotate);
      return autorotate;
    }

    private void setAutoRotateEnabled(boolean enabled) {
      if (hasWritePermission(appContext)) {
        Timber.d("Set auto rotate: %s", enabled);
        Settings.System.putInt(appContext.getContentResolver(),
            Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
      } else {
        Timber.e("Missing WRITE_SETTINGS permission");
      }
    }

    public final void register() {
      if (!registered) {
        originalAutoRotate = isAutoRotateEnabled();
        appContext.getContentResolver()
            .registerContentObserver(
                Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, this);
        registered = true;
      }
    }

    public final void unregister() {
      if (registered) {
        Timber.d("Unregister RotateFixReceiver");
        appContext.getContentResolver().unregisterContentObserver(this);
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
