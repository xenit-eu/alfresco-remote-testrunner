package eu.xenit.testing.integrationtesting.exception;

public class RemoteTestRunnerClientSideSystemException extends RemoteTestRunnerException {

    public RemoteTestRunnerClientSideSystemException(String message) {
        super(message + "\n" +
                "This is a client-side exception thrown to indicate a remote test runner problem on the serverside.\n" +
                "See alfresco log for more details and a server-side stacktrace of this exception.");
        fillInStackTrace();
        StackTraceElement[] stackTraceElements = getStackTrace();
        StackTraceElement[] newStackTrace = new StackTraceElement[stackTraceElements.length + 1];
        newStackTrace[0] = new StackTraceElement("This is the client-side error", "", "see-alfresco-logs", 0);
        for (int i = 0; i < stackTraceElements.length; i++) {
            newStackTrace[i + 1] = stackTraceElements[i];
        }
        setStackTrace(newStackTrace);
    }
}
