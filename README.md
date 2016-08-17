# OpenTOSCA container - TOSCA runtime

[![Build Status](https://travis-ci.org/OpenTOSCA/container.svg?branch=master)](https://travis-ci.org/OpenTOSCA/container)

Part of the [OpenTOSCA Ecosystem](http://www.opentosca.org)


## Build

- Run "mvn package" inside the root folder. 
- When the build is finished you can find the built archives under org.opentosca.container.product/target/products


## Setup in Eclipse

- After checkout/pull do "Import existing Maven Projects.." on the root dir ./container and select all found projects.
- When Eclipse asks to install the Tycho Configurators, hit Yes/Okay/Install (Be sure that m2e and it's repositories are know to your eclipse)
- under project ../org.opentosca.container.targetplatform open the *.target file "org.opentosca.container.targetplatform.target" and click "Set as Target Platform", this may take a while
- To start the container: open the *.product file org.opentosca.container.product.product under ../org.opentosca.container.product and hit Run/Debug in the right corner in the newly opened window


## Build & run using Docker

See [opentosca-dockerfiles](https://github.com/jojow/opentosca-dockerfiles).
