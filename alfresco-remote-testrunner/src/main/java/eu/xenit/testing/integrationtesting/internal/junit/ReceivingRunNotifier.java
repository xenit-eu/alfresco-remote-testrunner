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
