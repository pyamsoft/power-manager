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

package com.pyamsoft.powermanager.dagger.manager;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.birbit.android.jobqueue.TagConstraint;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.manager.Manager;
import timber.log.Timber;

abstract class ManagerBase implements Manager {

  @Override public final void enable(@NonNull Activity activity) {
    enable(activity.getApplication());
  }

  @Override public final void enable(@NonNull Fragment fragment) {
    enable(fragment.getActivity());
  }

  @Override public final void enable(@NonNull Service service) {
    enable(service.getApplication());
  }

  @Override public final void enable(@NonNull Activity activity, long time) {
    enable(activity.getApplication(), time);
  }

  @Override public final void enable(@NonNull Fragment fragment, long time) {
    enable(fragment.getActivity(), time);
  }

  @Override public final void enable(@NonNull Service service, long time) {
    enable(service.getApplication(), time);
  }

  @Override public final void disable(@NonNull Activity activity) {
    disable(activity.getApplication());
  }

  @Override public final void disable(@NonNull Fragment fragment) {
    disable(fragment.getActivity());
  }

  @Override public final void disable(@NonNull Service service) {
    disable(service.getApplication());
  }

  @Override public final void disable(@NonNull Activity activity, long time) {
    disable(activity.getApplication(), time);
  }

  @Override public final void disable(@NonNull Fragment fragment, long time) {
    disable(fragment.getActivity(), time);
  }

  @Override public final void disable(@NonNull Service service, long time) {
    disable(service.getApplication(), time);
  }

  protected final void cancelJobs(@NonNull Application application, @NonNull String tag) {
    Timber.d("Attempt job cancel");
    PowerManager.getJobManager(application).cancelJobsInBackground(null, TagConstraint.ANY, tag);
  }
}
