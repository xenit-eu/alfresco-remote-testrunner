# Common errors encountered during testing

When errors happen during testing, exceptions may be thrown by the test runner itself.

If you receive a `RemoteTestRunnerUserException`, this usually means something is not configured correctly.

These custom exceptions try to guide you to the problem by listing the known reasons why they were thrown, and possibly solutions.

## Autowiring something in a test class

* Make sure both your test jar and your jar under test are exporting at least one package.
* Dependencies you want to autowire must be exported

## Failed to get lock '{http://www.xenit.eu/testing/integrationtesting/}testrunner'

Only one test can be run at the same time. A lock guarantees that only one testrunner is active and that other parallel requests are rejected. It is possible that your JUnit (or Gradle) wants to run tests in parallel, make sure to disable that feature.
