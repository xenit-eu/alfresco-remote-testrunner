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
package eu.xenit.testing.integrationtesting.runner;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.xenit.testing.integrationtesting.internal.junit.SendingRunListener;
import eu.xenit.testing.integrationtesting.internal.protocol.TestResultsSender;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.StubbedResponse;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.InputStreamEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class AlfrescoTestRunnerTests {

    public static class ExampleTest {
        @Test
        public void test() {
            assertTrue(true);
        }
    }

    private RunNotifier notifier;
    private RunListener listener;
    private Description methodUnderTest;

    @Before
    public void Setup()
    {
        listener = mock(RunListener.class);
        notifier = new RunNotifier();
        notifier.addListener(listener);

        methodUnderTest = Description.createTestDescription(ExampleTest.class, "test");
    }

    @Test
    public void test_ok() throws Exception {
        // stubbing JUnit server side -> reply from the server is written to 'serverResponseStream'
        ByteArrayOutputStream serverResponseStream = new ByteArrayOutputStream();
        RunNotifier remoteNotifier = _createServerStubNotifier(serverResponseStream);
        remoteNotifier.fireTestStarted(methodUnderTest);
        remoteNotifier.fireTestFinished(methodUnderTest);

        // mock the server reply -> return HTTP 200 + 'serverResponseStream'
        Executor executor = _createHttpClientExecutorStub(StubbedResponse.HTTP_200, serverResponseStream.toByteArray());

        // class under test
        Runner runner = new AlfrescoTestRunner(ExampleTest.class, executor);
        runner.run(notifier);

        verify(listener).testStarted(any(Description.class));
        verify(listener).testFinished(any(Description.class));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void test_failed() throws Exception {
        // stubbing JUnit server side -> reply from the server is written to 'serverResponseStream'
        ByteArrayOutputStream serverResponseStream = new ByteArrayOutputStream();
        RunNotifier remoteNotifier = _createServerStubNotifier(serverResponseStream);
        remoteNotifier.fireTestStarted(methodUnderTest);
        remoteNotifier.fireTestFailure(new Failure(methodUnderTest, new RuntimeException("test failed")));

        // mock the server reply -> return HTTP 200 + 'serverResponseStream'
        Executor executor = _createHttpClientExecutorStub(StubbedResponse.HTTP_200, serverResponseStream.toByteArray());

        // class under test
        Runner runner = new AlfrescoTestRunner(ExampleTest.class, executor);
        runner.run(notifier);

        verify(listener).testStarted(any(Description.class));
        verify(listener).testFailure(any(Failure.class));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void test_ignored() throws Exception {
        // stubbing JUnit server side -> reply from the server is written to 'serverResponseStream'
        ByteArrayOutputStream serverResponseStream = new ByteArrayOutputStream();
        RunNotifier remoteNotifier = _createServerStubNotifier(serverResponseStream);
        remoteNotifier.fireTestIgnored(methodUnderTest);

        // mock the server reply -> return HTTP 200 + 'serverResponseStream'
        Executor executor = _createHttpClientExecutorStub(StubbedResponse.HTTP_200, serverResponseStream.toByteArray());

        // class under test
        Runner runner = new AlfrescoTestRunner(ExampleTest.class, executor);
        runner.run(notifier);

        verify(listener).testIgnored(any(Description.class));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void test_http_401() throws Exception {
        // mock the server reply -> return HTTP 401
        Executor executor = _createHttpClientExecutorStub(StubbedResponse.HTTP_401, new byte[0]);

        // class under test
        Runner runner = new AlfrescoTestRunner(ExampleTest.class, executor);
        runner.run(notifier);

        verify(listener).testFailure(any(Failure.class));
        verifyNoMoreInteractions(listener);
    }


    @Test
    public void test_connection_refused() throws Exception {
        // setup
        RunListener listener = mock(RunListener.class);
        RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        Executor executor = mock(Executor.class);
        when(executor.execute(any(Request.class))).thenThrow(
                new HttpHostConnectException(
                        new ConnectException("Connection refused"),
                        new HttpHost("localhost", 8080),
                        InetAddress.getLocalHost()
                ));

        // class under test
        Runner runner = new AlfrescoTestRunner(ExampleTest.class, executor);
        runner.run(notifier);

        // if we expect the client to indicate test has started -> uncomment
        // but for now, we wait for the backend runner to indicate a test has started
        // verify(listener).testStarted(any(Description.class));
        verify(listener).testFailure(any(Failure.class));
        verifyNoMoreInteractions(listener);
    }

    private Executor _createHttpClientExecutorStub(StatusLine status, byte[] serverResponse) throws IOException {
        Executor executor = mock(Executor.class);
        InputStreamEntity entity = new InputStreamEntity(new ByteArrayInputStream(serverResponse));
        when(executor.execute(any(Request.class))).thenReturn(new StubbedResponse(status, entity));
        return executor;
    }

    private RunNotifier _createServerStubNotifier(ByteArrayOutputStream serverResponseStream) {
        TestResultsSender sender = new TestResultsSender(serverResponseStream);
        SendingRunListener runListener = new SendingRunListener(sender);
        RunNotifier remoteNotifier = new RunNotifier();
        remoteNotifier.addListener(runListener);
        return remoteNotifier;
    }

}
