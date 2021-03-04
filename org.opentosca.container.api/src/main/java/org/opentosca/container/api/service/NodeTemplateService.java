package org.opentosca.container.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.xml.XTBoolean;

import org.opentosca.container.api.dto.NodeOperationDTO;
import org.opentosca.container.api.dto.NodeTemplateDTO;
import org.opentosca.container.api.dto.boundarydefinitions.InterfaceDTO;
import org.opentosca.container.api.dto.boundarydefinitions.InterfaceListDTO;
import org.opentosca.container.api.dto.boundarydefinitions.OperationDTO;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.tosca.extension.TParameter;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

/**
 * Provides data access functionality to retrieve node templates based on a service template. Throughout the class, it
 * is assumed that the passed service template id belongs to the passed CSAR, i.e., it is assumed that a check that this
 * is true is performed earlier.
 *
 * @author Ghareeb Falazi
 */
// TODO it is assumed that the name of the node template is the same as its id.
//  That assumption is not accurate
@Service
public class NodeTemplateService {

    private final CsarStorageService storage;

    @Inject
    public NodeTemplateService(CsarStorageService storage) {
        this.storage = storage;
    }

    /**
     * Gets a collection of node templates associated to a given service template.
     *
     * @param csarId               The id of the CSAR
     * @param serviceTemplateQName The QName of the service template within the given CSAR
     * @return A collection of node templates stored within the given service template.
     */
    public List<NodeTemplateDTO> getNodeTemplatesOfServiceTemplate(final String csarId,
                                                                   final String serviceTemplateQName) {
        final Csar csar = storage.findById(new CsarId(csarId));

        List<TNodeTemplate> nodeTemplates = csar.serviceTemplates().stream()
            .filter(st -> st.getId().equals(serviceTemplateQName))
            .findFirst()
            .get()
            .getTopologyTemplate()
            .getNodeTemplates();

        return nodeTemplates.stream()
            .map(toscaNodeTemplate -> createNodeTemplate(toscaNodeTemplate, csar))
            .collect(Collectors.toList());
    }

    /**
     * Gets the node template specified by its id
     *
     * @param csarId               The id of the CSAR
     * @param serviceTemplateQName The QName of the service template within the given CSAR
     * @param nodeTemplateId       The id of the node template we want to get and that belongs to the specified service
     *                             template
     * @return The node template specified by the given id
     * @throws NotFoundException If the service template does not contain the specified node template
     */
    public NodeTemplateDTO getNodeTemplateById(final String csarId, final String serviceTemplateQName,
                                               final String nodeTemplateId) throws NotFoundException {
        final Csar csar = storage.findById(new CsarId(csarId));
        TServiceTemplate serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateQName);
        TNodeTemplate nodeTemplate = ToscaEngine.resolveNodeTemplate(serviceTemplate, nodeTemplateId);

        return createNodeTemplate(nodeTemplate, csar);
    }

    /**
     * Checks whether the specified service template contains a given node template.
     *
     * @param csarId              The id of the CSAR
     * @param serviceTemplateName the name of the service template
     * @param nodeTemplateId      the id of the node template to check for
     * @return <code>true</code> when the CSAR contains the service template and the service
     * template contains the node template, otherwise <code>false</code>
     */
    public boolean hasNodeTemplate(final String csarId, final String serviceTemplateName, final String nodeTemplateId) {
        final Csar csar = this.storage.findById(new CsarId(csarId));
        try {
            TServiceTemplate serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateName);
            @SuppressWarnings("unused")
            TNodeTemplate indicator = ToscaEngine.resolveNodeTemplate(serviceTemplate, nodeTemplateId);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    /**
     * Gets the properties (as an XML document) of a given node template.
     */
    public Document getPropertiesOfNodeTemplate(final String csarId, final String serviceTemplateQName,
                                                final String nodeTemplateId) throws NotFoundException {
        final Csar csar = storage.findById(new CsarId(csarId));

        TServiceTemplate serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateQName);
        TNodeTemplate nodeTemplate = ToscaEngine.resolveNodeTemplate(serviceTemplate, nodeTemplateId);

        return ToscaEngine.getEntityTemplateProperties(nodeTemplate);
    }

    // TODO Careful! this method assumes that the namespace of a node template is the same namespace
    //  as its parent service template!

    /**
     * Creates a new instance of the NodeTemplateDTO class. It fetches the qualified name of node type of the node
     * template.
     */
    public NodeTemplateDTO createNodeTemplate(final TNodeTemplate toscaObject, final Csar csar) {
        final QName nodeTypeId = toscaObject.getType();
        final NodeTemplateDTO currentNodeTemplate = new NodeTemplateDTO();
        currentNodeTemplate.setId(toscaObject.getId());
        currentNodeTemplate.setName(toscaObject.getName());
        currentNodeTemplate.setNodeType(nodeTypeId.toString());

        final InterfaceListDTO interfaces = new InterfaceListDTO();
        final List<TInterface> ifaces = ToscaEngine.getInterfaces(toscaObject, csar);
        for (final TInterface toscaInterface : ifaces) {
            final InterfaceDTO interfaceDto = new InterfaceDTO();
            interfaceDto.setName(toscaInterface.getName());

            final Map<String, OperationDTO> operations = new HashMap<>();
            final List<TOperation> interfaceOperations = toscaInterface.getOperation();
            for (final TOperation operation : interfaceOperations) {
                final OperationDTO operationDto = new OperationDTO();
                operationDto.setName(operation.getName());
                operationDto.setNodeOperation(transformNodeOperations(operation));
                operations.put(operation.getName(), operationDto);
            }
            interfaceDto.setOperations(operations);
            interfaces.add(interfaceDto);
        }
        currentNodeTemplate.setInterfaces(interfaces);
        return currentNodeTemplate;
    }

    private NodeOperationDTO transformNodeOperations(TOperation operation) {
        TOperation.InputParameters inputParams = operation.getInputParameters();
        TOperation.OutputParameters outputParams = operation.getOutputParameters();

        final NodeOperationDTO nodeOperationDTO = new NodeOperationDTO();
        nodeOperationDTO.setName(operation.getName());
        nodeOperationDTO.setInputParameters(inputParams == null ? Collections.emptyList()
            : wrap(inputParams.getInputParameter()));
        nodeOperationDTO.setOutputParameters(outputParams == null ? Collections.emptyList()
            : wrap(outputParams.getOutputParameter()));
        return nodeOperationDTO;
    }

    private List<TParameter> wrap(final List<org.eclipse.winery.model.tosca.TParameter> params) {
        final List<TParameter> wrapped = new ArrayList<>();
        for (final org.eclipse.winery.model.tosca.TParameter param : params) {
            final TParameter tParam = new TParameter();
            tParam.setName(param.getName());
            tParam.setType(param.getType());
            tParam.setRequired(XTBoolean.fromValue(param.getRequired() ? "yes" : "no"));
            wrapped.add(tParam);
        }
        return wrapped;
    }
}
