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

package com.pyamsoft.powermanager.uicore.preference;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.presenter.Presenter;

public interface CustomTimeInputPreferencePresenter extends Presenter<Presenter.Empty> {

  void updateCustomTime(@NonNull String time, @NonNull OnCustomTimeUpdateCallback callback);

  void updateCustomTime(@NonNull String time, long delay,
      @NonNull OnCustomTimeUpdateCallback callback);

  void updateCustomTime(@NonNull String time, boolean updateView,
      @NonNull OnCustomTimeUpdateCallback callback);

  void updateCustomTime(@NonNull String time, long delay, boolean updateView,
      @NonNull OnCustomTimeUpdateCallback callback);

  void initializeCustomTime(@NonNull OnCustomTimeUpdateCallback callback);

  interface OnCustomTimeUpdateCallback {

    void onCustomTimeUpdate(long time);

    void onCustomTimeError();
  }
}
