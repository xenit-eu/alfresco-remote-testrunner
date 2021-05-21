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

import org.osgi.framework.*;
import org.springframework.context.ApplicationContext;

/**
 * Static utilities for finding an ApplicationContext in the OSGi environment.
 *
 * @author Laurent Van der Linden
 */
final class ContextUtils {
    private ContextUtils() {

    }

    public static BundleContext getBundleContext() {
        final Bundle bundle = FrameworkUtil.getBundle(ContextUtils.class);
        return bundle.getBundleContext();
    }

    private static ApplicationContext findApplicationContext(final String bundleName) {
        try {
            final BundleContext bundleContext = getBundleContext();
            if (bundleContext != null) {
                ServiceReference<?>[] references = bundleContext.getAllServiceReferences(
                        ApplicationContext.class.getName(),
                        String.format("(org.springframework.context.service.name=%s)", bundleName)
                );
                if (references != null && references.length > 0) {
                    final ServiceReference<?> contextServiceReference = references[0];
                    return (ApplicationContext) bundleContext.getService(contextServiceReference);
                }
            }
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static ApplicationContext getApplicationContext(String bundleName) {
        ApplicationContext applicationContext = findApplicationContext(bundleName);
        if(applicationContext == null) {
            throw new IllegalStateException("Cannot load spring context of bundle "+bundleName);
        }
        return applicationContext;
    }

    public static ApplicationContext getApplicationContext(Bundle bundle) {
        return getApplicationContext(bundle.getSymbolicName());
    }
}
