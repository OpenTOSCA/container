package org.opentosca.container.core.engine.impl.consolidation;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.container.core.engine.impl.ToscaEngineServiceImpl;
import org.opentosca.container.core.engine.impl.ToscaReferenceMapper;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.model.TExportedInterface;
import org.opentosca.container.core.tosca.model.TExportedOperation;
import org.opentosca.container.core.tosca.model.TExportedOperation.Plan;
import org.opentosca.container.core.tosca.model.TPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * This class consolidates the BoundaryDefinitions and Plans to PublicPlans.
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 */
public class ExportedInterfacesConsolidation {

    ToscaReferenceMapper toscaReferenceMapper = ToscaEngineServiceImpl.toscaReferenceMapper;

    /**
     * NamespaceContext
     */
    XPath xpath = XPathFactory.newInstance().newXPath();
    NamespaceContext nscontext = new NamespaceContext() {

        @Override
        public String getNamespaceURI(final String prefix) {
            String uri;
            if (prefix.equals("wsdl")) {
                uri = "http://schemas.xmlsoap.org/wsdl/";
            } else if (prefix.equals("xs")) {
                uri = "http://www.w3.org/2001/XMLSchema";
            } else if (prefix.equals("tosca")) {
                uri = "http://docs.oasis-open.org/tosca/ns/2011/12";
            } else {
                uri = null;
            }
            return uri;
        }

        // Dummy implementation
        // Suppress warnings because of this method is auto generated
        // and not
        // used.
        @SuppressWarnings("rawtypes")
        @Override
        public Iterator getPrefixes(final String val) {
            return null;
        }

        // Dummy implemenation - not used!
        @Override
        public String getPrefix(final String uri) {
            return null;
        }
    };

    private final Logger LOG = LoggerFactory.getLogger(ExportedInterfacesConsolidation.class);


    public ExportedInterfacesConsolidation() {
        this.xpath.setNamespaceContext(this.nscontext);
    }

    /**
     * Consolidates the exported interfaces of a CSAR.
     *
     * @param csarID the ID of the CSAR.
     * @return true for success, false if an error occured
     */
    public boolean consolidate(final CSARID csarID) {

        this.LOG.info("Consolidate the Interfaces of the BoundaryDefinitions of CSAR \"" + csarID + "\".");

        // return value is negated, thus inside this method a true means at
        // least one error
        final boolean errorOccured = false;

        final Map<PlanTypes, LinkedHashMap<QName, TPlan>> mapTypeToPlan = this.toscaReferenceMapper.getCSARIDToPlans(
            csarID);

        for (final QName serviceTemplateID : this.toscaReferenceMapper.getExportedInterfacesOfCSAR(csarID).keySet()) {

            this.LOG.debug("Consolidate the Interfaces of the ServiceTemplate \"" + serviceTemplateID + "\".");

            for (final TExportedInterface iface : this.toscaReferenceMapper.getExportedInterfacesOfCSAR(csarID)
                                                                           .get(serviceTemplateID)) {

                for (final TExportedOperation operation : iface.getOperation()) {

                    final Plan planReference = operation.getPlan();
                    if (null != planReference) {
                        final TPlan toscaPlan = (TPlan) planReference.getPlanRef();

                        final QName planID = new QName(
                            this.toscaReferenceMapper.getNamespaceOfPlan(csarID, toscaPlan.getId()), toscaPlan.getId());

                        // toscaReferenceMapper.setBoundaryInterfaceForCSARIDPlan(csarID,
                        // serviceTemplateID, planID, iface.getName());
                        // toscaReferenceMapper.setBoundaryOperationForCSARIDPlan(csarID,
                        // serviceTemplateID, planID, operation.getName());
                        this.toscaReferenceMapper.storeServiceTemplateBoundsPlan(csarID, serviceTemplateID,
                            iface.getName(), operation.getName(), planID);

                        mapTypeToPlan.get(PlanTypes.isPlanTypeURI(toscaPlan.getPlanType())).put(planID, toscaPlan);

                        this.toscaReferenceMapper.storePlanInputMessageID(csarID, planID,
                            this.getInputMessageQName(csarID, iface.getName(), operation.getName(), toscaPlan.getId(),
                                toscaPlan, this.toscaReferenceMapper.getListOfWSDLForCSAR(csarID)));

                    } else {
                        // just need the plans
                    }
                }

            }
        }

        return !errorOccured;
    }

