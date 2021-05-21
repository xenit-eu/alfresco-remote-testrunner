package eu.xenit.testing.integrationtesting.exception;

public class RemoteTestRunnerException extends RuntimeException {

    protected RemoteTestRunnerException(String message) {
        super(message);
    }

    protected RemoteTestRunnerException(String message, String[] helpText) {
        super(mergeHelpText(message, helpText));
    }

    protected RemoteTestRunnerException(String message, Throwable cause) {
        super(message, cause);
    }

    protected RemoteTestRunnerException(String message, String[] helpText, Throwable cause) {
        super(mergeHelpText(message, helpText), cause);
    }

    private static String mergeHelpText(String message, String[] helpText) {
        for (String text : helpText) {
            message += "\n * " + text;
        }
        return message;
    }

}
