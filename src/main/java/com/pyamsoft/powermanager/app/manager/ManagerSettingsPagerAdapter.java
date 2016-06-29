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

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.dagger.manager.DaggerManagerSettingsComponent;
import javax.inject.Inject;
import timber.log.Timber;

public final class ManagerSettingsPagerAdapter extends FragmentStatePagerAdapter {

  @NonNull public static final String TYPE_WIFI = "wifi";
  @NonNull public static final String TYPE_DATA = "data";
  @NonNull public static final String TYPE_BLUETOOTH = "bluetooth";
  @NonNull public static final String TYPE_SYNC = "sync";
  @NonNull static final String FRAGMENT_TYPE = "fragment_type";
  @NonNull private final Fragment manageFragment;
  @NonNull private final Fragment periodicFragment;
  @NonNull private final String type;
  @DrawableRes private int fabIcon;
  @ColorRes private int backgroundIcon;
  @Inject WifiPresenter wifiPresenter;
  @Inject DataPresenter dataPresenter;
  @Inject BluetoothPresenter bluetoothPresenter;
  @Inject SyncPresenter syncPresenter;

  public ManagerSettingsPagerAdapter(@NonNull FragmentManager fm, @NonNull String type) {
    super(fm);
    Timber.d("new ManagerSettingsPagerAdapter");
    manageFragment = ManagerManageFragment.newInstance(type);
    periodicFragment = ManagerPeriodicFragment.newInstance(type);
    this.type = type;

    DaggerManagerSettingsComponent.builder()
        .powerManagerComponent(PowerManager.getInstance().getPowerManagerComponent())
        .build()
        .inject(this);
  }

  @NonNull public final String getType() {
    return type;
  }

  @NonNull @Override public Fragment getItem(int position) {
    Fragment fragment;
    switch (position) {
      case 0:
        fragment = manageFragment;
        break;
      case 1:
        fragment = periodicFragment;
        break;
      default:
        throw new IllegalStateException("Fragment index is OOB");
    }
    return fragment;
  }

  @Override public int getCount() {
    return 2;
  }

  @Override public CharSequence getPageTitle(int position) {
    CharSequence title;
    switch (position) {
      case 0:
        title = "Manage";
        break;
      case 1:
        title = "Periodic";
        break;
      default:
        throw new IllegalStateException("Invalid index");
    }
    return title;
  }

  public final void unbind() {

  }
}
