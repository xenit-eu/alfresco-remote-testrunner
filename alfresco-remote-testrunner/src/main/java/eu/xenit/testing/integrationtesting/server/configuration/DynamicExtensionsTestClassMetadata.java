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

import com.github.dynamicextensionsalfresco.webscripts.annotations.Transaction;
import com.github.dynamicextensionsalfresco.webscripts.annotations.TransactionType;
import eu.xenit.testing.integrationtesting.exception.RemoteTestRunnerUserException;
import eu.xenit.testing.integrationtesting.runner.CustomBundleFilter;
import eu.xenit.testing.integrationtesting.runner.UseSpringContextOfBundle;
import java.lang.annotation.Annotation;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class DynamicExtensionsTestClassMetadata {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicExtensionsTestClassMetadata.class);

    private final Class<?> testClass;
    private final Bundle testBundle;
    private final BundleContext bundleContext;

    public DynamicExtensionsTestClassMetadata(Class<?> testClass, BundleContext bundleContext) {
        this.testClass = testClass;
        this.testBundle = FrameworkUtil.getBundle(testClass);
        this.bundleContext = bundleContext;
    }

    public ApplicationContext getTargetApplicationContext() {
        return ContextUtils.getApplicationContext(getSpringContextBundle());
    }

    public ApplicationContext getTestApplicationContext() {
        return ContextUtils.getApplicationContext(getTestBundle());
    }

    public ClassLoader getTestClassLoader() {
        return getTestApplicationContext().getClassLoader();
    }

    public Class<?> getTestClass() throws ClassNotFoundException {
        return withTestClassLoader(testClass);
    }

    public Class<?> withTestClassLoader(Class<?> clz) throws ClassNotFoundException {
        return Class.forName(clz.getName(), true, getTestClassLoader());
    }

    private Bundle getTestBundle() {
        return testBundle;
    }

    private Bundle getSpringContextBundle() {
        CustomBundleFilter bundleFilter = getSpringContextBundleFilter();

        Bundle bundle = bundleFilter.getBundleToUseAsSpringContext(bundleContext.getBundles());
        if (bundle == null) {
            if (!(bundleFilter instanceof NullBundleFilter)) {
                LOGGER.warn("Bundle filter {} returned null, falling back test context", bundleFilter.getClass());
            }
            return getTestBundle();
        }

        return bundle;
    }

    private CustomBundleFilter getSpringContextBundleFilter() {
        UseSpringContextOfBundle springContextOfBundle = getAnnotationAndSupers(UseSpringContextOfBundle.class,
                testClass);
        if (springContextOfBundle == null) {
            return new NullBundleFilter();
        }

        if (!springContextOfBundle.bundleId().isEmpty() && !springContextOfBundle.filter()
                .equals(CustomBundleFilter.class)) {
            throw new RemoteTestRunnerUserException(
                    "The @UseSpringContextOfBundle annotation does not allow specifying both bundleId and filter",
                    new String[]{
                            "You can only use one Spring Context for every integration test class. You can annotate services that should be available for all bundles with @OsgiService.",
                    });
        }

        if (!springContextOfBundle.bundleId().isEmpty()) {
            return new SymbolicNameBundleFilter(springContextOfBundle.bundleId());
        } else if (!springContextOfBundle.filter().equals(CustomBundleFilter.class)) {
            try {
                return springContextOfBundle.filter().getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RemoteTestRunnerUserException("Bundle filter constructor threw an exception", e);
            }
        } else {
            throw new RemoteTestRunnerUserException(
                    "The @UseSpringContextOfBundle annotation requires either a bundleId or a filter to be specified");
        }
    }

    private static <T extends Annotation> T getAnnotationAndSupers(Class<T> annotation, Class<?> cls) {
        if (cls.equals(Object.class)) {
            return null;
        }
        T ret = cls.getAnnotation(annotation);
        if (ret == null) {
            return getAnnotationAndSupers(annotation, cls.getSuperclass());
        }
        return ret;
    }

    public Transaction getTransactionAnnotation() {
        Transaction transaction = getAnnotationAndSupers(Transaction.class, testClass);
        if (transaction != null) {
            return transaction;
        }
        return new NullTransaction();
    }

    private static class NullTransaction implements Transaction {

        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }

        @Override
        public TransactionType value() {
            return TransactionType.REQUIRED;
        }

        @Override
        public boolean readOnly() {
            return false;
        }

        @Override
        public int bufferSize() {
            return 4096;
        }
    }
}
