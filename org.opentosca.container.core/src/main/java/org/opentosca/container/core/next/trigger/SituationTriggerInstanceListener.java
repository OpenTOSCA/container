package org.opentosca.container.core.next.trigger;

//import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//import javax.inject.Inject;
import javax.persistence.PostPersist;
//import javax.xml.namespace.QName;

import org.glassfish.jersey.uri.UriComponent;
import org.opentosca.container.core.common.Settings;
//import org.opentosca.container.core.engine.IToscaEngineService;
//import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.SituationTriggerInstance;
//import org.opentosca.container.core.next.model.SituationTriggerInstanceProperty;
import org.opentosca.container.core.next.model.SituationTriggerProperty;
import org.opentosca.container.core.next.repository.SituationTriggerInstanceRepository;
//import org.opentosca.container.core.service.IPlanInvocationEngine;
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
    ;
  }

  private class SituationTriggerInstanceObserver implements Runnable {

    final private Logger LOG = LoggerFactory.getLogger(SituationTriggerInstanceObserver.class);

    private final SituationTriggerInstanceRepository repo = new SituationTriggerInstanceRepository();

//    @Inject
//    private final IPlanInvocationEngine planInvocEngine;

//    @Inject
//    private final IToscaEngineService toscaEngine;

    private final SituationTriggerInstance instance;

    public SituationTriggerInstanceObserver(final SituationTriggerInstance instance) {
      this.instance = instance;
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

        // FIXME
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

//        try {
//          final String correlationId =
//            this.planInvocEngine.invokePlan(servInstance.getCsarId().toOldCsarId(), servInstance.getTemplateId(),
//              servInstance.getId(), planDTO);

          // now wait for finished execution
//          final ServiceTemplateInstanceID servInstanceId = new ServiceTemplateInstanceID(
//            servInstance.getCsarId().toOldCsarId(), servInstance.getTemplateId(), servInstance.getId().intValue());

//          TPlanDTO runningPlan =
//            this.planInvocEngine.getActivePublicPlanOfInstance(servInstanceId, correlationId);

//          while (!isPlanExecutionFinished(runningPlan, correlationId)) {
//            this.wait(10000);
//            runningPlan = this.planInvocEngine.getActivePublicPlanOfInstance(servInstanceId, correlationId);
//          }

          // plan finished, write output to triggerinstance
//          runningPlan.getOutputParameters().getOutputParameter()
//            .forEach(x -> this.instance.getOutputs().add(new SituationTriggerInstanceProperty(
//              x.getName(), x.getValue(), x.getType())));
//
//          this.instance.setFinished(true);
//          this.repo.update(this.instance);
//        } catch (final UnsupportedEncodingException | InterruptedException e) {
//          throw new RuntimeException(e);
//        }
      }
    }

    private boolean isPlanExecutionFinished(final TPlanDTO plan, final String correlationId) {
      return plan.getOutputParameters().getOutputParameter().stream()
        .anyMatch(param -> param.getName().equalsIgnoreCase("correlationid") && param.getValue().equals(correlationId));
    }
  }
}
