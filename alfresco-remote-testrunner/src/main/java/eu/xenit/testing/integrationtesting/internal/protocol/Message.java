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
package eu.xenit.testing.integrationtesting.internal.protocol;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class Message {

    private final MessageType messageType;
    private final Object object;

    public Message(MessageType messageType, Object object) {
        this.messageType = messageType;
        this.object = object;
    }

    MessageType getMessageType() {
        return messageType;
    }

    Object getObject() {
        return object;
    }

    <T> T asType(Class<T> clazz) {
        if (!messageType.getSerializedClass().equals(clazz)) {
            throw new IllegalArgumentException(
                    "Message type " + messageType.name() + " serializes class " + messageType.getSerializedClass()
                            + " and not requested class " + clazz);
        }
        return (T) getObject();
    }

    Description getDescription() {
        return asType(Description.class);
    }

    Failure getFailure() {
        return asType(Failure.class);
    }

    Result getResult() {
        return asType(Result.class);
    }

    public void notify(RunNotifier runNotifier) {
        getMessageType().notifyOfMessage(runNotifier, this);
    }
}
