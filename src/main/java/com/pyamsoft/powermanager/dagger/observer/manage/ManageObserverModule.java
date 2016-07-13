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

package com.pyamsoft.powermanager.dagger.observer.manage;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.app.observer.InterestObserver;
import com.pyamsoft.powermanager.dagger.ActivityScope;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class ManageObserverModule {

  @ActivityScope @Named("wifi") @Provides InterestObserver provideWifiObserver(
      @NonNull Context context) {
    return new WifiManageObserver(context);
  }

  @ActivityScope @Named("data") @Provides InterestObserver provideDataObserver(
      @NonNull Context context) {
    return new DataManageObserver(context);
  }

  @ActivityScope @Named("bluetooth") @Provides InterestObserver provideBluetoothObserver(
      @NonNull Context context) {
    return new BluetoothManageObserver(context);
  }

  @ActivityScope @Named("sync") @Provides InterestObserver provideSyncObserver(
      @NonNull Context context) {
    return new BluetoothManageObserver(context);
  }
}