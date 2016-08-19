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

package com.pyamsoft.powermanager.dagger.main;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.BuildConfig;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.powermanager.app.manager.ManagerSettingsPagerAdapter;
import com.pyamsoft.powermanager.app.overview.OverviewSelectionBus;
import com.pyamsoft.powermanager.dagger.main.MainPresenter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import rx.schedulers.Schedulers;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = PowerManager.class, sdk = 23)
public class MainPresenterTest {

  @CheckResult @NonNull public static MainPresenter getPresenter() {
    return new MainPresenter(Schedulers.immediate(), Schedulers.immediate());
  }

  @Test public void test_busResponse() {
    final MainPresenter presenter = getPresenter();
    final String type = ManagerSettingsPagerAdapter.TYPE_WIFI;
    final MainPresenter.MainView mockMainView = Mockito.mock(MainPresenter.MainView.class);
    Mockito.doAnswer(invocation -> {
      Assert.assertNotNull(invocation.getArguments());
      Assert.assertEquals(1, invocation.getArguments().length);
      Assert.assertEquals(type, invocation.getArguments()[0]);
      return null;
    }).when(mockMainView).loadFragmentFromOverview(type);

    // Register onto the bus
    presenter.bindView(mockMainView);
    presenter.resume();

    // Post the event
    OverviewSelectionBus.get().post(new OverviewSelectionBus.OverviewSelectionEvent(type));

    presenter.pause();
    presenter.unbindView();
  }

  @Test public void test_busRegister() {
    final MainPresenter presenter = getPresenter();
    final String type = ManagerSettingsPagerAdapter.TYPE_WIFI;
    final MainPresenter.MainView mockMainView = Mockito.mock(MainPresenter.MainView.class);
    Mockito.doAnswer(invocation -> {
      throw new IllegalStateException("This should not be called");
    }).when(mockMainView).loadFragmentFromOverview(type);

    // Post the event to nothing
    OverviewSelectionBus.get().post(new OverviewSelectionBus.OverviewSelectionEvent(type));

    // Register onto the bus
    presenter.bindView(mockMainView);
    presenter.resume();

    presenter.pause();
    presenter.unbindView();
  }

  @Test public void test_busUnregister() {
    final MainPresenter presenter = getPresenter();
    final String type = ManagerSettingsPagerAdapter.TYPE_WIFI;
    final MainPresenter.MainView mockMainView = Mockito.mock(MainPresenter.MainView.class);
    Mockito.doAnswer(invocation -> {
      throw new IllegalStateException("This should not be called");
    }).when(mockMainView).loadFragmentFromOverview(type);

    // Register onto the bus
    presenter.bindView(mockMainView);
    presenter.resume();

    presenter.pause();
    presenter.unbindView();

    // Post the event to nothing
    OverviewSelectionBus.get().post(new OverviewSelectionBus.OverviewSelectionEvent(type));
  }
}
