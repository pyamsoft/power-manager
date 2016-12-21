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
import com.pyamsoft.powermanagerpresenter.preference.CustomTimeInputPreferencePresenter;
import com.pyamsoft.powermanagerpresenter.preference.wifi.WifiEnablePreferenceLoader;
import java.util.Locale;

public class WifiEnableTimePreference extends CustomTimeInputPreference {

  private CustomTimeInputPreferencePresenter presenter;

  public WifiEnableTimePreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public WifiEnableTimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public WifiEnableTimePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public WifiEnableTimePreference(Context context) {
    super(context);
  }

  @NonNull @Override protected CharSequence formatSummaryStringForTime(long time) {
    return String.format(Locale.getDefault(), "Current WiFi enable time period: %d seconds", time);
  }

  @NonNull @Override protected CustomTimeInputPreferencePresenter getPresenter() {
    return presenter;
  }

  @Override protected void injectPresenter() {
    presenter = new WifiEnablePreferenceLoader().loadPersistent();
  }
}
