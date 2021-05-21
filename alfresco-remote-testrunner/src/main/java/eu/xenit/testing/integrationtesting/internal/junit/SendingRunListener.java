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
