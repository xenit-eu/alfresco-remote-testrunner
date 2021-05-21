package eu.xenit.testing.integrationtesting.tests.remote;

import eu.xenit.testing.integrationtesting.example.DummyService;
import eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner;
import eu.xenit.testing.integrationtesting.runner.UseSpringContextOfBundle;
import org.alfresco.service.ServiceRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Runs some example integration tests, for automated testing of this framework on jenkins
 */
@RunWith(AlfrescoTestRunner.class)
@UseSpringContextOfBundle(bundleId = "eu.xenit.testing.integration-testing.test-target-project")
public class UseSpringContextTest {


    @Autowired
    public ServiceRegistry serviceRegistry;
    @Autowired
    DummyService dummyService;

    @Test
    public void TestInject() {
        assertNotNull("serviceRegistry", serviceRegistry);
        assertNotNull("dummyService", dummyService);

    }

}
