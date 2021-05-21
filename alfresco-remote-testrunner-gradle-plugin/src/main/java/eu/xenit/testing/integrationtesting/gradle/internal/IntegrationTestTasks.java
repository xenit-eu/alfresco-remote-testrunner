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
package eu.xenit.testing.integrationtesting.gradle.internal;

import com.github.dynamicextensionsalfresco.gradle.configuration.Repository;
import com.github.dynamicextensionsalfresco.gradle.tasks.InstallBundle;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.testing.Test;

public final class IntegrationTestTasks {

    private final InstallBundle installIntegrationTestingBundle;
    private final InstallBundle installIntegrationTestsBundle;
    private final Test integrationTest;

    private IntegrationTestTasks(
            InstallBundle installIntegrationTestingBundle,
            InstallBundle installIntegrationTestsBundle
            , Test integrationTest) {
        this.installIntegrationTestingBundle = installIntegrationTestingBundle;
        this.installIntegrationTestsBundle = installIntegrationTestsBundle;
        this.integrationTest = integrationTest;
    }

    public static IntegrationTestTasks create(Project project,
            IntegrationTestConfigurations integrationTestConfigurations, AbstractArchiveTask dynamicExtensionArchive,
            SourceSet integrationTestSourceSet, Provider<Repository> repositoryProvider) {

        InstallBundle installIntegrationTesting = createInstallIntegrationTesting(project,
                integrationTestConfigurations);

        InstallBundle installIntegrationTests = createInstallIntegrationTests(project, dynamicExtensionArchive);
        installIntegrationTests.dependsOn(installIntegrationTesting);

        Test integrationTest = createIntegrationTest(project, integrationTestSourceSet, repositoryProvider);
        integrationTest.dependsOn(installIntegrationTests);
        return new IntegrationTestTasks(installIntegrationTesting, installIntegrationTests, integrationTest);
    }

    /**
     * Creates the integration test task itself that will run the integration tests.
     * <p>
     * It configures the integration test with a system property containing the Alfresco URL that will be contacted by the testrunner
     */
    private static Test createIntegrationTest(Project project, SourceSet integrationTestSourceSet,
            Provider<Repository> repositoryProvider) {
        return project.getTasks().create("integrationTest", Test.class, task -> {
            task.setGroup("verification");
            task.setTestClassesDirs(integrationTestSourceSet.getOutput().getClassesDirs());
            task.setClasspath(integrationTestSourceSet.getRuntimeClasspath());
            task.doFirst(task1 -> {
                Repository repository = repositoryProvider.getOrNull();
                if (repository != null) {
                    String urlBuilder = repository.getEndpoint().getProtocol().get()
                            + "://"
                            + repository.getAuthentication().getUsername().get()
                            + ":"
                            + repository.getAuthentication().getPassword().get()
                            + "@"
                            + repository.getEndpoint().getHost().get()
                            + ":"
                            + repository.getEndpoint().getPort().get()
                            + repository.getEndpoint().getServiceUrl().get();
                    task.systemProperty("eu.xenit.testing.integrationtesting.remote", urlBuilder);
                }
            });
        });
    }

    /**
     * Create task that installs the integration tests bundle itself (tests)
     * <p>
     * This task should be executed after alfresco is up. We can not add a dependsOn here, so the user will have to do that themselves
     */
    private static InstallBundle createInstallIntegrationTests(Project project,
            AbstractArchiveTask dynamicExtensionArchive) {
        return project.getTasks().create("installIntegrationTestBundle", InstallBundle.class, task -> {
            task.dependsOn(dynamicExtensionArchive);
            task.setGroup("install");
            task.getFiles().from(dynamicExtensionArchive.getArchiveFile());
        });
    }

    /**
     * Create task that installs the integration testing bundle itself (testrunner)
     * <p>
     * This task should be executed after alfresco is up. We can not add a dependsOn here, so the user will have to do that themselves
     */
    private static InstallBundle createInstallIntegrationTesting(Project project,
            IntegrationTestConfigurations integrationTestConfigurations) {
        return project.getTasks().create("installIntegrationTestingBundle", InstallBundle.class, task -> {
            task.setGroup("install");
            task.setFiles(integrationTestConfigurations.getIntegrationTestingBundle());
        });
    }

    public InstallBundle getInstallIntegrationTestingBundle() {
        return installIntegrationTestingBundle;
    }

    public InstallBundle getInstallIntegrationTestsBundle() {
        return installIntegrationTestsBundle;
    }

    public Test getIntegrationTest() {
        return integrationTest;
    }
}
