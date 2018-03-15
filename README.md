# OpenTOSCA Container - TOSCA Runtime

[![Build Status](https://travis-ci.org/OpenTOSCA/container.svg?branch=master)](https://travis-ci.org/OpenTOSCA/container)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FOpenTOSCA%2Fcontainer.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FOpenTOSCA%2Fcontainer?ref=badge_shield)

Part of the [OpenTOSCA Ecosystem](http://www.opentosca.org)

## Build

1. Run `mvn package` inside the root folder.
2. When completed, the built product can be found in `org.opentosca.container.product/target/products`.


## Setup in Eclipse
- Make sure to use the IAAS code style configuration (see [IAAS Code Style Configuration](docs/codestyle/Readme.md))
- After checkout, import the project to Eclipse (on the root directory) and select all found projects.
  - File > Import... > Maven > Existing Maven Projects > Next
  - Select appropriate Root Directory
  - Select all projects
  - OK
- When Eclipse asks to install the Tycho Configurators, hit Yes/Okay/Install (be sure that `m2e` and it's repositories are known to your Eclipse).
- Then, in the (sub-)project `target-definition` open the file `target-definition.target` and click `Set as Target Platform` (top right).
- To start the container, in (sub-)project `org.opentosca.container.product` open the `*.product` file and run the application.


## Haftungsausschluss

Dies ist ein Forschungsprototyp.
Die Haftung für entgangenen Gewinn, Produktionsausfall, Betriebsunterbrechung, entgangene Nutzungen, Verlust von Daten und Informationen, Finanzierungsaufwendungen sowie sonstige Vermögens- und Folgeschäden ist, außer in Fällen von grober Fahrlässigkeit, Vorsatz und Personenschäden, ausgeschlossen.

## Disclaimer of Warranty

Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE.
You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License.


## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FOpenTOSCA%2Fcontainer.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FOpenTOSCA%2Fcontainer?ref=badge_large)