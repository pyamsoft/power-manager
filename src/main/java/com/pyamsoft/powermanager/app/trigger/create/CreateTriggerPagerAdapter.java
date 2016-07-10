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

package com.pyamsoft.powermanager.app.trigger.create;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CreateTriggerPagerAdapter extends FragmentStatePagerAdapter {

  public static final int TOTAL_COUNT = 5;
  public static final int POSITION_BASIC = 0;
  public static final int POSITION_WIFI = 1;
  public static final int POSITION_DATA = 2;
  public static final int POSITION_BLUETOOTH = 3;
  public static final int POSITION_SYNC = 4;

  public CreateTriggerPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override public Fragment getItem(int position) {
    Fragment fragment;
    switch (position) {
      case POSITION_WIFI:
        fragment = new CreateTriggerManageFragment();
        break;
      default:
        fragment = new CreateTriggerBasicFragment();
    }
    return fragment;
  }

  @Override public int getCount() {
    return 5;
  }
}
