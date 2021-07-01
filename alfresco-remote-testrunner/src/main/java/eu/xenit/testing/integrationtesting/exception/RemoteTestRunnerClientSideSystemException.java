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
package eu.xenit.testing.integrationtesting.exception;

/**
 * This exception is thrown only on the client side, when the remote failed in such an unrecoverable way that it is not able
 * to send a properly serialized exception to the client anymore.
 */
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
