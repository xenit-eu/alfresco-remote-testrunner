package eu.xenit.testing.integrationtesting.internal.protocol;

import eu.xenit.testing.integrationtesting.exception.RemoteTestRunnerClientSideSystemException;
import java.util.function.BiConsumer;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public enum MessageType {
    RUN_STARTED(Description.class, (runNotifier, message) -> runNotifier.fireTestRunStarted(message.getDescription())),
    RUN_FINISHED(Result.class, (runNotifier, message) -> runNotifier.fireTestRunFinished(message.getResult())),
    TEST_STARTED(Description.class, (runNotifier, message) -> runNotifier.fireTestStarted(message.getDescription())),
    TEST_FINISHED(Description.class, (runNotifier, message) -> runNotifier.fireTestFinished(message.getDescription())),
    TEST_FAILURE(Failure.class, (runNotifier, message) -> runNotifier.fireTestFailure(message.getFailure())),
    TEST_IGNORED(Description.class, (runNotifier, message) -> runNotifier.fireTestIgnored(message.getDescription())),
    TEST_ASSUMPTION_FAILURE(Failure.class,
            (runNotifier, message) -> runNotifier.fireTestAssumptionFailed(message.getFailure())),
    LOG_STDOUT(String.class, (runNotifier, message) -> System.out.print(message.getObject())),
    LOG_STDERR(String.class, (runNotifier, message) -> System.err.print(message.getObject())),
    SYSTEM_ERROR(String.class, (runNotifier, message) -> {
        throw new RemoteTestRunnerClientSideSystemException((String) message.getObject());
    });

    private final Class<?> serializedClass;
    private final BiConsumer<RunNotifier, Message> notifier;

    MessageType(Class<?> serializedClass, BiConsumer<RunNotifier, Message> notifier) {
        this.serializedClass = serializedClass;
        this.notifier = notifier;
    }

    public Class<?> getSerializedClass() {
        return serializedClass;
    }

    void notifyOfMessage(RunNotifier runNotifier, Message message) {
        this.notifier.accept(runNotifier, message);
    }
}
