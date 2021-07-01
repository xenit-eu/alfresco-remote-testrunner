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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class ProtocolVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int majorVersion;
    private final Set<String> features;

    ProtocolVersion(int protocolVersion) {
        this.majorVersion = protocolVersion;
        features = new HashSet<>();
        features.add("java:" + System.getProperty("java.version"));
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public Set<String> getFeatures() {
        return Collections.unmodifiableSet(features);
    }

    public boolean isCompatibleWith(ProtocolVersion reader) {
        return reader.getMajorVersion() == this.getMajorVersion();
    }

    @Override
    public String toString() {
        return Integer.toString(majorVersion) + '[' + features + ']';
    }
}
