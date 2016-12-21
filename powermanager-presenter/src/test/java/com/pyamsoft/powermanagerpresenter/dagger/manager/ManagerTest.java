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

package com.pyamsoft.powermanagerpresenter.manager;

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

public class ManagerTest {

  // Even though these are for Wifi, the same code should be running for each of the device components
  // so testing against just Wifi "should" be fine
  @Mock ManagerWifiInteractorImpl interactor;
  private ManagerWifiImpl manager;

  @Before public void setUp() {
    interactor = Mockito.mock(ManagerWifiInteractorImpl.class);
    manager = new ManagerWifiImpl(interactor, Schedulers.immediate(), Schedulers.immediate());
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

  /**
   * Test the clean up function of the manager
   */
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

  @Test public void testQueueSetComplete() {
    final AtomicInteger count = new AtomicInteger(0);
    Mockito.doAnswer(invocation -> {
      log("Queue set success");
      count.incrementAndGet();
      return null;
    }).when(interactor).queueEnableJob();

    Mockito.when(interactor.cancelJobs()).thenAnswer(invocation -> {
      log("Succeed job cancel");
      return Observable.just(true);
    });

    Mockito.when(interactor.isManaged()).thenAnswer(invocation -> Observable.just(true));

    Mockito.when(interactor.isOriginalStateEnabled())
        .thenAnswer(invocation -> Observable.just(true));

    // Assert that before we destroy, count is 0
    assertEquals(0, count.get());

    manager.queueSet();

    // Queue should bump count
    assertEquals(1, count.get());
  }

  @Test public void testQueueSetNotManaged() {
    final AtomicInteger count = new AtomicInteger(0);
    Mockito.doAnswer(invocation -> {
      log("Queue set success");
      count.incrementAndGet();
      return null;
    }).when(interactor).queueEnableJob();

    Mockito.when(interactor.cancelJobs()).thenAnswer(invocation -> {
      log("Succeed job cancel");
      return Observable.just(true);
    });

    Mockito.when(interactor.isManaged()).thenAnswer(invocation -> Observable.just(false));

    Mockito.when(interactor.isOriginalStateEnabled())
        .thenAnswer(invocation -> Observable.just(true));

    // Assert that before we destroy, count is 0
    assertEquals(0, count.get());

    manager.queueSet();

    // Queue should not bump count
    assertEquals(0, count.get());
  }

  @Test public void testQueueSetNotOriginalStateEnabled() {
    final AtomicInteger count = new AtomicInteger(0);
    Mockito.doAnswer(invocation -> {
      log("Queue set success");
      count.incrementAndGet();
      return null;
    }).when(interactor).queueEnableJob();

    Mockito.when(interactor.cancelJobs()).thenAnswer(invocation -> {
      log("Succeed job cancel");
      return Observable.just(true);
    });

    Mockito.when(interactor.isManaged()).thenAnswer(invocation -> Observable.just(true));

    Mockito.when(interactor.isOriginalStateEnabled())
        .thenAnswer(invocation -> Observable.just(false));

    // Assert that before we destroy, count is 0
    assertEquals(0, count.get());

    manager.queueSet();

    // Queue should not bump count
    assertEquals(0, count.get());
  }
}
