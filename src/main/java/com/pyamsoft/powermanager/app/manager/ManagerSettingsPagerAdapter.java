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

package com.pyamsoft.powermanager.app.manager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import timber.log.Timber;

public final class ManagerSettingsPagerAdapter extends FragmentStatePagerAdapter {

  @NonNull private final Fragment settingsFragment;
  @NonNull private final String type;

  public ManagerSettingsPagerAdapter(@NonNull FragmentManager fm, @NonNull Fragment fragment,
      @NonNull String type) {
    super(fm);
    Timber.d("new ManagerSettingsPagerAdapter");
    settingsFragment = fragment;
    this.type = type;
  }

  @NonNull public final String getType() {
    return type;
  }

  @NonNull @Override public Fragment getItem(int position) {
    Fragment fragment;
    switch (position) {
      case 0:
        fragment = settingsFragment;
        break;
      case 1:
        fragment = new Fragment();
        break;
      default:
        throw new IllegalStateException("Fragment index is OOB");
    }
    return fragment;
  }

  @Override public int getCount() {
    return 2;
  }
}
