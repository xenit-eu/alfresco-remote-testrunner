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

import java.io.Serializable;


/**
 * This is an internal exception that is normally not shown to users.
 * <p>
 * This exception is a client-side standin for an exception that is thrown on the remote.
 * It tries to mask itself by taking on the message and stacktrace of the original exception on the remote.
 * <p>
 * We need this wrapper for 2 reasons:
 * 1. The exception that was thrown on the remote is not serializable. We can't send it and its data over the wire because we can't serialize it.
 * 2. The exception class from the remote is not known locally. We can't deserialize the exception we have received, so it is impossible to reconstruct it.
 * <p>
 * Because we can not know beforehand if the exception will be in any of these 2 cases, we will have to deal with it and always convert all remote exceptions to this exception.
 */
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
