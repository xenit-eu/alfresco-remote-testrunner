package eu.xenit.testing.integrationtesting.exception;

public class RemoteTestRunnerInternalException extends RemoteTestRunnerException {

    public RemoteTestRunnerInternalException(Throwable cause) {
        super(cause.toString(), new String[]{
                "This is probably not your fault, but an internal problem in the remote test runner.",
                "It might be a transient issue, so you may try again."
        }, cause);
    }

}
