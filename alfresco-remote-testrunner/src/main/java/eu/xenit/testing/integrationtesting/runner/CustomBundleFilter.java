package eu.xenit.testing.integrationtesting.runner;

import org.osgi.framework.Bundle;

/**
 * Interface that can be implemented by the user to specify which bundle's context to use for loading the test class
 * Created by Michiel Huygen on 26/04/2016.
 */
public interface CustomBundleFilter {

    Bundle getBundleToUseAsSpringContext(Bundle[] bundles);
}
