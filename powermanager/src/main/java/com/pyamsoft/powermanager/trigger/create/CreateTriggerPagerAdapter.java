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

package com.pyamsoft.powermanager.trigger.create;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.pyamsoft.powermanager.trigger.PowerTriggerListFragment;
import com.pyamsoft.powermanagermodel.sql.PowerTriggerEntry;
import timber.log.Timber;

class CreateTriggerPagerAdapter extends FragmentStatePagerAdapter {

  static final int TOTAL_COUNT = 5;
  private static final int POSITION_BASIC = 0;
  private static final int POSITION_WIFI = 1;
  private static final int POSITION_DATA = 2;
  private static final int POSITION_BLUETOOTH = 3;
  private static final int POSITION_SYNC = 4;
  @NonNull private final FragmentManager fragmentManager;

  CreateTriggerPagerAdapter(@NonNull Fragment fragment) {
    super(fragment.getChildFragmentManager());
    fragmentManager = fragment.getFragmentManager();
  }

  @Override public Fragment getItem(int position) {
    Fragment fragment;
    switch (position) {
      case POSITION_WIFI:
        fragment = CreateTriggerManageFragment.newInstance(CreateTriggerManageFragment.TYPE_WIFI);
        break;
      case POSITION_DATA:
        fragment = CreateTriggerManageFragment.newInstance(CreateTriggerManageFragment.TYPE_DATA);
        break;
      case POSITION_BLUETOOTH:
        fragment =
            CreateTriggerManageFragment.newInstance(CreateTriggerManageFragment.TYPE_BLUETOOTH);
        break;
      case POSITION_SYNC:
        fragment = CreateTriggerManageFragment.newInstance(CreateTriggerManageFragment.TYPE_SYNC);
        break;
      default:
        fragment = new CreateTriggerBasicFragment();
    }
    return fragment;
  }

  @Override public int getCount() {
    return TOTAL_COUNT;
  }

  void collect(@NonNull ViewPager viewPager) {
    final CreateTriggerBasicFragment basicFragment =
        (CreateTriggerBasicFragment) instantiateItem(viewPager, POSITION_BASIC);
    final CreateTriggerManageFragment wifiFragment =
        (CreateTriggerManageFragment) instantiateItem(viewPager, POSITION_WIFI);
    final CreateTriggerManageFragment dataFragment =
        (CreateTriggerManageFragment) instantiateItem(viewPager, POSITION_DATA);
    final CreateTriggerManageFragment bluetoothFragment =
        (CreateTriggerManageFragment) instantiateItem(viewPager, POSITION_BLUETOOTH);
    final CreateTriggerManageFragment syncFragment =
        (CreateTriggerManageFragment) instantiateItem(viewPager, POSITION_SYNC);

    final String name = basicFragment.getTriggerName();
    final int percent = basicFragment.getTriggerPercent();
    final boolean wifiToggle = wifiFragment.getTriggerToggle();
    final boolean wifiEnable = wifiFragment.getTriggerEnable();
    final boolean dataToggle = dataFragment.getTriggerToggle();
    final boolean dataEnable = dataFragment.getTriggerEnable();
    final boolean bluetoothToggle = bluetoothFragment.getTriggerToggle();
    final boolean bluetoothEnable = bluetoothFragment.getTriggerEnable();
    final boolean syncToggle = syncFragment.getTriggerToggle();
    final boolean syncEnable = syncFragment.getTriggerEnable();

    Timber.d("Post content values to bus");
    final PowerTriggerEntry entry =
        PowerTriggerEntry.CREATOR.create(percent, name, true, true, wifiToggle, dataToggle,
            bluetoothToggle, syncToggle, wifiEnable, dataEnable, bluetoothEnable, syncEnable);
    sendCreateEvent(entry);
  }

  private void sendCreateEvent(@NonNull PowerTriggerEntry entry) {
    final Fragment powerTriggerListFragment =
        fragmentManager.findFragmentByTag(PowerTriggerListFragment.TAG);
    if (powerTriggerListFragment instanceof PowerTriggerListFragment) {
      ((PowerTriggerListFragment) powerTriggerListFragment).getPresenter()
          .createPowerTrigger(entry);
    } else {
      throw new ClassCastException("Fragment is not PowerTriggerListFragment");
    }
  }
}
