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

package com.pyamsoft.powermanager.preference.wifi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.pyamsoft.powermanager.preference.CustomTimeInputPreference;
import com.pyamsoft.powermanagerpresenter.preference.PreferenceLoader;
import com.pyamsoft.powermanagerpresenter.preference.wifi.WifiPreferenceLoader;

public class WifiCustomTimePreference extends CustomTimeInputPreference {

  public WifiCustomTimePreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public WifiCustomTimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public WifiCustomTimePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public WifiCustomTimePreference(Context context) {
    super(context);
  }

  @NonNull @Override protected String getName() {
    return "WiFi";
  }

  @NonNull @Override protected PreferenceLoader getPreferenceLoader() {
    return new WifiPreferenceLoader();
  }
}
