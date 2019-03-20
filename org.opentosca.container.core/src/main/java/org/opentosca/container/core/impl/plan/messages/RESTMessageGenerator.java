package org.opentosca.container.core.impl.plan.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.impl.plan.ServiceProxy;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class RESTMessageGenerator {

  private final Logger LOG = LoggerFactory.getLogger(RESTMessageGenerator.class);

  public Map<String, String> createRequest(final CSARID csarID, final QName planInputMessageID,
                                           final List<TParameterDTO> inputParameter, final String correlationID) {

    final Map<String, String> map = new HashMap<>();
    final List<Document> docs = new ArrayList<>();

    final List<QName> serviceTemplates = ServiceProxy.toscaEngineService.getServiceTemplatesInCSAR(csarID);
    for (final QName serviceTemplate : serviceTemplates) {
      final List<String> nodeTemplates =
        ServiceProxy.toscaEngineService.getNodeTemplatesOfServiceTemplate(csarID, serviceTemplate);

      for (final String nodeTemplate : nodeTemplates) {
        final Document doc =
          ServiceProxy.toscaEngineService.getPropertiesOfTemplate(csarID, serviceTemplate, nodeTemplate);
        if (null != doc) {
          docs.add(doc);
          this.LOG.trace("Found property document: {}",
            ServiceProxy.xmlSerializerService.getXmlSerializer().docToString(doc, false));
        }
      }
    }

    this.LOG.trace("Processing a list of {} parameters", inputParameter.size());
    for (final TParameterDTO para : inputParameter) {
      this.LOG.trace("Put in the parameter {} with value \"{}\".", para.getName(), para.getValue());

      if (para.getType().equalsIgnoreCase("correlation")) {
        this.LOG.debug("Found Correlation Element! Put in CorrelationID \"" + correlationID + "\".");
        map.put(para.getName(), correlationID);
      } else if (para.getName().equalsIgnoreCase("csarName")) {
        this.LOG.debug("Found csarName Element! Put in csarName \"" + csarID + "\".");
        map.put(para.getName(), csarID.toString());
      } else if (para.getName().equalsIgnoreCase("containerApiAddress")) {
        this.LOG.debug("Found containerApiAddress Element! Put in containerApiAddress \""
          + Settings.CONTAINER_API_LEGACY + "\".");
        map.put(para.getName(), Settings.CONTAINER_API_LEGACY);
      } else if (para.getName().equalsIgnoreCase("instanceDataAPIUrl")) {
        this.LOG.debug("Found instanceDataAPIUrl Element! Put in instanceDataAPIUrl \""
          + Settings.CONTAINER_INSTANCEDATA_LEGACY_API + "\".");
        map.put(para.getName(), Settings.CONTAINER_INSTANCEDATA_LEGACY_API);
      } else {
        if (para.getName() == null || para.getValue().equals("")) {
          this.LOG.debug("The parameter \"" + para.getName()
            + "\" has an empty value, thus search in the properties.");
          String value = "";
          for (final Document doc : docs) {
            final NodeList nodes = doc.getElementsByTagNameNS("*", para.getName());
            this.LOG.trace("Found {} nodes.", nodes.getLength());
            if (nodes.getLength() > 0) {
              value = nodes.item(0).getTextContent();
              this.LOG.debug("Found value {}", value);
              break;
            }
          }
          if (value.equals("")) {
            this.LOG.debug("No value found.");
          }
          map.put(para.getName(), value);
        } else {
          this.LOG.debug("Found element \"" + para.getName() + "\"! Put in \"" + para.getValue() + "\".");
          map.put(para.getName(), para.getValue());
        }
      }
    }

    return map;
  }

}
