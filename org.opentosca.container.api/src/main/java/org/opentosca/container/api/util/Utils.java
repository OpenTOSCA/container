package org.opentosca.container.api.util;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.RelationshipSourceOrTarget;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

import org.opentosca.container.api.dto.plan.PlanDTO;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class Utils {

    /**
     * Get DTO for the plan with the given Id in the given Csar
     *
     * @param csar      the Csar containing the plan
     * @param planTypes an array with possible types of the plan
     * @param planId    the Id of the plan
     * @return the PlanDto if found or
     * @throws NotFoundException is thrown if the plan can not be found
     */
    public static PlanDTO getPlanDto(Csar csar, PlanType[] planTypes, String planId) throws NotFoundException {
        return csar.plans().stream()
            .filter(tplan -> Arrays.stream(planTypes).anyMatch(pt -> tplan.getPlanType().equals(pt.toString())))
            .filter(tplan -> tplan.getId() != null && tplan.getId().equals(planId))
            .findFirst()
            .map(PlanDTO::new)
            .orElseThrow(NotFoundException::new);
    }

    public static boolean hasOpenRequirements(final Csar csar) {
        TServiceTemplate serviceTemplate = csar.entryServiceTemplate();
        TTopologyTemplate topology = serviceTemplate.getTopologyTemplate();

        if (topology == null) {
            return false;
        }

        List<TNodeTemplate> nodeTemplates = topology.getNodeTemplates();
        List<TRelationshipTemplate> relationshipTemplates = topology.getRelationshipTemplates();

        for (final TNodeTemplate nodeTemplate : nodeTemplates) {
            if (nodeTemplate.getRequirements() == null) {
                continue;
            }
            final List<TRequirement> nodeTemplateRequirements = nodeTemplate.getRequirements();
            int foundRelations = 0;
            for (final TRelationshipTemplate relationship : relationshipTemplates) {
                RelationshipSourceOrTarget src = relationship.getSourceElement().getRef();
                RelationshipSourceOrTarget target = relationship.getTargetElement().getRef();
                if ((nodeTemplate.equals(ModelUtilities.getNodeTemplateFromRelationshipSourceOrTarget(topology, src)))
                    || (nodeTemplate.equals(ModelUtilities.getNodeTemplateFromRelationshipSourceOrTarget(topology, target)))) {
                    foundRelations++;
                }
            }

            if (foundRelations < nodeTemplateRequirements.size()) {
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
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
