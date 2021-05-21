package eu.xenit.testing.integrationtesting.server.discovery;

import eu.xenit.testing.integrationtesting.exception.RemoteTestRunnerUserException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DynamicExtensionsClassDiscovery {

    private final BundleContext bundleContext;

    @Autowired
    public DynamicExtensionsClassDiscovery(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private Set<Class<?>> findClasses(String name) {
        Set<Class<?>> result = new HashSet<>();
        final Bundle[] bundles = bundleContext.getBundles();
        for (Bundle bundle : bundles) {
            if (bundle.getState() == Bundle.ACTIVE) {
                if (!isDynamicExtensionsBundle(bundle)) {
                    continue;
                }
                try {
                    Class<?> c = bundle.loadClass(name);
                    result.add(c);
                } catch (ClassNotFoundException e) {
                    // No problem, this bundle doesn't have the class
                }
            }
        }
        return result;
    }

    public Class<?> getClass(String name) {
        Set<Class<?>> discoveredClasses = findClasses(name);
        switch (discoveredClasses.size()) {
            case 0:
                throw new RemoteTestRunnerUserException("Test class could not be found in any bundle", new String[]{
                        "Check the Dynamic Extensions dashboard to verify that your integration test bundle has status 'active'."
                });
            case 1:
                return discoveredClasses.iterator().next();
            default:
                String allBundles = discoveredClasses.stream()
                        .map(FrameworkUtil::getBundle)
                        .map(bundle -> bundle.getSymbolicName() + " [" + bundle.getBundleId() + "]")
                        .collect(Collectors.joining(", "));
                throw new RemoteTestRunnerUserException(
                        "Test class '" + name + "' is found in multiple bundles: " + allBundles, new String[]{
                        "Did you rename your integration test bundle Symbolic Name?",
                        "Did you rename your Gradle project?",
                        "Did you reuse package names/class names for different integration test bundles?",
                        "Perhaps an old version of your integration tests (with a different name) is still deployed in Dynamic Extensions? Check the dashboard to remove the bundle.",
                });
        }
    }

    private static boolean isDynamicExtensionsBundle(Bundle bundle) {
        return "true".equalsIgnoreCase(bundle.getHeaders().get("Alfresco-Dynamic-Extension"));
    }

}
