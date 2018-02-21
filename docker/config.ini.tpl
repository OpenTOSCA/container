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
org.opentosca.container.engine.ia.plugin.tomcat.url=http\://{{ .Env.ENGINE_IA_HOSTNAME }}\:8080
org.opentosca.container.engine.ia.plugin.tomcat.username=admin
org.opentosca.container.engine.ia.plugin.tomcat.password=admin
org.opentosca.container.engine.plan.plugin.bpelwso2.username=admin
org.opentosca.container.engine.plan.plugin.bpelwso2.password=admin
org.opentosca.container.engine.plan.plugin.bpelwso2.url=https\://{{ .Env.ENGINE_PLAN_HOSTNAME }}\:9443
org.opentosca.container.engine.plan.plugin.bpelwso2.services.url=http\://{{ .Env.ENGINE_PLAN_HOSTNAME }}\:9763/services
org.opentosca.container.connector.winery.url=http\://{{ .Env.CONTAINER_REPOSITORY_HOSTNAME }}\:8080/winery
org.opentosca.container.hostname={{ .Env.CONTAINER_HOSTNAME }}
org.opentosca.container.port=1337
org.opentosca.deployment.tests={{ .Env.CONTAINER_DEPLOYMENT_TESTS }}
