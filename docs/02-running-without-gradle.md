# Running integration tests without Gradle

The Dynamic Extensions bundle containing your integration tests requires a couple of OSGi manifest headers:

```ini
Alfresco-Dynamic-Extension : true
Require-Bundle: eu.xenit.testing.integration-testing.alfresco-remote-testrunner
DynamicImport-Package: *
Import-Package: eu.xenit.testing.integrationtesting.runner
```

Additionally, `Import-Package` headers must be added for the JUnit packages that are provided in the Remote Testrunner Dynamic Extension.

After building your integration tests Dynamic Extension, you must install following bundles in the Dynamic Extensions dashboard, in this order:

* First install your Dynamic Extension bundle under test (your application code), if any
* Then install the Remote Testrunner Dynamic Extension, `eu.xenit.testing.integration-testing:alfresco-remote-testrunner`,
* Finally, install the Dynamic Extension bundle containing your integration tests

When all bundles are installed correctly, you can run JUnit. You will need to supply a system property to the JVM that will run JUnit to specify the Alfresco server to use as a remote:

```
-Deu.xenit.testing.integrationtesting.remote=https://admin:admin@localhost:8080/alfresco/service
```
