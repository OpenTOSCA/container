package org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PluginHandler {

    public ResourceHandler resHandler;
    public BPELProcessFragments bpelFrags;
    public DocumentBuilderFactory docFactory;
    public DocumentBuilder docBuilder;

    public PluginHandler() {
        try {
            this.resHandler = new ResourceHandler();
            this.bpelFrags = new BPELProcessFragments();
            this.docFactory = DocumentBuilderFactory.newInstance();
            this.docFactory.setNamespaceAware(true);
            this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (final ParserConfigurationException e) {
            BPELInvokeOperationHandler.LOG.error("Couldn't initialize ResourceHandler", e);
        }
    }

    
    public Node appendLOGMessageActivity(final BPELPlanContext context, final String message) {
        String logMessageTempStringVarName = null;
        String logMessageContent = null;
        logMessageTempStringVarName = "instanceDataLogMsg_" + System.currentTimeMillis();
        logMessageContent = message;

        // create variables
        logMessageTempStringVarName =
            context.createGlobalStringVariable(logMessageTempStringVarName, logMessageContent).getVariableName();

        final String logMessageReqVarName = createLogRequestMsgVar(context);
        final String planInstanceURLVar = context.getPlanInstanceURLVarName();

        try {


            Node logPOSTNode =
                new BPELProcessFragments().createBPEL4RESTLightPlanInstanceLOGsPOSTAsNode(planInstanceURLVar,
                                                                                          logMessageTempStringVarName,
                                                                                          logMessageReqVarName);
            logPOSTNode = context.importNode(logPOSTNode);

            return logPOSTNode;

        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public void appendLOGMessageActivity(final BPELPlanContext context, final String message,
                                         Element elementToAppendTo) {
        String logMessageTempStringVarName = null;
        String logMessageContent = null;
        logMessageTempStringVarName = "instanceDataLogMsg_" + System.currentTimeMillis();
        logMessageContent = message;

        // create variables
        logMessageTempStringVarName =
            context.createGlobalStringVariable(logMessageTempStringVarName, logMessageContent).getVariableName();

        final String logMessageReqVarName = createLogRequestMsgVar(context);
        final String planInstanceURLVar = context.getPlanInstanceURLVarName();

        try {


            Node logPOSTNode =
                new BPELProcessFragments().createBPEL4RESTLightPlanInstanceLOGsPOSTAsNode(planInstanceURLVar,
                                                                                          logMessageTempStringVarName,
                                                                                          logMessageReqVarName);
            logPOSTNode = context.importNode(logPOSTNode);

            elementToAppendTo.appendChild(logPOSTNode);

        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void appendLOGMessageActivity(final BPELPlanContext context, final String message,
                                         final BPELPlanContext.Phase phase) {
        String logMessageTempStringVarName = null;
        String logMessageContent = null;
        logMessageTempStringVarName = "instanceDataLogMsg_" + System.currentTimeMillis();
        logMessageContent = message;

        // create variables
        logMessageTempStringVarName =
            context.createGlobalStringVariable(logMessageTempStringVarName, logMessageContent).getVariableName();

        final String logMessageReqVarName = createLogRequestMsgVar(context);
        final String planInstanceURLVar = context.getPlanInstanceURLVarName();

        try {


            Node logPOSTNode =
                new BPELProcessFragments().createBPEL4RESTLightPlanInstanceLOGsPOSTAsNode(planInstanceURLVar,
                                                                                          logMessageTempStringVarName,
                                                                                          logMessageReqVarName);
            logPOSTNode = context.importNode(logPOSTNode);

            switch (phase) {
                case PRE:
                    context.getPrePhaseElement().appendChild(logPOSTNode);
                    break;
                case PROV:
                    context.getProvisioningPhaseElement().appendChild(logPOSTNode);
                    break;
                case POST:
                    context.getPostPhaseElement().appendChild(logPOSTNode);
                    break;

            }


        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String createLogRequestMsgVar(final BPELPlanContext context) {
        final String logMsgReqVarName = "logMessage" + context.getIdForNames();

        try {
            final File opentoscaApiSchemaFile = this.bpelFrags.getOpenTOSCAAPISchemaFile();
            QName logMsgRequestQName = this.bpelFrags.getOpenToscaApiLogMsgReqElementQName();
            context.registerType(logMsgRequestQName, opentoscaApiSchemaFile);
            logMsgRequestQName = context.importQName(logMsgRequestQName);

            context.addGlobalVariable(logMsgReqVarName, BPELPlan.VariableType.ELEMENT, logMsgRequestQName);
        }
        catch (final IOException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }

        return logMsgReqVarName;
    }

    public String findInterfaceForOperation(final BPELPlanContext context, final AbstractOperation operation) {
        List<AbstractInterface> interfaces = null;
        if (context.getNodeTemplate() != null) {
            interfaces = context.getNodeTemplate().getType().getInterfaces();
        } else {
            interfaces = context.getRelationshipTemplate().getRelationshipType().getSourceInterfaces();
            interfaces.addAll(context.getRelationshipTemplate().getRelationshipType().getTargetInterfaces());
        }

        if (interfaces != null && interfaces.size() > 0) {
            for (final AbstractInterface iface : interfaces) {
                for (final AbstractOperation op : iface.getOperations()) {
                    if (op.equals(operation)) {
                        return iface.getName();
                    }
                }
            }
        }
        return null;

    }

    public Variable findVar(final BPELPlanContext context, final String propName) {
        Variable propWrapper = context.getPropertyVariable(propName);
        if (propWrapper == null) {
            propWrapper = context.getPropertyVariable(propName, true);
            if (propWrapper == null) {
                propWrapper = context.getPropertyVariable(propName, false);
            }
        }
        return propWrapper;
    }

    /**
     * Loads a BPEL Assign fragment which queries the csarEntrypath from the input message into String
     * variable.
     *
     * @param assignName the name of the BPEL assign
     * @param csarEntryXpathQuery the csarEntryPoint XPath query
     * @param stringVarName the variable to load the queries results into
     * @return a DOM Node representing a BPEL assign element
     * @throws IOException is thrown when loading internal bpel fragments fails
     * @throws SAXException is thrown when parsing internal format into DOM fails
     */
    public Node loadAssignXpathQueryToStringVarFragmentAsNode(final String assignName, final String xpath2Query,
                                                              final String stringVarName) throws IOException,
                                                                                          SAXException {
        final String templateString =
            loadAssignXpathQueryToStringVarFragmentAsString(assignName, xpath2Query, stringVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Loads a BPEL Assign fragment which queries the csarEntrypath from the input message into String
     * variable.
     *
     * @param assignName the name of the BPEL assign
     * @param xpath2Query the csarEntryPoint XPath query
     * @param stringVarName the variable to load the queries results into
     * @return a String containing a BPEL Assign element
     * @throws IOException is thrown when reading the BPEL fragment form the resources fails
     */
    public String loadAssignXpathQueryToStringVarFragmentAsString(final String assignName, final String xpath2Query,
                                                                  final String stringVarName) throws IOException {
        // <!-- {AssignName},{xpath2query}, {stringVarName} -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("assignStringVarWithXpath2Query.xml");
        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("{AssignName}", assignName);
        template = template.replace("{xpath2query}", xpath2Query);
        template = template.replace("{stringVarName}", stringVarName);
        return template;
    }

}
