package org.opentosca.planbuilder.postphase.plugin.vinothek.bpel.handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.convention.Properties;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.csarhandler.CSARHandler;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.postphase.plugin.vinothek.bpel.BPELVinothekPlugin;
import org.opentosca.planbuilder.postphase.plugin.vinothek.core.handler.VinothekPluginHandler;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - nyuuyn@googlemail.com
 *
 */
public class BPELVinothekPluginHandler implements VinothekPluginHandler<BPELPlanContext> {

    private final QName zipArtifactType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes",
        "ArchiveArtifact");
    private final QName bpelArtifactType = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable",
        "BPEL");

    private final CSARHandler csarHandler = new CSARHandler();
    private final DocumentBuilderFactory docFactory;
    private final DocumentBuilder docBuilder;

    public BPELVinothekPluginHandler() throws ParserConfigurationException {
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docFactory.setNamespaceAware(true);
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    private Node createSelfserviceApplicationUrlAssign(final String serverIpVarName, final String applicationName,
                    final String outputVarName, final String outputVarPartName, final String outputVarPrefix)
        throws IOException, SAXException {
        // <!--{serverIpVarName} {appName} {outputVarName} {outputVarPartName}
        // {outputVarPrefix} -->

        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("assignSelfserviceApplicationUrl.xml");
        final File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
        String template = FileUtils.readFileToString(bpelfragmentfile);
        template = template.replace("{serverIpVarName}", serverIpVarName);
        template = template.replace("{appName}", applicationName);
        template = template.replace("{outputVarName}", outputVarName);
        template = template.replace("{outputVarPartName}", outputVarPartName);
        template = template.replace("{outputVarPrefix}", outputVarPrefix);

        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(template));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    private AbstractArtifactReference fetchBPELAppDA(final List<AbstractDeploymentArtifact> das) {
        for (final AbstractDeploymentArtifact da : das) {
            if (da.getArtifactType().equals(this.bpelArtifactType)) {
                for (final AbstractArtifactReference ref : da.getArtifactRef().getArtifactReferences()) {
                    if (ref.getReference().endsWith(".zip")) {
                        return ref;
                    }
                }
            }
        }

        return null;
    }

    private AbstractArtifactReference fetchPhpAppDA(final List<AbstractDeploymentArtifact> das) {
        for (final AbstractDeploymentArtifact da : das) {
            if (da.getArtifactType().equals(this.zipArtifactType)) {
                for (final AbstractArtifactReference ref : da.getArtifactRef().getArtifactReferences()) {
                    if (ref.getReference().endsWith(".zip")) {
                        return ref;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate,
                    final AbstractNodeTypeImplementation nodeImpl) {
        if (ModelUtils.checkForTypeInHierarchy(nodeTemplate, BPELVinothekPlugin.phpApp)) {
            return this.handlePhpApp(context, nodeTemplate, nodeImpl);
        } else if (ModelUtils.checkForTypeInHierarchy(nodeTemplate, BPELVinothekPlugin.bpelProcess)) {
            return this.handleBPELApp(context, nodeTemplate, nodeImpl);
        }
        return false;
    }

    private boolean handleBPELApp(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate,
                    final AbstractNodeTypeImplementation nodeImpl) {
        // FIXME: this will be working under many assumptions (bpel-engine: wso2
        // bps.., no port reconfigs,..)
        // we try to construct an endpoint of the form
        // ServerIP:9763/services/BPELStack_buildPlanService/

        final List<AbstractDeploymentArtifact> das = new ArrayList<>();

        das.addAll(nodeTemplate.getDeploymentArtifacts());

        if (nodeImpl != null) {
            das.addAll(nodeImpl.getDeploymentArtifacts());
        }

        final AbstractArtifactReference bpelRef = this.fetchBPELAppDA(das);

        if (bpelRef == null) {
            return false;
        }

        CSARContent content;
        try {
            content = this.csarHandler.getCSARContentForID(new CSARID(context.getCSARFileName()));
            final String reference = bpelRef.getReference();
            final Set<AbstractFile> files = content.getFilesRecursively();
            AbstractFile daFile = null;

            for (final AbstractFile file : files) {
                final String path = file.getPath();

                // this decode is used as counter-measure against the double
                // encoding in winery
                if (file.getPath().equals(URLDecoder.decode(reference, "UTF-8"))) {
                    daFile = file;
                }
            }

            if (daFile == null) {
                return false;
            }

            // we'll try to find the root dir, which should resemble the
            // application path on an apache web server
            final Path daPath = daFile.getFile();
            final String absPath = daPath.toAbsolutePath().toString();

            final ZipFile zipFile = new ZipFile(absPath);

            final Enumeration<? extends ZipEntry> entries = zipFile.entries();

            final List<ZipEntry> fileEntries = new ArrayList<>();

            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    fileEntries.add(entry);
                }
            }

            // the entry with the shortest name should be our root dir
            ZipEntry deployXmlEntry = null;

            for (final ZipEntry entry : fileEntries) {

                if (entry.getName().equals("deploy.xml")) {
                    deployXmlEntry = entry;
                }
            }

            final InputStream is = zipFile.getInputStream(deployXmlEntry);

            final XPathFactory factory = XPathFactory.newInstance();

            final XPath xpath = factory.newXPath();

            xpath.setNamespaceContext(new NamespaceContext() {

                private final String ns = "http://www.apache.org/ode/schemas/dd/2007/03";

                @Override
                public String getNamespaceURI(final String prefix) {
                    return this.ns;
                }

                @Override
                public String getPrefix(final String namespaceURI) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public Iterator getPrefixes(final String namespaceURI) {
                    return null;
                }
            });

            final InputSource inputSource = new InputSource(is);

            final String value = (String) xpath.evaluate(
                "/ns:deploy/ns:process/ns:provide[@partnerLink='client']/ns:service/@name", inputSource,
                XPathConstants.STRING);

            final String serviceName = value.split(":")[1];

            // this is really a crude assumption of axis2 AND that the bps port
            // is still set to 9763
            final String applicationFolderName = ":9763/services/" + serviceName;

            zipFile.close();

            String serverIpVarName = null;

            for (final String serverPropName : org.opentosca.container.core.tosca.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {

                // find serverip var name of the VM hosting the application
                serverIpVarName = context.getVariableNameOfInfraNodeProperty(serverPropName);
                if (serverIpVarName != null) {
                    break;
                }
            }

            if (serverIpVarName == null) {
                return false;
            }

            // add selfserviceApplicationUrl to output
            context.addStringValueToPlanResponse("selfserviceApplicationUrl");

            final Element postPhaseElement = context.getPostPhaseElement();

            Node assignNode = this.createSelfserviceApplicationUrlAssign(serverIpVarName, applicationFolderName,
                context.getPlanResponseMessageName(), "payload", "tns");
            assignNode = context.importNode(assignNode);

            postPhaseElement.appendChild(assignNode);

        } catch (final UserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private boolean handlePhpApp(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate,
                    final AbstractNodeTypeImplementation nodeImpl) {

        // fetch the application zip file
        AbstractArtifactReference zipRef = null;

        if (nodeImpl == null) {
            zipRef = this.fetchPhpAppDA(nodeTemplate.getDeploymentArtifacts());
        } else {
            zipRef = this.fetchPhpAppDA(nodeImpl.getDeploymentArtifacts());
        }

        if (zipRef == null) {
            // didn't find appropiate artifact ref
            return false;
        }

        try {

            final CSARContent content = this.csarHandler.getCSARContentForID(new CSARID(context.getCSARFileName()));
            final String reference = zipRef.getReference();
            final Set<AbstractFile> files = content.getFilesRecursively();
            AbstractFile daFile = null;

            for (final AbstractFile file : files) {
                final String path = file.getPath();

                // this decode is used as counter-measure against the double
                // encoding in winery
                if (file.getPath().equals(URLDecoder.decode(reference, "UTF-8"))) {
                    daFile = file;
                }
            }

            if (daFile == null) {
                return false;
            }

            // we'll try to find the root dir, which should resemble the
            // application path on an apache web server
            final Path daPath = daFile.getFile();
            final String absPath = daPath.toAbsolutePath().toString();

            final ZipFile zipFile = new ZipFile(absPath);

            final Enumeration<? extends ZipEntry> entries = zipFile.entries();

            final List<ZipEntry> dirEntries = new ArrayList<>();

            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    dirEntries.add(entry);
                }
            }

            // the entry with the shortest name should be our root dir
            ZipEntry rootDirEntry = dirEntries.get(0);

            for (final ZipEntry entry : dirEntries) {
                if (entry.getName().length() <= rootDirEntry.getName().length()) {
                    rootDirEntry = entry;
                }
            }

            // this value will be concatenated with the form
            // http://VMIP/applicationFolderName
            final String applicationFolderName = "/" + rootDirEntry.getName();
            zipFile.close();

            // find serverip var name of the VM hosting the application
            final String serverIpVarName = context.getVariableNameOfInfraNodeProperty(
                Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);

            // add selfserviceApplicationUrl to output
            context.addStringValueToPlanResponse("selfserviceApplicationUrl");

            final Element postPhaseElement = context.getPostPhaseElement();

            Node assignNode = this.createSelfserviceApplicationUrlAssign(serverIpVarName, applicationFolderName,
                context.getPlanResponseMessageName(), "payload", "tns");
            assignNode = context.importNode(assignNode);

            postPhaseElement.appendChild(assignNode);

        } catch (final UserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (final SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
