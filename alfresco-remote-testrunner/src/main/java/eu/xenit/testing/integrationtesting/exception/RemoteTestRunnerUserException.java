package eu.xenit.testing.integrationtesting.exception;

public class RemoteTestRunnerUserException extends RemoteTestRunnerException {

    public RemoteTestRunnerUserException(String message) {
        super(message);
    }

    public RemoteTestRunnerUserException(String message, String[] helpText) {
        super(message, helpText);
    }

    public RemoteTestRunnerUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteTestRunnerUserException(String message, String[] helpText, Throwable cause) {
        super(message, helpText, cause);
    }

}
