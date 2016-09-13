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

package com.pyamsoft.powermanager.app.service.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.pyamsoft.pydroid.base.PersistLoader;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import timber.log.Timber;

public class FullNotificationActivity extends AppCompatActivity
    implements FullNotificationPresenter.FullNotificationView {

  @NonNull private static final String KEY_PRESENTER = "key_notification_presenter";
  @SuppressWarnings("WeakerAccess") FullNotificationPresenter presenter;
  private long loadedKey;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<FullNotificationPresenter>() {
              @NonNull @Override public PersistLoader<FullNotificationPresenter> createLoader() {
                return new FullNotificationPresenterLoader(getApplicationContext());
              }

              @Override public void onPersistentLoaded(@NonNull FullNotificationPresenter persist) {
                presenter = persist;
              }
            });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (!isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_PRESENTER, loadedKey);
    super.onSaveInstanceState(outState);
  }

  @Override protected void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override protected void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    Timber.d("Show Full Notification");
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(), new NotificationDialog(),
        "full_dialog");
  }

  @Override public void onDismissEvent() {
    Timber.d("Full Notification Dismissed");
    finish();
  }
}
