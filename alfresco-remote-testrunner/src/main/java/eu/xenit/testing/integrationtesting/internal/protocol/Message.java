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

    private <T> T asType(Class<T> clazz) {
        if(!messageType.getSerializedClass().equals(clazz)) {
            throw new IllegalArgumentException("Message type "+messageType.name()+" serializes class "+messageType.getSerializedClass()+" and not requested class "+clazz);
        }
        return (T)getObject();
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
