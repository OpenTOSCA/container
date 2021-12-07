package org.opentosca.planbuilder.core.bpmn.typebasedplanbuilder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.AbstractBuildPlanBuilder;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNPlanHandler;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This Class represents the high-level algorithm of the concept in <a href= "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL 2.0 BuildPlans fuer OpenTOSCA</a>. It
 * is responsible for generating the Build Plan Skeleton and assign plugins to handle the different templates inside a
 * TopologyTemplate.
 * </p>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPMNBuildProcessBuilder extends AbstractBuildPlanBuilder {

    final static Logger LOG = LoggerFactory.getLogger(BPMNBuildProcessBuilder.class);

    private BPMNPlanHandler planHandler;


    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public BPMNBuildProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        try {
            this.planHandler = new BPMNPlanHandler();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<AbstractPlan> buildPlans(Csar csar, TDefinitions definitions) {
        final List<AbstractPlan> plans = new ArrayList<>();
        LOG.info(""+definitions);
        for (final TServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            LOG.info("Erstellt der hier was?");
            final BPMNPlan newBuildPlan = buildPlan(csar, definitions, serviceTemplate);
            plans.add(newBuildPlan);

            return plans;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlan(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.TDefinitions, javax.xml.namespace.QName)
     */
    private BPMNPlan buildPlan(final Csar csar, final TDefinitions definitions,
                               final TServiceTemplate serviceTemplate) {
        LOG.info("Geht das hier?");
        // create empty plan from servicetemplate and add definitions
        String namespace;
        if (serviceTemplate.getTargetNamespace() != null) {
            namespace = serviceTemplate.getTargetNamespace();
        } else {
            namespace = definitions.getTargetNamespace();
        }

        QName serviceTemplateQname = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
        if (namespace.equals(serviceTemplateQname.getNamespaceURI())
            && serviceTemplate.getId().equals(serviceTemplateQname.getLocalPart())) {

            final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_bpmn_buildPlan");
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_bpmn_buildPlan";

            AbstractPlan buildPlan =
                AbstractBuildPlanBuilder.generatePOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate, csar);

            LOG.debug("Generated the following abstract prov plan: ");
            LOG.debug(buildPlan.toString());

            final BPMNPlan newBuildPlan =
                this.planHandler.createEmptyBPMNPlan(processNamespace, processName, buildPlan, "initiate");

            newBuildPlan.setTOSCAInterfaceName("OpenTOSCA-Lifecycle-Interface");
            newBuildPlan.setTOSCAOperationname("initiate");

            //this.planHandler.initializeBPMNSkeleton(newBuildPlan, csar);
            // newBuildPlan.setCsarName(csarName);

            return newBuildPlan;
        }

        LOG.warn("Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
            serviceTemplateQname.toString(), definitions.getId(), csar.id().csarName());
        return null;
    }

}
