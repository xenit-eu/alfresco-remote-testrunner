# Alfresco remote testrunner - Changelog

## 2.0.1 (2023-01-04)

* [#4](https://github.com/xenit-eu/alfresco-remote-testrunner/pull/4) [DEVEM-524](https://xenitsupport.jira.com/browse/DEVEM-524) Modify osgi manifest generation to allow for systems that only provide org.slf4j. > 2.0.0

## 2.0.0 (2021-08-10)

No changes from previous version.

## 2.0.0-rc.6 (2021-07-02)

### Changes

 * [#2](https://github.com/xenit-eu/alfresco-remote-testrunner/pull/2) Changed how integrationTest sourceset receives dependencies from main sourceset

## 2.0.0-rc.5 (2021-06-11)

### Fixes

* [#1](https://github.com/xenit-eu/alfresco-remote-testrunner/pull/1) relocate fluent-hc to work with alfresco 6.x versions

## 2.0.0-rc.4 (2021-05-25)

Configure description of Gradle plugin so the plugin descriptor pom is valid for maven central

## 2.0.0-rc.3 (2021-05-22)

Enable signing and javadoc/sources jars for the Gradle plugin, so it can be published to maven central as well

## 2.0.0-rc.2 (2021-05-22)

Version 2.0.0-rc.1 was not properly published.
This version fixes that.

## 2.0.0-rc.1 (2021-05-22)

> First open-source release of the Alfresco remote testrunner

See [UPGRADING-2.0.md](./UPGRADING-2.0.md) for a full migration guide

### Added

* [DEVEM-375](https://xenitsupport.jira.com/browse/DEVEM-375) Add gradle plugin to configure integration testing

### Changed

* [DEVEM-408](https://xenitsupport.jira.com/browse/DEVEM-408) Upgrade to DE version 2.0.1
* [DEVEM-376](https://xenitsupport.jira.com/browse/DEVEM-376) Rename `integration-testing` to `alfresco-remote-testrunner`
* [DEVEM-378](https://xenitsupport.jira.com/browse/DEVEM-378) Publish to maven central and gradle plugin portal

### Fixed

### Deleted

## 1.1.0 (2019-02-05)

## Fixed

* [DEVEM-347](https://xenitsupport.jira.com/browse/DEVEM-347) Test should not pass if target Alfresco is not reachable
* [DEVEM-345](https://xenitsupport.jira.com/browse/DEVEM-345) Cannot find @Test annotated methods

