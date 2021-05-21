package eu.xenit.testing.integrationtesting.tests.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AlfrescoTestRunner.class)
public class AssumptionIntegrationTest {
    @Test
    public void testAssumePass() {
        assumeTrue(true);
        assertTrue(true);
    }

    @Test
    public void testAssumeFail() {
        assumeTrue(false);
        assertTrue(false);
    }
}
