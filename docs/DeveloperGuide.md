
![OpenTOSCA](graphics/OpenTOSCALogo.jpg)

# Developer Guide OpenTOSCA Container

## Table of Contents

- [Table of Contents](#table-of-contents)
- [Introduction](#introduction)
  - [Import Code](#import-code)
- [How to](#how-to)
  - [Start container within Eclipse](#start-container-within-eclipse)
  - [Configure container to run with the OpenTOSCA Docker environment](#configure-container-to-run-with-the-opentosca-docker-environment)

## Introduction

This document helps to setup your IDE to develop the OpenTosca Container and its user interfaces (UIs).
In the following we assume that you have already installed your IDE of choice as well as a Java Development Kit of version 8 or higher.

The OpenTosca Container is deployed as a web archive, and therefore requires an application server to run.
In theory these application servers are interchangeable, but practice has shown that to be wrong.
As such development is focused on deploying to the popular Apache Tomcat application server.

To deploy the Container, you will need to install a version of Tomcat that supports the Java Servlet Specification 3.0, implying the use of Tomcat 7 or later.
It should be immaterial which version of Tomcat specifically you install, so long as it's 7 or later.

Note that during development you can use a specific maven-goal that avoids an installation of Tomcat by starting an embedded Tomcat instance.
This is not feasible for "production" deployments and also comes with a significant time-overhead during development, because it does not allow for "hot-swapping" of code.
As such it's highly recommended to install and set up Tomcat.

---

### Import Code


---

## How to

### Start container within Eclipse

### Configure container to run with the OpenTOSCA Docker environment

To run the container together with the OpenTOSCA Docker setup, remove the container configuration from the `docker-compose.yml` and `docker-compose.override.yml` files.

Afterwards, you need to configure the application properties to use your public IP as the `org.opentosca.container.hostname` address.
Find `org.opentosca. replace the IP address in `org.opentosca.container.hostname=129.69.214.56` with your IP address
