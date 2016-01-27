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

package com.pyamsoft.powermanager.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.ui.activity.MainActivity;

public abstract class ExplanationFragment extends Fragment {

  private Spannable explanation;

  abstract Spannable setupExplanationString();

  public abstract int getBackgroundColor();

  @Override public boolean onOptionsItemSelected(final MenuItem item) {
    final int itemId = item.getItemId();
    boolean handled;
    switch (itemId) {
      case R.id.menu_help:
        final Activity a = getActivity();
        if (a instanceof MainActivity) {
          final MainActivity main = (MainActivity) a;
          main.showExplanation(explanation, getBackgroundColor());
        }
        handled = true;
        break;
      default:
        handled = false;
    }
    return handled;
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    inflater.inflate(R.menu.menu_help, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    explanation = setupExplanationString();
  }
}
