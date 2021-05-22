# Upgrade guide: 1.x -> 2.0

1. Maven coordinates have been changed. The package is now named `eu.xenit.testing.integration-testing:alfresco-remote-testrunner`.

   <table>
    <tr>
    <th>Old</th>
    <th>New</th>
    </tr>
    <tr>
    <td>

    ```
    eu.xenit.testing:integration-testing
    ```

    </td>
    <td>

    ```
    eu.xenit.testing.integration-testing:alfresco-remote-testrunner
    ```

    </td>
    </tr>
    </table>

2. The `eu.xenit.apix.integrationtesting.runner.ApixIntegration` class has been removed. Use the `eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner` class instead.

   <table>
    <tr>
    <th>Old</th>
    <th>New</th>
    </tr>
    <tr>
    <td>

    ```java
    import eu.xenit.apix.integrationtesting.runner.ApixIntegration;
    import org.junit.runner.RunWith;
   
    
   @RunWith(ApixIntegration.class)
   class XYZ {
    // [...]
   }
    ```

    </td>
    <td>

    ```java
    import eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner;
    import org.junit.runner.RunWith;
   
    
   @RunWith(AlfrescoTestRunner.class)
   class XYZ {
    // [...]
   }
    ```

    </td>
    </tr>
    </table>

3. Usage of `apix-integration-testing.properties` has been removed. Instead of configuring the Alfresco server in this file, specify configuration as the Java system property `eu.xenit.testing.integrationtesting.remote` when running tests.


4. Usage of the system properties `alfresco.url`, `username` and `password` to configure the Alfresco server, use the single system property `eu.xenit.testing.integrationtesting.remote`.

   <table>
    <tr>
    <th>Old</th>
    <th>New</th>
    </tr>
    <tr>
    <td>

    ```
   -Dalfresco.url=http://localhost:8080/alfresco -Dusername=admin -Dpassword=mypassword
    ```

    </td>
    <td>

    ```
   -Deu.xenit.testing.integrationtesting.remote=http://admin:mypassword@localhost:8080/alfresco/s
    ```

    </td>
    </tr>
    <tr>
    <td>

    ```groovy
    test {
      systemProperty('alfresco.url', 'http://localhost:8080/alfresco')
      systemProperty('username', 'admin')
      systemProperty('password', 'mypassword')
    }
    ```

    </td>
    <td>

    ```groovy
    test {
        systemProperty('eu.xenit.testing.integrationtesting.remote', "http://admin:mypassword@localhost:8080:alfresco/s")
    }
    ```

    </td>
    </tr>
    </table>
