package org.opentosca.container.api.util;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.RelationshipSourceOrTarget;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate.SourceOrTargetElement;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class ModelUtil {

    public static boolean hasOpenRequirements(final Csar csar) {
        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();
        TTopologyTemplate topology = serviceTemplate.getTopologyTemplate();
        
        List<TNodeTemplate> nodeTemplates = topology.getNodeTemplates();
        List<TRelationshipTemplate> relationshipTemplates = topology.getRelationshipTemplates();
        
        for (final TNodeTemplate nodeTemplate : nodeTemplates) {
            if (nodeTemplate.getRequirements() == null) { continue; }
            final List<TRequirement> nodeTemplateRequirements = nodeTemplate.getRequirements().getRequirement();
            int foundRelations = 0;
            for (final TRelationshipTemplate relationship : relationshipTemplates) {
                RelationshipSourceOrTarget src = relationship.getSourceElement().getRef();
                RelationshipSourceOrTarget target = relationship.getTargetElement().getRef();
                if ((src instanceof TNodeTemplate && nodeTemplate.equals((TNodeTemplate)src))
                    || (target instanceof TNodeTemplate && nodeTemplate.equals((TNodeTemplate)target))) {
                    foundRelations++;
                }
            }
            
            if (foundRelations < nodeTemplateRequirements.size()) { 
                return true;
            }
        }
        return false;
    }
    
    // FIXME move onto IToscaEngineService? Only makes sense in 
    public static boolean hasOpenRequirements(final CSARID csarId,
                                              final IToscaEngineService service) throws UserException, SystemException {
        final QName serviceTemplateId = service.getServiceTemplatesInCSAR(csarId).get(0);

        final List<String> nodeTemplateIds = service.getNodeTemplatesOfServiceTemplate(csarId, serviceTemplateId);
        final List<String> relationshipTemplateIds =
            service.getRelationshipTemplatesOfServiceTemplate(csarId, serviceTemplateId);

        for (final String nodeTemplateId : nodeTemplateIds) {
            final List<QName> nodeReqs = service.getNodeTemplateRequirements(csarId, serviceTemplateId, nodeTemplateId);
            int foundRelations = 0;

            for (final String relationshipTemplateId : relationshipTemplateIds) {
                final QName relationReq =
                    service.getRelationshipTemplateSource(csarId, serviceTemplateId, relationshipTemplateId);
                if (relationReq.getLocalPart().equals(nodeTemplateId)) {
                    foundRelations++;
                }
            }

            if (foundRelations < nodeReqs.size()) {
                return true;
            }

        }
        return false;
    }

    public static Element fetchFirstChildElement(final Document doc, final String childElementLocalName) {
        final NodeList childe = doc.getDocumentElement().getElementsByTagName(childElementLocalName);
        for (int i = 0; i < childe.getLength(); i++) {
            if (childe.item(i) instanceof Element) {
                return (Element) childe.item(i);
            }
        }
        return null;
    }

    public static Document createDocumentFromElement(final Element element) {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setIgnoringComments(true);
            final Document doc = dbf.newDocumentBuilder().newDocument();
            final Node importedNode = doc.importNode(element, true);
            doc.appendChild(importedNode);
            return doc;
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }



}
