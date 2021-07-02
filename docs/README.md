# Alfresco Remote Testrunner

The Alfresco Remote Testrunner is a JUnit runner that forwards tests to a running Alfresco Repository and reports the results back.

The Alfresco Remote Testrunner and your integration tests are packaged in [Dynamic Extensions](https://github.com/xenit-eu/dynamic-extensions-for-alfresco) bundles, so they can be installed in a running Alfresco Repository without installing an AMP.

The test runner can run standalone with JUnit after manually installing the required DE bundles.

A Gradle plugin is provided to automatically build and install the dynamic extension when integration tests are run.

## Documentation

* [Authoring integration tests](./01-authoring-tests.md)
* [Running tests with the Gradle plugin](./02-running-gradle-plugin.md) | [Running tests without Gradle](./02-running-without-gradle.md)
* [Common test errors (and their solutions)](./03-common-test-errors.md)
