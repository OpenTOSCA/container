# Use maven as a buildtool and dependency manager

* Status: implemented

## Context and Problem Statement

To enable portability and reproducibility of building the container application, a buildtool is of utmost importance.
It simplifies the buildprocess and should preferrably allow a minimal configuration effort for IDEs.

## Decision Drivers 

* Core Concern is management of building the container, preferrably in a single command invocation
* Support for dependency management is optional, but highly appreciated
* The tool should also support deployment of the finished web application to an application server
* Cross Platform tooling, Automation and support for Running a Test suite are also necessary

## Considered Options

* Maven
* Gradle
* Ant
  
## Decision Outcome

Maven was chosen both for it's maturity and integration with established IDEs.
It also was previously used to automate the container build when the container was still deployed as an OSGI application.
Additionally maven dependencies are the de-facto standard distribution mechanism in the java ecosystem.

## Pros and Cons of the Options

### Maven

* Mature and tested ecosystem
* De-Facto standard for dependency distribution
* Existing maven structure for previous incarnation of container allows code reuse
* Strong support for testing, automation and known cross-platform usability

### Gradle

* Not quite as mature ecosystem as maven
* Dependency management is possible, but doesn't open new avenues over maven
* Cross-Platform compatibility for gradle is present, but often requires additional support through gradle-wrapper
* Support for testing and automation is present

### Ant

* Ecosystem is mature, but configuration over convention paradigm
* No builtin support for dependency management
* Cross-Platform compatibility is not builtin
* Support for testing is incomplete, automation with current CI providers may require additional support
