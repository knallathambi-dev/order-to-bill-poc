# SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

# Normal startup: 20/25sec, optimized startup: 11 sec on 4CPU
FROM eclipse-temurin:17-jdk-alpine as builder

ARG MAVEN_REPOSITORY_URL="https://repo1.maven.org/maven2"

RUN apk add --no-cache --update \
       curl \
       bash

HEALTHCHECK --interval=15s --timeout=3s --retries=3 CMD curl --silent --fail http://localhost:8080/actuator/health | grep "UP" || exit 1

WORKDIR /app
COPY target/discobole-orderorchestration-orchestration-delivery*-exec.jar ./
RUN mv -T ./discobole-orderorchestration-orchestration-delivery*-exec.jar app.jar \
      && wget -q ${MAVEN_REPOSITORY_URL}/io/opentelemetry/javaagent/opentelemetry-javaagent/1.24.0/opentelemetry-javaagent-1.24.0.jar \
      && mv -T ./opentelemetry-javaagent-*.jar opentelemetry-javaagent.jar

# Information sur l'image Docker
LABEL name="orchestration-delivery" \
      version="1.9.0" \
      description="orchestration-delivery Spring Boot application" \
      maintainer="aelhoseiny.ext@orange.com"

# make our image running with non root user, files are read-only
RUN addgroup java && adduser -D javauser java
# hadolint ignore=DL3002
USER root
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker,console
ENV APP_CLASS="com.orange.discobole.orderorchestration.orchestrationdelivery.OrchestrationDeliveryApplication"

ENTRYPOINT ["java","-jar","app.jar"]

# Démarrage de l'application fait dans l'image de base.