    /**
     * TODO implement for wsdl 2.0 TODO transitive reloading of imported stuff? TODO all informations
     * have to be in one wsdl (change this?)
     *
     * @param planID
     *
     * @param boundaryPlan
     * @param list
     * @throws XPathExpressionException
     */
    private QName getInputMessageQName(final CSARID csarID, final String wsdlInterfaceName,
                    final String wsdlOperationName, final String planID, final TPlan boundaryPlan,
                    final List<Document> list) {

        this.LOG.debug("Try to find the InputMessageID for CSAR " + csarID + " and plan " + boundaryPlan.getId());

        this.LOG.debug("countwsdl: " + list.size() + " interfacename:" + wsdlInterfaceName + " operationname:"
            + wsdlOperationName);

        // IXMLSerializer serializer =
        // ServiceHandler.xmlSerializerService.getXmlSerializer();

        for (final Document doc : list) {

            try {
                // select specific PortType/Interface with name and operation
                String exprString = "/wsdl:definitions/wsdl:portType[@name=\"" + wsdlInterfaceName
                    + "\"]/wsdl:operation[@name=\"" + wsdlOperationName + "\"]/wsdl:input/@message";
                XPathExpression expr;

                expr = this.xpath.compile(exprString);
                final NodeList messageQName = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

                this.LOG.debug(exprString);
                this.LOG.trace("Found results: " + messageQName.getLength());
                // LOG.trace(serializer.docToString(doc, true));

                // if there is a PortType/Interface in this document, then there
                // is the message defined as well
                if (messageQName.getLength() == 1) {

                    final QName id = new QName(this.toscaReferenceMapper.getNamespaceOfPlan(csarID, planID), planID);

                    this.LOG.debug("Found the message QName {} for plan {}.", messageQName, id);

                    // check whether synchronous or asynchronous
                    exprString = "/wsdl:definitions/wsdl:portType[@name=\"" + wsdlInterfaceName
                        + "\"]/wsdl:operation[@name=\"" + wsdlOperationName + "\"]/wsdl:output/@message";
                    expr = this.xpath.compile(exprString);
                    final NodeList output = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                    if (output.getLength() > 0) {
                        // this is a synchronous bpel plan
                        this.LOG.debug("This plan is synchronous.");
                        this.toscaReferenceMapper.storePlanAsynchronousBoolean(csarID, id, false);
                    } else if (output.getLength() == 0) {
                        // this is an asynchronous bpel plan
                        this.LOG.debug("This plan is asynchronous.");
                        this.toscaReferenceMapper.storePlanAsynchronousBoolean(csarID, id, true);
                    }

                    // wsdl porttype/interface to wsdl message
                    String value = messageQName.item(0).getNodeValue();
                    // String prefix = value.substring(0, value.indexOf(":"));
                    final String name = value.substring(value.indexOf(":") + 1);
                    // String namespace = doc.lookupNamespaceURI(prefix);
                    // QName wsdlMessageID = new QName(namespace, name);

                    this.LOG.debug("Found the PortType. Searching for the message \"" + name + "\".");

                    // wsdl message to schema message
                    // TODO multiple parts?
                    exprString = "/wsdl:definitions/wsdl:message[@name=\"" + name + "\"]/wsdl:part/@element";
                    // exprString = "/wsdl:definitions/wsdl:message[@name=\"" +
                    // name
                    // + "\"]/wsdl:part[@name=\"payload\"]/@element";
                    expr = this.xpath.compile(exprString);
                    final NodeList messages = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

                    this.LOG.debug(exprString);
                    this.LOG.debug("Count results: " + messages.getLength());

                    if (messages.getLength() == 1) {

                        value = messages.item(0).getNodeValue();

                        this.LOG.debug("Value of " + messages.item(0).getLocalName() + " is " + value);

                        final QName messageID = new QName(
                            doc.lookupNamespaceURI(value.substring(0, value.indexOf(":"))),
                            value.substring(value.indexOf(":") + 1));
                        this.LOG.debug("Found message QName: " + messageID.toString());
                        return messageID;

                    }
                }

            } catch (final XPathExpressionException e) {
                e.printStackTrace();
            }
        }

        this.LOG.error("Did not find the message!");

        return null;
    }
}
