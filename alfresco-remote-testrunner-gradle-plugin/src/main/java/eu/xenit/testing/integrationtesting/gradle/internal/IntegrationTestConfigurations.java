package eu.xenit.testing.integrationtesting.gradle.internal;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyHandler;

public final class IntegrationTestConfigurations {

    private final Configuration integrationTestingBundle;
    private final Configuration integrationTestImplementationRemote;
    private final Configuration integrationTestImplementationLocal;

    private IntegrationTestConfigurations(Configuration integrationTestingBundle,
            Configuration integrationTestImplementationRemote,
            Configuration integrationTestImplementationLocal) {
        this.integrationTestingBundle = integrationTestingBundle;
        this.integrationTestImplementationRemote = integrationTestImplementationRemote;
        this.integrationTestImplementationLocal = integrationTestImplementationLocal;
    }

    public Configuration getIntegrationTestingBundle() {
        return integrationTestingBundle;
    }

    public Configuration getIntegrationTestImplementationRemote() {
        return integrationTestImplementationRemote;
    }

    public Configuration getIntegrationTestImplementationLocal() {
        return integrationTestImplementationLocal;
    }

    public static IntegrationTestConfigurations create(Project project) {
        ConfigurationContainer configurations = project.getConfigurations();
        DependencyHandler dependencyHandler = project.getDependencies();
        Configuration integrationTestingBundle = createIntegrationTestingBundle(configurations, dependencyHandler);
        Configuration integrationTestImplementationRemote = createIntegrationTestImplementationRemote(configurations);
        Configuration integrationTestImplementationLocal = createIntegrationTestImplementationLocal(configurations,
                dependencyHandler)
                .extendsFrom(integrationTestingBundle, integrationTestImplementationRemote);

        // Add dependency of integrationTestImplementationLocal on alfrescoProvided configuration if it exists
        // All compileOnly dependencies that are used to compile the main sources are potentially also needed during unit tests,
        // because they may be part of the API of the code under test. If these are not included, NoClassDefFoundError exceptions
        // may be thrown from the integration test client.
        project.getPluginManager().withPlugin("eu.xenit.alfresco", appliedPlugin -> {
            integrationTestImplementationLocal.extendsFrom(configurations.getByName("alfrescoProvided"));
        });

        // Create or update integrationTestImplementation, which is used internally by the local testrunner
        Configuration integrationTestImplementation = configurations.maybeCreate("integrationTestImplementation");
        integrationTestImplementation.extendsFrom(integrationTestImplementationLocal);

        return new IntegrationTestConfigurations(integrationTestingBundle, integrationTestImplementationRemote,
                integrationTestImplementationLocal);
    }

    /**
     * Creates the integrationTestImplementationLocal configuration.
     * <p>
     * This configuration is used for the client part of the integration tests.
     * The integration test client sends the request to run a test to Alfresco and shows the results that are sent back
     */
    private static Configuration createIntegrationTestImplementationLocal(ConfigurationContainer configurations,
            DependencyHandler dependencyHandler) {
        return configurations.create("integrationTestImplementationLocal", configuration -> {
            configuration.defaultDependencies(dependencies -> {
                dependencies.add(dependencyHandler.create("commons-lang:commons-lang:1.0.0"));
            });
        });
    }

    /**
     * Creates the integrationTestImplementationRemote configuration.
     * <p>
     * This configuration is used for the server part of the integration tests.
     * The integration test server is a dynamic extensions bundle that is uploaded to Alfresco and is executed there.
     * It contains all dependencies of the integration tests themselves
     */
    private static Configuration createIntegrationTestImplementationRemote(ConfigurationContainer configurations) {
        return configurations.create("integrationTestImplementationRemote");
    }

    /**
     * Creates the integrationTestBundle configuration.
     * <p>
     * The integration testing bundle contains the JUnit driver that runs client side and the Alfresco webscripts that
     * run server side to provide test execution functionality
     */
    private static Configuration createIntegrationTestingBundle(ConfigurationContainer configurations,
            DependencyHandler dependencyHandler) {
        return configurations.create("integrationTestBundle", configuration -> {
            configuration.defaultDependencies(dependencies -> {
                dependencies
                        .add(dependencyHandler
                                .create("eu.xenit.testing.integration-testing:alfresco-remote-testrunner:"
                                        + BuildConfig.VERSION));
            });
        });
    }
}
