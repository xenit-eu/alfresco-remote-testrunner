package eu.xenit.testing.integrationtesting.server.configuration;

import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DynamicExtensionsTestClassMetadataFactory {

    private final BundleContext bundleContext;

    @Autowired
    public DynamicExtensionsTestClassMetadataFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public DynamicExtensionsTestClassMetadata createMetadataForClass(Class<?> clazz) {
        return new DynamicExtensionsTestClassMetadata(clazz, bundleContext);
    }

}
