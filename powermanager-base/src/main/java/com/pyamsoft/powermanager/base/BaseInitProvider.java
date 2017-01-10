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

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.evernote.android.job.JobManager;
import com.pyamsoft.pydroid.IPYDroidApp;
import com.pyamsoft.pydroid.SingleInitContentProvider;

public abstract class BaseInitProvider extends SingleInitContentProvider
    implements IPYDroidApp<PowerManagerComponent> {

  @Nullable private PowerManagerComponent component;

  @CallSuper @Override protected void onInstanceCreated(@NonNull Context context) {
    Injector.set(component);
  }

  @CallSuper @Override protected void onFirstCreate(@NonNull Context context) {
    super.onFirstCreate(context);
    component = createModule(context);
    JobManager.create(context);
  }

  @NonNull @Override public final PowerManagerComponent provideComponent() {
    if (component == null) {
      throw new NullPointerException("PowerManagerComponent is NULL");
    }
    return component;
  }

  @CheckResult @NonNull
  protected abstract PowerManagerComponent createModule(@NonNull Context context);
}
