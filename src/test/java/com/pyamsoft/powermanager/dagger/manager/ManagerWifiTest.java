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

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static com.pyamsoft.powermanager.TestUtils.expected;
import static com.pyamsoft.powermanager.TestUtils.log;
import static org.junit.Assert.assertEquals;

public class ManagerWifiTest {

  @Mock WearAwareManagerInteractor interactor;
  private ManagerWifi manager;

  @Before public void setUp() {
    interactor = Mockito.mock(ManagerWifiInteractor.class);
    manager = new ManagerWifi(interactor, Schedulers.immediate(), Schedulers.immediate());
  }

  /**
   * Make sure that the base observable does not continue if the job cancelling fails for any
   * reason
   */
  @Test public void testBaseObservableEmptyStream() {
    Mockito.when(interactor.cancelJobs()).thenAnswer(invocation -> {
      log("Fail job cancel");
      return Observable.just(false);
    });

    final TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
    manager.baseObservable().subscribe(testSubscriber);

    testSubscriber.assertNoValues();
    testSubscriber.assertCompleted();

    // Also expect this assertion to fail
    try {
      // This will fail because the observable stream returns empty, meaning no value is ever emitted
      testSubscriber.assertValue(false);
    } catch (AssertionError e) {
      expected("Expected error: %s", e);
    }
  }

  /**
   * Make sure the base observable continues properly if job cancelling succeeds
   */
  @Test public void testBaseObservable() {
    Mockito.when(interactor.cancelJobs()).thenAnswer(invocation -> {
      log("Succeed job cancel");
      return Observable.just(true);
    });

    Mockito.when(interactor.isManaged()).thenAnswer(invocation -> Observable.just(true));

    final TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
    manager.baseObservable().subscribe(testSubscriber);

    testSubscriber.assertValue(true);
    testSubscriber.assertCompleted();
  }

  @Test public void testCleanup() {
    final AtomicInteger count = new AtomicInteger(0);
    Mockito.doAnswer(invocation -> {
      log("Destroyed");
      count.incrementAndGet();
      return null;
    }).when(interactor).destroy();

    // Assert that before we destroy, count is 0
    assertEquals(0, count.get());

    // Call cleanup, does the thing
    manager.cleanup();

    // Destroy should bump count
    assertEquals(1, count.get());
  }
}
