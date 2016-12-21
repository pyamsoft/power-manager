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

package com.pyamsoft.powermanager.base;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import timber.log.Timber;

public abstract class ModulePagerAdapter extends FragmentStatePagerAdapter {

  @Nullable private PagerItem currentItem;

  protected ModulePagerAdapter(@NonNull FragmentManager fm) {
    super(fm);
  }

  @Override public void setPrimaryItem(ViewGroup container, int position, Object object) {
    super.setPrimaryItem(container, position, object);
    Timber.d("Set primary item: %d", position);
    if (currentItem != null) {
      Timber.d("Unselect old item: %s", currentItem);
      currentItem.onUnselected();
    }

    if (object instanceof PagerItem) {
      final PagerItem pagerItem = (PagerItem) object;
      Timber.d("Set current item: %s", pagerItem);
      pagerItem.onSelected();
      currentItem = pagerItem;
    }
  }

  @Override public final Fragment getItem(int position) {
    Fragment fragment;
    switch (position) {
      case 0:
        fragment = getManageFragment();
        break;
      case 1:
        fragment = getPeriodicFragment();
        break;
      default:
        throw new RuntimeException("Invalid position: " + position);
    }

    return fragment;
  }

  @Override public final int getCount() {
    return 2;
  }

  @Override public CharSequence getPageTitle(int position) {
    final CharSequence title;
    switch (position) {
      case 0:
        title = "Manage";
        break;
      case 1:
        title = "Periodic";
        break;
      default:
        throw new RuntimeException("Invalid position: " + position);
    }

    return title;
  }

  @CheckResult @NonNull protected abstract ManagePreferenceFragment getManageFragment();

  @CheckResult @NonNull protected abstract PeriodicPreferenceFragment getPeriodicFragment();
}
