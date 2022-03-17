package org.opentosca.planbuilder.export;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.XmlId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.ids.elements.PlanId;
import org.eclipse.winery.model.ids.elements.PlansId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.common.RepositoryFileReference;

import org.apache.ode.schemas.dd._2007._03.TProvide;
import org.apache.tika.mime.MediaType;
import org.opentosca.container.core.impl.service.FileSystem;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.planbuilder.export.exporters.SimpleFileExporter;
import org.opentosca.planbuilder.integration.layer.AbstractExporter;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.Deploy;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
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
public class WineryExporter extends AbstractExporter {

    private final static Logger LOG = LoggerFactory.getLogger(WineryExporter.class);
    private final SimpleFileExporter simpleExporter = new SimpleFileExporter();

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


    // TODO: refactor with single list
    public PlanExportResult exportToCSAR(final List<AbstractPlan> plans, final CsarId csarId, IRepository repository, CsarStorageService storage) {
        List<AbstractPlan> bpmnBpelPlans = new ArrayList<>();
        for (final AbstractPlan plan : plans) {
            if (plan instanceof BPELPlan) {
                bpmnBpelPlans.add(plan);
            }

            if (plan instanceof BPMNPlan) {
                bpmnBpelPlans.add(plan);
            }
        }

        return exportBPELToCSAR(bpmnBpelPlans, csarId, repository, storage);
    }

