package org.opentosca.deployment.verification.job;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Set;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.container.core.next.xml.DomUtil;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class MysqlConnectJob implements NodeTemplateJob {

  private static Logger logger = LoggerFactory.getLogger(MysqlConnectJob.class);

  public MysqlConnectJob() {
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } catch (Exception e) {
      logger.error("MySQL Driver not found", e);
    }
  }

  @Override
  public synchronized VerificationResult execute(VerificationContext context,
      AbstractNodeTemplate nodeTemplate, NodeTemplateInstance nodeTemplateInstance) {

    final VerificationResult result = new VerificationResult();
    result.setName(MysqlConnectJob.class.getSimpleName());
    result.setNodeTemplateInstance(nodeTemplateInstance);
    result.start();

    final Set<NodeTemplateInstance> stackNodes = Sets.newHashSet(nodeTemplateInstance);
    // Resolve all instances underneath the current instance
    Jobs.resolveInfrastructureNodes(nodeTemplateInstance, context, stackNodes);

    final Map<String, String> properties = Jobs.mergePlanProperties(stackNodes);

    final String hostname = Jobs.resolveHostname(properties);
    if (Strings.isNullOrEmpty(hostname)) {
      result.append("Could not determine appropriate hostname.");
      result.failed();
      return result;
    }
    logger.info("Determined hostname={}", hostname);

    String dbname = properties.get("dbname");
    String dbuser = properties.get("dbuser");
    String dbpassword = properties.get("dbpassword");
    if (Strings.isNullOrEmpty(dbname)) {
      result.append("Could not determine appropriate database name.");
      result.failed();
      return result;
    }
    if (Strings.isNullOrEmpty(dbuser)) {
      result.append("Could not determine appropriate database user.");
      result.failed();
      return result;
    }
    if (Strings.isNullOrEmpty(dbpassword)) {
      result.append("Could not determine appropriate database password.");
      result.failed();
      return result;
    }
    logger.info("Database name={}; user={}; password={}", dbname, dbuser, dbpassword);

    Connection conn = null;
    try {
      final String url = String.format("jdbc:mysql://%s:3306/%s?user=%s&password=%s", hostname,
          dbname, dbuser, dbpassword);
      conn = DriverManager.getConnection(url);
      result.append(String.format("Successfully connected to database \"%s\".", dbname));
      result.success();
    } catch (Exception e) {
      logger.error("Could not connect to database", e);
      result
          .append(String.format("Could not connect to database \"%s\": " + e.getMessage(), dbname));
      result.failed();
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (Throwable e) {
        logger.warn("Could not close JDBC Connection", e);
      }
    }

    return result;
  }

  @Override
  public synchronized boolean canExecute(AbstractNodeTemplate nodeTemplate) {
    if (nodeTemplate.getProperties() != null
        && nodeTemplate.getProperties().getDOMElement() != null) {

      final Element el = nodeTemplate.getProperties().getDOMElement();
      final NodeList nodes = el.getChildNodes();

      /*
       * If a node template contains properties to specify database connection parameters we derive
       * that we can try connect to it via a MySQL driver.
       */

      if (DomUtil.matchesNodeName(".*dbname.*", nodes)
          && DomUtil.matchesNodeName(".*dbuser.*", nodes)
          && DomUtil.matchesNodeName(".*dbpassword.*", nodes)) {
        return true;
      }
    }
    return false;
  }
}
