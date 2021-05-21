package eu.xenit.example.repo.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner;
import org.alfresco.service.ServiceRegistry;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

@RunWith(AlfrescoTestRunner.class)
public class AlfrescoIntegrationTest {


    @Autowired
    private ServiceRegistry serviceRegistry;

    @Test
    public void ShouldInjectAlfrescoServices() {
        assertNotNull(serviceRegistry);
    }


    @Test
    public void TestSuccess() {

        System.out.println("Running TestSuccess");
        assertEquals("Hello", "Hello");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This test demonstrates that test failures can be communicated back from alfresco to
     * the local testrunner, so it is expected to fail!
     */
    @Test(expected = RuntimeException.class)
    public void TestExpectedFail() {
        System.out.println("Running TestExpectedFail");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Expected!");
    }

    @Test
    public void TestFail() {
        System.out.println("Running TestFail");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("FAILURE!");
    }

    @Ignore
    @Test
    public void TestIgnore() {
        System.out.println("Running Ignore");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestAssertFail() {
        System.out.println("Running AssertFail");
        assertEquals(true, false);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
