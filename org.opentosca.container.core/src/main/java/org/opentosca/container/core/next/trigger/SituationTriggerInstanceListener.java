package org.opentosca.container.core.next.trigger;

//import java.io.UnsupportedEncodingException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//import javax.inject.Inject;
import javax.inject.Inject;
import javax.persistence.PostPersist;
import javax.xml.namespace.QName;
//import javax.xml.namespace.QName;

import org.glassfish.jersey.uri.UriComponent;
import org.opentosca.container.core.common.Settings;
//import org.opentosca.container.core.engine.IToscaEngineService;
//import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.SituationTriggerInstance;
//import org.opentosca.container.core.next.model.SituationTriggerInstanceProperty;
import org.opentosca.container.core.next.model.SituationTriggerInstanceProperty;
import org.opentosca.container.core.next.model.SituationTriggerProperty;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.SituationTriggerInstanceRepository;
//import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
//import org.eclipse.winery.model.tosca.TPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SituationTriggerInstanceListener {

  private static final List<SituationTriggerInstanceObserver> obs = new ArrayList<>();

  @PostPersist
  public void startSituationTriggerInstanceObserver(final SituationTriggerInstance instance) {
    final SituationTriggerInstanceObserver obs = new SituationTriggerInstanceObserver(instance);
    SituationTriggerInstanceListener.obs.add(obs);
    new Thread(obs).start();
  }

  private class SituationTriggerInstanceObserver implements Runnable {

    final private Logger LOG = LoggerFactory.getLogger(SituationTriggerInstanceObserver.class);

    private final SituationTriggerInstanceRepository repo = new SituationTriggerInstanceRepository();

    // FIXME we can't inject into the JPA-Managed listener, so we need to perform some kind of service lookup here
    @Inject
    private final IPlanInvocationEngine planInvocEngine;

    private final PlanInstanceRepository planRepository = new PlanInstanceRepository();

    private final SituationTriggerInstance instance;

    public SituationTriggerInstanceObserver(final SituationTriggerInstance instance) {
      this.instance = instance;
      // FIXME: deal with this mess
      this.planInvocEngine = null;
    }

    @Override
    public void run() {

      this.instance.setStarted(true);
      this.repo.update(this.instance);

      this.LOG.debug("Started SituationTriggerInstance " + this.instance.getId());

      final String interfaceName = this.instance.getSituationTrigger().getInterfaceName();
      final String operationName = this.instance.getSituationTrigger().getOperationName();
      final Set<SituationTriggerProperty> inputs = this.instance.getSituationTrigger().getInputs();

      final ServiceTemplateInstance servInstance = this.instance.getSituationTrigger().getServiceInstance();
      final NodeTemplateInstance nodeInstance = this.instance.getSituationTrigger().getNodeInstance();

      if (nodeInstance == null) {
        // plan invocation
        // FIXME
        //        final QName planId = this.toscaEngine.getToscaReferenceMapper()
        //          .getBoundaryPlanOfCSARInterface(servInstance.getCsarId().toOldCsarId(),
        //            interfaceName, operationName);
        //        final TPlan plan = this.toscaEngine.getToscaReferenceMapper()
        //          .getPlanForCSARIDAndPlanID(servInstance.getCsarId().toOldCsarId(), planId);

        // FIXME reinstate actual plan invocation by getting managed planEngine instance
        final TPlanDTO planDTO = new TPlanDTO();// new TPlanDTO(plan, planId.getNamespaceURI());

        for (final TParameterDTO param : planDTO.getInputParameters().getInputParameter()) {
          if (param.getName().equals("OpenTOSCAContainerAPIServiceInstanceURL")) {
            String url = Settings.CONTAINER_INSTANCEDATA_API + "/" + servInstance.getId();
            url = url.replace("{csarid}", servInstance.getCsarId().toOldCsarId().getFileName());
            url = url.replace("{servicetemplateid}",
              UriComponent.encode(servInstance.getTemplateId().toString(),
                UriComponent.Type.PATH_SEGMENT));

            final URI uri = URI.create(UriComponent.encode(url, UriComponent.Type.PATH));
            param.setValue(uri.toString());
          }

          if (param.getValue() == null) {
            for (final SituationTriggerProperty val : inputs) {
              if (param.getName().equals(val.getName())) {
                param.setValue(val.getValue());
              }
            }

          }
        }

        try {
            final String correlationId = planInvocEngine.createCorrelationId();
            // FIXME QName natural key migration to string leftover
            planInvocEngine.invokePlan(servInstance.getCsarId(), QName.valueOf(servInstance.getTemplateId()),
              servInstance.getId(), planDTO, correlationId);

            // now wait for finished execution
          PlanInstance planInstance = planRepository.findByCorrelationId(correlationId);
          while (!(planInstance.getState() == PlanInstanceState.FINISHED)
           || planInstance.getState() == PlanInstanceState.FAILED) {
            Thread.sleep(10000);
            planInstance = planRepository.findByCorrelationId(correlationId);
          }

          // plan finished, write output to trigger instance
          planInstance.getOutputs()
            .forEach(x -> instance.getOutputs()
              .add(new SituationTriggerInstanceProperty(x.getName(), x.getValue(), x.getType())));

          instance.setFinished(true);
          repo.update(instance);
        } catch (final InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
}
