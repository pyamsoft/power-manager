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

package com.pyamsoft.powermanager.app.sync;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.base.BaseOverviewPagerPresenter;
import com.pyamsoft.pydroid.app.PersistLoader;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class SyncOverviewPresenterLoader extends PersistLoader<BaseOverviewPagerPresenter> {

  @Inject @Named("sync_overview") Provider<BaseOverviewPagerPresenter> presenterProvider;

  SyncOverviewPresenterLoader(@NonNull Context context) {
    super(context);
  }

  @NonNull @Override public BaseOverviewPagerPresenter loadPersistent() {
    PowerManager.get(getContext()).provideComponent().plusSyncScreenComponent().inject(this);
    return presenterProvider.get();
  }
}
