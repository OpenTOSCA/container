/**
 *
 */
package org.opentosca.planbuilder.postphase.plugin.vinothek.bpel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.postphase.plugin.vinothek.bpel.handler.BPELVinothekPluginHandler;
import org.opentosca.planbuilder.postphase.plugin.vinothek.core.VinothekPlugin;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - nyuuyn@googlemail.com
 *
 */
public class BPELVinothekPlugin extends VinothekPlugin<BPELPlanContext> {

    private static final String pluginId = "OpenTOSCA PlanBuilder PostPhase Plugin Vinothek";
    public static final QName phpApp = new QName("http://opentosca.org/types/declarative", "PhpApplication");
    public static final QName bpelProcess = new QName("http://opentosca.org/declarative/", "BPEL");
    private final QName zipArtifactType =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ArchiveArtifact");

    private BPELVinothekPluginHandler handler;

    public BPELVinothekPlugin() {
        try {
            this.handler = new BPELVinothekPluginHandler();
        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private List<AbstractImplementationArtifact> getIAsForInterfaces(final List<AbstractImplementationArtifact> ias) {
        final List<AbstractImplementationArtifact> iasForIfaces = new ArrayList<>();
        for (final AbstractImplementationArtifact ia : ias) {
            if (ia.getOperationName() == null || ia.getOperationName().equals("")) {
                iasForIfaces.add(ia);
            }
        }
        return iasForIfaces;
    }

    private List<AbstractImplementationArtifact> getIAsForLifecycleInterface(final List<AbstractImplementationArtifact> ias) {
        final List<AbstractImplementationArtifact> iasForIfaces = new ArrayList<>();
        for (final AbstractImplementationArtifact ia : ias) {
            if (ia.getInterfaceName().equals("http://docs.oasis-open.org/tosca/ns/2011/12/interfaces/lifecycle")) {
                iasForIfaces.add(ia);
            }
        }
        return iasForIfaces;
    }

    private List<AbstractImplementationArtifact> getIAsForOperations(final List<AbstractImplementationArtifact> ias) {
        final List<AbstractImplementationArtifact> iasForIfaces = new ArrayList<>();
        for (final AbstractImplementationArtifact ia : ias) {
            if (ia.getOperationName() != null && !ia.getOperationName().equals("")) {
                iasForIfaces.add(ia);
            }
        }
        return iasForIfaces;
    }

    @Override
    public String getID() {
        return BPELVinothekPlugin.pluginId;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
        // check if the node is really a phpApp
        if (this.canHandleCreate(nodeTemplate)) {
            final AbstractNodeTypeImplementation nodeImpl = this.selectNodeTypeImplementation(context);
            return this.handler.handle(context, nodeTemplate, nodeImpl);
        } else {
            return false;
        }
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context,
                                final AbstractRelationshipTemplate relationshipTemplate) {
        // only handling nodeTemplates
        return false;
    }

    private boolean isZipArtifact(final AbstractDeploymentArtifact artifact) {
        if (artifact.getArtifactType().equals(this.zipArtifactType)) {
            return true;
        } else {
            return false;
        }
    }

    private AbstractNodeTypeImplementation selectNodeTypeImplementation(final BPELPlanContext templateContext) {
        final AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
        if (nodeTemplate == null) {
            return null;
        }
        final AbstractNodeType nodeType = nodeTemplate.getType();

        AbstractInterface usableIface = null;

        // check whether the nodeType contains any TOSCA interface
        for (final AbstractInterface iface : nodeType.getInterfaces()) {
            if (iface.getName().equals("http://docs.oasis-open.org/tosca/ns/2011/12/interfaces/lifecycle")) {
                // check if we have operations to work with, e.g. install,
                // configure and start
                int toscaOperations = 0;
                for (final AbstractOperation operation : iface.getOperations()) {
                    switch (operation.getName()) {
                        case "install":
                        case "start":
                        case "configure":
                            toscaOperations++;
                            break;
                        default:
                            break;
                    }
                }
                if (toscaOperations != iface.getOperations().size()) {
                    // we just accept pure TOSCA interfaces
                    continue;
                } else {
                    usableIface = iface;
                }
            }
        }

        for (final AbstractNodeTypeImplementation nodeImpl : nodeTemplate.getImplementations()) {
            // check whether all deploymentartifacts are ZipArtifacts
            int zipArtifactCount = 0;
            for (final AbstractDeploymentArtifact deplArtifact : nodeImpl.getDeploymentArtifacts()) {
                if (this.isZipArtifact(deplArtifact)) {
                    zipArtifactCount++;
                }
            }

            if (nodeImpl.getDeploymentArtifacts().size() != zipArtifactCount) {
                // this implementation doesn't suit this plugin, skip it
                continue;
            }

            // check the IA's with the found interfaces, and if we found an IA
            // we
            // can use for one of the interfaces we'll use that
            final List<AbstractImplementationArtifact> iasForInterfaces =
                this.getIAsForLifecycleInterface(this.getIAsForInterfaces(nodeImpl.getImplementationArtifacts()));
            final List<AbstractImplementationArtifact> iasForOperations =
                this.getIAsForLifecycleInterface(this.getIAsForOperations(nodeImpl.getImplementationArtifacts()));

            // first check if we have an IA for a whole interface
            if (iasForInterfaces.size() == 1) {
                // found an implementation for the lifecycle interface ->
                // nodeTypeImpl will suffice
                return nodeImpl;
            }

            if (usableIface != null) {
                // check if operations in the interface are implementated by
                // single
                // ia's
                if (usableIface.getOperations().size() == iasForOperations.size()) {
                    // TODO pretty vague check but should suffice
                    return nodeImpl;
                }
            } else {
                // if the node doesn't have an interface basically no extra
                // operations will be executed, just upload of zip da's into the
                // right spots
                return nodeImpl;
            }

        }
        return null;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext context, AbstractNodeTemplate nodeTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext context, AbstractRelationshipTemplate relationshipTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canHandleTerminate(AbstractNodeTemplate nodeTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canHandleTerminate(AbstractRelationshipTemplate relationshipTemplate) {
        // TODO Auto-generated method stub
        return false;
    }

}
