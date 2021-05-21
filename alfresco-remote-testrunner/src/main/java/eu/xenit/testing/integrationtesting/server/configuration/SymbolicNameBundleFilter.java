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
