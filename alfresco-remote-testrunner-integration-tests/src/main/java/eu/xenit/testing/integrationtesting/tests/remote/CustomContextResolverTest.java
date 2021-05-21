package eu.xenit.testing.integrationtesting.tests.remote;

import eu.xenit.testing.integrationtesting.example.DummyService;
import eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner;
import eu.xenit.testing.integrationtesting.runner.CustomBundleFilter;
import eu.xenit.testing.integrationtesting.runner.UseSpringContextOfBundle;
import org.alfresco.service.ServiceRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Runs some example integration tests, for automated testing of this framework on jenkins
 */
@RunWith(AlfrescoTestRunner.class)
@UseSpringContextOfBundle(filter = CustomContextResolverTest.TestBundleFilter.class)
public class CustomContextResolverTest {


    @Autowired
    public ServiceRegistry serviceRegistry;
    @Autowired
    DummyService dummyService;

    @Test
    public void TestRunTest() {

        assertNotNull("serviceRegistry", serviceRegistry);
        assertNotNull("dummyService", dummyService);
    }

    public static class TestBundleFilter implements CustomBundleFilter {

        public boolean isCalled =false;

        @Override
        public Bundle getBundleToUseAsSpringContext(Bundle[] bundles) {
            isCalled = true;
            boolean containsIntegrationTests = false;
            boolean containsRunner = false;
            Bundle ret = null;

            for (Bundle b : bundles) {
                System.out.println("bundle '"+b.getSymbolicName()+"'");
                if (b.getSymbolicName().equals("eu.xenit.testing.integration-testing.alfresco-remote-testrunner-integration-tests"))
                    containsIntegrationTests = true;
                if (b.getSymbolicName().equals("eu.xenit.testing.integration-testing.alfresco-remote-testrunner"))
                    containsRunner = true;
                if (b.getSymbolicName().equals("eu.xenit.testing.integration-testing.test-target-project"))
                    ret = b;
            }
            assertTrue("Input bundles should contain the test bundle",containsIntegrationTests);
            assertTrue("Input bundles should contain the test runner framework",containsRunner);
            assertNotNull("Input bundles should contain the example project", ret);

            return ret;


        }
    }

}
