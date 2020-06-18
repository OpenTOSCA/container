package org.opentosca.planbuilder.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;

import org.apache.ode.schemas.dd._2007._03.TProvide;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.oasis_open.docs.tosca.ns._2011._12.Definitions;
import org.oasis_open.docs.tosca.ns._2011._12.ObjectFactory;
import org.oasis_open.docs.tosca.ns._2011._12.Plan;
import org.oasis_open.docs.tosca.ns._2011._12.TBoolean;
import org.oasis_open.docs.tosca.ns._2011._12.TBoundaryDefinitions;
import org.oasis_open.docs.tosca.ns._2011._12.TExportedInterface;
import org.oasis_open.docs.tosca.ns._2011._12.TExportedOperation;
import org.oasis_open.docs.tosca.ns._2011._12.TExtensibleElements;
import org.oasis_open.docs.tosca.ns._2011._12.TParameter;
import org.oasis_open.docs.tosca.ns._2011._12.TPlan;
import org.oasis_open.docs.tosca.ns._2011._12.TPlans;
import org.oasis_open.docs.tosca.ns._2011._12.TServiceTemplate;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.impl.service.FileSystem;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.legacy.core.model.CSARContent;
import org.opentosca.planbuilder.core.csarhandler.CSARHandler;
import org.opentosca.planbuilder.export.exporters.SimpleFileExporter;
import org.opentosca.planbuilder.integration.layer.AbstractExporter;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.Deploy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * This class is a Exporter class for local filesystem exports based on the OpenTOSCA Core
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
@Service
public class Exporter extends AbstractExporter {

    private final static Logger LOG = LoggerFactory.getLogger(Exporter.class);

    private final SimpleFileExporter simpleExporter = new SimpleFileExporter();

    private final ObjectFactory toscaFactory = new ObjectFactory();
    private final CSARHandler handler = new CSARHandler();


    public class PlanExportResult{
        public Path csarFile;
        public Collection<String> planIds;
        
        public PlanExportResult(Path csarFile, Collection<String> planIds) {
            this.csarFile = csarFile;
            this.planIds = planIds;
        }
    }
    
    /**
     * Constructor
     */
    public Exporter() {
    }

    /**
     * Exports the given BuildPlan to the given URI
     *
     * @param destination the absolute location to export to
     * @param buildPlan   the BuildPlan to export
     * @throws IOException   is thrown when reading/writing to the given URI fails
     * @throws JAXBException is thrown when writing with JAXB fails
     */
    public void exportToPlanFile(final URI destination, final AbstractPlan buildPlan) throws IOException, JAXBException {
        this.simpleExporter.export(destination, (BPELPlan) buildPlan);
    }

    public PlanExportResult exportToCSAR(final List<AbstractPlan> plans, final CSARID csarId) {
        final List<BPELPlan> bpelPlans = new ArrayList<>();        
        
        for (final AbstractPlan plan : plans) {
            if (plan instanceof BPELPlan) {
                bpelPlans.add((BPELPlan) plan);
            }
        }

        return exportBPELToCSAR(bpelPlans, csarId);
    }

