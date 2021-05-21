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
package eu.xenit.testing.integrationtesting.server.configuration;

import eu.xenit.testing.integrationtesting.runner.CustomBundleFilter;
import java.util.Arrays;
import org.osgi.framework.Bundle;

public class SymbolicNameBundleFilter implements CustomBundleFilter {

    private final String symbolicName;

    public SymbolicNameBundleFilter(String symbolicName) {

        this.symbolicName = symbolicName;
    }

    @Override
    public Bundle getBundleToUseAsSpringContext(Bundle[] bundles) {
        return Arrays.stream(bundles).filter(bundle -> bundle.getSymbolicName().equals(symbolicName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No bundle with symbolic name '" + symbolicName + "' exists."));
    }
}
