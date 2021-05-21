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
