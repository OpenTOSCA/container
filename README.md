
# OpenTOSCA Container - TOSCA Runtime

[![Build Status](https://travis-ci.org/OpenTOSCA/container.svg?branch=master)](https://travis-ci.org/OpenTOSCA/container)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Part of the [OpenTOSCA Ecosystem](http://www.opentosca.org)

## Build

1. Run `git update-index --assume-unchanged ./org.opentosca.container.core/src/main/resources/application.properties`
   to ignore custom configuration changes inside the application.properties.
2. Update [application.properties](org.opentosca.container.core/src/main/resources/application.properties) and
   replace `localhost` with your external IP address, e.g., `192.168.1.100`.
3. Run `mvn package` inside the root folder.
4. Afterwards, the [OpenTOSCA-container.war](org.opentosca.container.war/target/OpenTOSCA-container.war)
   can be deployed using a tomcat webserver.

## Setup in IntelliJ

1. [Install Lombok](https://projectlombok.org/setup/intellij).
2. Open the project using `File` > `Open` and navigate to the container folder.
3. Right click the [pom.xml](pom.xml) and select `Add as Maven project`.
4. Run the `Container` run configuration.

## Setup in Eclipse

1. [Install Lombok](https://projectlombok.org/setup/eclipse).
2. Import project via `Import existing maven projects..`
3. Add created war file of project `org.opentosca.container.war` to suitable server configured within your eclipse, e.g., Tomcat
4. (AdditionalInfo) Usually the application runs on port 1337 and without a prefix in the path -> change port of tomcat to 1337 and remove the path of the added WAR project

## Run via embedded Tomcat

1. Switch to folder org.opentosca.container.war
2. Run the following command: 'org.apache.tomcat.maven:tomcat7-maven-plugin:2.0:run'
3. Be sure to use Java 8

## Haftungsausschluss

Dies ist ein Forschungsprototyp.
Die Haftung für entgangenen Gewinn, Produktionsausfall, Betriebsunterbrechung, entgangene Nutzungen, Verlust von Daten und Informationen, Finanzierungsaufwendungen sowie sonstige Vermögens- und Folgeschäden ist, außer in Fällen von grober Fahrlässigkeit, Vorsatz und Personenschäden, ausgeschlossen.

## Disclaimer of Warranty

Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE.
You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License.
