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

package com.pyamsoft.powermanager.ui.radio;

import android.view.View;
import com.pyamsoft.powermanager.R;

public final class RadioWifi extends RadioBase {

  RadioWifi(RadioPresenter presenter) {
    super(presenter);
  }

  @Override public String getName() {
    return WIFI;
  }

  @Override public int getStatusbarColor() {
    return R.color.green700;
  }

  @Override public int getToolbarColor() {
    return R.color.green500;
  }

  @Override public boolean isFABEnabled() {
    final RadioPresenter presenter = getPresenter();
    if (presenter == null) {
      return false;
    } else {
      return presenter.isManagedWifi();
    }
  }

  @Override public boolean isFABMiniEnabled() {
    final RadioPresenter presenter = getPresenter();
    if (presenter == null) {
      return false;
    } else {
      return presenter.isIntervalEnabledWifi();
    }
  }

  @Override public int getFABIconDisabled() {
    return R.drawable.ic_signal_wifi_off_white_24dp;
  }

  @Override public int getFABIconEnabled() {
    return R.drawable.ic_network_wifi_white_24dp;
  }

  @Override public View.OnClickListener getFABOnClickListener() {
    return new View.OnClickListener() {
      @Override public void onClick(View v) {
        final RadioPresenter presenter = getPresenter();
        if (presenter != null) {
          presenter.setManagedWifi(!presenter.isManagedWifi());
        }
      }
    };
  }

  @Override public View.OnClickListener getFABMiniOnClickListener() {
    return new View.OnClickListener() {
      @Override public void onClick(View v) {
        final RadioPresenter presenter = getPresenter();
        if (presenter != null) {
          presenter.setIntervalEnabledWifi(!presenter.isIntervalEnabledWifi());
        }
      }
    };
  }
}