    private org.eclipse.winery.model.tosca.TDefinitions getEntryDefs(Csar csar, IRepository repo) {
        Collection<RepositoryFileReference> entryDefRefs = new HashSet<RepositoryFileReference>();
        entryDefRefs.addAll(repo.getContainedFiles(new ServiceTemplateId(new QName(csar.entryServiceTemplate().getTargetNamespace(), csar.entryServiceTemplate().getId()))));
        for (RepositoryFileReference ref : entryDefRefs) {
            if (ref.getFileName().endsWith(".tosca")) {
                try {
                    return repo.definitionsFromRef(ref);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    // TODO: check plan language in the field of AbstractPlan
    // avoid using "instanceof"
    public PlanExportResult exportBPELToCSAR(final List<AbstractPlan> plans, final CsarId csarId, IRepository repository, CsarStorageService storage) {
        Csar csar = storage.findById(csarId);
        Collection<String> exportedBpelPlanIds = new ArrayList<String>();
        final TDefinitions defs = this.getEntryDefs(csar, repository);
        final TServiceTemplate serviceTemplate = defs.getServiceTemplates().get(0);
        final String csarName = csarId.csarName();
        final Path tempDir = FileSystem.getTemporaryFolder();
        final Path pathToRepackagedCsar = FileSystem.getTemporaryFolder();
        final Path repackagedCsar = pathToRepackagedCsar.resolve(csarName);

        try {

            final List<AbstractPlan> plansToExport = new ArrayList<>();

            // add plans element to servicetemplates
            List<TPlan> planList = serviceTemplate.getPlans();
            if (planList == null) {
                planList = new ArrayList<>();
                serviceTemplate.setPlans(planList);
            }

            // add the plan as an operation to the boundary
            // definitions
            TBoundaryDefinitions boundary = serviceTemplate.getBoundaryDefinitions();
            if (boundary == null) {
                boundary = new TBoundaryDefinitions();
                serviceTemplate.setBoundaryDefinitions(boundary);
            }

            List<TExportedInterface> ifaces = boundary.getInterfaces();

            if (ifaces == null) {
                ifaces = new ArrayList<>();
                boundary.setInterfaces(ifaces);
            }

            for (final AbstractPlan plan : plans) {
                if (plan instanceof BPELPlan) {
                    if (new QName(plan.getServiceTemplate().getTargetNamespace(), plan.getServiceTemplate().getId()).equals(buildQName(defs, serviceTemplate))) {
                        final TPlan generatedPlanElement = generateTPlanElement((BPELPlan) plan, repository, new ServiceTemplateId(new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId())));
                        exportedBpelPlanIds.add(generatedPlanElement.getId());
                        planList.add(generatedPlanElement);
                        plansToExport.add(plan);
                        TExportedInterface exportedIface = null;

                        // find already set openTOSCA lifecycle interface
                        for (final TExportedInterface exIface : ifaces) {
                            if (exIface.getName() != null && exIface.getName().equals(((BPELPlan) plan).getTOSCAInterfaceName())) {
                                exportedIface = exIface;
                            }
                        }

                        if (exportedIface == null) {
                            exportedIface = new TExportedInterface();
                            exportedIface.setName(((BPELPlan) plan).getTOSCAInterfaceName());
                            ifaces.add(exportedIface);
                        }

                        boolean alreadySpecified = false;
                        for (final TExportedOperation op : exportedIface.getOperation()) {
                            if (op.getName().equals(((BPELPlan) plan).getTOSCAOperationName())) {
                                alreadySpecified = true;
                            }
                        }

                        if (!alreadySpecified) {
                            final TExportedOperation newOp = new TExportedOperation();
                            newOp.setName(((BPELPlan) plan).getTOSCAOperationName());
                            final TExportedOperation.Plan newPlanRefElement =
                                new TExportedOperation.Plan();
                            newPlanRefElement.setPlanRef(generatedPlanElement);
                            newOp.setPlan(newPlanRefElement);
                            exportedIface.getOperation().add(newOp);
                        }
                    }

                    serviceTemplate.setBoundaryDefinitions(boundary);

                    ServiceTemplateId id = BackendUtils.getDefinitionsChildId(ServiceTemplateId.class, serviceTemplate.getTargetNamespace(), serviceTemplate.getId(), false);
                    BackendUtils.persist(repository, id, defs);

                    // Check if selfservice is already available
                    final Path selfServiceDir = tempDir.resolve("SELFSERVICE-Metadata");
                    final Path selfServiceDataXml = selfServiceDir.resolve("data.xml");
                    final JAXBContext jaxbContextWineryApplication = JAXBContext.newInstance(Application.class);

                    if (Files.exists(selfServiceDir) && Files.exists(selfServiceDataXml)) {
                        final Unmarshaller u = jaxbContextWineryApplication.createUnmarshaller();
                        final Application appDesc = (Application) u.unmarshal(selfServiceDataXml.toFile());

                        if (appDesc.getOptions() != null) {
                            // check if planInput etc. is set properly
                            final List<AbstractPlan> exportedPlans = new ArrayList<>();
                            for (final ApplicationOption option : appDesc.getOptions().getOption()) {
                                for (final AbstractPlan planToExport : plansToExport) {
                                    if (option.getPlanServiceName()
                                        .equals(getBuildPlanServiceName(((BPELPlan) plan).getDeploymentDeskriptor()).getLocalPart())) {
                                        final Path planInputFile = selfServiceDir.resolve(option.getPlanInputMessageUrl());
                                        if (!Files.exists(planInputFile)) {
                                            // the planinput file is defined in the xml, but no file exists in the csar -> write one
                                            writePlanInputMessageInstance((BPELPlan) plan, planInputFile.toFile());
                                            exportedPlans.add(planToExport);
                                        }
                                    }
                                }
                            }

                            if (exportedPlans.size() != plansToExport.size()) {
                                int optionCounter = 1 + appDesc.getOptions().getOption().size();
                                for (final AbstractPlan planToExport : plansToExport) {
                                    if (exportedPlans.contains(planToExport)) {
                                        continue;
                                    }
                                    if(planToExport instanceof BPELPlan){
                                        final ApplicationOption option = createApplicationOption((BPELPlan) planToExport, optionCounter);
                                        writePlanInputMessageInstance((BPELPlan) planToExport,
                                            selfServiceDir.resolve("plan.input.default." + optionCounter + ".xml").toFile());

                                        appDesc.getOptions().getOption().add(option);
                                        optionCounter++;
                                    }
                                }

                                final Marshaller wineryAppMarshaller = jaxbContextWineryApplication.createMarshaller();
                                wineryAppMarshaller.marshal(appDesc, selfServiceDataXml.toFile());
                            }
                        } else {
                            int optionCounter = 1;
                            final Application.Options options = new Application.Options();

                            for (final AbstractPlan planToExport : plansToExport) {
                                if(planToExport instanceof BPELPlan) {
                                    final ApplicationOption option = createApplicationOption((BPELPlan) plan, optionCounter);
                                    writePlanInputMessageInstance((BPELPlan) plan,
                                        selfServiceDir.resolve("plan.input.default." + optionCounter + ".xml").toFile());
                                    optionCounter++;
                                    options.getOption().add(option);
                                }
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

                        for (final AbstractPlan planToExport : plansToExport) {
                            if(planToExport instanceof BPELPlan) {
                                final ApplicationOption option = createApplicationOption((BPELPlan) planToExport, optionCounter);
                                writePlanInputMessageInstance((BPELPlan) planToExport,
                                    selfServiceDir.resolve("plan.input.default." + optionCounter + ".xml").toFile());
                                optionCounter++;
                                options.getOption().add(option);
                            }
                        }
                        appDesc.setOptions(options);

                        final Marshaller wineryAppMarshaller = jaxbContextWineryApplication.createMarshaller();
                        wineryAppMarshaller.marshal(appDesc, selfServiceDataXml.toFile());
                    }

                    FileSystem.zip(repackagedCsar, tempDir);
                }

                if (plan instanceof BPMNPlan) {
                    LOG.info("Processing BPMN a plan id {} with language {}", plan.getId(), plan.getLanguage());
                    if (new QName(plan.getServiceTemplate().getTargetNamespace(), plan.getServiceTemplate().getId()).equals(buildQName(defs, serviceTemplate))) {
                        LOG.info(""+ ((BPMNPlan) plan).getBpmnDocument());
                        final TPlan generatedPlanElement = generateTPlanElement((BPMNPlan) plan, repository, new ServiceTemplateId(new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId())));
                        exportedBpelPlanIds.add(generatedPlanElement.getId());
                        planList.add(generatedPlanElement);
                        plansToExport.add(plan);
                        TExportedInterface exportedIface = null;

                        // find already set openTOSCA lifecycle interface
                        for (final TExportedInterface exIface : ifaces) {
                            if (exIface.getName() != null && exIface.getName().equals(((BPMNPlan) plan).getTOSCAInterfaceName())) {
                                exportedIface = exIface;
                            }
                        }

                        if (exportedIface == null) {
                            exportedIface = new TExportedInterface();
                            exportedIface.setName(((BPMNPlan) plan).getTOSCAInterfaceName());
                            ifaces.add(exportedIface);
                        }

                        boolean alreadySpecified = false;
                        for (final TExportedOperation op : exportedIface.getOperation()) {
                            if (op.getName().equals(((BPMNPlan) plan).getTOSCAOperationName())) {
                                alreadySpecified = true;
                            }
                        }

                        if (!alreadySpecified) {
                            final TExportedOperation newOp = new TExportedOperation();
                            newOp.setName(((BPMNPlan) plan).getTOSCAOperationName());
                            final TExportedOperation.Plan newPlanRefElement =
                                new TExportedOperation.Plan();
                            newPlanRefElement.setPlanRef(generatedPlanElement);
                            newOp.setPlan(newPlanRefElement);
                            exportedIface.getOperation().add(newOp);
                        }
                    }

                    serviceTemplate.setBoundaryDefinitions(boundary);

                    ServiceTemplateId id = BackendUtils.getDefinitionsChildId(ServiceTemplateId.class, serviceTemplate.getTargetNamespace(), serviceTemplate.getId(), false);
                    BackendUtils.persist(repository, id, defs);

                    // Check if selfservice is already available
                    final Path selfServiceDir = tempDir.resolve("SELFSERVICE-Metadata");
                    final Path selfServiceDataXml = selfServiceDir.resolve("data.xml");
                    final JAXBContext jaxbContextWineryApplication = JAXBContext.newInstance(Application.class);

                    // TODO: implement the complete flow
                    if (Files.exists(selfServiceDir) && Files.exists(selfServiceDataXml)) {

                    } else {
                        // write SELFSERVICE-Metadata folder and files
                        Files.createDirectories(selfServiceDir);
                        // safeguard against an exception by checking whether the thing exists before trying to create it
                        if (!Files.exists(selfServiceDataXml)) {
                            Files.createFile(selfServiceDataXml);
                        }
                    }

                    final Application appDesc = new Application();

                    appDesc.setDisplayName(csarName);
                    appDesc.setDescription("No description available. This application was partially generated");
                    appDesc.setIconUrl("");
                    appDesc.setImageUrl("");

                    int optionCounter = 1;
                    final Application.Options options = new Application.Options();
                    for (final AbstractPlan planToExport : plansToExport) {
                        if (planToExport instanceof BPMNPlan) {
                            final ApplicationOption option = createApplicationOption(planToExport, optionCounter);

                            writeBPMNPlanInputParameter((BPMNPlan) planToExport,
                                selfServiceDir.resolve("plan.input.default." + optionCounter + ".xml").toFile());

                            optionCounter++;

                            options.getOption().add(option);
                        }
                    }

                    appDesc.setOptions(options);

                    final Marshaller wineryAppMarshaller = jaxbContextWineryApplication.createMarshaller();
                    wineryAppMarshaller.marshal(appDesc, selfServiceDataXml.toFile());


                    FileSystem.zip(repackagedCsar, tempDir);
                }

            }
        } catch (final IOException e) {
            WineryExporter.LOG.error("Some IO Exception occured", e);
        } catch (final JAXBException e) {
            WineryExporter.LOG.error("Some error while marshalling with JAXB", e);
        }

        WineryExporter.LOG.debug(repackagedCsar.toString());
        return new PlanExportResult(repackagedCsar, exportedBpelPlanIds);
    }

    private ApplicationOption createApplicationOption(final AbstractPlan plan, final int optionCounter) {
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
        if (plan instanceof BPELPlan) {
            option.setPlanServiceName(getBuildPlanServiceName(((BPELPlan) plan).getDeploymentDeskriptor()).getLocalPart());
        } else if (plan instanceof BPMNPlan) {
            // TODO: set correct service name
            option.setPlanServiceName("");
        }
        option.setPlanInputMessageUrl("plan.input.default." + optionCounter + ".xml");
        return option;
    }

    /**
     * Builds a valid QName for the given ServiceTemplate based on the given Definitions document
     *
     * @param defs            a JAXB Definitions
     * @param serviceTemplate a JAXB TServiceTemplate
     * @return a QName denoting the given ServiceTemplate
     */
    private QName buildQName(final org.eclipse.winery.model.tosca.TDefinitions defs, final org.eclipse.winery.model.tosca.TServiceTemplate serviceTemplate) {
        String namespace = serviceTemplate.getTargetNamespace();
        if (namespace == null) {
            namespace = defs.getTargetNamespace();
        }
        final String id = serviceTemplate.getId();
        return new QName(namespace, id);
    }

    /**
     * Generates a JAXB TPlan element for the given BuildPlan
     *
     * @param generatedPlan a Plan
     * @return a JAXB TPlan Object which represents the given BuildPlan
     */
    private org.eclipse.winery.model.tosca.TPlan generateTPlanElement(final AbstractPlan generatedPlan, IRepository repo, ServiceTemplateId servId) throws IOException, JAXBException {
        final TPlan plan = new TPlan();
        final TPlan.PlanModelReference ref = new TPlan.PlanModelReference();
        final List<TParameter> inputParams = new ArrayList<>();
        final List<TParameter> outputParams = new ArrayList<>();

        final Path tempDir = FileSystem.getTemporaryFolder();
        final Path planPath = tempDir.resolve(generateRelativePlanPath(generatedPlan));
        WineryExporter.LOG.debug(planPath.toString());
        Files.createDirectories(planPath.getParent());
        Files.createFile(planPath);

        if (generatedPlan instanceof BPELPlan) {
            this.simpleExporter.export(planPath.toUri(), (BPELPlan) generatedPlan);

            PlansId plansId = new PlansId(servId);

            PlanId planId = new PlanId(plansId, new XmlId(QName.valueOf(generatedPlan.getId()).getLocalPart(), false));
            RepositoryFileReference fileRef = new RepositoryFileReference(planId, planPath.getFileName().toString());
            repo.putContentToFile(fileRef, Files.newInputStream(planPath), MediaType.APPLICATION_ZIP);

            ref.setReference(repo.id2RelativePath(planId).resolve(planPath.getFileName()).toString());

            plan.setPlanModelReference(ref);

            for (final String paramName : ((BPELPlan) generatedPlan).getWsdl().getInputMessageLocalNames()) {
                // the builder supports only string types
                final TParameter param = new TParameter.Builder(paramName, "String", true).build();
                inputParams.add(param);
            }

            for (final String paramName : ((BPELPlan) generatedPlan).getWsdl().getOuputMessageLocalNames()) {
                final TParameter param = new TParameter.Builder(paramName, "String", true).build();
                outputParams.add(param);
            }

            plan.setId(QName.valueOf(generatedPlan.getId()).getLocalPart());
            plan.setPlanLanguage(BPELPlan.bpelNamespace);
        }

        if (generatedPlan instanceof BPMNPlan) {
            LOG.info(""+((BPMNPlan) generatedPlan).getBpmnDocument());

            this.simpleExporter.export(planPath.toUri(), (BPMNPlan) generatedPlan);

            PlansId plansId = new PlansId(servId);

            PlanId planId = new PlanId(plansId, new XmlId(QName.valueOf(generatedPlan.getId()).getLocalPart(), false));
            RepositoryFileReference fileRef = new RepositoryFileReference(planId, planPath.getFileName().toString());
            repo.putContentToFile(fileRef, Files.newInputStream(planPath), MediaType.APPLICATION_ZIP);

            ref.setReference(repo.id2RelativePath(planId).resolve(planPath.getFileName()).toString());

            plan.setPlanModelReference(ref);

            for (final String paramName : ((BPMNPlan) generatedPlan).getInputParameters()) {
                // the builder supports only string types
                final TParameter param = new TParameter.Builder(paramName, "String", true).build();
                inputParams.add(param);
            }

            for (final String paramName : ((BPMNPlan) generatedPlan).getOutputParameters()) {
                final TParameter param = new TParameter.Builder(paramName, "String", true).build();
                outputParams.add(param);
            }

            plan.setId(QName.valueOf(generatedPlan.getId()).getLocalPart());
            plan.setPlanLanguage(BPMNPlan.bpmnNamespace);
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

        return plan;
    }

    /**
     * Generates a relative path for the BuildPlan to be used inside a CSAR file
     *
     * @param buildPlan the BuildPlan to get the path for
     * @return a relative Path to be used inside a CSAR
     */
    private String generateRelativePlanPath(final AbstractPlan buildPlan) {
        if (buildPlan instanceof BPELPlan) {
            return "Plans/" + ((BPELPlan) buildPlan).getBpelProcessElement().getAttribute("name") + ".zip";
        }else{
            return "Plans/" + ((BPMNPlan) buildPlan).getBpmnProcessElement().getAttribute("id") + ".zip";
        }
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

    // TODO: implement method
    /**
     *
     * @param buildPlan
     * @param xmlFile
     */
    private void writeBPMNPlanInputParameter(final BPMNPlan buildPlan, final File xmlFile) {

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

    public class PlanExportResult {
        public Path csarFile;
        public Collection<String> planIds;

        public PlanExportResult(Path csarFile, Collection<String> planIds) {
            this.csarFile = csarFile;
            this.planIds = planIds;
        }
    }
}
