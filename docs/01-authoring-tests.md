# Authoring integration tests

Integration tests classes that have to be run inside Alfresco are annotated with an `@RunWith(AlfrescoTestRunner.class)` annotation.

Your integration test is a spring component, so you can use the Spring `@Autowired` annotation to access beans.

Example:

```java
import eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.model.ContentModel;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@RunWith(AlfrescoTestRunner.class)
public class AlfrescoIntegrationTest {

    @Autowired
    public FileFolderService fileFolderService;

    @Test
    public void testCreateNode() {
        NodeRef createdNode = transactionHelper.doInTransaction(() -> {
            NodeRef sharedFolder = repository.getSharedHome();
            return fileFolderService.create(sharedFolder, "integrationTest", ContentModel.TYPE_FOLDER).getNodeRef();
        }, false, true);

        assertNotNull(createdNode);
    }
}
```

## Supported testrunner features

### JUnit core

Supported:

* You can use JUnit 4 `@Before` and `@After` annotations, these will work as usual.
* All [Assertions](https://github.com/junit-team/junit4/wiki/Assertions) & [Assumptions](https://github.com/junit-team/junit4/wiki/Assumptions-with-assume) you can imagine.
* [`@Ignore`](https://github.com/junit-team/junit4/wiki/Ignoring-tests) 'ing tests

Not supported:

* The `@BeforeClass` and `@AfterClass` annotations can be used, however these will also be run before and after **each** method instead of once per class.
* Any custom test runner with `@RunWith` is not supported.
* Static fields in test classes.

### Spring features

Supported:

* `@Autowired` on fields/constructors
* `@Qualifier` to select a specific implementation to autowire

Not supported:

* `@Component` on classes. Don't try to create components in your integration test bundle

### Alfresco Remote Testrunner features

Annotations on the test class:

* `@UseSpringContextOfBundle`: Runs tests with the application context of a different bundle. By default, tests will be run in the context of the test bundle.
* `@Transaction`: Runs tests with this transaction level. You can use this to disable transactions when you are going to manage transactions yourself in your test. By default, tests will run in a read-write transaction.
