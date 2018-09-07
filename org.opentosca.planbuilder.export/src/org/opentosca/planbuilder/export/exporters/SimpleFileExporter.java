package org.opentosca.planbuilder.export.exporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.ode.schemas.dd._2007._03.TInvoke;
import org.apache.ode.schemas.dd._2007._03.TProvide;
import org.apache.ode.schemas.dd._2007._03.TService;
import org.opentosca.container.core.service.IFileAccessService;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.Deploy;
import org.opentosca.planbuilder.model.plan.bpel.GenericWsdlWrapper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.wsdl.ServiceImpl;

/**
 * <p>
 * This class is used to export buildPlans on filesystems
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class SimpleFileExporter {

    private final static Logger LOG = LoggerFactory.getLogger(SimpleFileExporter.class);

    // wrapper class for the rewriting of service names in WSDL's
    public class Service2ServiceEntry {

        public QName service0;
        public QName service1;

        public Service2ServiceEntry(final QName service0, final QName service1) {
            this.service0 = service0;
            this.service1 = service1;
        }
    }

    /**
     * Exports the given BuildPlan to the given URI location
     *
     * @param destination the URI to export to
     * @param buildPlan the BuildPlan to export
     * @return true iff exporting the BuildPlan was successful
     * @throws IOException is thrown when reading/writing the file fails
     * @throws JAXBException is thrown when writing with JAXB fails
     */
    public boolean export(final URI destination, final BPELPlan buildPlan) throws IOException, JAXBException {
        if (!new File(destination).getName().contains("zip")) {
            return false;
        }
        // fetch imported files
        final Set<File> importedFiles = buildPlan.getImportedFiles();

        SimpleFileExporter.LOG.debug("BuildPlan has following files attached");
        for (final File file : importedFiles) {
            SimpleFileExporter.LOG.debug(file.getAbsolutePath());
        }

        // fetch import elements
        final List<Element> importElements = buildPlan.getBpelImportElements();

        SimpleFileExporter.LOG.debug("BuildPlan has following import elements");
        for (final Element element : importElements) {
            SimpleFileExporter.LOG.debug("LocalName: " + element.getLocalName());
            SimpleFileExporter.LOG.debug("location:" + element.getAttribute("location"));
        }

        // fetch wsdl
        final GenericWsdlWrapper wsdl = buildPlan.getWsdl();

        // generate temp folder
        final File tempDir = this.getFileAccessService().getTemp();
        SimpleFileExporter.LOG.debug("Trying to write files in system temp folder: " + tempDir.getAbsolutePath());
        final File tempFolder = new File(tempDir, Long.toString(System.currentTimeMillis()));
        tempFolder.mkdir();
        SimpleFileExporter.LOG.debug("Trying to write files to temp folder: " + tempFolder.getAbsolutePath());

        final List<File> exportedFiles = new ArrayList<>();

        // match importedFiles with importElements, to change temporary paths
        // inside import elements to relative paths inside the generated zip
        for (final File importedFile : importedFiles) {
            for (final Element importElement : importElements) {
                final String filePath = importedFile.getAbsolutePath();
                final String locationPath = importElement.getAttribute("location");
                SimpleFileExporter.LOG.debug("checking filepath:");
                SimpleFileExporter.LOG.debug(filePath);
                SimpleFileExporter.LOG.debug("with: ");
                SimpleFileExporter.LOG.debug(locationPath);
                if (importedFile.getAbsolutePath().trim().equals(importElement.getAttribute("location").trim())) {
                    // found the import element for the corresponding file
                    // get file name
                    final String fileName = importedFile.getName();
                    SimpleFileExporter.LOG.debug("Trying to reset path to: " + fileName);
                    // change location attribute in import element
                    importElement.setAttribute("location", fileName);
                    // copy file to tempdir
                    final File fileLocationInDir = new File(tempFolder, fileName);
                    FileUtils.copyFile(importedFile, fileLocationInDir);

                    LOG.debug("Adding " + fileLocationInDir + " to files to export");
                    exportedFiles.add(fileLocationInDir);
                }
            }
        }

        LOG.debug("Imported files:" + importedFiles);
        LOG.debug("Exported files:" + exportedFiles);

        // write deploy.xml
        SimpleFileExporter.LOG.debug("Starting marshalling");
        final Deploy deployment = buildPlan.getDeploymentDeskriptor();

        // rewrite service names in deploy.xml and potential wsdl files
        try {
            this.rewriteServiceNames(deployment, exportedFiles, buildPlan.getCsarName());
        }
        catch (final WSDLException e) {
            LOG.warn("Rewriting of Service names failed", e);
        }
        catch (final FileNotFoundException e) {
            LOG.warn("Something went wrong with locating wsdl files that needed to be changed", e);
        }
        catch(Exception e) {
        	LOG.warn("Unable to rewrite service names", e);
        }

        final File deployXmlFile = new File(tempFolder, "deploy.xml");
        deployXmlFile.createNewFile();
        final JAXBContext jaxbContext = JAXBContext.newInstance(Deploy.class);
        final Marshaller m = jaxbContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // output to console uncomment this: m.marshal(deployment, System.out);
        m.marshal(deployment, deployXmlFile);

        // save wsdl in tempfolder
        final File wsdlFile = new File(tempFolder, wsdl.getFileName());
        FileUtils.writeStringToFile(wsdlFile, wsdl.getFinalizedWsdlAsString());

        // save bpel file in tempfolder
        final File bpelFile = new File(tempFolder, wsdl.getFileName().replace(".wsdl", ".bpel"));
        try {
            this.writeBPELDocToFile(bpelFile, buildPlan.getBpelDocument());
        }
        catch (final TransformerException e) {
            SimpleFileExporter.LOG.error("Error while writing BPEL Document to a file", e);
            return false;
        }

        // package temp dir and move to destination URI
        final ServiceReference<?> servRef = FrameworkUtil.getBundle(this.getClass()).getBundleContext()
                                                         .getServiceReference(IFileAccessService.class.getName());
        final IFileAccessService service =
            (IFileAccessService) FrameworkUtil.getBundle(this.getClass()).getBundleContext().getService(servRef);
        service.zip(tempFolder, new File(destination));
        return true;
    }

    private class Mapping {
        private final QName key;
        private final QName val;


        protected Mapping(final QName key, final QName val) {
            this.key = key;
            this.val = val;
        }

        @Override
        public boolean equals(final Object obj) {

            if (obj instanceof Mapping) {
                final Mapping map = (Mapping) obj;
                return map.key.equals(this.key) && map.val.equals(this.val);
            }

            return super.equals(obj);
        }

        @Override
        public String toString() {
            return this.key.toString() + this.val.toString();
        }
    }

    private void rewriteServiceNames(final Deploy deploy, final List<File> referencedFiles,
                                     final String csarName) throws WSDLException, FileNotFoundException {
        final WSDLFactory factory = WSDLFactory.newInstance();
        final WSDLReader reader = factory.newWSDLReader();
        final WSDLWriter writer = factory.newWSDLWriter();

        // first fetch all provide and invoke element which aren't using the
        // 'client' partnerLink
        // single process only
        final List<TInvoke> invokes = deploy.getDeploymentProcess().getInvoke();
        final List<TProvide> provides = deploy.getDeploymentProcess().getProvide();

        // the services and their new name the dd uses, excluding the client
        // services, will be added here
        final Set<Mapping> invokedServicesToRewrite = new HashSet<>();
        final Set<Mapping> providedServicesToRewrite = new HashSet<>();

        LOG.debug("Starting to determine services to rewrite");
        LOG.debug("Starting to determine invoked services");
        for (final TInvoke invoke : invokes) {
            if (invoke.getPartnerLink().equals("client")) {
                continue;
            }

            final TService service = invoke.getService();
            final QName serviceName = service.getName();

            final QName renamedServiceName = new QName(serviceName.getNamespaceURI(),
                csarName + serviceName.getLocalPart() + System.currentTimeMillis());

            LOG.debug("Adding " + serviceName + " to be rewrited to " + renamedServiceName);
            invokedServicesToRewrite.add(new Mapping(serviceName, renamedServiceName));

            service.setName(renamedServiceName);

        }

        LOG.debug("Starting to determine provided services");
        for (final TProvide provide : provides) {
            if (provide.getPartnerLink().equals("client")) {
                continue;
            }

            final TService service = provide.getService();
            final QName serviceName = service.getName();

            final QName renamedServiceName = new QName(serviceName.getNamespaceURI(),
                csarName + serviceName.getLocalPart() + System.currentTimeMillis());

            LOG.debug("Adding " + serviceName + " to be rewrited to " + renamedServiceName);
            providedServicesToRewrite.add(new Mapping(serviceName, renamedServiceName));

            service.setName(renamedServiceName);
        }

        this.rewriteServices(invokedServicesToRewrite, writer, reader, referencedFiles);
        this.rewriteServices(providedServicesToRewrite, writer, reader, referencedFiles);

    }

    private void rewriteServices(final Set<Mapping> servicesToRewrite, final WSDLWriter writer, final WSDLReader reader,
                                 final List<File> referencedFiles) throws WSDLException, FileNotFoundException {

        LOG.debug("Rewriting service names:");
        LOG.debug("Files referenced:" + referencedFiles);
        LOG.debug("Services to rewrite:" + servicesToRewrite);

        for (final Mapping service : servicesToRewrite) {
            final QName serviceName = service.key;
            for (final File file : referencedFiles) {
                if (!file.getAbsolutePath().endsWith(".wsdl")) {
                    continue;
                }

                final Definition def = reader.readWSDL(file.getAbsolutePath());

                final List<QName> servicesToRemove = new ArrayList<>();

                boolean foundMatch = false;
                // fetch defined services
                for (final Object obj : def.getAllServices().values()) {
                    final Service serviceA = (Service) obj;

                    if (serviceName.equals(serviceA.getQName())) {
                        // found wsdl with service we have to rewrite
                        servicesToRemove.add(serviceA.getQName());

                        final Service newService = new ServiceImpl();

                        for (final Object o : serviceA.getPorts().values()) {
                            final Port port = (Port) o;
                            newService.addPort(port);
                        }

                        newService.setQName(service.val);

                        def.addService(newService);
                        foundMatch = true;
                    }
                }

                if (foundMatch) {
                    for (final QName serviceToRemove : servicesToRemove) {
                        def.removeService(serviceToRemove);

                    }

                    writer.writeWSDL(def, new FileOutputStream(file));
                    break;
                }
            }
        }
    }

    /**
     * Returns the FileAccessService of the OpenTOSCA Core
     *
     * @return the IFileAccessService of the OpenTOSCA Core
     */
    private IFileAccessService getFileAccessService() {
        final BundleContext ctx = FrameworkUtil.getBundle(Exporter.class).getBundleContext();
        final ServiceReference serviceReference = ctx.getServiceReference(IFileAccessService.class.getName());
        final IFileAccessService service = (IFileAccessService) ctx.getService(serviceReference);
        return service;
    }

    /**
     * Writes the given DOM Document to the location denoted by the given File
     *
     * @param destination a File denoting the location to export to
     * @param doc the Document to export
     * @throws TransformerException is thrown when initializing a TransformerFactory or writing the
     *         Document fails
     * @throws FileNotFoundException is thrown when the File denoted by the File Object doesn't exist
     */
    private void writeBPELDocToFile(final File destination, final Document doc) throws TransformerException,
                                                                                FileNotFoundException {
        final TransformerFactory tFactory = TransformerFactory.newInstance();
        final Transformer transformer = tFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        final DOMSource source = new DOMSource(doc);
        final StreamResult result = new StreamResult(new FileOutputStream(destination));
        transformer.transform(source, result);
    }
}
