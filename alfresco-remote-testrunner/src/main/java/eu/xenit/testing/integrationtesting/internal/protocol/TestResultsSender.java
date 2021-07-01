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

import eu.xenit.testing.integrationtesting.exception.RemoteTestRunnerInternalException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Serializable;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;
import org.apache.http.util.EncodingUtils;

public class TestResultsSender {

    public static final ProtocolVersion PROTOCOL_VERSION = new ProtocolVersion(1);

    private final OutputStreamWriter outputStream;

    public TestResultsSender(OutputStream outputStream) {
        this.outputStream = new OutputStreamWriter(outputStream);
        send(MessageType.PROTOCOL_VERSION, PROTOCOL_VERSION);
    }

    public void send(MessageType messageType, Serializable object) {
        send0(messageType, object, false);
    }

    private void send0(MessageType messageType, Serializable object, boolean failOnError) {
        try {
            String serializedObject = Base64.encodeBase64String(SerializationUtils.serialize(object));
            outputStream.append(messageType.name());
            outputStream.write(" ");
            outputStream.write(serializedObject);
            outputStream.write("\n");
            outputStream.flush();
        } catch (Exception exception) {
            if (failOnError) {
                throw new RemoteTestRunnerInternalException(exception);
            }
            ByteArrayOutputStream exceptionStack = new ByteArrayOutputStream();
            exception.printStackTrace(new PrintStream(exceptionStack));
            send0(MessageType.SYSTEM_ERROR, exceptionStack.toString(), true);
        }
    }

    public void captureAndForward(Runnable runnable) {
        PrintStream origOut = System.out;
        PrintStream origErr = System.err;

        PrintStream newOut = new ProxyStream(origOut, MessageType.LOG_STDOUT, this);
        PrintStream newErr = new ProxyStream(origErr, MessageType.LOG_STDERR, this);

        try {
            System.setOut(newOut);
            System.setErr(newErr);
            //noinspection NestedTryStatement
            try {
                runnable.run();
            } finally {
                newOut.flush();
                newErr.flush();
            }
        } catch (Exception e) {
            send(MessageType.SYSTEM_ERROR, e.toString());
            throw new RemoteTestRunnerInternalException(e);
        } finally {
            System.setOut(origOut);
            System.setErr(origErr);
        }
    }

    private static class ProxyStream extends PrintStream {

        private final MessageType messageType;
        private final TestResultsSender testResultsSender;

        public ProxyStream(PrintStream original, MessageType messageType, TestResultsSender testResultsSender) {
            super(original);
            this.messageType = messageType;
            this.testResultsSender = testResultsSender;
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            super.write(buf, off, len);
            testResultsSender.send(messageType, EncodingUtils.getAsciiString(buf, off, len));
        }
    }
}
