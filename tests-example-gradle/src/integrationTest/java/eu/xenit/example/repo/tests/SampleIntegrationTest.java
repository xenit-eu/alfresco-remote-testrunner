package eu.xenit.example.repo.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import eu.xenit.example.repo.DummyService;
import eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner;
import eu.xenit.testing.integrationtesting.runner.UseSpringContextOfBundle;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.NodeRef;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

@RunWith(AlfrescoTestRunner.class)
@UseSpringContextOfBundle(bundleId = "alfresco-remote-testrunner-example")
public class SampleIntegrationTest {


    @Autowired
    public Repository repository;

    @Autowired
    public DummyService dummyService;

    @Test
    public void CanGetCompanyHome() {
        NodeRef c = repository.getCompanyHome();
        System.out.println(c);

        assertNotNull(c);

    }

    @Test
    public void CanCallOtherBundle() {
        assertEquals(3, dummyService.doWelcome(2));
    }
}
