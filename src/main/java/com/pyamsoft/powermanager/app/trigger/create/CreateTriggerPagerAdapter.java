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

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.pyamsoft.powermanager.app.trigger.PowerTriggerFragment;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import timber.log.Timber;

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
    return 5;
  }

  public final void collect(@NonNull ViewPager viewPager) {
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
    final ContentValues values = PowerTriggerEntry.FACTORY.marshal().name(name)
        .percent(percent)
        .enabled(false)
        .available(true)
        .toggleWifi(wifiToggle)
        .toggleData(dataToggle)
        .toggleBluetooth(bluetoothToggle)
        .toggleSync(syncToggle)
        .enableWifi(wifiEnable)
        .enableData(dataEnable)
        .enableBluetooth(bluetoothEnable)
        .enableSync(syncEnable)
        .asContentValues();
    PowerTriggerFragment.Bus.get().post(values);
  }
}
