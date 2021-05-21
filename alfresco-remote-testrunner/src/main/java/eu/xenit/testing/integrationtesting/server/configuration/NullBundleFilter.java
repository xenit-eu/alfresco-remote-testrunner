package eu.xenit.testing.integrationtesting.server.configuration;

import eu.xenit.testing.integrationtesting.runner.CustomBundleFilter;
import org.osgi.framework.Bundle;

public class NullBundleFilter implements CustomBundleFilter {

    @Override
    public Bundle getBundleToUseAsSpringContext(Bundle[] bundles) {
        return null;
    }
}
