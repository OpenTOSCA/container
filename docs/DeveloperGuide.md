
![OpenTOSCA](graphics/OpenTOSCALogo.jpg)

# Developer Guide OpenTOSCA Container

## Table of Contents

- [Table of Contents](#table-of-contents)
- [Introduction](#introduction)
- [Import Code](#import-code)
- [How to](#how-to)
  - [Start container](#start-container)
    - [Embedded Tomcat](#embedded-tomcat)
    - [Deploy to Installed Tomcat](#deploy-to-installed-tomcat)
  - [Configure container to run with the OpenTOSCA Docker environment](#configure-container-to-run-with-the-opentosca-docker-environment)

## Introduction

This document helps to setup your IDE to develop the OpenTosca Container and its user interfaces (UIs).
In the following we assume that you have already installed your IDE of choice as well as a Java Development Kit of version 8 or higher.
Additionally we assume that you have installed Maven, which we use as buildtool, as well as possibly necessary integration plugins for maven in your IDE.

The OpenTosca Container is deployed as a web archive, and therefore requires an application server to run.
In theory these application servers are interchangeable, but practice has shown that to be wrong.
As such development is focused on deploying to the popular Apache Tomcat application server.

To deploy the Container, you will need to install a version of Tomcat that supports the Java Servlet Specification 3.0, implying the use of Tomcat 7 or later.
It should be immaterial which version of Tomcat specifically you install, so long as it's 7 or later.

Note that during development you can use a specific maven-goal that avoids an installation of Tomcat by starting an embedded Tomcat instance.
This is not feasible for "production" deployments and also comes with a significant time-overhead during development, because it does not allow for "hot-swapping" of code.
As such it's highly recommended to install and set up Tomcat.

---

## Import Code

The code follows the standard maven conventions, as such importing the root project as a maven project should work out of the box.
Should it not work out of the box, make sure that recursive project discovery is enabled and that you select the pom at the version control root as your starting point.

---

## How to

### Start container

The container can be started in basically two different ways.
Either you use the embedded tomcat goal from maven to directly run the container, or you deploy the web archive created by maven to an installed Tomcat instance.

#### Embedded Tomcat

To run the Container on the embedded tomcat, you want to execute the maven goal `tomcat7:run` in the `org.opentosca.container.war` project.
This can be accomplished by invoking the command `mvn tomcat7:run -pl :org.opentosca.container.war` or by configuring your IDE to perform the equivalent action.

#### Deploy to Installed Tomcat

To deploy the Container to an installed Tomcat, you will need to register your local Tomcat instance with your IDE.
This process differs between IDEs, but *in general* you can configure the available application servers in the Settings panel.

When setting up the Tomcat instance, remember to make sure that the HTTP/1.1 port is set to `1337`.
Do also make sure that the context path of the war you deploy is set to `/` to avoid needing special configuration for the OpenTosca UI.
It has been beneficial to set up a fully packed war deployment for troubleshooting before attempting to set up an "exploded war" deployment.


### Configure container to run with the OpenTOSCA Docker environment

To run the container together with the OpenTOSCA Docker setup, remove the container configuration from the `docker-compose.yml` and `docker-compose.override.yml` files.

Afterwards, you need to configure the application properties to use your public IP as the `org.opentosca.container.hostname` address.
Find `org.opentosca.container.core/src/main/resources/application.properties` and replace the IP address with your external IP address, typically starting with `129.69.`
