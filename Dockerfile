FROM maven:3-jdk-8 as builder

RUN rm /dev/random && ln -s /dev/urandom /dev/random

WORKDIR /opt/opentosca/container
COPY . /opt/opentosca/container
RUN mvn package


FROM openjdk:8
LABEL maintainer "Johannes Wettinger <jowettinger@gmail.com>, Michael Wurster <miwurster@gmail.com>"

ARG DOCKERIZE_VERSION=v0.3.0

ENV PUBLIC_HOSTNAME localhost
ENV CONTAINER_REPOSITORY_HOSTNAME localhost
ENV CONTAINER_REPOSITORY_PORT 8091
ENV ENGINE_PLAN_HOSTNAME localhost
ENV ENGINE_IA_HOSTNAME localhost
ENV ENGINE_IA_USER_NAME admin
ENV ENGINE_IA_PWD admin
ENV ENGINE_PLAN ODE
ENV ENGINE_PLAN_ROOT_URL http://localhost:9763/ode
ENV ENGINE_PLAN_SERVICES_URL http://localhost:9763/ode/processes
ENV COLLABORATION_MODE false
ENV COLLABORATION_HOSTNAMES ""
ENV COLLABORATION_PORTS ""

RUN rm /dev/random && ln -s /dev/urandom /dev/random \
    && wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

COPY --from=builder /opt/opentosca/container/org.opentosca.container.product/target/products/org.opentosca.container.product/linux/gtk/x86_64 /opt/opentosca/container

WORKDIR /opt/opentosca/container

RUN ln -s /opt/opentosca/container/OpenTOSCA /usr/local/bin/opentosca-container \
    && chmod +x /usr/local/bin/opentosca-container

ADD docker/config.ini.tpl /opt/opentosca/container/config.ini.tpl
ADD docker/OpenTOSCA.ini.tpl /opt/opentosca/container/OpenTOSCA.ini.tpl

EXPOSE 1337

CMD dockerize -template /opt/opentosca/container/config.ini.tpl:/opt/opentosca/container/configuration/config.ini \
    -template /opt/opentosca/container/OpenTOSCA.ini.tpl:/opt/opentosca/container/OpenTOSCA.ini \
    /usr/local/bin/opentosca-container
