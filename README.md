
# OpenTOSCA Container - TOSCA Runtime

[![Java CI with Maven](https://github.com/OpenTOSCA/container/actions/workflows/maven.yml/badge.svg)](https://github.com/OpenTOSCA/container/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/cf7cfef4836942e7a14d889869546575)](https://www.codacy.com/gh/OpenTOSCA/container/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenTOSCA/container&amp;utm_campaign=Badge_Grade)

Part of the [OpenTOSCA Ecosystem](http://www.opentosca.org)

## Info

The OpenTOSCA Container is java/maven based runtime for deploying and managing TOSCA-based applications. The backend uses [Winery](https://github.com/eclipse/winery) therefore all CSAR exported from a Winery repository should be compatible within the runtime.

## Development & Stable Versions

`master` is the main development branch, the `stable` branch represents the latest stable branch and is also available as [tags](https://github.com/OpenTOSCA/container/tags)

## Build

1.  Run `git update-index --assume-unchanged ./org.opentosca.container.core/src/main/resources/application.properties` to ignore custom configuration changes inside the application.properties.
2.  Update [application.properties](org.opentosca.container.core/src/main/resources/application.properties) and replace `localhost` with your external IP address, e.g., `192.168.1.100`.
3.  Run `mvn package` inside the root folder.
4.  Afterwards, the [OpenTOSCA-container.war](org.opentosca.container.war/target/OpenTOSCA-container.war) can be deployed using a tomcat webserver.

## Setup in IntelliJ

1.  Open the project using `File` > `Open` and navigate to the container folder.
2.  Right click the [pom.xml](pom.xml) and select `Add as Maven project`.
3.  Run the `Container` run configuration.

## Setup in Eclipse

1.  Import project via `Import existing maven projects..`
2.  Add created war file of project `org.opentosca.container.war` to suitable server configured within your eclipse, e.g., Tomcat
3.  (AdditionalInfo) Usually the application runs on port 1337 and without a prefix in the path -> change port of tomcat to 1337 and remove the path of the added WAR project

## Run via SpringBoot

1.  Run `mvn install` in root of project
2.  Go to directory `org.opentosca.container.war` and run `mvn spring-boot:run` and the runtime should be available under localhost:1337

## Creating a new stable tag

1.  Run `mvn release:update-versions -DautoVersionSubmodules=true` and set the version to the prefered version for the container, or just use `mvn --batch-mode release:update-versions -DautoVersionSubmodules=true` to increment the current version. Remove -SNAPSHOT via `mvn versions:set -DremoveSnapshot` [More Info](https://maven.apache.org/maven-release/maven-release-plugin/examples/update-versions.html)
2.  Lock winery SNAPSHOT version via `mvn versions:lock-snapshots`
3.  Then run `git tag <tagname>` where tagname is the version and if a major release add name to it, afterwards run `git push origin --tags`


## Disclaimer of Warranty

Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE.
You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License.

## Haftungsausschluss

Dies ist ein Forschungsprototyp.
Die Haftung für entgangenen Gewinn, Produktionsausfall, Betriebsunterbrechung, entgangene Nutzungen, Verlust von Daten und Informationen, Finanzierungsaufwendungen sowie sonstige Vermögens- und Folgeschäden ist, außer in Fällen von grober Fahrlässigkeit, Vorsatz und Personenschäden, ausgeschlossen.

## Acknowledgements

The initial code contribution has been supported by the [Federal Ministry for Economic Affairs and Energy] as part of the [CloudCycle] project (01MD11023).
Current development is supported by the Federal Ministry for Economic Affairs and Energy as part of the the [PlanQK] project (01MK20005N), and the DFG’s Excellence Initiative project [SimTech] (EXC 2075 - 390740016).
Additional development has been funded by the Federal Ministry for Economic Affairs and Energy projects [SmartOrchestra] (01MD16001F) and [SePiA.Pro] (01MD16013F), as well as by the [DFG] (Deutsche Forschungsgemeinschaft) projects [SustainLife] (641730) and [ADDCompliance] (636503).
Further development is also funded by the European Union’s Horizon 2020 project [RADON] (825040).

 [CloudCycle]: http://www.cloudcycle.org/en
  [Federal Ministry for Economic Affairs and Energy]: http://www.bmwi.de/EN  
  [SmartOrchestra]: http://smartorchestra.de/en
  [SePiA.Pro]: http://projekt-sepiapro.de/en
  [ADDCompliance]: http://addcompliance.cs.univie.ac.at
  [SustainLife]: http://www.iaas.uni-stuttgart.de/forschung/projects/SustainLife
  [RADON]: http://radon-h2020.eu
  [DFG]: http://www.dfg.de/en
  [PlanQK]: https://planqk.de
  [SimTech]: https://www.simtech.uni-stuttgart.de/
