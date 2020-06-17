# Container Configuration
# Your external IP adress, e.g. 129.69.214.56
org.opentosca.container.hostname={{ .Env.CONTAINER_HOSTNAME }}
org.opentosca.container.port={{ .Env.CONTAINER_PORT }}

# IA Engine Configuration (endpoint and credentials)
org.opentosca.container.engine.ia.hostname={{ .Env.ENGINE_IA_HOSTNAME }}
org.opentosca.container.engine.ia.port={{ .Env.ENGINE_IA_PORT }}
org.opentosca.container.engine.ia.plugin.tomcat.url=http://{{ .Env.ENGINE_IA_HOSTNAME }}:{{ .Env.ENGINE_IA_PORT }}
org.opentosca.container.engine.ia.plugin.tomcat.password={{ .Env.ENGINE_IA_PASSWORD }}
org.opentosca.container.engine.ia.plugin.tomcat.username={{ .Env.ENGINE_IA_USER }}

# BPEL Plan Engine Configuration (endpoint and credentials)
org.opentosca.container.engine.plan.plugin.bpel.engine={{ .Env.ENGINE_PLAN_BPEL }}
org.opentosca.container.engine.plan.plugin.bpel.url=http://{{ .Env.ENGINE_PLAN_BPEL_HOSTNAME }}:{{ .Env.ENGINE_PLAN_BPEL_PORT }}/{{ .Env.ENGINE_PLAN_BPEL_CONTEXT }}
org.opentosca.container.engine.plan.plugin.bpel.password={{ .Env.ENGINE_PLAN_BPEL_PASSWORD }}
org.opentosca.container.engine.plan.plugin.bpel.username={{ .Env.ENGINE_PLAN_BPEL_USER }}
org.opentosca.container.engine.plan.plugin.bpel.services.url=http://{{ .Env.ENGINE_PLAN_BPEL_HOSTNAME }}:{{ .Env.ENGINE_PLAN_BPEL_PORT }}/{{ .Env.ENGINE_PLAN_BPEL_CONTEXT }}/{{ .Env.ENGINE_PLAN_BPEL_SERVICES_PATH }}

# BPMN Plan Engine Configuration (endpoint and credentials)
org.opentosca.container.engine.plan.plugin.bpmn.engine={{ .Env.ENGINE_PLAN_BPMN }}
org.opentosca.container.engine.plan.plugin.bpmn.url=http://{{ .Env.ENGINE_PLAN_BPMN_HOSTNAME }}:{{ .Env.ENGINE_PLAN_BPMN_PORT }}/{{ .Env.ENGINE_PLAN_BPMN_CONTEXT }}
org.opentosca.container.engine.plan.plugin.bpmn.username={{ .Env.ENGINE_PLAN_BPMN_USER }}
org.opentosca.container.engine.plan.plugin.bpmn.password={{ .Env.ENGINE_PLAN_BPMN_PASSWORD }}

# Container Mode Repository (winery)
org.opentosca.container.connector.winery.url=http://{{ .Env.CONTAINER_REPOSITORY_HOSTNAME }}:{{ .Env.CONTAINER_REPOSITORY_PORT }}/{{ .Env.CONTAINER_REPOSITORY_CONTEXT }}

# Local MQTT broker
org.opentosca.container.broker.mqtt.port={{ .Env.MQTT_BROKER_PORT }}
org.opentosca.container.broker.mqtt.username={{ .Env.MQTT_BROKER_USER }}
org.opentosca.container.broker.mqtt.password={{ .Env.MQTT_BROKER_PASSWORD }}

# Distributed IA deployment
org.opentosca.container.collaboration.mode={{ .Env.COLLABORATION_MODE }}
org.opentosca.container.collaboration.hostnames={{ .Env.COLLABORATION_HOSTNAMES }}
org.opentosca.container.collaboration.ports={{ .Env.COLLABORATION_PORTS }}

# Testing
org.opentosca.deployment.tests={{ .Env.CONTAINER_DEPLOYMENT_TESTS }}
org.opentosca.bus.management.mocking={{ .Env.CONTAINER_BUS_MANAGEMENT_MOCK }}
org.opentosca.engine.ia.keepfiles={{ .Env.ENGINE_IA_KEEP_FILES }}