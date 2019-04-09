package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.AbstractTransformingPlanbuilder;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

public class BPELTransformationProcessBuilder extends AbstractTransformingPlanbuilder {

	@Override
	public AbstractPlan buildPlan(String sourceCsarName, AbstractDefinitions sourceDefinitions,
			QName sourceServiceTemplateId, String targetCsarName, AbstractDefinitions targetDefinitions,
			QName targetServiceTemplateId) {

		AbstractServiceTemplate sourceServiceTemplate = null;
		AbstractServiceTemplate targetServiceTemplate = null;
		sourceServiceTemplate = this.getServiceTemplate(sourceDefinitions, sourceServiceTemplateId);
		targetServiceTemplate = this.getServiceTemplate(targetDefinitions, targetServiceTemplateId);

		this.generateTFOG(sourceCsarName, sourceDefinitions, sourceServiceTemplate, targetCsarName, targetDefinitions,
				targetServiceTemplate);

		return null;
	}

	private AbstractServiceTemplate getServiceTemplate(AbstractDefinitions defs, QName serviceTemplateId) {
		for (AbstractServiceTemplate servTemplate : defs.getServiceTemplates()) {
			if (servTemplate.getQName().equals(serviceTemplateId)) {
				return servTemplate;
			}
		}
		return null;
	}

	@Override
	public List<AbstractPlan> buildPlans(String sourceCsarName, AbstractDefinitions sourceDefinitions,
			String targetCsarName, AbstractDefinitions targetDefinitions) {
		// TODO Auto-generated method stub
		return null;
	}

}
