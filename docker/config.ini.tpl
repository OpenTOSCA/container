#
# Container Configuration
#
osgi.framework=file\:plugins/org.eclipse.osgi_3.11.3.v20170209-1843.jar
osgi.framework.extensions=reference\:file\:org.eclipse.osgi.compatibility.state_1.0.200.v20160504-1419.jar
osgi.bundles=reference\:file\:javax.persistence_2.1.1.v201509150925.jar@1\:start,reference\:file\:org.eclipse.equinox.simpleconfigurator_1.1.200.v20160504-1450.jar@1\:start
osgi.bundles.defaultStartLevel=4
eclipse.product=org.opentosca.container.application.opentoscacontainer
eclipse.application=org.opentosca.container.application.application
eclipse.p2.profile=DefaultProfile
eclipse.p2.data.area=@config.dir/../p2
equinox.use.ds=true
org.eclipse.equinox.simpleconfigurator.configUrl=file\:org.eclipse.equinox.simpleconfigurator/bundles.info
org.opentosca.container.engine.ia.hostname={{ .Env.ENGINE_IA_HOSTNAME }}
org.opentosca.container.engine.ia.port={{ .Env.ENGINE_IA_PORT }}
org.opentosca.container.engine.ia.plugin.tomcat.url=http\://{{ .Env.ENGINE_IA_HOSTNAME }}\:{{ .Env.ENGINE_IA_PORT }}
org.opentosca.container.engine.ia.plugin.tomcat.username={{ .Env.ENGINE_IA_USER_NAME }}
org.opentosca.container.engine.ia.plugin.tomcat.password={{ .Env.ENGINE_IA_PWD }}
org.opentosca.container.engine.plan.plugin.bpel.engine={{ .Env.ENGINE_PLAN_BPEL }}
org.opentosca.container.engine.plan.plugin.bpel.url={{ .Env.ENGINE_PLAN_BPEL_ROOT_URL }}
org.opentosca.container.engine.plan.plugin.bpel.username={{ .Env.ENGINE_PLAN_BPEL_USER_NAME }}
org.opentosca.container.engine.plan.plugin.bpel.password={{ .Env.ENGINE_PLAN_BPEL_PWD }}
org.opentosca.container.engine.plan.plugin.bpel.services.url={{ .Env.ENGINE_PLAN_BPEL_SERVICES_URL }}
org.opentosca.container.engine.plan.plugin.bpmn.engine={{ .Env.ENGINE_PLAN_BPMN }}
org.opentosca.container.engine.plan.plugin.bpmn.url={{ .Env.ENGINE_PLAN_BPMN_ROOT_URL }}
org.opentosca.container.engine.plan.plugin.bpmn.username={{ .Env.ENGINE_PLAN_BPMN_USER_NAME }}
org.opentosca.container.engine.plan.plugin.bpmn.password={{ .Env.ENGINE_PLAN_BPMN_PWD }}
org.opentosca.container.connector.winery.url=http\://{{ .Env.CONTAINER_REPOSITORY_HOSTNAME }}\:{{ .Env.CONTAINER_REPOSITORY_PORT }}/winery
org.opentosca.container.hostname={{ .Env.PUBLIC_HOSTNAME }}
org.opentosca.container.port=1337
org.opentosca.deployment.tests={{ .Env.CONTAINER_DEPLOYMENT_TESTS }}
org.opentosca.container.broker.mqtt.port=1883
org.opentosca.container.broker.mqtt.username=admin
org.opentosca.container.broker.mqtt.password=admin
org.opentosca.container.collaboration.mode={{ .Env.COLLABORATION_MODE }}
org.opentosca.container.collaboration.hostnames={{ .Env.COLLABORATION_HOSTNAMES }}
org.opentosca.container.collaboration.ports={{ .Env.COLLABORATION_PORTS }}
