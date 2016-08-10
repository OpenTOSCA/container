/**
 * This package provides functionality of resolving the references inside of
 * Definitions in a passed CSAR. This is needed to provide the data connection
 * between references inside of TOSCA files and the corresponding objects via
 * the ToscaReferenceMapper .
 * 
 * While resolving these steps taken are: <br>
 * * Start the resolving due the DefinitionsResolver. <br>
 * * Validate and import resources due the ImportResolver. The resources are the
 * files pointed at by Import elements inside of TOSCA files. <br>
 * * Resolve references inside of TOSCA files due the various Resolver according
 * to the possible child elements of a Definitions element. <br>
 * * Find and store the referenced informations due the ReferenceMapper for each
 * found reference.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
package org.opentosca.toscaengine.service.impl.resolver;