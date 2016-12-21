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

package com.pyamsoft.powermanager.logger;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.DialogLoggerBinding;
import com.pyamsoft.powermanagerpresenter.logger.LoggerPresenter;
import timber.log.Timber;

public class LoggerDialog extends DialogFragment implements LoggerPresenter.Provider {

  private DialogLoggerBinding binding;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_logger, container, false);
    return binding.getRoot();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    binding.unbind();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    addOptionsPreferenceFragment();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    PowerManager.getRefWatcher(this).watch(this);
  }

  private void addOptionsPreferenceFragment() {
    final FragmentManager fragmentManager = getChildFragmentManager();
    if (fragmentManager.findFragmentByTag(LoggerPreferenceFragment.TAG) == null) {
      fragmentManager.beginTransaction()
          .replace(R.id.dialog_logger_options_container, new LoggerPreferenceFragment(),
              LoggerPreferenceFragment.TAG)
          .commit();
    }
  }

  @Override public void onPrepareLogContentRetrieval() {
    Timber.d("onPrepareLogContentRetrieval");
  }

  @Override public void onLogContentRetrieved(@NonNull String logLine) {
    Timber.d("onLogContentRetrieved: %s", logLine);
  }

  @Override public void onAllLogContentsRetrieved() {
    Timber.d("onAllLogContentsRetrieved");
  }

  @Override public void onLogDeleted(@NonNull String logId) {
    Timber.d("onLogDeleted: %s", logId);
  }
}