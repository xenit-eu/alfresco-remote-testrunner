package eu.xenit.testing.integrationtesting.tests.remote;

import eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner;
import org.alfresco.service.ServiceRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Runs some example integration tests, for automated testing of this framework on jenkins
 */
@RunWith(AlfrescoTestRunner.class)
public class BasicIntegrationTest {


    @Autowired
    public ServiceRegistry serviceRegistry;

    @Test
    public void ShouldInjectAlfrescoServices()
    {
        assertNotNull(serviceRegistry);
    }


    @Test
    public void TestRunTest() {

        System.out.println("Running test");
        assertEquals("Hello", "Hello");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
