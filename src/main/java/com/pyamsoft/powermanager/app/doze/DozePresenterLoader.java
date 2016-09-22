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

package com.pyamsoft.powermanager.app.doze;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.base.BaseManagePreferencePresenter;
import com.pyamsoft.pydroid.app.PersistLoader;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class DozePresenterLoader extends PersistLoader<BaseManagePreferencePresenter> {

  @Inject @Named("doze_manage_pref") Provider<BaseManagePreferencePresenter> presenterProvider;

  DozePresenterLoader(@NonNull Context context) {
    super(context);
  }

  @NonNull @Override public BaseManagePreferencePresenter loadPersistent() {
    PowerManager.get(getContext()).provideComponent().plusDozeScreenComponent().inject(this);
    return presenterProvider.get();
  }
}
