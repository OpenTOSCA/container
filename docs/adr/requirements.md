# Preexisting requirements

The following is a non-exhaustive list of requirements that serve as basis for architectural decisions.
Requirements that are not imposed by the problem domain (of provisioning a TOSCA CSAR) are marked as "pragmatic requirement"

- Availability as a REST API
  - Deploy as Web Archive to a Java application server (pragmatic requirement)
  - Use Tomcat as application Server (pragmatic requirement)
- Persistent Storage capabilities for CSARs as well as metadata surrounding deployment
  - Use of the already existing support for persistent storage of CSARs implemented in OpenTOSCA Winery
- Support for existing "Planbuilder" service capabilities
- Support for existing Application Bus code and infrastructure
- Support for existing Management Bus code and infrastructure
- Support for Plugin-Based extension of the capabilities implemented by the Container application
  - Specifically this refers to supporting the existing plugin systems of Planbuilder, Plan Engine as well as Management and Application Bus
- …