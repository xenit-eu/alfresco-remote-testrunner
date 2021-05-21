/**
 * Copyright 2021 Xenit Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.xenit.testing.integrationtesting.tests.local;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

/**
 * Tests the AlfrescoTestRunner client side remote test runner
 * Created by Michiel Huygen on 22/04/2016.
 */
public class AlfrescoIntegrationTest {

    private RunNotifier notifier;
    private RunListener listener;

    @Before
    public void Setup() {
        listener = mock(RunListener.class);
        notifier = new RunNotifier();
        notifier.addListener(listener);
    }

    @Test
    public void TestSuccess() throws Exception {
        runTest("TestSuccess");

        verify(listener).testStarted(any(Description.class));
        verify(listener).testFinished(any(Description.class));
    }

    @Test
    public void TestExpectedFail() throws Exception {

        runTest("TestExpectedFail");

        verify(listener).testStarted(any(Description.class));
        verify(listener).testFinished(any(Description.class));
    }

    @Test
    public void TestFail() throws Exception {

        runTest("TestFail");

        verify(listener).testStarted(any(Description.class));
        verify(listener).testFailure(any(Failure.class));
        verify(listener).testFinished(any(Description.class));
    }

    @Test
    public void TestAssumeFail() throws Exception {

        runTest("TestAssumeFail");

        verify(listener).testStarted(any(Description.class));
        verify(listener).testAssumptionFailure(any(Failure.class));
        verify(listener).testFinished(any(Description.class));
    }

    @Test
    public void TestIgnore() throws Exception {

        runTest("TestIgnore");

        verify(listener).testIgnored(any(Description.class));
    }

    @Test
    public void TestAssertFail() throws Exception {

        runTest("TestAssertFail");

        verify(listener).testStarted(any(Description.class));
        verify(listener).testFailure(any(Failure.class));
        verify(listener).testFinished(any(Description.class));
    }

    @Test
    public void TestPrintln() throws NoTestsRemainException, InitializationError {
        final java.io.PrintStream ori = System.out;

        final String[] output = {""};

        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                ori.write(b);
                output[0] += (char)b;
            }
        }));

        runTest("TestPrintln");

        System.setOut(ori);

        assertTrue(output[0].contains("Line printed\n"));
    }

    @Test
    public void TestStreamResults() throws NoTestsRemainException, InitializationError {
        runTest("TestStreamResults");
        // TODO: Maybe add support for fast streaming later on?
    }

    @Test
    public void TestStreamResultsLong() throws NoTestsRemainException, InitializationError {
        //TODO: this should normally stream results back,
        // so write a test that monitors when the results are received
        // and see if there is time between each receive :)
        runTest("TestStreamResultsLong");
    }

    protected void runTest(final String methodName) throws InitializationError, NoTestsRemainException {
        AlfrescoTestRunner run = new AlfrescoTestRunner(
                eu.xenit.testing.integrationtesting.tests.remote.AlfrescoIntegrationTest.class);
        run.filter(new Filter() {
            @Override
            public boolean shouldRun(Description description) {
                return description.getMethodName().equals(methodName);
            }

            @Override
            public String describe() {
                return "lala";
            }
        });

        run.run(notifier);
    }
}
