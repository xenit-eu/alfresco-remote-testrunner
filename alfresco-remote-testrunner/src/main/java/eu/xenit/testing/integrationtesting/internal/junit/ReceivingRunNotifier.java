package eu.xenit.testing.integrationtesting.internal.junit;

import eu.xenit.testing.integrationtesting.internal.protocol.Message;
import eu.xenit.testing.integrationtesting.internal.protocol.TestResultsReceiver;
import java.io.IOException;
import org.junit.runner.notification.RunNotifier;

public class ReceivingRunNotifier {

    private final RunNotifier runNotifier;

    public ReceivingRunNotifier(RunNotifier runNotifier) {

        this.runNotifier = runNotifier;
    }

    public void receiveAll(TestResultsReceiver receiver) throws IOException {
        Message message;
        while((message = receiver.receive()) != null) {
            message.notify(runNotifier);
        }
    }

}
