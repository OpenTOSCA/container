# OpenTOSCA Container - TOSCA Runtime

[![Build Status](https://travis-ci.org/OpenTOSCA/container.svg?branch=master)](https://travis-ci.org/OpenTOSCA/container)

Part of the [OpenTOSCA Ecosystem](http://www.opentosca.org)


## Build

1. Run `mvn package` inside the root folder.
2. When completed, the built product can be found in `org.opentosca.container.product/target/products`.


## Setup in Eclipse

- After checkout, import the project to Eclipse (on the root directory) and select all found projects.
- When Eclipse asks to install the Tycho Configurators, hit Yes/Okay/Install (be sure that `m2e` and it's repositories are known to your Eclipse).
- Then, in project `org.opentosca.container.targetplatform` open the file `org.opentosca.container.targetplatform.target` and click `Set as Target Platform`.
- To start the container, in project `org.opentosca.container.product` open the `*.product` file and run the application.


## Build & Run Using Docker

Dockerfiles and Docker Compose configuration is available, see [opentosca-dockerfiles](https://github.com/OpenTOSCA/opentosca-dockerfiles).

There are also pre-built Docker images availabe on [Docker Hub](https://hub.docker.com/r/opentosca).
