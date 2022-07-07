FROM maven:3-jdk-11 as builder

RUN rm /dev/random && ln -s /dev/urandom /dev/random

WORKDIR /tmp/opentosca/container
COPY . /tmp/opentosca/container

RUN mvn package -DskipTests=true -Dmaven.javadoc.skip=true -B \
    && mkdir /tmp/build \
    && unzip /tmp/opentosca/container/org.opentosca.container.war/target/OpenTOSCA-container.war -d /tmp/build/container

FROM tomcat:9.0-jdk11
LABEL maintainer = "Benjamin Weder <weder@iaas.uni-stuttgart.de>, Lukas Harzenetter <lharzenetter@gmx.de>, Michael Wurster <miwurster@gmail.com>"

ARG DOCKERIZE_VERSION=v0.6.1

ENV CONTAINER_HOSTNAME localhost
ENV CONTAINER_PORT 1337

ENV CONTAINER_REPOSITORY_HOSTNAME localhost
ENV CONTAINER_REPOSITORY_PORT 8091
ENV CONTAINER_REPOSITORY_CONTEXT winery

ENV CONTAINER_BUS_MANAGEMENT_MOCK false
ENV CONTAINER_DEPLOYMENT_TESTS false

ENV ENGINE_IA_HOSTNAME localhost
ENV ENGINE_IA_PORT 8090
ENV ENGINE_IA_USER admin
ENV ENGINE_IA_PASSWORD admin
ENV ENGINE_IA_JAVA17_HOSTNAME localhost
ENV ENGINE_IA_JAVA17_PORT 8093
ENV ENGINE_IA_JAVA17_USER admin
ENV ENGINE_IA_JAVA17_PASSWORD admin
ENV ENGINE_IA_KEEP_FILES true

ENV ENGINE_PLAN_BPEL ODE
ENV ENGINE_PLAN_BPEL_USER ""
ENV ENGINE_PLAN_BPEL_PASSWORD ""
ENV ENGINE_PLAN_BPEL_HOSTNAME localhost
ENV ENGINE_PLAN_BPEL_PORT 9763
ENV ENGINE_PLAN_BPEL_CONTEXT ode
ENV ENGINE_PLAN_BPEL_SERVICES_PATH processes

ENV ENGINE_PLAN_BPMN Camunda
ENV ENGINE_PLAN_BPMN_USER admin
ENV ENGINE_PLAN_BPMN_PASSWORD admin
ENV ENGINE_PLAN_BPMN_HOSTNAME localhost
ENV ENGINE_PLAN_BPMN_PORT 8092
ENV ENGINE_PLAN_BPMN_CONTEXT engine-rest

ENV MQTT_BROKER_PORT 1883
ENV MQTT_BROKER_USER admin
ENV MQTT_BROKER_PASSWORD admin

ENV COLLABORATION_MODE false
ENV COLLABORATION_HOSTNAMES ""
ENV COLLABORATION_PORTS ""

RUN apt-get update \
  && apt-get install -y wget \
  && rm -rf /var/lib/apt/lists/*

RUN rm /dev/random && ln -s /dev/urandom /dev/random \
    && wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

RUN rm -rf ${CATALINA_HOME}/webapps/*
COPY --from=builder /tmp/build/container ${CATALINA_HOME}/webapps/ROOT

COPY .docker/application.properties.tpl /tmp/opentosca/container/application.properties.tpl
COPY .docker/server.xml.tpl /tmp/opentosca/container/server.xml.tpl

EXPOSE ${CONTAINER_PORT}

CMD dockerize -template /tmp/opentosca/container/application.properties.tpl:${CATALINA_HOME}/webapps/ROOT/WEB-INF/classes/application.properties \
    -template /tmp/opentosca/container/server.xml.tpl:${CATALINA_HOME}/conf/server.xml \
    && export spring_config_location=${CATALINA_HOME}/webapps/ROOT/WEB-INF/classes/application.properties \
    && ${CATALINA_HOME}/bin/catalina.sh run
