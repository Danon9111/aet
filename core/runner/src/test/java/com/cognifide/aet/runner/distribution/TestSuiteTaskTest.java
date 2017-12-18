/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.runner.distribution;

import com.google.inject.Key;

import com.cognifide.aet.runner.conversion.SuiteIndexWrapper;
import com.cognifide.aet.runner.testsuitescope.TestSuiteScope;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Provider;
import javax.jms.Destination;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestSuiteTaskTest {

  private TestSuiteTask tested;

  @Mock
  private Provider<TestLifeCycle> testLifeCycleProvider;

  @Mock
  private TestSuiteScope scope;

  @Mock
  private SuiteIndexWrapper suiteIndexWrapper;

  @Mock
  private Destination destination;

  @Before
  public void setUp() throws Exception {
    tested = new TestSuiteTask(testLifeCycleProvider, scope, RunnerMode.ONLINE, suiteIndexWrapper, destination,
            false);
  }

  @Test
  public void run_expectTestSuiteRunAndDestinationSeededInScope() throws Exception {
    tested.run();
    verify(scope, times(1)).enter();
    verify(scope, times(1)).seed(Matchers.<Key<SuiteIndexWrapper>>any(), eq(suiteIndexWrapper));
    verify(scope, times(1)).seed(Matchers.<Key<Destination>>any(), eq(destination));
    verify(scope, times(1)).exit();
  }
}
