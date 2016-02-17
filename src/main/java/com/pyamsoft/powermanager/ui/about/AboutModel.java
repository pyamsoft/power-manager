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

package com.pyamsoft.powermanager.ui.about;

import android.content.Intent;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.pydroid.util.AppUtil;

final class AboutModel {

  private static final String TAG = AboutModel.class.getSimpleName();
  private final Intent intent;

  AboutModel() {
    intent = AppUtil.getApplicationInfoIntent(PowerManager.class)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  }

  Intent getInfoIntent() {
    return intent;
  }
}