    public PlanExportResult exportBPELToCSAR(final List<BPELPlan> plans, final CSARID csarId) {
        
        Collection<String> exportedBpelPlanIds = new ArrayList<String>();

        CSARContent csarContent = null;
        try {
            csarContent = this.handler.getCSARContentForID(csarId);
        } catch (final UserException e1) {
            Exporter.LOG.error("Error occured while trying to retrieve CSAR content", e1);
        }

        if (csarContent == null) {
            return null;
        }

        final String csarName = csarId.getFileName();

        final Path tempDir = FileSystem.getTemporaryFolder();
        final Path pathToRepackagedCsar = FileSystem.getTemporaryFolder();
        final Path repackagedCsar = pathToRepackagedCsar.resolve(csarName);

        try {
            final Set<AbstractFile> files = csarContent.getFilesRecursively();
            final AbstractFile mainDefFile = csarContent.getRootTOSCA();
            final File rootDefFile = mainDefFile.getFile().toFile();
            final Definitions defs = parseDefinitionsFile(rootDefFile);
            final List<TServiceTemplate> servTemps = getServiceTemplates(defs);

            final List<BPELPlan> plansToExport = new ArrayList<>();

            // add plans element to servicetemplates
            for (final TServiceTemplate serviceTemplate : servTemps) {
                TPlans toscaPlansElement = serviceTemplate.getPlans();
                if (toscaPlansElement == null) {
                    toscaPlansElement = this.toscaFactory.createTPlans();
                    serviceTemplate.setPlans(toscaPlansElement);
                }
                final List<TPlan> planList = toscaPlansElement.getPlan();

                // add the plan as an operation to the boundary
                // definitions
                TBoundaryDefinitions boundary = serviceTemplate.getBoundaryDefinitions();
                if (boundary == null) {
                    boundary = this.toscaFactory.createTBoundaryDefinitions();
                    serviceTemplate.setBoundaryDefinitions(boundary);
                }

                org.oasis_open.docs.tosca.ns._2011._12.TBoundaryDefinitions.Interfaces ifaces =
                    boundary.getInterfaces();

                if (ifaces == null) {
                    ifaces = this.toscaFactory.createTBoundaryDefinitionsInterfaces();
                    boundary.setInterfaces(ifaces);
                }

                for (final BPELPlan plan : plans) {
                    if (plan.getServiceTemplate().getQName().equals(buildQName(defs, serviceTemplate))) {

                        final TPlan generatedPlanElement = generateTPlanElement(plan);
                        exportedBpelPlanIds.add(generatedPlanElement.getId());
                        planList.add(generatedPlanElement);
                        plansToExport.add(plan);

                        TExportedInterface exportedIface = null;

                        // find already set openTOSCA lifecycle interface
                        for (final TExportedInterface exIface : ifaces.getInterface()) {

                            if (exIface.getName() != null && exIface.getName().equals(plan.getTOSCAInterfaceName())) {
                                exportedIface = exIface;
                            }
                        }

                        if (exportedIface == null) {
                            exportedIface = this.toscaFactory.createTExportedInterface();
                            exportedIface.setName(plan.getTOSCAInterfaceName());
                            ifaces.getInterface().add(exportedIface);
                        }

                        boolean alreadySpecified = false;
                        for (final TExportedOperation op : exportedIface.getOperation()) {
                            if (op.getName().equals(plan.getTOSCAOperationName())) {
                                alreadySpecified = true;
                            }
                        }

                        if (!alreadySpecified) {
                            final TExportedOperation newOp = this.toscaFactory.createTExportedOperation();
                            newOp.setName(plan.getTOSCAOperationName());
                            final org.oasis_open.docs.tosca.ns._2011._12.TExportedOperation.Plan newPlanRefElement =
                                this.toscaFactory.createTExportedOperationPlan();
                            newPlanRefElement.setPlanRef(generatedPlanElement);
                            newOp.setPlan(newPlanRefElement);
                            exportedIface.getOperation().add(newOp);
                        }
                    }
                }
            }

            final Path csarRoot = Paths.get(csarContent.getDirectory(".").getPath()).getParent();
            for (final AbstractFile file : files) {
                if (file.getFile().toString().equals(rootDefFile.toString())) {
                    continue;
                }

                final Path filePath = Paths.get(file.getPath());
                final Path relativeToRoot = csarRoot.relativize(filePath);
                final Path newLocation = tempDir.resolve(relativeToRoot);
                LOG.debug("Exporting {} to {}", file.getFile(), newLocation.toAbsolutePath().toString());
                // must ensure that the directory we want to copy the file to exists.
                Files.createDirectories(newLocation.getParent());
                Files.copy(file.getFile(), newLocation);
            }

            // write new defs file

            final File newDefsFile = tempDir.resolve(csarRoot.relativize(Paths.get(mainDefFile.getPath()))).toFile();
            Files.createDirectories(newDefsFile.toPath().getParent());
            newDefsFile.createNewFile();

            final JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
            final Marshaller m = jaxbContext.createMarshaller();
            final FileWriter writer = new FileWriter(newDefsFile);

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // output to the console: m.marshal(defs, System.out);
            try {
                m.marshal(defs, writer);
            } catch (final FactoryConfigurationError e) {
                LOG.error("Could not configure factory for XML Export", e);
            }

            // write plans
            for (final BPELPlan plan : plansToExport) {
                final Path planPath = tempDir.resolve(generateRelativePlanPath(plan));
                Exporter.LOG.debug(planPath.toString());
                Files.createDirectories(planPath.getParent());
                Files.createFile(planPath);
                this.simpleExporter.export(planPath.toUri(), plan);
            }

            // Check if selfservice is already available
            final Path selfServiceDir = tempDir.resolve("SELFSERVICE-Metadata");
            final Path selfServiceDataXml = selfServiceDir.resolve("data.xml");
            final JAXBContext jaxbContextWineryApplication = JAXBContext.newInstance(Application.class);

            if (Files.exists(selfServiceDir) && Files.exists(selfServiceDataXml)) {
                final Unmarshaller u = jaxbContextWineryApplication.createUnmarshaller();
                final Application appDesc = (Application) u.unmarshal(selfServiceDataXml.toFile());

                if (appDesc.getOptions() != null) {
                    // check if planInput etc. is set properly
                    final List<BPELPlan> exportedPlans = new ArrayList<>();
                    for (final ApplicationOption option : appDesc.getOptions().getOption()) {
                        for (final BPELPlan plan : plansToExport) {
                            if (option.getPlanServiceName()
                                .equals(getBuildPlanServiceName(plan.getDeploymentDeskriptor()).getLocalPart())) {
                                final Path planInputFile = selfServiceDir.resolve(option.getPlanInputMessageUrl());
                                if (!Files.exists(planInputFile)) {
                                    // the planinput file is defined in the xml, but no file exists in the csar -> write one
                                    writePlanInputMessageInstance(plan, planInputFile.toFile());
                                    exportedPlans.add(plan);
                                }
                            }
                        }
                    }

                    if (exportedPlans.size() != plansToExport.size()) {

                        int optionCounter = 1 + appDesc.getOptions().getOption().size();
                        for (final BPELPlan plan : plansToExport) {
                            if (exportedPlans.contains(plan)) {
                                continue;
                            }

                            final ApplicationOption option = createApplicationOption(plan, optionCounter);
                            writePlanInputMessageInstance(plan,
                                selfServiceDir.resolve("plan.input.default." + optionCounter + ".xml").toFile());

                            appDesc.getOptions().getOption().add(option);
                            optionCounter++;
                        }

                        final Marshaller wineryAppMarshaller = jaxbContextWineryApplication.createMarshaller();
                        wineryAppMarshaller.marshal(appDesc, selfServiceDataXml.toFile());
                    }
                } else {
                    int optionCounter = 1;
                    final Application.Options options = new Application.Options();

                    for (final BPELPlan plan : plansToExport) {
                        final ApplicationOption option = createApplicationOption(plan, optionCounter);
                        writePlanInputMessageInstance(plan,
                            selfServiceDir.resolve("plan.input.default." + optionCounter + ".xml").toFile());
                        optionCounter++;
                        options.getOption().add(option);
                    }
                    appDesc.setOptions(options);

                    final Marshaller wineryAppMarshaller = jaxbContextWineryApplication.createMarshaller();
                    wineryAppMarshaller.marshal(appDesc, selfServiceDataXml.toFile());
                }
            } else {
                // write SELFSERVICE-Metadata folder and files
                Files.createDirectories(selfServiceDir);
                // safeguard against an exception by checking whether the thing exists before trying to create it
                if (!Files.exists(selfServiceDataXml)) {
                    Files.createFile(selfServiceDataXml);
                }
                final Application appDesc = new Application();

                appDesc.setDisplayName(csarName);
                appDesc.setDescription("No description available. This application was partially generated");
                appDesc.setIconUrl("");
                appDesc.setImageUrl("");

                int optionCounter = 1;
                final Application.Options options = new Application.Options();

                for (final BPELPlan plan : plansToExport) {
                    final ApplicationOption option = createApplicationOption(plan, optionCounter);
                    writePlanInputMessageInstance(plan,
                        selfServiceDir.resolve("plan.input.default." + optionCounter + ".xml").toFile());
                    optionCounter++;
                    options.getOption().add(option);
                }
                appDesc.setOptions(options);

                final Marshaller wineryAppMarshaller = jaxbContextWineryApplication.createMarshaller();
                wineryAppMarshaller.marshal(appDesc, selfServiceDataXml.toFile());
            }

            FileSystem.zip(repackagedCsar, tempDir);
        } catch (final IOException e) {
            Exporter.LOG.error("Some IO Exception occured", e);
        } catch (final JAXBException e) {
            Exporter.LOG.error("Some error while marshalling with JAXB", e);
        } catch (final SystemException e) {
            Exporter.LOG.error("Some error in the openTOSCA Core", e);
        }
        
        Exporter.LOG.debug(repackagedCsar.toString());        
        return new PlanExportResult(repackagedCsar, exportedBpelPlanIds);
    }

