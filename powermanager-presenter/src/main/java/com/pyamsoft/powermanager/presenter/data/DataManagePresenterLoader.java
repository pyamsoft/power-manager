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

package com.pyamsoft.powermanager.presenter.data;

import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.presenter.Injector;
import com.pyamsoft.powermanager.presenter.base.ManagePreferencePresenter;
import com.pyamsoft.pydroid.app.PersistLoader;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class DataManagePresenterLoader extends PersistLoader<ManagePreferencePresenter> {

  @SuppressWarnings("WeakerAccess") @Inject @Named("data_manage_pref")
  Provider<ManagePreferencePresenter> presenterProvider;

  public DataManagePresenterLoader() {
  }

  @NonNull @Override public ManagePreferencePresenter loadPersistent() {
    Injector.get().provideComponent().plusDataScreenComponent().inject(this);
    return presenterProvider.get();
  }
}