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

package com.pyamsoft.powermanager.app.trigger;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import timber.log.Timber;

public final class PowerTriggerPagerAdapter extends FragmentStatePagerAdapter {

  public PowerTriggerPagerAdapter(@NonNull FragmentManager fm) {
    super(fm);
    Timber.d("new PowertTriggerPagerAdapter");
  }

  @NonNull @Override public Fragment getItem(int position) {
    return new PowerTriggerFragment();
  }

  @Override public int getCount() {
    return 1;
  }
}
