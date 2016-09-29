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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static com.pyamsoft.powermanager.TestUtils.expected;

public class ManagerWifiTest {

  @Mock WearAwareManagerInteractor interactor;
  private ManagerWifi manager;

  @Before public void setUp() {
    interactor = Mockito.mock(ManagerWifiInteractor.class);
    manager = new ManagerWifi(interactor, Schedulers.immediate(), Schedulers.immediate());
  }

  @Test public void testBaseObservableEmptyStream() {
    Mockito.when(interactor.cancelJobs()).thenAnswer(new Answer<Observable<Boolean>>() {
      @Override public Observable<Boolean> answer(InvocationOnMock invocation) throws Throwable {
        return Observable.just(false);
      }
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

  @Test public void testBaseObservable() {
    Mockito.when(interactor.cancelJobs()).thenAnswer(new Answer<Observable<Boolean>>() {
      @Override public Observable<Boolean> answer(InvocationOnMock invocation) throws Throwable {
        // This is where we would cancel jobs, if we had any
        return Observable.just(true);
      }
    });

    Mockito.when(interactor.isManaged()).thenAnswer(new Answer<Observable<Boolean>>() {
      @Override public Observable<Boolean> answer(InvocationOnMock invocation) throws Throwable {
        // This is where we would cancel jobs, if we had any
        return Observable.just(true);
      }
    });

    final TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
    manager.baseObservable().subscribe(testSubscriber);

    testSubscriber.assertValue(true);
    testSubscriber.assertCompleted();
  }
}
