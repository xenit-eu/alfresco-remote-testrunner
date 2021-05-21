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
package eu.xenit.testing.integrationtesting.gradle;

import static eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner.PROP_REMOTE_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.github.dynamicextensionsalfresco.gradle.DynamicExtensionPlugin;
import eu.xenit.gradle.alfrescosdk.AlfrescoPlugin;
import java.util.List;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.api.internal.tasks.InputChangesAwareTaskAction;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class IntegrationTestPluginTest {

    private DefaultProject getDefaultProject() {
        DefaultProject project = (DefaultProject) ProjectBuilder.builder().build();
        return project;
    }

    @Test
    public void testAppliedWithoutJavaPlugin() {
        DefaultProject project = getDefaultProject();
        project.getPluginManager().apply(IntegrationTestPlugin.class);

        assertNull(project.getConfigurations().findByName("integrationTestImplementation"));
    }

    @Test
    public void testAppliedWithJavaPlugin() {
        DefaultProject project = getDefaultProject();
        project.getPluginManager().apply(IntegrationTestPlugin.class);
        project.getPluginManager().apply(JavaPlugin.class);

        assertNotNull(project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets()
                .findByName("integrationTest"));
        assertNotNull(project.getConfigurations().findByName("integrationTestImplementation"));
        assertNotNull(project.getConfigurations().findByName("integrationTestImplementationRemote"));
        assertNotNull(project.getConfigurations().findByName("integrationTestImplementationLocal"));
        assertNotNull(project.getConfigurations().findByName("integrationTestBundle"));
    }

    @Test
    public void testAppliedWithDynamicExtensionsPlugin() {
        DefaultProject project = getDefaultProject();
        project.getPluginManager().apply(JavaPlugin.class);
        project.getPluginManager().apply(DynamicExtensionPlugin.class);
        project.getPluginManager().apply(IntegrationTestPlugin.class);

        assertNotNull(project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets()
                .findByName("integrationTest"));
        assertNotNull(project.getConfigurations().findByName("integrationTestImplementation"));
        assertNotNull(project.getConfigurations().findByName("integrationTestImplementationRemote"));
        assertNotNull(project.getConfigurations().findByName("integrationTestImplementationLocal"));
        assertNotNull(project.getConfigurations().findByName("integrationTestBundle"));
        assertNotNull(project.getExtensions().findByName("alfrescoDynamicExtensions"));
    }

    @Test
    public void testIntegrationTestTaskConfiguration() {
        DefaultProject project = getDefaultProject();
        project.getPluginManager().apply(JavaPlugin.class);
        project.getPluginManager().apply(DynamicExtensionPlugin.class);
        project.getPluginManager().apply(IntegrationTestPlugin.class);

        project.evaluate();

        org.gradle.api.tasks.testing.Test testTask = project.getTasks()
                .withType(org.gradle.api.tasks.testing.Test.class).findByName("integrationTest");
        assertNotNull(testTask);
        List<InputChangesAwareTaskAction> taskActions = testTask.getTaskActions();

        //Only run doFirst actions
        for (InputChangesAwareTaskAction taskAction : taskActions) {
            if(taskAction.getDisplayName().equals("Execute doFirst {} action")) {
                taskAction.execute(testTask);
            }
        }

        assertTrue(testTask.getSystemProperties().containsKey(PROP_REMOTE_URL));
        assertEquals("http://admin:admin@localhost:8080/alfresco/service", testTask.getSystemProperties().get(PROP_REMOTE_URL));
    }

    @Test
    public void testAppliedWithAlfrescoPlugin() {
        DefaultProject project = getDefaultProject();
        project.getPluginManager().apply(JavaPlugin.class);
        project.getPluginManager().apply(IntegrationTestPlugin.class);
        project.getPluginManager().apply(AlfrescoPlugin.class);

        assertTrue(project.getConfigurations().getByName("integrationTestImplementationLocal").getExtendsFrom()
                .contains(project.getConfigurations().getByName("alfrescoProvided")));

    }


}
