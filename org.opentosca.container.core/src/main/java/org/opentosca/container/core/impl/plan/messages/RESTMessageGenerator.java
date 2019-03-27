package org.opentosca.container.core.impl.plan.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class RESTMessageGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(RESTMessageGenerator.class);

  @Inject
  private CsarStorageService storageBridge;

  public Map<String, String> createRequest(final CSARID csarID, final QName planInputMessageID,
                                           final List<TParameterDTO> inputParameter, final String correlationID) {
    final List<TEntityTemplate.Properties> docs = new ArrayList<>();

    Csar csar = storageBridge.findById(new CsarId(csarID));
    List<TServiceTemplate> templates = csar.serviceTemplates();
    for (final TServiceTemplate serviceTemplate : templates) {
      final List<TNodeTemplate> nodeTemplates = serviceTemplate.getTopologyTemplate().getNodeTemplates();
      for (final TNodeTemplate nodeTemplate : nodeTemplates) {
        TEntityTemplate.Properties ntProps = nodeTemplate.getProperties();
        if (ntProps != null) {
          docs.add(ntProps);
        }
      }
    }

    LOG.trace("Processing a list of {} parameters", inputParameter.size());
    final Map<String, String> map = new HashMap<>();
    for (final TParameterDTO para : inputParameter) {
      LOG.trace("Put in the parameter {} with value \"{}\".", para.getName(), para.getValue());

      if (para.getType().equalsIgnoreCase("correlation")) {
        LOG.debug("Found Correlation Element! Put in CorrelationID \"" + correlationID + "\".");
        map.put(para.getName(), correlationID);
      } else if (para.getName().equalsIgnoreCase("csarName")) {
        LOG.debug("Found csarName Element! Put in csarName \"" + csarID + "\".");
        map.put(para.getName(), csarID.toString());
      } else if (para.getName().equalsIgnoreCase("containerApiAddress")) {
        LOG.debug("Found containerApiAddress Element! Put in containerApiAddress \""
          + Settings.CONTAINER_API_LEGACY + "\".");
        map.put(para.getName(), Settings.CONTAINER_API_LEGACY);
      } else if (para.getName().equalsIgnoreCase("instanceDataAPIUrl")) {
        LOG.debug("Found instanceDataAPIUrl Element! Put in instanceDataAPIUrl \""
          + Settings.CONTAINER_INSTANCEDATA_LEGACY_API + "\".");
        map.put(para.getName(), Settings.CONTAINER_INSTANCEDATA_LEGACY_API);
      } else {
        if (para.getName() == null || para.getValue().equals("")) {
          LOG.debug("The parameter \"" + para.getName()
            + "\" has an empty value, thus search in the properties.");
          String value = "";
          for (final TEntityTemplate.Properties props : docs) {
            // downcast SHOULD be safe here
            Document doc = (Document) props.getAny();
            if (doc == null) {
              continue;
            }
            final NodeList nodes = doc.getElementsByTagNameNS("*", para.getName());
            LOG.trace("Found {} nodes.", nodes.getLength());
            if (nodes.getLength() > 0) {
              value = nodes.item(0).getTextContent();
              LOG.debug("Found value {}", value);
              break;
            }
          }
          if (value.equals("")) {
            LOG.debug("No value found.");
          }
          map.put(para.getName(), value);
        } else {
          LOG.debug("Found element \"" + para.getName() + "\"! Put in \"" + para.getValue() + "\".");
          map.put(para.getName(), para.getValue());
        }
      }
    }
    return map;
  }
}
