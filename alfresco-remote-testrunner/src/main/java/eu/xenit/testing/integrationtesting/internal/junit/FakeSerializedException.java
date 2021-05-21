package eu.xenit.testing.integrationtesting.internal.junit;

import java.io.Serializable;

public class FakeSerializedException extends Exception implements Serializable {


    public FakeSerializedException(Throwable exception) {
        super(exception.toString());
        setStackTrace(exception.getStackTrace());
        if (exception.getCause() != exception && exception.getCause() != null) {
            initCause(new FakeSerializedException(exception.getCause()));
        }
    }

    @Override
    public String toString() {
        return super.getLocalizedMessage();
    }
}
