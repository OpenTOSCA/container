package org.opentosca.planbuilder.core.bpmn.fragments;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.common.file.ResourceAccess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDiagramElement;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDiagramType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class BPMNDiagramFragments {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNDiagramFragments.class);

    private final DocumentBuilderFactory docFactory;
    private final DocumentBuilder docBuilder;


    public BPMNDiagramFragments() throws ParserConfigurationException {
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docFactory.setNamespaceAware(true);
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
    public Node transformStringToNode(String xmlString) throws SAXException, IOException {
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    private String createShapeTemplate(BPMNDiagramElement diagram) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNDIShape.xml"));
        template = template.replaceAll("Shape_IdToReplace", diagram.getId());
        template = template.replaceAll("Event_IdToReplace", diagram.getRefScope().getId());
        template = template.replaceAll("X_PosToReplace", String.valueOf(diagram.getXpos()));
        template = template.replaceAll("Y_PosToReplace", String.valueOf(diagram.getYpos()));
        template = template.replaceAll("WidthToReplace", String.valueOf(diagram.getWidth()));
        template = template.replaceAll("HeightToReplace", String.valueOf(diagram.getHeight()));
        return template;
    }

    private Node createShapeAsNode(BPMNDiagramElement diagram) throws IOException, SAXException {
        String template = createShapeTemplate(diagram);
        return transformStringToNode(template);
    }


    private String createEdgeTemplate(BPMNDiagramElement diagram) throws IOException {
        String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("bpmn-snippets/BPMNDIEdge.xml"));
        template = template.replaceAll("Edge_IdToReplace", diagram.getId());
        template = template.replaceAll("Flow_IdToReplace", diagram.getRefScope().getId());
        template = template.replaceAll("X_SrcPosToReplace", String.valueOf(diagram.getXpos()));
        template = template.replaceAll("Y_SrcPosToReplace", String.valueOf(diagram.getYpos()));
        template = template.replaceAll("X_DstPosToReplace", String.valueOf(diagram.getWaypointOutX()));
        template = template.replaceAll("Y_DstPosToReplace", String.valueOf(diagram.getWaypointOutY()));
        return template;
    }

    private Node createEdgeAsNode(BPMNDiagramElement diagram) throws IOException, SAXException {
        String template = createEdgeTemplate(diagram);
        return transformStringToNode(template);
    }

    public Node createBPMNDiagramElementAsNode(BPMNDiagramElement diagram) throws IOException, SAXException {
        LOG.debug("Creating BPMN Diagram as Node {} with type {}", diagram.getId(), diagram.getType());
        if (diagram.getType() == BPMNDiagramType.EDGE) {
            return this.createEdgeAsNode(diagram);
        } else if (diagram.getType() == BPMNDiagramType.SHAPE) {
            return this.createShapeAsNode(diagram);
        }
        LOG.debug("No matching type is found for diagram");
        return null;
    }
}
