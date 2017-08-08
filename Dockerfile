FROM maven:3-jdk-8 as builder

ARG GIT_REPO_URL=https://github.com/OpenTOSCA/container.git
ARG GIT_BRANCH=master

RUN rm /dev/random && ln -s /dev/urandom /dev/random

WORKDIR /opt/opentosca/container

RUN git clone --recursive --depth=1 ${GIT_REPO_URL} -b ${GIT_BRANCH} /opt/opentosca/container \
    && mvn package


FROM openjdk:8
LABEL maintainer "Johannes Wettinger <jowettinger@gmail.com>, Michael Wurster <miwurster@gmail.com>"

ARG DOCKERIZE_VERSION=v0.3.0

ENV CONTAINER_HOSTNAME localhost
ENV CONTAINER_REPOSITORY_HOSTNAME localhost
ENV ENGINE_PLAN_HOSTNAME localhost
ENV ENGINE_IA_HOSTNAME localhost

RUN rm /dev/random && ln -s /dev/urandom /dev/random \
    && wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

COPY --from=builder /opt/opentosca/container/org.opentosca.container.product/target/products/org.opentosca.container.product/linux/gtk/x86_64 /opt/opentosca/container

WORKDIR /opt/opentosca/container

RUN ln -s /opt/opentosca/container/OpenTOSCA /usr/local/bin/opentosca-container \
    && chmod +x /usr/local/bin/opentosca-container

ADD docker/config.ini.tpl /opt/opentosca/container/config.ini.tpl

EXPOSE 1337

CMD dockerize -template /opt/opentosca/container/config.ini.tpl:/opt/opentosca/container/configuration/config.ini \
    /usr/local/bin/opentosca-container
