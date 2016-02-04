/*
 * Copyright 2013 - 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.ui.detail;

import android.content.SharedPreferences;

/**
 * The detail interface has two Floating Action Buttons, a Title and an Image
 */
public interface DetailInterface {

  void onSmallFABChecked();

  void onSmallFABUnchecked();

  void onLargeFABChecked();

  void onLargeFABUnchecked();

  void onPreferenceChanged(final SharedPreferences preferences, final String key);

  String getTarget();
}
