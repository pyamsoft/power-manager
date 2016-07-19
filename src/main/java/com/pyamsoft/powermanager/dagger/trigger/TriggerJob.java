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

package com.pyamsoft.powermanager.dagger.trigger;

import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.Params;
import com.pyamsoft.powermanager.dagger.base.BaseJob;

public class TriggerJob extends BaseJob {

  public static final int PRIORITY = 2;
  @NonNull public static final String TRIGGER_TAG = "trigger";

  protected TriggerJob(long delay) {
    super(new Params(PRIORITY).setDelayMs(delay).addTags(TRIGGER_TAG));
  }

  @Override public void onRun() throws Throwable {

  }
}