    private ApplicationOption createApplicationOption(final BPELPlan plan, final int optionCounter) {
        final ApplicationOption option = new ApplicationOption();
        switch (plan.getType()) {
            case BUILD:
                option.setName("Build" + optionCounter);
                option.setDescription("Generated BuildPlan");
                break;
            case MANAGEMENT:
                option.setName("Manage" + optionCounter);
                option.setDescription("Generated ManagementPlan");
                break;
            case TERMINATION:
                option.setName("Terminate" + optionCounter);
                option.setDescription("Generated TerminationPlan");
                break;
        }
        option.setId(String.valueOf(optionCounter));
        option.setIconUrl("");
        option.setPlanServiceName(getBuildPlanServiceName(plan.getDeploymentDeskriptor()).getLocalPart());
        option.setPlanInputMessageUrl("plan.input.default." + optionCounter + ".xml");
        return option;
    }

    /**
     * Parses the given file to a JAXB Definitions class
     *
     * @param file a File denoting to a TOSCA Definitions file
     * @return a JAXB Definitions class object if parsing was without errors, else null
     */
    private Definitions parseDefinitionsFile(final File file) {
        Definitions def = null;
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            def = (Definitions) unmarshaller.unmarshal(new FileReader(file));
        } catch (final JAXBException e) {
            Exporter.LOG.error("Error while reading a Definitions file", e);
            return null;
        } catch (final FileNotFoundException e) {
            Exporter.LOG.error("Definitions file not found", e);
            return null;
        }
        return def;
    }

    /**
     * Builds a valid QName for the given ServiceTemplate based on the given Definitions document
     *
     * @param defs            a JAXB Definitions
     * @param serviceTemplate a JAXB TServiceTemplate
     * @return a QName denoting the given ServiceTemplate
     */
    private QName buildQName(final Definitions defs, final TServiceTemplate serviceTemplate) {
        String namespace = serviceTemplate.getTargetNamespace();
        if (namespace == null) {
            namespace = defs.getTargetNamespace();
        }
        final String id = serviceTemplate.getId();
        return new QName(namespace, id);
    }

    /**
     * Returns a List of TServiceTemplate of the given Definitions document
     *
     * @param defs a JAXB Definitions document
     * @return a List of TServiceTemplate which are the ServiceTemplates of the given Definitions Document
     */
    private List<TServiceTemplate> getServiceTemplates(final Definitions defs) {
        final List<TServiceTemplate> servTemps = new ArrayList<>();

        for (final TExtensibleElements element : defs.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
            if (element instanceof TServiceTemplate) {
                servTemps.add((TServiceTemplate) element);
            }
        }
        return servTemps;
    }

    /**
     * Generates a JAXB TPlan element for the given BuildPlan
     *
     * @param generatedPlan a Plan
     * @return a JAXB TPlan Object which represents the given BuildPlan
     */
    private TPlan generateTPlanElement(final BPELPlan generatedPlan) {
        final TPlan plan = new Plan();
        final TPlan.PlanModelReference ref = new TPlan.PlanModelReference();
        final TPlan.InputParameters inputParams = new TPlan.InputParameters();
        final TPlan.OutputParameters outputParams = new TPlan.OutputParameters();
        final List<TParameter> inputParamsList = inputParams.getInputParameter();
        final List<TParameter> outputParamsList = outputParams.getOutputParameter();

        ref.setReference(generateRelativePlanPath(generatedPlan));
        plan.setPlanModelReference(ref);

        for (final String paramName : generatedPlan.getWsdl().getInputMessageLocalNames()) {
            // the builder supports only string types
            final TParameter param = this.toscaFactory.createTParameter();
            param.setName(paramName);
            param.setRequired(TBoolean.YES);
            param.setType("String");
            inputParamsList.add(param);
        }

        for (final String paramName : generatedPlan.getWsdl().getOuputMessageLocalNames()) {
            final TParameter param = this.toscaFactory.createTParameter();
            param.setName(paramName);
            param.setRequired(TBoolean.YES);
            param.setType("String");
            outputParamsList.add(param);
        }

        plan.setInputParameters(inputParams);
        plan.setOutputParameters(outputParams);

        switch (generatedPlan.getType()) {
            case BUILD:
                plan.setPlanType(PlanType.BUILD.toString());
                break;
            case TERMINATION:
                plan.setPlanType(PlanType.TERMINATION.toString());
                break;
            case TRANSFORMATION:
                plan.setPlanType(PlanType.TRANSFORMATION.toString());
                break;
            default:
                // every other plan is a management plan
            case MANAGEMENT:
                plan.setPlanType(PlanType.MANAGEMENT.toString());
                break;
        }

        plan.setId(generatedPlan.getBpelProcessElement().getAttribute("name"));
        plan.setPlanLanguage(BPELPlan.bpelNamespace);

        return plan;
    }

    /**
     * Generates a relative path for the BuildPlan to be used inside a CSAR file
     *
     * @param buildPlan the BuildPlan to get the path for
     * @return a relative Path to be used inside a CSAR
     */
    private String generateRelativePlanPath(final BPELPlan buildPlan) {
        return "Plans/" + buildPlan.getBpelProcessElement().getAttribute("name") + ".zip";
    }

    private QName getBuildPlanServiceName(final Deploy deploy) {
        // generated buildplans have only one process!
        for (final TProvide provide : deploy.getProcess().get(0).getProvide()) {
            // "client" is a convention
            if (provide.getPartnerLink().equals("client")) {
                return provide.getService().getName();
            }
        }
        return null;
    }

    private void writePlanInputMessageInstance(final BPELPlan buildPlan, final File xmlFile) throws IOException {
        final String messageNs = buildPlan.getWsdl().getTargetNamespace();
        final String requestMessageLocalName = buildPlan.getWsdl().getRequestMessageLocalName();
        final List<String> inputParamNames = buildPlan.getWsdl().getInputMessageLocalNames();

        final VinothekKnownParameters paramMappings = new VinothekKnownParameters();
        final String soapMessagePrefix = createPrefixPartOfSoapMessage(messageNs, requestMessageLocalName);
        final String soapMessageSuffix = createSuffixPartOfSoapMessage(requestMessageLocalName);

        String soapMessage = soapMessagePrefix;
        for (final String inputParamName : inputParamNames) {
            soapMessage += paramMappings.createXmlElement(inputParamName);
        }
        soapMessage += soapMessageSuffix;

        Files.write(xmlFile.toPath(), soapMessage.getBytes(StandardCharsets.UTF_8));
    }

    private String createPrefixPartOfSoapMessage(final String namespace, final String messageBodyRootLocalName) {
        final String soapEnvelopePrefix =
            "<soapenv:Envelope xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:org=\""
                + namespace
                + "\"><soapenv:Header><wsa:ReplyTo><wsa:Address>%CALLBACK-URL%</wsa:Address></wsa:ReplyTo><wsa:Action>"
                + namespace
                + "/initiate</wsa:Action><wsa:MessageID>%CORRELATION-ID%</wsa:MessageID></soapenv:Header><soapenv:Body><org:"
                + messageBodyRootLocalName + ">";
        return soapEnvelopePrefix;
    }

    private String createSuffixPartOfSoapMessage(final String messageBodyRootLocalName) {
        final String soapEnvelopeSuffix = "</org:" + messageBodyRootLocalName + "></soapenv:Body></soapenv:Envelope>";
        return soapEnvelopeSuffix;
    }
}
