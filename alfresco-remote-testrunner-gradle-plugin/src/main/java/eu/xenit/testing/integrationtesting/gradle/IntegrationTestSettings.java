package eu.xenit.testing.integrationtesting.gradle;

import com.github.dynamicextensionsalfresco.gradle.configuration.Repository;
import java.util.Collections;
import java.util.List;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;

public class IntegrationTestSettings {

    private ListProperty<String> integrationTestPackages;
    private Property<Repository> repository;

    public IntegrationTestSettings(ObjectFactory objectFactory) {
        integrationTestPackages = objectFactory.listProperty(String.class);
        repository = objectFactory.property(Repository.class);
    }

    public ListProperty<String> getIntegrationTestPackages() {
        return integrationTestPackages;
    }

    public void setIntegrationTestPackages(List<String> integrationTestPackages) {
        this.integrationTestPackages.set(integrationTestPackages);
    }

    public void setIntegrationTestPackage(String integrationTestPackage) {
        this.integrationTestPackages.set(Collections.singletonList(integrationTestPackage));
    }

    public void setRepository(Repository repository) {
        this.repository.set(repository);
    }

    public Property<Repository> getRepository() {
        return repository;
    }
}
