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

package com.pyamsoft.powermanager.app.preference.wifi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.pyamsoft.powermanager.Singleton;
import com.pyamsoft.powermanager.app.preference.CustomTimeInputPreference;
import com.pyamsoft.powermanager.dagger.preference.CustomTimeInputPreferencePresenter;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;

public class WifiDisableTimePreference extends CustomTimeInputPreference {
  @Inject @Named("wifi_custom_disable") CustomTimeInputPreferencePresenter presenter;

  public WifiDisableTimePreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public WifiDisableTimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public WifiDisableTimePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public WifiDisableTimePreference(Context context) {
    super(context);
  }

  @NonNull @Override protected CharSequence formatSummaryStringForTime(long time) {
    return String.format(Locale.getDefault(), "Current Wifi disable time period: %d seconds", time);
  }

  @NonNull @Override protected CustomTimeInputPreferencePresenter getPresenter() {
    return presenter;
  }

  @Override protected void injectPresenter(@NonNull Context context) {
    Singleton.Dagger.with(context).plusCustomPreferenceComponent().inject(this);
  }
}
