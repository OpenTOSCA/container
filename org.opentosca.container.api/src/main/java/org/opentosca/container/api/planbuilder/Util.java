package org.opentosca.container.api.planbuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.selfservice.ApplicationOption;

import org.apache.ode.schemas.dd._2007._03.TProvide;
import org.opentosca.container.core.impl.service.FileSystem;
import org.opentosca.planbuilder.export.VinothekKnownParameters;
import org.opentosca.planbuilder.export.WineryExporter;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.Deploy;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class Util {

    public static class SelfServiceOptionWrapper {

        public ApplicationOption option;
        public File planInputMessageFile;

        public SelfServiceOptionWrapper(final ApplicationOption option, final File planInputMessageFile) {
            this.option = option;
            this.planInputMessageFile = planInputMessageFile;
        }

        @Override
        public String toString() {
            return "SelfServiceOption Id: " + this.option.getId() + " Name: " + this.option.getName();
        }
    }

    public static SelfServiceOptionWrapper generateSelfServiceOption(final BPELPlan buildPlan) throws IOException {
        final String id = String.valueOf(System.currentTimeMillis());
        final ApplicationOption option = new ApplicationOption();

        final File tmpDir = FileSystem.getTemporaryFolder().toFile();
        tmpDir.mkdir();

        final File planInputMessageFile = new File(tmpDir, "plan.input.default." + id + ".xml");

        option.setName(Util.getBuildPlanServiceName(buildPlan.getDeploymentDeskriptor()).getLocalPart());
        option.setId(id);
        option.setIconUrl("");
        option.setDescription("N/A");
        option.setPlanServiceName(Util.getBuildPlanServiceName(buildPlan.getDeploymentDeskriptor()).getLocalPart());
        option.setPlanInputMessageUrl("plan.input.default." + id + ".xml");
        Util.writePlanInputMessageInstance(buildPlan, planInputMessageFile);

        return new SelfServiceOptionWrapper(option, planInputMessageFile);
    }

    /**
     * Writes given BuildPlan to temporary folder.
     *
     * @param buildPlan a BuildPlan
     * @return a File denoting the export location
     */
    public static File writePlan2TmpFolder(final BPELPlan buildPlan) {
        final WineryExporter planBuilderExporter = new WineryExporter();

        try {
            final File tmpDir = FileSystem.getTemporaryFolder().toFile();
            tmpDir.mkdir();

            final File uploadFile = new File(tmpDir.getAbsoluteFile() + System.getProperty("file.separator")
                + buildPlan.getBpelProcessElement().getAttribute("name") + ".zip");
            planBuilderExporter.exportToPlanFile(uploadFile.toURI(), buildPlan);
            return uploadFile;
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        } catch (final JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static QName getBuildPlanServiceName(final Deploy deploy) {
        // generated buildplans have only one process!
        for (final TProvide provide : deploy.getProcess().get(0).getProvide()) {
            // "client" is a convention
            if (provide.getPartnerLink().equals("client")) {
                return provide.getService().getName();
            }
        }
        return null;
    }

    private static void writePlanInputMessageInstance(final BPELPlan buildPlan, final File xmlFile) throws IOException {
        final String messageNs = buildPlan.getWsdl().getTargetNamespace();
        final String requestMessageLocalName = buildPlan.getWsdl().getRequestMessageLocalName();
        final List<String> inputParamNames = buildPlan.getWsdl().getInputMessageLocalNames();

        final VinothekKnownParameters paramMappings = new VinothekKnownParameters();
        final String soapMessagePrefix = Util.createPrefixPartOfSoapMessage(messageNs, requestMessageLocalName);
        final String soapMessageSuffix = Util.createSuffixPartOfSoapMessage(requestMessageLocalName);

        String soapMessage = soapMessagePrefix;
        for (final String inputParamName : inputParamNames) {
            soapMessage += paramMappings.createXmlElement(inputParamName);
        }
        soapMessage += soapMessageSuffix;

        Files.write(xmlFile.toPath(), soapMessage.getBytes(StandardCharsets.UTF_8));
    }

    private static String createPrefixPartOfSoapMessage(final String namespace, final String messageBodyRootLocalName) {
        return "<soapenv:Envelope xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:org=\""
            + namespace
            + "\"><soapenv:Header><wsa:ReplyTo><wsa:Address>%CALLBACK-URL%</wsa:Address></wsa:ReplyTo><wsa:Action>"
            + namespace
            + "/initiate</wsa:Action><wsa:MessageID>%CORRELATION-ID%</wsa:MessageID></soapenv:Header><soapenv:Body><org:"
            + messageBodyRootLocalName + ">";
    }

    private static String createSuffixPartOfSoapMessage(final String messageBodyRootLocalName) {
        return "</org:" + messageBodyRootLocalName + "></soapenv:Body></soapenv:Envelope>";
    }
}
