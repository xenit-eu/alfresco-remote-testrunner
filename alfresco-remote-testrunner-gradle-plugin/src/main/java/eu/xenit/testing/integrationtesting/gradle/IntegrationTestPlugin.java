package eu.xenit.testing.integrationtesting.gradle;

import aQute.bnd.gradle.BundleTaskConvention;
import com.github.dynamicextensionsalfresco.gradle.configuration.BaseConfig;
import com.github.dynamicextensionsalfresco.gradle.tasks.DeBundleTaskConvention;
import com.github.dynamicextensionsalfresco.gradle.tasks.InstallBundle;
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import eu.xenit.testing.integrationtesting.gradle.internal.IntegrationTestConfigurations;
import eu.xenit.testing.integrationtesting.gradle.internal.IntegrationTestTasks;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.plugins.ide.idea.IdeaPlugin;

@NonNullApi
public class IntegrationTestPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        IntegrationTestSettings integrationTestSettings = createPluginSettings(project);
        project.getPlugins().withType(JavaPlugin.class, java -> {
            IntegrationTestConfigurations integrationTestConfigurations = IntegrationTestConfigurations.create(project);

            SourceSet integrationTestSourceSet = createIntegrationTestSourceSet(project);
            AbstractArchiveTask dynamicExtensionArchive = createDynamicExtensionsJarTask(
                    project,
                    integrationTestSourceSet,
                    integrationTestConfigurations.getIntegrationTestImplementationRemote(),
                    integrationTestSettings
            );

            IntegrationTestTasks integrationTestTasks = IntegrationTestTasks
                    .create(project, integrationTestConfigurations, dynamicExtensionArchive, integrationTestSourceSet,
                            integrationTestSettings.getRepository());

            // If an installBundle task exists, wire it up so the integration test depends on it
            InstallBundle installBundle = project.getTasks().withType(InstallBundle.class).findByName("installBundle");
            if (installBundle != null) {
                integrationTestTasks.getIntegrationTest().dependsOn(installBundle);
            }

            // Hook up
            project.getTasks().getByName("check").dependsOn(integrationTestTasks.getIntegrationTest());

            // Configure idea plugin to add integrationTest sourceset to test sources
            project.getPlugins().withType(IdeaPlugin.class, ideaPlugin -> {
                ideaPlugin.getModel().module(ideaModule -> {
                    ideaModule.setTestSourceDirs(
                            DefaultGroovyMethods.plus(
                                    ideaModule.getTestResourceDirs(),
                                    integrationTestSourceSet.getAllSource().getSrcDirs()
                            )
                    );
                });
            });
        });

    }

    /**
     * Creates the <code>alfrescoIntegrationTest</code> settings object for this plugin where the user can configure it
     */
    private IntegrationTestSettings createPluginSettings(Project project) {
        IntegrationTestSettings integrationTestSettings = project.getExtensions()
                .create("alfrescoIntegrationTest", IntegrationTestSettings.class, project.getObjects());

        project.getPlugins().withId("eu.xenit.de", (_plugin) -> {
            BaseConfig deBaseConfig = project.getExtensions().findByType(BaseConfig.class);
            if (deBaseConfig != null) {
                // Configure the repository property of our own configuration
                // With the one from the DE configuration, if that configuration exists
                integrationTestSettings.getRepository().convention(deBaseConfig.getRepository());
            }
        });
        return integrationTestSettings;
    }

    /**
     * Create source set for <code>integrationTest</code> and makes it inherit from the <code>main</code> sourceset
     */
    private SourceSet createIntegrationTestSourceSet(Project project) {
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class)
                .getSourceSets();

        SourceSet mainSourceSet = sourceSets.getByName("main");

        SourceSet integrationTestsSourceSet = sourceSets.maybeCreate("integrationTest");

        integrationTestsSourceSet.setCompileClasspath(
                integrationTestsSourceSet.getCompileClasspath()
                        .plus(mainSourceSet.getCompileClasspath())
                        .plus(mainSourceSet.getOutput())
        );
        integrationTestsSourceSet.setRuntimeClasspath(
                integrationTestsSourceSet.getRuntimeClasspath()
                        .plus(mainSourceSet.getRuntimeClasspath())
                        .plus(mainSourceSet.getOutput())
        );

        return integrationTestsSourceSet;

    }

    /**
     * Create task that creates the dynamic extensions jar that contains the integration tests
     * <p>
     * Note: currently we first shadow the dependencies that are required on Alfresco side into the jar, and then
     * generate the actual bundle with the correct headers
     */
    private AbstractArchiveTask createDynamicExtensionsJarTask(Project project, SourceSet sourceSet,
            Configuration remoteDependencies, IntegrationTestSettings integrationTestSettings) {
        return project.getTasks().create("_alfrescoIntegrationTestInternal_ShadowJar", ShadowJar.class, task -> {
            task.dependsOn(sourceSet.getClassesTaskName());
            task.from(sourceSet.getOutput());
            task.getArchiveClassifier().set("integration-test-all");
            task.setConfigurations(Collections.singletonList(remoteDependencies));
            DeBundleTaskConvention deBundleTaskConvention = new DeBundleTaskConvention(task);
            task.getConvention().getPlugins().put("de", deBundleTaskConvention);
            BundleTaskConvention bundleTaskConvention = task.getConvention().getPlugin(BundleTaskConvention.class);
            bundleTaskConvention.setSourceSet(sourceSet);
            Map<String, String> manifest = new HashMap<>();
            manifest.put("Bundle-Name", project.getName() + " Integration Tests");
            manifest.put("Bundle-SymbolicName", project.getName() + "-integration-test");
            task.doFirst(task1 -> {
                if (!"unspecified".equals(project.getVersion())) {
                    manifest.put("Bundle-Version", project.getVersion().toString());
                }
                manifest.put("Alfresco-Dynamic-Extension", "true");
                manifest.put("Require-Bundle", "eu.xenit.testing.integration-testing.alfresco-remote-testrunner");
                manifest.put("DynamicImport-Package", "*");
                manifest.put("Import-Package", "org.alfresco.*,"
                        + "eu.xenit.testing.integrationtesting.runner.*,"
                        + "org.junit;version=4.12,"
                        + "org.junit.runner;version=4.12,"
                        + "org.junit.runner.manipulation;version=4.12,"
                        + "org.junit.runners;version=4.12,"
                        + "org.junit.runners.model;version=4.12,"
                        + "org.junit.experimental.categories;version=4.12,"
                        + "org.junit.internal;version=4.12,"
                        + "org.junit.rules;version=4.12,"
                        + "org.junit.matchers;version=4.12,"
                        + "org.junit.runner.notification;version=4.12");
                manifest.put("Export-Package",
                        String.join(",", integrationTestSettings.getIntegrationTestPackages().get()));
                bundleTaskConvention.bnd(manifest);
            });
        });

    }


}
