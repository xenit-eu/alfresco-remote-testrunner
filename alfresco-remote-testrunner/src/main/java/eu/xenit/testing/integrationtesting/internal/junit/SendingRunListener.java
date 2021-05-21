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
package eu.xenit.testing.integrationtesting.internal.junit;

import eu.xenit.testing.integrationtesting.internal.protocol.MessageType;
import eu.xenit.testing.integrationtesting.internal.protocol.TestResultsSender;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class SendingRunListener extends RunListener {

    private final TestResultsSender sender;

    public SendingRunListener(TestResultsSender sender) {
        this.sender = sender;
    }

    @Override
    public void testRunStarted(Description description) {
        sender.send(MessageType.RUN_STARTED, description);
    }

    @Override
    public void testRunFinished(Result result) {
        sender.send(MessageType.RUN_FINISHED, result);
    }

    @Override
    public void testStarted(Description description) {
        sender.send(MessageType.TEST_STARTED, description);
    }

    @Override
    public void testFinished(Description description) {
        sender.send(MessageType.TEST_FINISHED, description);
    }

    @Override
    public void testFailure(Failure failure) {
        Failure fakeFailure = new Failure(failure.getDescription(), new FakeSerializedException(failure.getException()));
        sender.send(MessageType.TEST_FAILURE, fakeFailure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        Failure fakeFailure = new Failure(failure.getDescription(), new FakeSerializedException(failure.getException()));
        sender.send(MessageType.TEST_ASSUMPTION_FAILURE, fakeFailure);
    }

    @Override
    public void testIgnored(Description description) {
        sender.send(MessageType.TEST_IGNORED, description);
    }
}
