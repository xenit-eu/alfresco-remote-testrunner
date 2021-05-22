# Alfresco Remote Testrunner

> [![Xenit Logo](https://xenit.eu/wp-content/uploads/2017/09/XeniT_Website_Logo.png)](https://xenit.eu/open-source)
>
> Alfresco Remote Testrunner is a part of the Xenit Open Source Tooling around Alfresco. Xenit is company with a deep expertise
> and a strong team around Alfresco. If you'd like to learn more about our
> [tools](https://xenit.eu/open-source), [services](https://xenit.eu/alfresco) and
> [products](https://xenit.eu/alfresco-products), please visit our [website](https://xenit.eu).

Run your Alfresco JUnit integration tests with standard JUnit test tooling.

The Alfresco Remote Testrunner is a JUnit runner that forwards tests to a running Alfresco instance and reports the results back.

The Testrunner and your integration tests are packaged in a [dynamic extension](https://github.com/xenit-eu/dynamic-extensions-for-alfresco), so they can be installed in a running Alfresco without installing an AMP.

The test runner can run standalone with JUnit, and a Gradle plugin is provided to automatically build and install the dynamic extension when integration tests are run.

## Creating an integration test

Integration tests classes that have to be run inside Alfresco are annotated with an `@RunWith(AlfrescoTestRunner.class)` annotation.

Your integration test is a spring component, so you can use the Spring `@Autowired` annotation to access beans.

<details>
<summary>Example integration test</summary>

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

</details>

## Running integration tests

### Without Gradle

To run integration tests you first have to install the Remote Testrunner dynamic extension, `eu.xenit.testing.integration-testing:alfresco-remote-testrunner`, in Alfresco. After this dynamic extension is installed, you can install your integration test dynamic extension.

#### Setup target server

- First deploy the 2 bundles explained above to your Alfresco server
- You can now run the tests just like normal JUnit Tests, or using gradle or maven

Provide the URL and credentials to your target server using system properties when invoking your build:

```
-Deu.xenit.testing.integrationtesting.remote=https://admin:admin@localhost:8080/alfresco/service
```

### Using the gradle plugin

```groovy
plugins {
    id "eu.xenit.alfresco-remote-testrunner" version "2.0.0"
    id "eu.xenit.de" version "2.0.1"
}

alfrescoIntegrationTest {
    integrationTestPackages = ["eu.xenit.example.test.base.package"]
}

// Configuration of Dynamic Extensions is used to configure the repository
alfrescoDynamicExtensions {
    repository {
        // Configure the URL to the Alfresco services endpoint
        endpoint {
            protocol = "http"
            host = "localhost"
            port = 8080
            serviceUrl = "/alfresco/service"
        }

        // Configure admin credentials to install DE bundles
        authentication {
            username = "admin"
            password = "admin"
        }
    }
}
```

## Errors during testing

### Autowiring something in a test class

* Make sure both your test jar and your jar under test are exporting at least one package.
* Dependencies you want to autowire must be exported

### Failed to get lock '{http://www.xenit.eu/testing/integrationtesting/}testrunner'

Only one test can be run at the same time. A lock guarantees that only one testrunner is active and that other parallel requests are rejected. It is possible that your JUnit (or Gradle) wants to run tests in parallel, make sure to disable that feature.
