# Use Spring for Dependency Injection and discovery of plugins

* Status: accepted

## Context and Problem Statement

To facilitate testing, a clean, object oriented architecture as well as the plugin systems for various components a configurable Inversion of Control (IOC) container is required.
This container is responsible for plugin discovery, as well as injecting services required by the API to serve it's "external" customers.

## Decision Drivers 

* Support for a plugin system that can discover additional components not originally compiled into the deployed WAR
* Support for minimal configuration, allowing easy modification and discovery by convention

## Considered Options

* Spring
* Guice

## Decision Outcome

The chosen IoC container is Spring, because it supports plugin discovery at minimal configuration and has easy support for servlet-based injection with `spring-mvc` and `spring-web`.

### Negative consequences

The Plugins loaded cannot be adjusted at runtime.
At time of writing, no such capability is required or planned.

## Pros and Cons

### Spring

* Well-Maintained and diverse IoC container supporting various configuration mechanisms
* No support for changes in registration during runtime
* Support for `javax.Inject` annotations as well as injection-site adaption through custom annotations
* No direct support for servlet-based injection, but available as `spring-web`

### Guice

* No support for XML-Based configuration
* No direct support for servlet-based injection
* At time of writing seems to be discontinued by original maintainer Google
